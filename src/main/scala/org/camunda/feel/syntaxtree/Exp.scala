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
package org.camunda.feel.syntaxtree

import org.camunda.feel.{
  Date,
  DateTime,
  DayTimeDuration,
  LocalDateTime,
  LocalTime,
  Number,
  Time,
  YearMonthDuration
}

/**
  * @author Philipp Ossler
  */
sealed trait Exp

sealed trait Comparison extends Exp {
  val x: Exp
  val y: Exp
}

case object ConstNull extends Exp

case object ConstInputValue extends Exp

case class ConstNumber(value: Number) extends Exp

case class ConstBool(value: Boolean) extends Exp

case class ConstString(value: String) extends Exp

case class ConstDate(value: Date) extends Exp

case class ConstLocalTime(value: LocalTime) extends Exp

case class ConstTime(value: Time) extends Exp

case class ConstLocalDateTime(value: LocalDateTime) extends Exp

case class ConstDateTime(value: DateTime) extends Exp

case class ConstYearMonthDuration(value: YearMonthDuration) extends Exp

case class ConstDayTimeDuration(value: DayTimeDuration) extends Exp

case class ConstList(items: List[Exp]) extends Exp

case class ConstContext(entries: List[(String, Exp)]) extends Exp

case class ConstRange(start: RangeBoundary, end: RangeBoundary) extends Exp

case class InputLessThan(x: Exp) extends Exp

case class InputLessOrEqual(x: Exp) extends Exp

case class InputGreaterThan(x: Exp) extends Exp

case class InputGreaterOrEqual(x: Exp) extends Exp

case class InputEqualTo(x: Exp) extends Exp

case class Interval(start: IntervalBoundary, end: IntervalBoundary) extends Exp

case class AtLeastOne(xs: List[Exp]) extends Exp

case class Not(x: Exp) extends Exp

case class Ref(names: List[String]) extends Exp

object Ref {
  def apply(name: String) = new Ref(List(name))
}

case class PathExpression(path: Exp, key: String) extends Exp

case class Addition(x: Exp, y: Exp) extends Exp

case class Subtraction(x: Exp, y: Exp) extends Exp

case class Multiplication(x: Exp, y: Exp) extends Exp

case class Division(x: Exp, y: Exp) extends Exp

case class Exponentiation(x: Exp, y: Exp) extends Exp

case class ArithmeticNegation(x: Exp) extends Exp

case class Equal(x: Exp, y: Exp) extends Comparison

case class LessThan(x: Exp, y: Exp) extends Comparison

case class LessOrEqual(x: Exp, y: Exp) extends Comparison

case class GreaterThan(x: Exp, y: Exp) extends Comparison

case class GreaterOrEqual(x: Exp, y: Exp) extends Comparison

case class FunctionInvocation(function: String, params: FunctionParameters)
    extends Exp

case class JavaFunctionInvocation(className: String,
                                  methodName: String,
                                  arguments: List[String])
    extends Exp

case class QualifiedFunctionInvocation(path: Exp,
                                       function: String,
                                       params: FunctionParameters)
    extends Exp

case class FunctionDefinition(parameters: List[String], body: Exp) extends Exp

case class If(condition: Exp, statement: Exp, elseStatement: Exp) extends Exp

case class Disjunction(x: Exp, y: Exp) extends Exp

case class Conjunction(x: Exp, y: Exp) extends Exp

case class In(x: Exp, test: Exp) extends Exp

case class InstanceOf(x: Exp, typeName: String) extends Exp

case class SomeItem(iterators: List[(String, Exp)], condition: Exp) extends Exp

case class EveryItem(iterators: List[(String, Exp)], condition: Exp) extends Exp

case class For(iterators: List[(String, Exp)], exp: Exp) extends Exp

case class Filter(list: Exp, filter: Exp) extends Exp

case class IterationContext(start: Exp, end: Exp) extends Exp

case class UnaryTestExpression(exp: Exp) extends Exp
