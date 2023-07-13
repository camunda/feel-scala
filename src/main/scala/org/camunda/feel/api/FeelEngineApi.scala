package org.camunda.feel.api

import org.camunda.feel.FeelEngine
import org.camunda.feel.context.Context
import org.camunda.feel.impl.interpreter.EvalContext
import org.camunda.feel.syntaxtree.ParsedExpression

import scala.jdk.CollectionConverters.MapHasAsScala

/**
  * The API to interact with the FEEL engine.
  *
  * @param engine the FEEL engine to interact with
  */
class FeelEngineApi(engine: FeelEngine) {

  // =========== parse expression ===========

  /**
    * Parses an FEEL expression.
    *
    * @param expression the expression to parse
    * @return the result of the parsing as [[ParseResult]]
    */
  def parseExpression(expression: String): ParseResult =
    engine.parseExpression(expression = expression) match {
      case Right(parsedExpression) => SuccessfulParseResult(parsedExpression = parsedExpression)
      case Left(failure) => FailedParseResult(
        expression = expression,
        failure = failure
      )
    }

  /**
    * Parses an FEEL unary-tests expression.
    *
    * @param expression the expression to parse
    * @return the result of the parsing as [[ParseResult]]
    */
  def parseUnaryTests(expression: String): ParseResult =
    engine.parseUnaryTests(expression = expression) match {
      case Right(parsedExpression) => SuccessfulParseResult(parsedExpression = parsedExpression)
      case Left(failure) => FailedParseResult(
        expression = expression,
        failure = failure
      )
    }

  // =========== evaluate parsed expression ===========

  /**
    * Evaluates a parsed expression with the given context.
    *
    * @param expression the parsed expression to evaluate
    * @param context    the context for the evaluation
    * @return the result of the evaluation as [[EvaluationResult]]
    */
  def evaluate(expression: ParsedExpression, context: Context): EvaluationResult =
    engine.evaluate(
      expression = expression,
      context = context
    )

  /**
    * Evaluates a parsed expression with the given context.
    *
    * @param expression the parsed expression to evaluate
    * @param variables  the variable context for the evaluation
    * @return the result of the evaluation as [[EvaluationResult]]
    */
  def evaluate(expression: ParsedExpression, variables: Map[String, Any] = Map()): EvaluationResult =
    evaluate(
      expression = expression,
      context = Context.StaticContext(variables = variables)
    )

  /**
    * Evaluates a parsed expression with the given context.
    *
    * @param expression the parsed expression to evaluate
    * @param variables  the variable context for the evaluation.
    * @return the result of the evaluation as [[EvaluationResult]]
    */
  def evaluate(expression: ParsedExpression, variables: java.util.Map[String, Object]): EvaluationResult =
    evaluate(
      expression = expression,
      context = Context.StaticContext(variables = variables.asScala.toMap)
    )

  /**
    * Evaluates a parsed unary-tests expression with the given input value and context.
    *
    * @param expression the parsed expression to evaluate
    * @param inputValue the input value for the evaluation of the unary-tests
    * @param context    the context for the evaluation
    * @return the result of the evaluation as [[EvaluationResult]]
    */
  def evaluateWithInput(expression: ParsedExpression, inputValue: Any, context: Context): EvaluationResult =
    engine.evaluate(
      expression = expression,
      context = createContextWithInput(context, inputValue)
    )

  /**
    * Evaluates a parsed unary-tests expression with the given input value and context.
    *
    * @param expression the parsed expression to evaluate
    * @param inputValue the input value for the evaluation of the unary-tests
    * @param variables  the variable context for the evaluation
    * @return the result of the evaluation as [[EvaluationResult]]
    */
  def evaluateWithInput(expression: ParsedExpression, inputValue: Any, variables: Map[String, Any] = Map()): EvaluationResult =
    evaluateWithInput(
      expression = expression,
      inputValue = inputValue,
      context = Context.StaticContext(variables = variables)
    )

  /**
    * Evaluates a parsed unary-tests expression with the given input value and context.
    *
    * @param expression the parsed expression to evaluate
    * @param inputValue the input value for the evaluation of the unary-tests
    * @param variables  the variable context for the evaluation
    * @return the result of the evaluation as [[EvaluationResult]]
    */
  def evaluateWithInput(expression: ParsedExpression, inputValue: Any, variables: java.util.Map[String, Object]): EvaluationResult =
    evaluateWithInput(
      expression = expression,
      inputValue = inputValue,
      context = Context.StaticContext(variables = variables.asScala.toMap)
    )

  // =========== parse and evaluate expression ===========

  /**
    * Evaluates an FEEL expression with the given context. This is a shortcut and skips the explicit
    * parsing step before the evaluation.
    *
    * @param expression the expression to evaluate
    * @param context    the context for the evaluation
    * @return the result of the evaluation as [[EvaluationResult]]
    */
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

  /**
    * Evaluates an FEEL expression with the given context. This is a shortcut and skips the explicit
    * parsing step before the evaluation.
    *
    * @param expression the expression to evaluate
    * @param variables  the variable context for the evaluation
    * @return the result of the evaluation as [[EvaluationResult]]
    */
  def evaluateExpression(expression: String, variables: Map[String, Any] = Map()): EvaluationResult =
    evaluateExpression(
      expression = expression,
      context = Context.StaticContext(variables = variables)
    )

  /**
    * Evaluates an FEEL expression with the given context. This is a shortcut and skips the explicit
    * parsing step before the evaluation.
    *
    * @param expression the expression to evaluate
    * @param variables  the variable context for the evaluation
    * @return the result of the evaluation as [[EvaluationResult]]
    */
  def evaluateExpression(expression: String, variables: java.util.Map[String, Object]): EvaluationResult =
    evaluateExpression(
      expression = expression,
      context = Context.StaticContext(variables = variables.asScala.toMap)
    )

  // =========== parse and evaluate unary-tests ===========

  /**
    * Evaluates an FEEL unary-tests expression with the given input value and context. This is a
    * shortcut and skips the explicit parsing step before the evaluation.
    *
    * @param expression the expression to evaluate
    * @param inputValue the input value for the evaluation of the unary-tests
    * @param context    the context for the evaluation
    * @return the result of the evaluation as [[EvaluationResult]]
    */
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

  /**
    * Evaluates an FEEL unary-tests expression with the given input value and context. This is a
    * shortcut and skips the explicit parsing step before the evaluation.
    *
    * @param expression the expression to evaluate
    * @param inputValue the input value for the evaluation of the unary-tests
    * @param variables  the variable context for the evaluation
    * @return the result of the evaluation as [[EvaluationResult]]
    */
  def evaluateUnaryTests(expression: String, inputValue: Any, variables: Map[String, Any] = Map()): EvaluationResult =
    evaluateUnaryTests(
      expression = expression,
      inputValue = inputValue,
      context = Context.StaticContext(variables = variables)
    )

  /**
    * Evaluates an FEEL unary-tests expression with the given input value and context. This is a
    * shortcut and skips the explicit parsing step before the evaluation.
    *
    * @param expression the expression to evaluate
    * @param inputValue the input value for the evaluation of the unary-tests
    * @param variables  the variable context for the evaluation
    * @return the result of the evaluation as [[EvaluationResult]]
    */
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
