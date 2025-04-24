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
import org.camunda.feel.{
  Date,
  DateTime,
  DayTimeDuration,
  LocalDateTime,
  LocalTime,
  Number,
  Time,
  YearMonthDuration,
  dateTimeFormatter,
  dateTimeFormatterWithOffset,
  dateTimeFormatterWithZoneId,
  localDateTimeFormatter,
  localTimeFormatter
}

import java.time.Duration
import java.util.regex.Pattern

/** FEEL supports the following datatypes: number string boolean days and time duration years and
  * months duration time date and time Duration and date/time datatypes have no literal syntax. They
  * must be constructed from a string representation using a built-in function (10.3.4.1).
  *
  * @author
  *   Philipp Ossler
  */
sealed trait Val extends Ordered[Val] {

  protected val properties: Map[String, Val] = Map.empty

  /** Returns the value of a given property. The available properties depends on the value type.
    *
    * @param name
    *   the name of the property
    * @return
    *   the value of the property, or None if it doesn't exist
    */
  def property(name: String): Option[Val] = properties.get(name)

  /** Returns the names of the available properties.
    *
    * @return
    *   the available property names
    */
  def propertyNames(): Iterable[String] = properties.keys

  override def compare(that: Val): Int = (this, that) match {
    case (ValNumber(x), ValNumber(y))                       => x compare y
    case (ValString(x), ValString(y))                       => x compare y
    case (ValDate(x), ValDate(y))                           => x.compareTo(y)
    case (ValLocalTime(x), ValLocalTime(y))                 => x.compareTo(y)
    case (ValTime(x), ValTime(y))                           => x.compareTo(y)
    case (ValLocalDateTime(x), ValLocalDateTime(y))         => x.compareTo(y)
    case (ValDateTime(x), ValDateTime(y))                   => x.compareTo(y)
    case (ValYearMonthDuration(x), ValYearMonthDuration(y)) =>
      x.toTotalMonths compare y.toTotalMonths
    case (ValDayTimeDuration(x), ValDayTimeDuration(y))     => x.compareTo(y)
    case _                                                  =>
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
    case ValList(list)           =>
      list.headOption
        .map(head => head.isComparable && list.forall(_.getClass == head.getClass))
        .getOrElse(false)
    case _                       => false
  }

  def toEither: Either[ValError, Val] = this match {
    case e: ValError      => Left(e)
    case e: ValFatalError => Left(ValError(e.toString))
    case v                => Right(v)
  }

  def toOption: Option[Val] = this match {
    case _: ValError               => None
    case fatalError: ValFatalError => Some(fatalError)
    case v                         => Some(v)
  }

}

case class ValNumber(value: Number) extends Val {
  override def toString: String = value.toString()
}

case class ValBoolean(value: Boolean) extends Val {
  override def toString: String = value.toString
}

case class ValString(value: String) extends Val {
  override def toString: String = s"\"$value\""
}

case class ValDate(value: Date) extends Val {
  override protected val properties: Map[String, Val] = Map(
    "year"    -> ValNumber(value.getYear),
    "month"   -> ValNumber(value.getMonthValue),
    "day"     -> ValNumber(value.getDayOfMonth),
    "weekday" -> ValNumber(value.getDayOfWeek.getValue)
  )

  override def toString: String = value.toString
}

case class ValLocalTime(value: LocalTime) extends Val {
  override protected val properties: Map[String, Val] = Map(
    "hour"        -> ValNumber(value.getHour),
    "minute"      -> ValNumber(value.getMinute),
    "second"      -> ValNumber(value.getSecond),
    "time offset" -> ValNull,
    "timezone"    -> ValNull
  )

  override def toString: String = value.format(localTimeFormatter)
}

case class ValTime(value: Time) extends Val {
  override protected val properties: Map[String, Val] = Map(
    "hour"        -> ValNumber(value.getHour),
    "minute"      -> ValNumber(value.getMinute),
    "second"      -> ValNumber(value.getSecond),
    "time offset" ->
      ValDayTimeDuration(Duration.ofSeconds(value.getOffsetInTotalSeconds)),
    "timezone"    -> value.getZoneId.map(ValString).getOrElse(ValNull)
  )

  override def toString: String = value.format
}

case class ValLocalDateTime(value: LocalDateTime) extends Val {
  override val properties: Map[String, Val] = Map(
    "year"        -> ValNumber(value.getYear),
    "month"       -> ValNumber(value.getMonthValue),
    "day"         -> ValNumber(value.getDayOfMonth),
    "weekday"     -> ValNumber(value.getDayOfWeek.getValue),
    "hour"        -> ValNumber(value.getHour),
    "minute"      -> ValNumber(value.getMinute),
    "second"      -> ValNumber(value.getSecond),
    "time offset" -> ValNull,
    "timezone"    -> ValNull
  )

  override def toString: String = value.format(localDateTimeFormatter)
}

case class ValDateTime(value: DateTime) extends Val {
  override val properties: Map[String, Val] = Map(
    "year"        -> ValNumber(value.getYear),
    "month"       -> ValNumber(value.getMonthValue),
    "day"         -> ValNumber(value.getDayOfMonth),
    "weekday"     -> ValNumber(value.getDayOfWeek.getValue),
    "hour"        -> ValNumber(value.getHour),
    "minute"      -> ValNumber(value.getMinute),
    "second"      -> ValNumber(value.getSecond),
    "time offset" -> ValDayTimeDuration(Duration.ofSeconds(value.getOffset.getTotalSeconds)),
    "timezone"    -> {
      if (ValDateTime.hasTimeZone(value)) ValString(value.getZone.getId)
      else ValNull
    }
  )

  override def toString: String = ValDateTime.format(value)
}

object ValDateTime {

  def hasTimeZone(value: DateTime): Boolean = !value.getOffset.equals(value.getZone)

  def format(value: DateTime): String = {
    if (hasTimeZone(value)) {
      value.format(dateTimeFormatterWithZoneId)
    } else {
      value.format(dateTimeFormatterWithOffset)
    }
  }

}

case class ValYearMonthDuration(value: YearMonthDuration) extends Val {

  override def toString: String = ValYearMonthDuration.format(value)

  override val properties: Map[String, Val] = Map(
    "years"  -> ValNumber(value.getYears),
    "months" -> ValNumber(value.getMonths)
  )
}

object ValYearMonthDuration {

  def format(value: YearMonthDuration): String = {
    val year  = value.toTotalMonths / 12
    val month = value.toTotalMonths % 12

    if (year == 0 && month == 0)
      "P0Y"
    else if (year <= 0 && month <= 0)
      "-" + mkString(-year, -month)
    else
      mkString(year, month)
  }

  private def mkString(year: Long, month: Long): String = {
    val y = Option(year).filterNot(_ == 0).map(_ + "Y").getOrElse("")
    val m = Option(month).filterNot(_ == 0).map(_ + "M").getOrElse("")

    val stringValue = new StringBuilder("P")
    stringValue.append(y).append(m)
    stringValue.toString()
  }

}

case class ValDayTimeDuration(value: DayTimeDuration) extends Val {
  override def toString: String = ValDayTimeDuration.format(value)

  override val properties: Map[String, Val] = Map(
    "days"    -> ValNumber(value.toDays),
    "hours"   -> ValNumber(value.toHours % 24),
    "minutes" -> ValNumber(value.toMinutes % 60),
    "seconds" -> ValNumber(value.getSeconds % 60)
  )
}

object ValDayTimeDuration {

  def format(value: DayTimeDuration): String = {
    val day    = value.toDays
    val hour   = value.toHours    % 24
    val minute = value.toMinutes  % 60
    val second = value.getSeconds % 60

    if (day == 0 && hour == 0 && minute == 0 && second == 0)
      "P0D"
    else if (day <= 0 && hour <= 0 && minute <= 0 && second <= 0)
      "-" + mkString(-day, -hour, -minute, -second)
    else
      mkString(day, hour, minute, second)
  }

  private def mkString(day: Long, hour: Long, minute: Long, second: Long): String = {
    val d = Option(day).filterNot(_ == 0).map(_ + "D").getOrElse("")
    val h = Option(hour).filterNot(_ == 0).map(_ + "H").getOrElse("")
    val m = Option(minute).filterNot(_ == 0).map(_ + "M").getOrElse("")
    val s = Option(second).filterNot(_ == 0).map(_ + "S").getOrElse("")

    val stringValue = new StringBuilder("P")
    stringValue.append(d)
    if (h.nonEmpty || m.nonEmpty || s.nonEmpty) {
      stringValue.append("T")
      stringValue.append(h).append(m).append(s)
    }
    stringValue.toString()
  }

}

case class ValError(error: String) extends Val {
  override def toString: String = s"error(\"$error\")"
}

case class ValFatalError(error: String) extends Val {
  override def toString: String = s"fatal error(\"$error\")"
}

case object ValNull extends Val {
  override def toString: String = "null"
}

case class ValFunction(params: List[String], invoke: List[Val] => Any, hasVarArgs: Boolean = false)
    extends Val {

  val paramSet: Set[String] = params.toSet

  override def toString: String = s"function(${params.mkString(", ")})"
}

case class ValContext(context: Context) extends Val {
  override def toString: String = context.variableProvider.getVariables
    .map { case (key, value) => s"$key:$value" }
    .mkString(start = "{", sep = ", ", end = "}")
}

case class ValList(itemsAsSeq: Seq[Val]) extends Val {

  override def toString: String = itemsAsSeq.mkString(start = "[", sep = ", ", end = "]")

  // / BACKWARD COMPATIBILITY ///
  // / Following methods are added only for backwards compatibility
  def this(items: List[Val]) = this(items: Seq[Val])
  @deprecated("1.19.4", "Use itemsAsSeq instead to avoid a copy")
  def items: List[Val] = itemsAsSeq.toList
  def copy(items: List[Val]): ValList = new ValList(items)
}

object ValList {
  def apply(items: List[Val]): ValList = new ValList(items)
  def apply(items: Seq[Val]): ValList  = new ValList(items)
}

case class ValRange(start: RangeBoundary, end: RangeBoundary) extends Val {
  override def toString: String = {
    val startSymbol = if (start.isClosed) "[" else "("
    val endSymbol   = if (end.isClosed) "]" else ")"

    s"$startSymbol${start.value}..${end.value}$endSymbol"
  }
}
