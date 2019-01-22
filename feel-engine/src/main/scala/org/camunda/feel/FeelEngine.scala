package org.camunda.feel

import org.camunda.feel.parser.FeelParser
import org.camunda.feel.parser.FeelParser._
import org.camunda.feel.interpreter.FeelInterpreter
import org.camunda.feel.interpreter.DefaultValueMapper
import org.camunda.feel.interpreter.CompositeContext._
import org.camunda.feel.interpreter._
import org.camunda.feel.parser.Exp
import org.camunda.feel.spi._

/**
  * @author Philipp Ossler
  */
class FeelEngine(val functionProvider: FunctionProvider =
                   FunctionProvider.EmptyFunctionProvider,
                 val valueMapper: ValueMapper = DefaultValueMapper.instance) {

  logger.info(s"Engine created. [value-mapper: $valueMapper, function-provider: $functionProvider]")

  val interpreter = new FeelInterpreter

  def evalExpression(expression: String,
                     context: Map[String, Any] = Map()): EvalResult = {
    eval(FeelParser.parseExpression, expression, rootContext(context))
  }

  def evalExpression(expression: String, context: Context): EvalResult = {
    eval(FeelParser.parseExpression, expression, context)
  }

  def evalUnaryTests(expression: String,
                     context: Map[String, Any] = Map()): EvalResult = {
    eval(FeelParser.parseUnaryTests, expression, rootContext(context))
  }

  def evalUnaryTests(expression: String, context: Context): EvalResult = {
    eval(FeelParser.parseUnaryTests, expression, context)
  }

  def eval(exp: ParsedExpression,
           context: Map[String, Any] = Map()): EvalResult = {
    eval(exp, rootContext(context))
  }

  private def eval(parser: String => ParseResult[Exp],
                   expression: String,
                   context: Context): EvalResult = parser(expression) match {
    case Success(exp, _) => eval(ParsedExpression(exp, expression), context)
    case e: NoSuccess =>
      ParseFailure(s"failed to parse expression '$expression':\n$e")
  }

  def eval(exp: ParsedExpression, context: Context): EvalResult = {
    interpreter.eval(exp.expression)(rootContext(context)) match {
      case ValError(cause) =>
        EvalFailure(s"failed to evaluate expression '${exp.text}':\n$cause")
      case value => EvalValue(valueMapper.unpackVal(value))
    }
  }

  private def rootContext(variables: Map[String, Any]) =
    RootContext(variables = variables,
                functionProvider = functionProvider,
                valueMapper = valueMapper)

  private def rootContext(context: Context): Context = {
    context match {
      case c: RootContext => c
      case c: DefaultContext => {
        val fp =
          if (c.functionProvider == FunctionProvider.EmptyFunctionProvider) {
            functionProvider
          } else if (functionProvider == FunctionProvider.EmptyFunctionProvider) {
            c.functionProvider
          } else {
            new FunctionProvider.CompositeFunctionProvider(
              List(functionProvider, c.functionProvider))
          }

        RootContext(
          variables = c.variables,
          additionalFunctions = c.functions,
          variableProvider = c.variableProvider,
          functionProvider = fp,
          valueMapper = valueMapper
        )
      }
      case c =>
        RootContext(functionProvider = functionProvider,
                    valueMapper = valueMapper) + c
    }
  }

}
