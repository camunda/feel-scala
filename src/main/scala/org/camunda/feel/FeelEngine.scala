package org.camunda.feel

import org.camunda.feel.parser.FeelParser
import org.camunda.feel.parser.FeelParser._
import org.camunda.feel.interpreter.FeelInterpreter
import org.camunda.feel.interpreter.Context
import org.camunda.feel.interpreter._
import org.camunda.feel.parser.Exp

/**
 * @author Philipp Ossler
 */
class FeelEngine {

  val interpreter = new FeelInterpreter

  def evalExpression(expression: String, context: Map[String, Any] = Map()): EvalResult = {
    eval(FeelParser.parseExpression, expression, context)
  }

  def evalSimpleUnaryTest(expression: String, context: Map[String, Any] = Map()): EvalResult = {
    eval(FeelParser.parseSimpleUnaryTest, expression, context)
  }

  private def eval(parser: String => ParseResult[Exp], expression: String, context: Map[String, Any]) =
    parser(expression) match {
      case Success(exp, _) => {
        interpreter.eval(exp)(Context(context)) match {
          case ValError(cause) => EvalFailure(s"failed to evaluate expression '$expression':\n$cause")
          case value => EvalValue( ValueMapper.unpackVal(value) )
        }
      }
      case e: NoSuccess => {
        ParseFailure(s"failed to parse expression '$expression':\n$e")
      }
    }

}