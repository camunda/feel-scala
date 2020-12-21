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
package org.camunda.feel.impl

import fastparse.Parsed
import org.camunda.feel.FeelEngine.UnaryTests
import org.camunda.feel.context.Context
import org.camunda.feel.impl.interpreter.{
  BuiltinFunctions,
  EvalContext,
  FeelInterpreter
}
import org.camunda.feel.impl.parser.FeelParser
import org.camunda.feel.syntaxtree.{Val, ValError, ValFunction}
import org.camunda.feel.valuemapper.ValueMapper
import org.camunda.feel.{
  Date,
  DateTime,
  DayTimeDuration,
  LocalDateTime,
  LocalTime,
  Time,
  YearMonthDuration,
  _
}

trait FeelIntegrationTest {

  val interpreter: FeelInterpreter =
    new FeelInterpreter

  private val clock: TimeTravelClock = new TimeTravelClock

  def eval(
      expression: String,
      variables: Map[String, Any] = Map(),
      functions: Map[String, ValFunction] = Map()
  ): Val = {

    val context =
      Context.StaticContext(variables = variables, functions = functions.map {
        case (n, f) => n -> List(f)
      }.toMap)

    eval(expression, context)
  }

  def eval(expression: String, context: Context): Val = {

    FeelParser.parseExpression(expression) match {
      case Parsed.Success(exp, _) =>
        interpreter.eval(exp)(rootContext + context)
      case e: Parsed.Failure => {
        ValError(s"failed to parse expression '$expression':\n$e")
      }
    }
  }

  def evalUnaryTests(input: Any,
                     expression: String,
                     variables: Map[String, Any] = Map()): Val = {

    val ctx = rootContext ++ variables + (UnaryTests.defaultInputVariable -> input)

    FeelParser.parseUnaryTests(expression) match {
      case Parsed.Success(exp, _) => interpreter.eval(exp)(ctx)
      case e: Parsed.Failure => {
        ValError(s"failed to parse expression '$expression':\n$e")
      }
    }
  }

  val rootContext: EvalContext = EvalContext.wrap(
    Context.StaticContext(variables = Map.empty,
                          functions = new BuiltinFunctions(clock).functions)
  )(ValueMapper.defaultValueMapper)

  def withClock(testCode: TimeTravelClock => Any): Unit = {
    try {
      testCode(clock)
    } finally clock.reset()
  }

  def date(date: String): Date = date

  def time(time: String): Time = time

  def dateTime(dt: String): DateTime = dt

  def yearMonthDuration(duration: String): YearMonthDuration = duration

  def dayTimeDuration(duration: String): DayTimeDuration = duration

  def localTime(time: String): LocalTime = time

  def localDateTime(dateTime: String): LocalDateTime = dateTime

}
