package org.camunda.feel.interpreter.impl

import org.camunda.feel.FeelEngine.UnaryTests
import org.camunda.feel.impl._
import org.camunda.feel.impl.parser.FeelParser
import org.camunda.feel.impl.parser.FeelParser._

trait FeelIntegrationTest {

  val interpreter: FeelInterpreter =
    new FeelInterpreter

  def eval(expression: String,
           variables: Map[String, Any] = Map(),
           functions: Map[String, ValFunction] = Map()): Val = {

    val context = Context.StaticContext(
      variables = variables,
      functions = functions.map { case (n, f) => n -> List(f) }.toMap
    )

    eval(expression, context)
  }

  def eval(expression: String, context: Context): Val = {

    FeelParser.parseExpression(expression) match {
      case Success(exp, _) =>
        interpreter.eval(exp)(rootContext + context)
      case e: NoSuccess => {
        ValError(s"failed to parse expression '$expression':\n$e")
      }
    }
  }

  def evalUnaryTests(input: Any,
                     expression: String,
                     variables: Map[String, Any] = Map()): Val = {

    val ctx = rootContext ++ variables + (UnaryTests.defaultInputVariable -> input)

    FeelParser.parseUnaryTests(expression) match {
      case Success(exp, _) => interpreter.eval(exp)(ctx)
      case e: NoSuccess => {
        ValError(s"failed to parse expression '$expression':\n$e")
      }
    }
  }

  val rootContext: EvalContext = EvalContext.wrap(
    Context.StaticContext(variables = Map.empty,
                          functions = BuiltinFunctions.functions))(
    ValueMapper.defaultValueMapper)

  def date(date: String): Date = date

  def time(time: String): Time = time

  def dateTime(dt: String): DateTime = dt

  def yearMonthDuration(duration: String): YearMonthDuration = duration

  def dayTimeDuration(duration: String): DayTimeDuration = duration

  def localTime(time: String): LocalTime = time

  def localDateTime(dateTime: String): LocalDateTime = dateTime

}
