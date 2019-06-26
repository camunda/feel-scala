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
sealed trait Val {

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
                       invoke: List[Val] => Val,
                       hasVarArgs: Boolean = false)
    extends Val {

  val paramSet: Set[String] = params.toSet
}

case class ValContext(context: Context) extends Val

case class ValList(items: List[Val]) extends Val
