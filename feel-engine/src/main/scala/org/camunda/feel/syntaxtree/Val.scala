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

import org.camunda.feel._
import org.camunda.feel.context.Context

/**
  * FEEL supports the following datatypes:
  * number
  * string
  * boolean
  * days and time duration
  * years and months duration
  * time
  * date and time
Duration and date/time datatypes have no literal syntax. They must be constructed from a string representation using a
built-in function (10.3.4.1).
  *
  * @author Philipp Ossler
  */
sealed trait Val extends Ordered[Val] {

  override def compare(that: Val): Int = (this, that) match {
    case (ValNumber(x), ValNumber(y))               => x compare y
    case (ValString(x), ValString(y))               => x compare y
    case (ValDate(x), ValDate(y))                   => x.compareTo(y)
    case (ValLocalTime(x), ValLocalTime(y))         => x.compareTo(y)
    case (ValTime(x), ValTime(y))                   => x.compareTo(y)
    case (ValLocalDateTime(x), ValLocalDateTime(y)) => x.compareTo(y)
    case (ValDateTime(x), ValDateTime(y))           => x.compareTo(y)
    case (ValYearMonthDuration(x), ValYearMonthDuration(y)) =>
      x.toTotalMonths compare y.toTotalMonths
    case (ValDayTimeDuration(x), ValDayTimeDuration(y)) => x.compareTo(y)
    case _ =>
      throw new IllegalArgumentException(s"$this can not be compared to $that")
  }

  def isComparable: Boolean = this match {
    case _: ValNumber            => true
    case _: ValString            => true
    case _: ValDate              => true
    case _: ValLocalTime         => true
    case _: ValTime              => true
    case _: ValLocalDateTime     => true
    case _: ValDateTime          => true
    case _: ValYearMonthDuration => true
    case _: ValDayTimeDuration   => true
    case ValList(list) =>
      list.headOption
        .map(head =>
          head.isComparable && list.forall(_.getClass == head.getClass))
        .getOrElse(false)
    case _ => false
  }

  def toEither: Either[ValError, Val] = this match {
    case e: ValError => Left(e)
    case v           => Right(v)
  }

  def toOption: Option[Val] = this match {
    case e: ValError => None
    case v           => Some(v)
  }

}

case class ValNumber(value: Number) extends Val

case class ValBoolean(value: Boolean) extends Val

case class ValString(value: String) extends Val

case class ValDate(value: Date) extends Val

case class ValLocalTime(value: LocalTime) extends Val

case class ValTime(value: Time) extends Val

case class ValLocalDateTime(value: LocalDateTime) extends Val

case class ValDateTime(value: DateTime) extends Val

case class ValYearMonthDuration(value: YearMonthDuration) extends Val

case class ValDayTimeDuration(value: DayTimeDuration) extends Val

case class ValError(error: String) extends Val

case object ValNull extends Val

case class ValFunction(params: List[String],
                       invoke: List[Val] => Any,
                       hasVarArgs: Boolean = false)
    extends Val {

  val paramSet: Set[String] = params.toSet
}

case class ValContext(context: Context) extends Val

case class ValList(items: List[Val]) extends Val
