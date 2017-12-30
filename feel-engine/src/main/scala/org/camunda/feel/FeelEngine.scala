package org.camunda.feel

import org.camunda.feel.parser.FeelParser
import org.camunda.feel.parser.FeelParser._
import org.camunda.feel.interpreter.FeelInterpreter
import org.camunda.feel.interpreter.DefaultValueMapper
import org.camunda.feel.interpreter._
import org.camunda.feel.parser.Exp
import org.camunda.feel.spi._

/**
 * @author Philipp Ossler
 */
class FeelEngine(val functionProvider: FunctionProvider = FunctionProvider.EmptyFunctionProvider, val valueMapper: ValueMapper = DefaultValueMapper.instance) {

  val interpreter: org.camunda.feel.interpreter.FeelInterpreter = new FeelInterpreter

  def evalExpression(expression: String, context: Map[String, Any] = Map()): EvalResult = {
    val ctx = RootContext(context, functionProvider = functionProvider, valueMapper = valueMapper)
    eval(FeelParser.parseExpression, expression, ctx)
  }

  def evalExpression(expression: String, context: Context): EvalResult = {
    eval(FeelParser.parseExpression, expression, context)
  }

  def evalUnaryTests(expression: String, context: Map[String, Any] = Map()): EvalResult = {
    val ctx = RootContext(context, functionProvider = functionProvider, valueMapper = valueMapper)
    eval(FeelParser.parseUnaryTests, expression, ctx)
  }

  def evalUnaryTests(expression: String, context: Context): EvalResult = {
    eval(FeelParser.parseUnaryTests, expression, context)
  }

  def eval(exp: ParsedExpression, context: Map[String, Any] = Map()): EvalResult = {
    val ctx = RootContext(context, functionProvider = functionProvider, valueMapper = valueMapper)
    eval(exp, ctx)
  }

  private def eval(parser: String => ParseResult[Exp], expression: String, context: Context): EvalResult = parser(expression) match {
    case Success(exp, _) => eval(ParsedExpression(exp, expression), context)
    case e: NoSuccess => ParseFailure(s"failed to parse expression '$expression':\n$e")
  }

  def eval(exp: ParsedExpression, context: Context): EvalResult = interpreter.eval(exp.expression)(context) match {
    case ValError(cause) => EvalFailure(s"failed to evaluate expression '${exp.text}':\n$cause")
    case value => EvalValue( valueMapper.unpackVal(value) )
  }

}
