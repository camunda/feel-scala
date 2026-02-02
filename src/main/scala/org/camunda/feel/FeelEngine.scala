/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.feel

import fastparse.Parsed
import org.camunda.feel.FeelEngine.{
  Configuration,
  EvalExpressionResult,
  EvalUnaryTestsResult,
  Failure
}
import org.camunda.feel.api.{
  EvaluationFailure,
  EvaluationFailureType,
  EvaluationResult,
  FailedEvaluationResult,
  SuccessfulEvaluationResult
}
import org.camunda.feel.context.{Context, FunctionProvider}
import org.camunda.feel.impl.interpreter.{BuiltinFunctions, EvalContext, FeelInterpreter}
import org.camunda.feel.impl.parser.{ExpressionValidator, FeelParser}
import org.camunda.feel.syntaxtree.{Exp, ParsedExpression, ValError, ValFatalError}
import org.camunda.feel.valuemapper.ValueMapper.CompositeValueMapper
import org.camunda.feel.valuemapper.{CustomValueMapper, ValueMapper}

import scala.jdk.CollectionConverters.MapHasAsScala
import scala.util.Try
object FeelEngine {

  type EvalExpressionResult = Either[Failure, Any]
  type EvalUnaryTestsResult = Either[Failure, Boolean]

  case class Configuration(externalFunctionsEnabled: Boolean = false) {
    override def toString: String = s"{externalFunctionsEnabled: $externalFunctionsEnabled}"
  }

  def defaultFunctionProvider: FunctionProvider =
    FunctionProvider.EmptyFunctionProvider

  def defaultValueMapper: ValueMapper = ValueMapper.defaultValueMapper

  def defaultConfiguration: Configuration = Configuration()

  def defaultClock: FeelEngineClock = FeelEngineClock.SystemClock

  case class Failure(message: String)

  /** @deprecated
    *   Use [[org.camunda.feel.api.FeelEngineBuilder]] instead.
    */
  @deprecated class Builder {

    private var functionProvider_ : FunctionProvider          = defaultFunctionProvider
    private var valueMapper_ : ValueMapper                    = defaultValueMapper
    private var customValueMappers_ : List[CustomValueMapper] = List.empty
    private var clock_ : FeelEngineClock                      = defaultClock
    private var configuration_ : Configuration                = defaultConfiguration

    def functionProvider(functionProvider: FunctionProvider): Builder = {
      functionProvider_ = functionProvider
      this
    }

    def customValueMapper(customValueMapper: CustomValueMapper): Builder = {
      customValueMappers_ = customValueMapper :: customValueMappers_
      valueMapper_ = CompositeValueMapper(customValueMappers_)
      this
    }

    def valueMapper(valueMapper: ValueMapper): Builder = {
      valueMapper_ = valueMapper
      this
    }

    def clock(clock: FeelEngineClock): Builder = {
      clock_ = clock
      this
    }

    def enableExternalFunctions(enable: Boolean): Builder = {
      configuration_ = configuration_.copy(externalFunctionsEnabled = enable)
      this
    }

    def build: FeelEngine = new FeelEngine(
      functionProvider = functionProvider_,
      valueMapper = valueMapper_,
      configuration = configuration_,
      clock = clock_
    )

  }

  object UnaryTests {
    val inputVariable: String        = "inputVariableName"
    val defaultInputVariable: String = "cellInput"
  }

}

class FeelEngine(
    val functionProvider: FunctionProvider = FeelEngine.defaultFunctionProvider,
    val valueMapper: ValueMapper = FeelEngine.defaultValueMapper,
    val configuration: Configuration = FeelEngine.defaultConfiguration,
    val clock: FeelEngineClock = FeelEngine.defaultClock
) {

  private val interpreter = new FeelInterpreter(valueMapper)

  private val validator = new ExpressionValidator(
    externalFunctionsEnabled = configuration.externalFunctionsEnabled
  )

  logger.debug(
    s"Engine created. [" +
      s"value-mapper: $valueMapper, " +
      s"function-provider: $functionProvider, " +
      s"clock: $clock, " +
      s"configuration: $configuration]"
  )

  private val rootContext: EvalContext = EvalContext.create(
    valueMapper = valueMapper,
    functionProvider = FunctionProvider.CompositeFunctionProvider(
      List(
        new BuiltinFunctions(clock, valueMapper),
        functionProvider
      )
    )
  )

  private def parse(
      parser: String => Parsed[Exp],
      expression: String
  ): Either[Failure, ParsedExpression] =
    Try {
      parser(expression) match {
        case Parsed.Success(exp, _)      => Right(ParsedExpression(exp, expression))
        case Parsed.Failure(_, _, extra) =>
          Left(Failure(s"failed to parse expression '$expression': ${extra.trace().aggregateMsg}"))
      }
    }.recover(failure => Left(Failure(s"failed to parse expression '$expression': $failure"))).get

  private def validate(exp: ParsedExpression): Either[Failure, ParsedExpression] = {

    validator
      .validateExpression(exp.expression)
      .map(failure =>
        Failure(s"""validation of expression '${exp.text}' failed: ${failure.message}""")
      )
      .toLeft(exp)
  }

  private def eval(exp: ParsedExpression, context: EvalContext): EvaluationResult = {
    interpreter.eval(exp.expression)(context) match {
      case _ if containsAssertionError(context) => {
        val failureMessage = getAssertErrorMessage(context)
        FailedEvaluationResult(
          failure = Failure(
            s"Assertion failure on evaluate the expression '${exp.text}': ${failureMessage}"
          ),
          suppressedFailures = context.failureCollector.failures
        )
      }
      case ValError(cause)                      =>
        FailedEvaluationResult(
          failure = Failure(s"failed to evaluate expression '${exp.text}': $cause"),
          suppressedFailures = context.failureCollector.failures
        )

      case ValFatalError(cause) =>
        FailedEvaluationResult(
          failure = Failure(s"failed to evaluate expression '${exp.text}': $cause"),
          suppressedFailures = context.failureCollector.failures
        )

      case value =>
        SuccessfulEvaluationResult(
          result = valueMapper.unpackVal(value),
          suppressedFailures = context.failureCollector.failures
        )
    }
  }

  /** Check if an {@link EvaluationFailureType.ASSERT_FAILURE} error is raised during the evaluation
    * of an expression
    * @param context
    *   the context of the evaluation
    * @return
    *   true if an an {@link EvaluationFailureType.ASSERT_FAILURE} is raised, false otherwise
    */
  private def containsAssertionError(context: EvalContext): Boolean = {
    context.failureCollector.failures.exists(_.failureType == EvaluationFailureType.ASSERT_FAILURE)
  }

  private def getAssertErrorMessage(context: EvalContext): String = {
    context.failureCollector.failures
      .find(_.failureType == EvaluationFailureType.ASSERT_FAILURE)
      .get
      .failureMessage
  }

  // ============ public API ============

  /** @deprecated
    *   Use [[org.camunda.feel.api.FeelEngineApi]] instead.
    */
  @deprecated def evalExpression(
      expression: String,
      variables: java.util.Map[String, Object]
  ): EvalExpressionResult =
    evalExpression(
      expression = expression,
      context = Context.StaticContext(variables.asScala.toMap)
    )

  /** @deprecated
    *   Use [[org.camunda.feel.api.FeelEngineApi]] instead.
    */
  @deprecated def evalExpression(
      expression: String,
      variables: Map[String, Any] = Map()
  ): EvalExpressionResult =
    evalExpression(
      expression = expression,
      context = Context.StaticContext(variables)
    )

  /** @deprecated
    *   Use [[org.camunda.feel.api.FeelEngineApi]] instead.
    */
  @deprecated def evalExpression(expression: String, context: Context): EvalExpressionResult =
    parseExpression(expression)
      .flatMap(parsedExpression => eval(parsedExpression, context))

  /** @deprecated
    *   Use [[org.camunda.feel.api.FeelEngineApi]] instead.
    */
  @deprecated def evalUnaryTests(
      expression: String,
      variables: java.util.Map[String, Object]
  ): EvalUnaryTestsResult =
    evalUnaryTests(
      expression = expression,
      context = Context.StaticContext(variables.asScala.toMap)
    )

  /** @deprecated
    *   Use [[org.camunda.feel.api.FeelEngineApi]] instead.
    */
  @deprecated def evalUnaryTests(
      expression: String,
      variables: Map[String, Any] = Map()
  ): EvalUnaryTestsResult =
    evalUnaryTests(
      expression = expression,
      context = Context.StaticContext(variables)
    )

  /** @deprecated
    *   Use [[org.camunda.feel.api.FeelEngineApi]] instead.
    */
  @deprecated def evalUnaryTests(expression: String, context: Context): EvalUnaryTestsResult = {
    parseUnaryTests(expression)
      .flatMap(parsedExpression => eval(parsedExpression, context))
      .map(value => value.asInstanceOf[Boolean])
  }

  /** @deprecated
    *   Use [[org.camunda.feel.api.FeelEngineApi]] instead.
    */
  @deprecated def eval(exp: ParsedExpression, context: Context): EvalExpressionResult =
    evaluate(expression = exp, context = context).toEither

  /** @deprecated
    *   Use [[org.camunda.feel.api.FeelEngineApi]] instead.
    */
  @deprecated def eval(
      exp: ParsedExpression,
      variables: java.util.Map[String, Object]
  ): EvalExpressionResult =
    eval(
      exp = exp,
      context = Context.StaticContext(variables.asScala.toMap)
    )

  /** @deprecated
    *   Use [[org.camunda.feel.api.FeelEngineApi]] instead.
    */
  @deprecated def eval(
      parsedExpression: ParsedExpression,
      variables: Map[String, Any] = Map()
  ): EvalExpressionResult =
    eval(
      exp = parsedExpression,
      context = Context.StaticContext(variables)
    )

  /** @deprecated
    *   Use [[org.camunda.feel.api.FeelEngineApi]] instead.
    */
  @deprecated def parseExpression(expression: String): Either[Failure, ParsedExpression] =
    parse(FeelParser.parseExpression, expression)
      .flatMap(validate)

  /** @deprecated
    *   Use [[org.camunda.feel.api.FeelEngineApi]] instead.
    */
  @deprecated def parseUnaryTests(expression: String): Either[Failure, ParsedExpression] =
    parse(FeelParser.parseUnaryTests, expression)
      .flatMap(validate)

  /** @deprecated
    *   Use [[org.camunda.feel.api.FeelEngineApi]] instead.
    */
  @deprecated def evaluate(expression: ParsedExpression, context: Context): EvaluationResult =
    Try {
      validate(expression) match {
        case Right(_)      =>
          eval(expression, EvalContext.empty(valueMapper).merge(rootContext).merge(context))
        case Left(failure) => FailedEvaluationResult(failure = failure)
      }
    }.recover(failure =>
      FailedEvaluationResult(
        failure = Failure(s"failed to evaluate expression '${expression.text}' : $failure")
      )
    ).get

}
