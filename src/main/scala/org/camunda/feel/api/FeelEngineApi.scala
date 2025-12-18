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
package org.camunda.feel.api

import org.camunda.feel.FeelEngine
import org.camunda.feel.FeelEngine.Failure
import org.camunda.feel.context.Context
import org.camunda.feel.impl.InterruptibleTimeout
import org.camunda.feel.impl.interpreter.EvalContext
import org.camunda.feel.syntaxtree.ParsedExpression

import java.util.concurrent.RejectedExecutionException
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._
import scala.jdk.CollectionConverters.MapHasAsScala

/** The API to interact with the FEEL engine.
  *
  * @param engine
  *   the FEEL engine to interact with
  */
class FeelEngineApi(engine: FeelEngine) {

  // Timeout support is implemented at the API layer (and not in FeelEngine) to keep the core engine
  // API stable and backward-compatible. A timeout works by evaluating on a worker thread and
  // interrupting it on expiry; stopping is therefore cooperative.

  private def failedTimedOut(expression: String, timeout: FiniteDuration): EvaluationResult =
    FailedEvaluationResult(
      failure = Failure(
        s"failed to evaluate expression '$expression': evaluation timed out after ${timeout.toMillis}ms"
      )
    )

  private def failedInterrupted(expression: String): EvaluationResult =
    FailedEvaluationResult(
      failure = Failure(
        s"failed to evaluate expression '$expression': evaluation interrupted"
      )
    )

  private def failedRejected(expression: String): EvaluationResult =
    FailedEvaluationResult(
      failure = Failure(
        s"failed to evaluate expression '$expression': evaluation rejected because the timeout executor is saturated"
      )
    )

  private def withTimeout(
      expression: String,
      timeout: FiniteDuration
  )(
      thunk: => EvaluationResult
  ): EvaluationResult = {
    try {
      // Run evaluation on a worker thread so we can cancel/interrupt it on timeout.
      // This is cooperative cancellation: evaluation stops promptly only if it reacts to interrupts
      // (e.g. blocking calls like Thread.sleep, or explicit interrupt checks inside the interpreter).
      InterruptibleTimeout.run(timeout) {
        thunk
      }
    } catch {
      case InterruptibleTimeout.TimedOut(_)   => failedTimedOut(expression, timeout)
      case InterruptibleTimeout.Interrupted() => failedInterrupted(expression)
      case InterruptibleTimeout.Rejected(_)   => failedRejected(expression)
      case _: RejectedExecutionException      => failedRejected(expression)
      case _: InterruptedException            =>
        // Preserve the caller thread's interrupted status.
        Thread.currentThread().interrupt()
        failedInterrupted(expression)
    }
  }

  // =========== parse expression ===========

  /** Parses an FEEL expression.
    *
    * @param expression
    *   the expression to parse
    * @return
    *   the result of the parsing as [[ParseResult]]
    */
  def parseExpression(expression: String): ParseResult =
    engine.parseExpression(expression = expression) match {
      case Right(parsedExpression) => SuccessfulParseResult(parsedExpression = parsedExpression)
      case Left(failure)           =>
        FailedParseResult(
          expression = expression,
          failure = failure
        )
    }

  /** Parses an FEEL unary-tests expression.
    *
    * @param expression
    *   the expression to parse
    * @return
    *   the result of the parsing as [[ParseResult]]
    */
  def parseUnaryTests(expression: String): ParseResult =
    engine.parseUnaryTests(expression = expression) match {
      case Right(parsedExpression) => SuccessfulParseResult(parsedExpression = parsedExpression)
      case Left(failure)           =>
        FailedParseResult(
          expression = expression,
          failure = failure
        )
    }

  // =========== evaluate parsed expression ===========

  /** Evaluates a parsed expression with the given context.
    *
    * @param expression
    *   the parsed expression to evaluate
    * @param context
    *   the context for the evaluation
    * @return
    *   the result of the evaluation as [[EvaluationResult]]
    */
  def evaluate(expression: ParsedExpression, context: Context): EvaluationResult =
    engine.evaluate(
      expression = expression,
      context = context
    )

  /** Evaluates a parsed expression with the given context and a timeout.
    */
  def evaluate(
      expression: ParsedExpression,
      context: Context,
      timeout: FiniteDuration
  ): EvaluationResult =
    withTimeout(expression.text, timeout) {
      engine.evaluate(
        expression = expression,
        context = context
      )
    }

  /** Evaluates a parsed expression with the given context and a timeout.
    */
  def evaluate(
      expression: ParsedExpression,
      context: Context,
      timeout: java.time.Duration
  ): EvaluationResult =
    evaluate(expression, context, timeout.toMillis.millis)

  /** Evaluates a parsed expression with the given context.
    *
    * @param expression
    *   the parsed expression to evaluate
    * @param variables
    *   the variable context for the evaluation
    * @return
    *   the result of the evaluation as [[EvaluationResult]]
    */
  def evaluate(
      expression: ParsedExpression,
      variables: Map[String, Any] = Map()
  ): EvaluationResult =
    evaluate(
      expression = expression,
      context = Context.StaticContext(variables = variables)
    )

  /** Evaluates a parsed expression with the given context.
    *
    * @param expression
    *   the parsed expression to evaluate
    * @param variables
    *   the variable context for the evaluation.
    * @return
    *   the result of the evaluation as [[EvaluationResult]]
    */
  def evaluate(
      expression: ParsedExpression,
      variables: java.util.Map[String, Object]
  ): EvaluationResult =
    evaluate(
      expression = expression,
      context = Context.StaticContext(variables = variables.asScala.toMap)
    )

  /** Evaluates a parsed unary-tests expression with the given input value and context.
    *
    * @param expression
    *   the parsed expression to evaluate
    * @param inputValue
    *   the input value for the evaluation of the unary-tests
    * @param context
    *   the context for the evaluation
    * @return
    *   the result of the evaluation as [[EvaluationResult]]
    */
  def evaluateWithInput(
      expression: ParsedExpression,
      inputValue: Any,
      context: Context
  ): EvaluationResult =
    engine.evaluate(
      expression = expression,
      context = createContextWithInput(context, inputValue)
    )

  /** Evaluates a parsed unary-tests expression with the given input value, context and a timeout.
    */
  def evaluateWithInput(
      expression: ParsedExpression,
      inputValue: Any,
      context: Context,
      timeout: FiniteDuration
  ): EvaluationResult =
    withTimeout(expression.text, timeout) {
      engine.evaluate(
        expression = expression,
        context = createContextWithInput(context, inputValue)
      )
    }

  /** Evaluates a parsed unary-tests expression with the given input value, context and a timeout.
    */
  def evaluateWithInput(
      expression: ParsedExpression,
      inputValue: Any,
      context: Context,
      timeout: java.time.Duration
  ): EvaluationResult =
    evaluateWithInput(expression, inputValue, context, timeout.toMillis.millis)

  /** Evaluates a parsed unary-tests expression with the given input value and context.
    *
    * @param expression
    *   the parsed expression to evaluate
    * @param inputValue
    *   the input value for the evaluation of the unary-tests
    * @param variables
    *   the variable context for the evaluation
    * @return
    *   the result of the evaluation as [[EvaluationResult]]
    */
  def evaluateWithInput(
      expression: ParsedExpression,
      inputValue: Any,
      variables: Map[String, Any] = Map()
  ): EvaluationResult =
    evaluateWithInput(
      expression = expression,
      inputValue = inputValue,
      context = Context.StaticContext(variables = variables)
    )

  /** Evaluates a parsed unary-tests expression with the given input value and context.
    *
    * @param expression
    *   the parsed expression to evaluate
    * @param inputValue
    *   the input value for the evaluation of the unary-tests
    * @param variables
    *   the variable context for the evaluation
    * @return
    *   the result of the evaluation as [[EvaluationResult]]
    */
  def evaluateWithInput(
      expression: ParsedExpression,
      inputValue: Any,
      variables: java.util.Map[String, Object]
  ): EvaluationResult =
    evaluateWithInput(
      expression = expression,
      inputValue = inputValue,
      context = Context.StaticContext(variables = variables.asScala.toMap)
    )

  // =========== parse and evaluate expression ===========

  /** Evaluates an FEEL expression with the given context. This is a shortcut and skips the explicit
    * parsing step before the evaluation.
    *
    * @param expression
    *   the expression to evaluate
    * @param context
    *   the context for the evaluation
    * @return
    *   the result of the evaluation as [[EvaluationResult]]
    */
  def evaluateExpression(expression: String, context: Context): EvaluationResult =
    engine.parseExpression(expression = expression) match {
      case Right(parsedExpression) =>
        evaluate(
          expression = parsedExpression,
          context = context
        )
      case Left(parseFailure)      =>
        FailedEvaluationResult(
          failure = parseFailure
        )
    }

  /** Evaluates an FEEL expression with the given context and a timeout.
    */
  def evaluateExpression(
      expression: String,
      context: Context,
      timeout: FiniteDuration
  ): EvaluationResult =
    engine.parseExpression(expression = expression) match {
      case Right(parsedExpression) =>
        evaluate(
          expression = parsedExpression,
          context = context,
          timeout = timeout
        )
      case Left(parseFailure)      =>
        FailedEvaluationResult(
          failure = parseFailure
        )
    }

  /** Evaluates an FEEL expression with the given context and a timeout.
    */
  def evaluateExpression(
      expression: String,
      context: Context,
      timeout: java.time.Duration
  ): EvaluationResult =
    evaluateExpression(expression, context, timeout.toMillis.millis)

  /** Evaluates an FEEL expression with the given context. This is a shortcut and skips the explicit
    * parsing step before the evaluation.
    *
    * @param expression
    *   the expression to evaluate
    * @param variables
    *   the variable context for the evaluation
    * @return
    *   the result of the evaluation as [[EvaluationResult]]
    */
  def evaluateExpression(
      expression: String,
      variables: Map[String, Any] = Map()
  ): EvaluationResult =
    evaluateExpression(
      expression = expression,
      context = Context.StaticContext(variables = variables)
    )

  /** Evaluates an FEEL expression with the given context. This is a shortcut and skips the explicit
    * parsing step before the evaluation.
    *
    * @param expression
    *   the expression to evaluate
    * @param variables
    *   the variable context for the evaluation
    * @return
    *   the result of the evaluation as [[EvaluationResult]]
    */
  def evaluateExpression(
      expression: String,
      variables: java.util.Map[String, Object]
  ): EvaluationResult =
    evaluateExpression(
      expression = expression,
      context = Context.StaticContext(variables = variables.asScala.toMap)
    )

  // =========== parse and evaluate unary-tests ===========

  /** Evaluates an FEEL unary-tests expression with the given input value and context. This is a
    * shortcut and skips the explicit parsing step before the evaluation.
    *
    * @param expression
    *   the expression to evaluate
    * @param inputValue
    *   the input value for the evaluation of the unary-tests
    * @param context
    *   the context for the evaluation
    * @return
    *   the result of the evaluation as [[EvaluationResult]]
    */
  def evaluateUnaryTests(expression: String, inputValue: Any, context: Context): EvaluationResult =
    engine.parseUnaryTests(expression = expression) match {
      case Right(parsedExpression) =>
        evaluate(
          expression = parsedExpression,
          context = createContextWithInput(context, inputValue)
        )
      case Left(parseFailure)      =>
        FailedEvaluationResult(
          failure = parseFailure
        )
    }

  /** Evaluates an FEEL unary-tests expression with the given input value, context and a timeout.
    */
  def evaluateUnaryTests(
      expression: String,
      inputValue: Any,
      context: Context,
      timeout: FiniteDuration
  ): EvaluationResult =
    engine.parseUnaryTests(expression = expression) match {
      case Right(parsedExpression) =>
        evaluate(
          expression = parsedExpression,
          context = createContextWithInput(context, inputValue),
          timeout = timeout
        )
      case Left(parseFailure)      =>
        FailedEvaluationResult(
          failure = parseFailure
        )
    }

  /** Evaluates an FEEL unary-tests expression with the given input value, context and a timeout.
    */
  def evaluateUnaryTests(
      expression: String,
      inputValue: Any,
      context: Context,
      timeout: java.time.Duration
  ): EvaluationResult =
    evaluateUnaryTests(expression, inputValue, context, timeout.toMillis.millis)

  /** Evaluates an FEEL unary-tests expression with the given input value and context. This is a
    * shortcut and skips the explicit parsing step before the evaluation.
    *
    * @param expression
    *   the expression to evaluate
    * @param inputValue
    *   the input value for the evaluation of the unary-tests
    * @param variables
    *   the variable context for the evaluation
    * @return
    *   the result of the evaluation as [[EvaluationResult]]
    */
  def evaluateUnaryTests(
      expression: String,
      inputValue: Any,
      variables: Map[String, Any] = Map()
  ): EvaluationResult =
    evaluateUnaryTests(
      expression = expression,
      inputValue = inputValue,
      context = Context.StaticContext(variables = variables)
    )

  /** Evaluates an FEEL unary-tests expression with the given input value and context. This is a
    * shortcut and skips the explicit parsing step before the evaluation.
    *
    * @param expression
    *   the expression to evaluate
    * @param inputValue
    *   the input value for the evaluation of the unary-tests
    * @param variables
    *   the variable context for the evaluation
    * @return
    *   the result of the evaluation as [[EvaluationResult]]
    */
  def evaluateUnaryTests(
      expression: String,
      inputValue: Object,
      variables: java.util.Map[String, Object]
  ): EvaluationResult =
    evaluateUnaryTests(
      expression = expression,
      inputValue = inputValue,
      context = Context.StaticContext(variables = variables.asScala.toMap)
    )

  // =========== internal helpers ===========

  private def createContextWithInput(context: Context, inputValue: Any): EvalContext = {
    val inputVariable     = FeelEngine.UnaryTests.defaultInputVariable
    val wrappedInputValue = engine.valueMapper.toVal(inputValue)

    EvalContext.wrap(context, engine.valueMapper).add(inputVariable -> wrappedInputValue)
  }

}
