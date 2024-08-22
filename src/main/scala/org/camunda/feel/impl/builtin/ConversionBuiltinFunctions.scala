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
package org.camunda.feel.impl.builtin

import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree._
import org.camunda.feel.valuemapper.ValueMapper
import org.camunda.feel.{
  Date,
  YearMonthDuration,
  isDayTimeDuration,
  isLocalDateTime,
  isOffsetDateTime,
  isOffsetTime,
  isValidDate,
  isYearMonthDuration,
  stringToDate,
  stringToDateTime,
  stringToDayTimeDuration,
  stringToLocalDateTime,
  stringToLocalTime,
  stringToNumber,
  stringToYearMonthDuration
}

import java.math.BigDecimal
import java.time._
import scala.util.Try

class ConversionBuiltinFunctions(valueMapper: ValueMapper) {

  def functions = Map(
    "date"                      -> List(dateFunction, dateFunction3),
    "date and time"             -> List(dateTime, dateTime2),
    "time"                      -> List(timeFunction, timeFunction3, timeFunction4),
    "number"                    -> List(numberFunction, numberFunction2, numberFunction3),
    "string"                    -> List(stringFunction),
    "duration"                  -> List(durationFunction),
    "years and months duration" -> List(durationFunction2)
  )

  private def dateFunction = builtinFunction(
    params = List("from"),
    invoke = {
      case List(ValString(from))        => parseDate(from)
      case List(ValLocalDateTime(from)) => ValDate(from.toLocalDate())
      case List(ValDateTime(from))      => ValDate(from.toLocalDate())
      case List(ValDate(from))          => ValDate(from)
    }
  )

  private def dateFunction3 = builtinFunction(
    params = List("year", "month", "day"),
    invoke = { case List(ValNumber(year), ValNumber(month), ValNumber(day)) =>
      Try {
        ValDate(LocalDate.of(year.intValue, month.intValue, day.intValue))
      }.getOrElse {
        ValError(s"Failed to parse date from: year=$year, month=$month, day=$day")
      }
    }
  )

  private def dateTime =
    builtinFunction(
      params = List("from"),
      invoke = { case List(ValString(from)) =>
        parseDateTime(from)
      }
    )

  private def dateTime2 = builtinFunction(
    params = List("date", "time"),
    invoke = {
      case List(ValDate(date), ValLocalTime(time))              =>
        ValLocalDateTime(date.atTime(time))
      case List(ValDate(date), ValTime(time))                   =>
        ValDateTime(time.withDate(date))
      case List(ValLocalDateTime(dateTime), ValLocalTime(time)) =>
        ValLocalDateTime(dateTime.toLocalDate().atTime(time))
      case List(ValLocalDateTime(dateTime), ValTime(time))      =>
        ValDateTime(time.withDate(dateTime.toLocalDate()))
      case List(ValDateTime(dateTime), ValLocalTime(time))      =>
        ValLocalDateTime(dateTime.toLocalDate().atTime(time))
      case List(ValDateTime(dateTime), ValTime(time))           =>
        ValDateTime(time.withDate(dateTime.toLocalDate()))
      case List(ValLocalDateTime(date), ValString(timezone))    =>
        ValDateTime(date.atZone(ZoneId.of(timezone)))
      case List(ValDateTime(date), ValString(timezone))         =>
        ValDateTime(date.withZoneSameInstant(ZoneId.of(timezone)))
    }
  )

  private def timeFunction = builtinFunction(
    params = List("from"),
    invoke = {
      case List(ValString(from))        => parseTime(from)
      case List(ValLocalDateTime(from)) => ValLocalTime(from.toLocalTime())
      case List(ValDateTime(from))      => ValTime(ZonedTime.of(from))
      case List(ValDate(from))          =>
        ValTime(ZonedTime.of(LocalTime.MIDNIGHT, ZoneOffset.UTC))
    }
  )

  private def timeFunction3 = builtinFunction(
    params = List("hour", "minute", "second"),
    invoke = { case List(ValNumber(hour), ValNumber(minute), ValNumber(second)) =>
      Try {
        val nanos = second.bigDecimal
          .remainder(BigDecimal.ONE)
          .movePointRight(9)
          .intValue
        ValLocalTime(
          LocalTime
            .of(hour.intValue, minute.intValue, second.intValue, nanos)
        )
      }.getOrElse {
        ValError(s"Failed to parse local-time from: hour=$hour, minute=$minute, second=$second")
      }
    }
  )

  private def timeFunction4 = builtinFunction(
    params = List("hour", "minute", "second", "offset"),
    invoke = {
      case List(
            ValNumber(hour),
            ValNumber(minute),
            ValNumber(second),
            ValDayTimeDuration(offset)
          ) =>
        Try {
          val nanos       = second.bigDecimal
            .remainder(BigDecimal.ONE)
            .movePointRight(9)
            .intValue
          val localTime   =
            LocalTime.of(hour.intValue, minute.intValue, second.intValue, nanos)
          val zonedOffset = ZoneOffset.ofTotalSeconds(offset.getSeconds.toInt)

          ValTime(ZonedTime.of(localTime, zonedOffset))
        }.getOrElse {
          ValError(
            s"Failed to parse time from: hour=$hour, minute=$minute, second=$second, offset=$offset"
          )
        }
      case List(ValNumber(hour), ValNumber(minute), ValNumber(second), ValNull) =>
        Try {
          ValLocalTime(LocalTime.of(hour.intValue, minute.intValue, second.intValue))
        }.getOrElse {
          ValError(s"Failed to parse local-time from: hour=$hour, minute=$minute, second=$second")
        }
    }
  )

  private def numberFunction =
    builtinFunction(
      params = List("from"),
      invoke = { case List(ValString(from)) =>
        parseNumber(from)
      }
    )

  private def parseNumber(from: String): Val = {
    Try(
      ValNumber(from)
    ).getOrElse(
      ValError(s"Can't parse '$from' as a number")
    )
  }

  private def numberFunction2 = builtinFunction(
    params = List("from", "grouping separator"),
    invoke = {
      case List(ValString(from), ValString(grouping)) if (isValidGroupingSeparator(grouping)) =>
        parseNumber(from.replace(grouping, ""))

      case List(ValString(_), ValString(_)) =>
        ValError(s"illegal argument for grouping. Must be one of ' ', ',' or '.'")
    }
  )

  private def numberFunction3 = builtinFunction(
    params = List("from", "grouping separator", "decimal separator"),
    invoke = {
      case List(ValString(from), ValString(grouping), ValString(decimal))
          if (isValidGroupingSeparator(grouping) && isValidDecimalSeparator(
            decimal
          ) && grouping != decimal) =>
        parseNumber(from.replace(grouping, "").replace(decimal, "."))

      case List(ValString(from), ValNull, ValString(decimal)) if isValidDecimalSeparator(decimal) =>
        parseNumber(from.replace(decimal, "."))

      case List(ValString(from), ValString(grouping), ValNull)
          if isValidGroupingSeparator(grouping) =>
        parseNumber(from.replace(grouping, ""))

      case List(ValString(_), ValString(_), ValString(_)) =>
        ValError(
          s"illegal arguments for grouping or decimal. Must be one of ' ' (grouping only), ',' or '.'"
        )
    }
  )

  private def isValidGroupingSeparator(separator: String) =
    separator == " " || separator == "," || separator == "."

  private def isValidDecimalSeparator(separator: String) =
    separator == "," || separator == "."

  private def stringFunction = builtinFunction(
    params = List("from"),
    invoke = {
      case List(ValNull)         => ValNull
      case List(from: ValString) => from
      case List(from)            => ValString(toString(from))
    }
  )

  private def toString(from: Val): String = {
    from match {
      case ValContext(context) =>
        context.variableProvider.getVariables
          .map { case (key, value) =>
            val asVal    = valueMapper.toVal(value)
            val asString = toString(asVal)
            s"$key:$asString"
          }
          .mkString(start = "{", sep = ", ", end = "}")
      case ValList(items)      =>
        items
          .map(toString)
          .mkString(start = "[", sep = ", ", end = "]")
      case from                => from.toString
    }
  }

  private def durationFunction =
    builtinFunction(
      params = List("from"),
      invoke = { case List(ValString(from)) =>
        parseDuration(from)
      }
    )

  private def durationFunction2 = builtinFunction(
    params = List("from", "to"),
    invoke = {
      case List(ValDate(from), ValDate(to))                   =>
        ValYearMonthDuration(Period.between(from, to).withDays(0).normalized)
      case List(ValLocalDateTime(from), ValLocalDateTime(to)) =>
        ValYearMonthDuration(
          Period
            .between(from.toLocalDate, to.toLocalDate)
            .withDays(0)
            .normalized
        )
      case List(ValDateTime(from), ValDateTime(to))           =>
        ValYearMonthDuration(
          Period
            .between(from.toLocalDate, to.toLocalDate)
            .withDays(0)
            .normalized
        )
      case List(ValDateTime(from), ValLocalDateTime(to))      =>
        ValYearMonthDuration(
          Period
            .between(from.toLocalDate, to.toLocalDate)
            .withDays(0)
            .normalized
        )
      case List(ValLocalDateTime(from), ValDateTime(to))      =>
        ValYearMonthDuration(
          Period
            .between(from.toLocalDate, to.toLocalDate)
            .withDays(0)
            .normalized
        )
      case List(ValDate(from), ValDateTime(to))               =>
        ValYearMonthDuration(Period.between(from, to.toLocalDate()).withDays(0).normalized)
      case List(ValDate(from), ValLocalDateTime(to))          =>
        ValYearMonthDuration(Period.between(from, to.toLocalDate()).withDays(0).normalized)
      case List(ValDateTime(from), ValDate(to))               =>
        ValYearMonthDuration(Period.between(from.toLocalDate(), to).withDays(0).normalized)
      case List(ValLocalDateTime(from), ValDate(to))          =>
        ValYearMonthDuration(Period.between(from.toLocalDate(), to).withDays(0).normalized)
    }
  )

  private def parseDate(d: String): Val = {
    if (isValidDate(d)) {
      Try(ValDate(d)).getOrElse {
        ValError(s"Failed to parse date from '$d'")
      }
    } else {
      ValError(s"Failed to parse date from '$d'")
    }
  }

  private def parseTime(t: String): Val = {
    if (isOffsetTime(t)) {
      Try(ValTime(t)).getOrElse {
        ValError(s"Failed to parse time from '$t'")
      }
    } else {
      Try(ValLocalTime(t)).getOrElse {
        ValError(s"Failed to parse local-time from '$t'")
      }
    }
  }

  private def parseDateTime(dt: String): Val = {
    if (isValidDate(dt)) {
      Try(ValLocalDateTime((dt: Date).atTime(0, 0))).getOrElse {
        ValError(s"Failed to parse date(-time) from '$dt'")
      }
    } else if (isOffsetDateTime(dt)) {
      Try(ValDateTime(dt)).getOrElse {
        ValError(s"Failed to parse date-time from '$dt'")
      }
    } else if (isLocalDateTime(dt)) {
      Try(ValLocalDateTime(dt)).getOrElse {
        ValError(s"Failed to parse local-date-time from '$dt'")
      }
    } else {
      ValError(s"Failed to parse date-time from '$dt'")
    }
  }

  private def parseDuration(d: String): Val = {
    if (isYearMonthDuration(d)) {
      Try(ValYearMonthDuration((d: YearMonthDuration).normalized)).getOrElse {
        ValError(s"Failed to parse year-month-duration from '$d'")
      }
    } else if (isDayTimeDuration(d)) {
      Try(ValDayTimeDuration(d)).getOrElse {
        ValError(s"Failed to parse day-time-duration from '$d'")
      }
    } else {
      ValError(s"Failed to parse duration from '$d'")
    }
  }

}
