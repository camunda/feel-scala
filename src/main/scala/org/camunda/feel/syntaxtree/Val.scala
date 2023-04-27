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

import org.camunda.feel.context.Context
import org.camunda.feel.{Date, DateTime, DayTimeDuration, LocalDateTime, LocalTime, Number, Time, YearMonthDuration}

import java.math.BigInteger
import java.time.Duration

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

  protected val properties: Map[String, Val] = Map.empty

  /**
    * Returns the value of a given property. The available properties depends on the value type.
    *
    * @param name the name of the property
    * @return the value of the property, or None if it doesn't exist
    */
  def property(name: String): Option[Val] = properties.get(name)

  /**
    * Returns the names of the available properties.
    *
    * @return the available property names
    */
  def propertyNames(): Iterable[String] = properties.keys

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

case class ValDate(value: Date) extends Val {
  override protected val properties: Map[String, Val] = Map(
    "year" -> ValNumber(value.getYear),
    "month" -> ValNumber(value.getMonthValue),
    "day" -> ValNumber(value.getDayOfMonth),
    "weekday" -> ValNumber(value.getDayOfWeek.getValue)
  )
}

case class ValLocalTime(value: LocalTime) extends Val {
  override protected val properties: Map[String, Val] = Map(
    "hour" -> ValNumber(value.getHour),
    "minute" -> ValNumber(value.getMinute),
    "second" -> ValNumber(value.getSecond),
    "time offset" -> ValNull,
    "timezone" -> ValNull
  )
}

case class ValTime(value: Time) extends Val {
  override protected val properties: Map[String, Val] = Map(
    "hour" -> ValNumber(value.getHour),
    "minute" -> ValNumber(value.getMinute),
    "second" -> ValNumber(value.getSecond),
    "time offset" ->
      ValDayTimeDuration(Duration.ofSeconds(value.getOffsetInTotalSeconds)),
    "timezone" -> value.getZoneId.map(ValString).getOrElse(ValNull)
  )
}

case class ValLocalDateTime(value: LocalDateTime) extends Val {
  override val properties: Map[String, Val] = Map(
    "year" -> ValNumber(value.getYear),
    "month" -> ValNumber(value.getMonthValue),
    "day" -> ValNumber(value.getDayOfMonth),
    "weekday" -> ValNumber(value.getDayOfWeek.getValue),
    "hour" -> ValNumber(value.getHour),
    "minute" -> ValNumber(value.getMinute),
    "second" -> ValNumber(value.getSecond),
    "time offset" -> ValNull,
    "timezone" -> ValNull
  )
}

case class ValDateTime(value: DateTime) extends Val {
  override val properties: Map[String, Val] = Map(
    "year" -> ValNumber(value.getYear),
    "month" -> ValNumber(value.getMonthValue),
    "day" -> ValNumber(value.getDayOfMonth),
    "weekday" -> ValNumber(value.getDayOfWeek.getValue),
    "hour" -> ValNumber(value.getHour),
    "minute" -> ValNumber(value.getMinute),
    "second" -> ValNumber(value.getSecond),
    "time offset" -> ValDayTimeDuration(
      Duration.ofSeconds(value.getOffset.getTotalSeconds)),
    "timezone" -> {
      if (hasTimeZone) ValString(value.getZone.getId)
      else ValNull
    }
  )

  private def hasTimeZone = !value.getOffset.equals(value.getZone)
}

case class ValYearMonthDuration(value: YearMonthDuration) extends Val {
  override val properties: Map[String, Val] = Map(
    "years" -> ValNumber(value.getYears),
    "months" -> ValNumber(value.getMonths)
  )
}

case class ValDayTimeDuration(value: DayTimeDuration) extends Val {
  override def toString: String = {
    def appendIfNotZero(l: Long, append: String): String =
      if (l == 0) ""
      else l + append

    val day = value.toDays
    val hour = value.toHours % 24
    val minute = value.toMinutes % 60
    val second = value.getSeconds % 60

    val signedNonZero = {
      val components = List(day, hour, minute, second)
      val t = if (components.tail.exists(_ != 0)) "T" else ""

      if (components.forall(_ == 0)) None
      else if (components.forall(_ <= 0)) Option("-P", -day, t, -hour, -minute, -second)
      else Option("P", day, t, hour, minute, second)
    }

    signedNonZero
      .map(a => (
        a._1,
        appendIfNotZero(a._2, "D"),
        a._3,
        appendIfNotZero(a._4, "H"),
        appendIfNotZero(a._5, "M"),
        appendIfNotZero(a._6, "S")
      ).productIterator.mkString)
      .getOrElse("PT0S")
  }
  override val properties: Map[String, Val] = Map(
    "days" -> ValNumber(value.toDays),
    "hours" -> ValNumber(value.toHours % 24),
    "minutes" -> ValNumber(value.toMinutes % 60),
    "seconds" -> ValNumber(value.getSeconds % 60)
  )
}

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

case class ValRange(start: RangeBoundary, end: RangeBoundary) extends Val
