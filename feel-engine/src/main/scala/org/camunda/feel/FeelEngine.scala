package org.camunda.feel

import org.camunda.feel.parser.FeelParser
import org.camunda.feel.parser.FeelParser._
import org.camunda.feel.interpreter.FeelInterpreter
import org.camunda.feel.interpreter.Context
import org.camunda.feel.interpreter._
import org.camunda.feel.parser.Exp
import org.camunda.feel.script.CompiledFeelScript
import org.camunda.feel.spi.DefaultFunctionProviders.EmptyFunctionProvider
import org.camunda.feel.spi.FunctionProvider

/**
 * @author Philipp Ossler
 */
class FeelEngine(functionProvider: FunctionProvider = EmptyFunctionProvider) {

  val interpreter = new FeelInterpreter

  def evalExpression(expression: String, context: Map[String, Any] = Map()): EvalResult = {
    eval(FeelParser.parseExpression, expression, context)
  }

  def evalSimpleUnaryTests(expression: String, context: Map[String, Any] = Map()): EvalResult = {
    eval(FeelParser.parseSimpleUnaryTests, expression, context)
  }
  
  def eval(exp: ParsedExpression, context: Map[String, Any] = Map()): EvalResult = evalParsedExpression(exp, context)
  
  private def eval(parser: String => ParseResult[Exp], expression: String, context: Map[String, Any]) = parser(expression) match {
    case Success(exp, _) => evalParsedExpression(ParsedExpression(exp, expression), context)
    case e: NoSuccess => ParseFailure(s"failed to parse expression '$expression':\n$e")
  }
  
  private def evalParsedExpression(exp: ParsedExpression, context: Map[String, Any]): EvalResult = interpreter.eval(exp.expression)(Context(context, functionProvider)) match {
    case ValError(cause) => EvalFailure(s"failed to evaluate expression '${exp.text}':\n$cause")
    case value => EvalValue( ValueMapper.unpackVal(value) )
  }
   
}