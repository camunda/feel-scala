package org.camunda.feel.interpreter

import org.camunda.feel._
import org.camunda.feel.parser.FeelParser
import org.camunda.feel.parser.FeelParser._

trait FeelIntegrationTest {

  val interpreter: org.camunda.feel.interpreter.FeelInterpreter = new FeelInterpreter

  def eval(expression: String, variables: Map[String, Any] = Map(), functions: Map[(String, Int), ValFunction] = Map()): Val = {
    FeelParser.parseExpression(expression) match {
      case Success(exp, _) => {
        interpreter.eval(exp)(RootContext(variables, functions))
      }
      case e: NoSuccess => {
        ValError(s"failed to parse expression '$expression':\n$e")
      }
    }
  }

  def date(date: String): Date = date

  def time(time: String): Time = time

  def yearMonthDuration(duration: String): YearMonthDuration = duration

  def dayTimeDuration(duration: String): DayTimeDuration = duration

}
