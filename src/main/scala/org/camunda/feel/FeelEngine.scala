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

import org.camunda.feel.FeelEngine.{
  Configuration,
  EvalExpressionResult,
  EvalUnaryTestsResult,
  Failure
}
import org.camunda.feel.context.{Context, FunctionProvider, VariableProvider}
import org.camunda.feel.impl.interpreter.{
  BuiltinFunctions,
  EvalContext,
  FeelInterpreter
}
import org.camunda.feel.impl.parser.FeelParser._
import org.camunda.feel.impl.parser.{ExpressionValidator, FeelParser}
import org.camunda.feel.syntaxtree.{Exp, ParsedExpression, ValError}
import org.camunda.feel.valuemapper.ValueMapper.CompositeValueMapper
import org.camunda.feel.valuemapper.{CustomValueMapper, ValueMapper}

import scala.collection.JavaConverters._

object FeelEngine {

  type EvalExpressionResult = Either[Failure, Any]
  type EvalUnaryTestsResult = Either[Failure, Boolean]

  case class Configuration(externalFunctionsEnabled: Boolean = false)

  def defaultFunctionProvider: FunctionProvider =
    FunctionProvider.EmptyFunctionProvider

  def defaultValueMapper: ValueMapper = ValueMapper.defaultValueMapper

  def defaultConfiguration: Configuration = Configuration()

  def defaultClock: FeelEngineClock = FeelEngineClock.SystemClock

  case class Failure(message: String)

  class Builder {

    private var functionProvider_ : FunctionProvider = defaultFunctionProvider
    private var valueMapper_ : ValueMapper = defaultValueMapper
    private var customValueMappers_ : List[CustomValueMapper] = List.empty
    private var clock_ : FeelEngineClock = defaultClock
    private var configuration_ : Configuration = defaultConfiguration

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
      configuration = configuration_
    )

  }

  object UnaryTests {
    val inputVariable: String = "inputVariableName"
    val defaultInputVariable: String = "cellInput"
  }

}

class FeelEngine(
    val functionProvider: FunctionProvider = FeelEngine.defaultFunctionProvider,
    val valueMapper: ValueMapper = FeelEngine.defaultValueMapper,
    val configuration: Configuration = FeelEngine.defaultConfiguration,
    val clock: FeelEngineClock = FeelEngine.defaultClock) {

  val interpreter = new FeelInterpreter()

  val validator = new ExpressionValidator(
    externalFunctionsEnabled = configuration.externalFunctionsEnabled)

  logger.info(
    s"Engine created. [" +
      s"value-mapper: $valueMapper, " +
      s"function-provider: $functionProvider, " +
      s"clock: $clock, " +
      s"configuration: $configuration]")

  private val rootContext: EvalContext = new EvalContext(
    valueMapper = valueMapper,
    variableProvider = VariableProvider.EmptyVariableProvider,
    functionProvider = FunctionProvider.CompositeFunctionProvider(
      List(new BuiltinFunctions(clock), functionProvider))
  )

  def evalExpression(
      expression: String,
      variables: java.util.Map[String, Object]): EvalExpressionResult =
    evalExpression(expression, variables.asScala.toMap)

  def evalExpression(
      expression: String,
      variables: Map[String, Any] = Map()): EvalExpressionResult = {
    eval(FeelParser.parseExpression,
         expression,
         Context.StaticContext(variables))
  }

  def evalExpression(expression: String,
                     context: Context): EvalExpressionResult = {
    eval(FeelParser.parseExpression, expression, context)
  }

  def evalUnaryTests(
      expression: String,
      variables: java.util.Map[String, Object]): EvalUnaryTestsResult =
    evalUnaryTests(expression, variables.asScala.toMap)

  def evalUnaryTests(
      expression: String,
      variables: Map[String, Any] = Map()): EvalUnaryTestsResult = {
    eval(FeelParser.parseUnaryTests,
         expression,
         Context.StaticContext(variables))
      .map(value => value.asInstanceOf[Boolean])
  }

  def evalUnaryTests(expression: String,
                     context: Context): EvalUnaryTestsResult = {
    eval(FeelParser.parseUnaryTests, expression, context)
      .map(value => value.asInstanceOf[Boolean])
  }

  private def eval(parser: String => ParseResult[Exp],
                   expression: String,
                   context: Context): EvalExpressionResult =
    parse(parser, expression)
      .flatMap(expr => eval(expr, context))

  private def parse(parser: String => ParseResult[Exp],
                    expression: String): Either[Failure, ParsedExpression] =
    parser(expression) match {
      case Success(exp, _) => Right(ParsedExpression(exp, expression))
      case e: NoSuccess =>
        Left(Failure(s"failed to parse expression '$expression': $e"))
    }

  private def validate(
      exp: ParsedExpression): Either[Failure, ParsedExpression] = {

    validator
      .validateExpression(exp.expression)
      .map(failure =>
        Failure(
          s"""validation of expression '${exp.text}' failed: ${failure.message}"""))
      .toLeft(exp)
  }

  def eval(exp: ParsedExpression, context: Context): EvalExpressionResult =
    validate(exp).flatMap(_ => eval(exp, rootContext + context))

  private def eval(exp: ParsedExpression,
                   context: EvalContext): EvalExpressionResult = {
    interpreter.eval(exp.expression)(context) match {
      case ValError(cause) =>
        Left(Failure(s"failed to evaluate expression '${exp.text}': $cause"))
      case value => Right(valueMapper.unpackVal(value))
    }
  }

  def eval(exp: ParsedExpression,
           variables: java.util.Map[String, Object]): EvalExpressionResult =
    eval(exp, variables.asScala.toMap)

  def eval(exp: ParsedExpression,
           variables: Map[String, Any] = Map()): EvalExpressionResult = {
    eval(exp, Context.StaticContext(variables))
  }

  def parseExpression(expression: String): Either[Failure, ParsedExpression] =
    parse(FeelParser.parseExpression, expression)
      .flatMap(validate)

  def parseUnaryTests(expression: String): Either[Failure, ParsedExpression] =
    parse(FeelParser.parseUnaryTests, expression)
      .flatMap(validate)

}
