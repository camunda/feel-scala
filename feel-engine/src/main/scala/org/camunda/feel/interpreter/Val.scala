package org.camunda.feel.interpreter

import org.camunda.feel._

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
sealed trait Val

case class ValNumber(value: Number) extends Val

case class ValBoolean(value: Boolean) extends Val

case class ValString(value: String) extends Val

case class ValDate(value: Date) extends Val

case class ValTime(value: Time) extends Val

case class ValDateTime(value: DateTime) extends Val

case class ValYearMonthDuration(value: YearMonthDuration) extends Val

case class ValDayTimeDuration(value: DayTimeDuration) extends Val

case class ValError(error: String) extends Val

case object ValNull extends Val

case class ValFunction(params: List[String], invoke: List[Val] => Val, requireInputVariable: Boolean = false) extends Val

case class ValContext(entries: List[(String, Val)]) extends Val

case class ValVariableContext(entry: (String) => Option[Val]) extends Val

case class ValList(items: List[Val]) extends Val