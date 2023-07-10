package org.camunda.feel.api

import org.camunda.feel.FeelEngine
import org.camunda.feel.context.Context
import org.camunda.feel.impl.interpreter.EvalContext
import org.camunda.feel.syntaxtree.ParsedExpression

import scala.jdk.CollectionConverters.MapHasAsScala

class FeelEngineApi(engine: FeelEngine) {

  // =========== parse expression ===========

  def parseExpression(expression: String): ParseResult =
    engine.parseExpression(expression = expression) match {
      case Right(parsedExpression) => SuccessfulParseResult(parsedExpression = parsedExpression)
      case Left(failure) => FailedParseResult(
        expression = expression,
        failure = failure
      )
    }

  def parseUnaryTests(expression: String): ParseResult =
    engine.parseUnaryTests(expression = expression) match {
      case Right(parsedExpression) => SuccessfulParseResult(parsedExpression = parsedExpression)
      case Left(failure) => FailedParseResult(
        expression = expression,
        failure = failure
      )
    }

  // =========== evaluate parsed expression ===========

  def evaluate(expression: ParsedExpression, context: Context): EvaluationResult =
    engine.evaluate(
      expression = expression,
      context = context
    )

  def evaluate(expression: ParsedExpression, variables: Map[String, Any] = Map()): EvaluationResult =
    evaluate(
      expression = expression,
      context = Context.StaticContext(variables = variables)
    )

  def evaluate(expression: ParsedExpression, variables: java.util.Map[String, Object]): EvaluationResult =
    evaluate(
      expression = expression,
      context = Context.StaticContext(variables = variables.asScala.toMap)
    )

  def evaluateWithInput(expression: ParsedExpression, inputValue: Any, context: Context): EvaluationResult =
    engine.evaluate(
      expression = expression,
      context = createContextWithInput(context, inputValue)
    )

  def evaluateWithInput(expression: ParsedExpression, inputValue: Any, variables: Map[String, Any] = Map()): EvaluationResult =
    evaluateWithInput(
      expression = expression,
      inputValue = inputValue,
      context = Context.StaticContext(variables = variables)
    )

  def evaluateWithInput(expression: ParsedExpression, inputValue: Any, variables: java.util.Map[String, Object]): EvaluationResult =
    evaluateWithInput(
      expression = expression,
      inputValue = inputValue,
      context = Context.StaticContext(variables = variables.asScala.toMap)
    )

  // =========== parse and evaluate expression ===========

  def evaluateExpression(expression: String, context: Context): EvaluationResult =
    engine.parseExpression(expression = expression) match {
      case Right(parsedExpression) => evaluate(
        expression = parsedExpression,
        context = context
      )
      case Left(parseFailure) => FailedEvaluationResult(
        failure = parseFailure
      )
    }

  def evaluateExpression(expression: String, variables: Map[String, Any] = Map()): EvaluationResult =
    evaluateExpression(
      expression = expression,
      context = Context.StaticContext(variables = variables)
    )

  def evaluateExpression(expression: String, variables: java.util.Map[String, Object]): EvaluationResult =
    evaluateExpression(
      expression = expression,
      context = Context.StaticContext(variables = variables.asScala.toMap)
    )

  // =========== parse and evaluate unary-tests ===========

  def evaluateUnaryTests(expression: String, inputValue: Any, context: Context): EvaluationResult =
    engine.parseUnaryTests(expression = expression) match {
      case Right(parsedExpression) => evaluate(
        expression = parsedExpression,
        context = createContextWithInput(context, inputValue)
      )
      case Left(parseFailure) => FailedEvaluationResult(
        failure = parseFailure
      )
    }

  def evaluateUnaryTests(expression: String, inputValue: Any, variables: Map[String, Any] = Map()): EvaluationResult =
    evaluateUnaryTests(
      expression = expression,
      inputValue = inputValue,
      context = Context.StaticContext(variables = variables)
    )

  def evaluateUnaryTests(expression: String, inputValue: Object, variables: java.util.Map[String, Object]): EvaluationResult =
    evaluateUnaryTests(
      expression = expression,
      inputValue = inputValue,
      context = Context.StaticContext(variables = variables.asScala.toMap)
    )

  // =========== internal helpers ===========

  private def createContextWithInput(context: Context, inputValue: Any): EvalContext = {
    val inputVariable = FeelEngine.UnaryTests.defaultInputVariable
    val wrappedInputValue = engine.valueMapper.toVal(inputValue)

    EvalContext.wrap(context, engine.valueMapper).add(inputVariable -> wrappedInputValue)
  }

}
