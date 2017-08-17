package org.camunda.feel.interpreter

import scala.collection.immutable.StringOps
import org.camunda.feel._

/**
 * @author Philipp Ossler
 */
trait Compareable[T] {

  def value: T

  def <(x: Compareable[_]): Boolean = op(x, <)

  def <=(x: Compareable[_]): Boolean = op(x, <=)

  def >(x: Compareable[_]): Boolean = op(x, >)

  def >=(x: Compareable[_]): Boolean = op(x, >=)

  def <(x: T): Boolean

  def <=(x: T): Boolean

  def >(x: T): Boolean

  def >=(x: T): Boolean

  // type hacking to interpreter operators in a simple way
  // the interpreter should check the types
  private def op(x: Compareable[_], f: T => Boolean): Boolean = x match {
    case i: Compareable[T] => f(i.value)
    case i => throw new IllegalArgumentException(s"expect '${value.getClass}' but found '${x.getClass}'")
  }
}

object Compareable {

  implicit class CompareableNumber(val value: Number) extends Compareable[Number] {

	  def <(x: Number) = value < x

	  def <=(x: Number) = value <= x

	  def >(x: Number) = value > x

	  def >=(x: Number) = value >= x

	}

	implicit class CompareableDate(val value: Date) extends Compareable[Date] {

	  def <(x: Date) = value isBefore x

	  def <=(x: Date) = value == x || (value isBefore x)

	  def >(x: Date) = value isAfter x

	  def >=(x: Date) = value == x || (value isAfter x)

	}

	implicit class CompareableTime(val value: Time) extends Compareable[Time] {

	  def <(x: Time) = value isBefore x

	  def <=(x: Time) = value == x || (value isBefore x)

	  def >(x: Time) = value isAfter x

	  def >=(x: Time) = value == x || (value isAfter x)
	}

	implicit class CompareableDateTime(val value: DateTime) extends Compareable[DateTime] {

	  def <(x: DateTime) = value isBefore x

	  def <=(x: DateTime) = value == x || (value isBefore x)

	  def >(x: DateTime) = value isAfter x

	  def >=(x: DateTime) = value == x || (value isAfter x)
	}

  implicit class CompareableYearMonthDuration(val value: YearMonthDuration) extends Compareable[YearMonthDuration] {

	  def <(x: YearMonthDuration) = value.minus(x).isNegative

	  def <=(x: YearMonthDuration) = value.minus(x).isNegative || value.minus(x).isZero

	  def >(x: YearMonthDuration) = !value.minus(x).isNegative && !value.minus(x).isZero

	  def >=(x: YearMonthDuration) = !value.minus(x).isNegative

	}

  implicit class CompareableDayTimeDuration(val value: DayTimeDuration) extends Compareable[DayTimeDuration] {

	  def <(x: DayTimeDuration) = value.minus(x).isNegative

	  def <=(x: DayTimeDuration) = value.minus(x).isNegative || value.minus(x).isZero

	  def >(x: DayTimeDuration) = !value.minus(x).isNegative && !value.minus(x).isZero

	  def >=(x: DayTimeDuration) = !value.minus(x).isNegative

	}

  // currently, not part of the spec
  implicit class CompareableString(val value: String) extends Compareable[String] {

    def <(x: String) = (value: StringOps) < x

    def <=(x: String) = (value: StringOps) <= x

    def >(x: String) = (value: StringOps) > x

    def >=(x: String) = (value: StringOps) >= x

  }

}
