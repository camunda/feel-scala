package org.camunda.feel.impl.builtin

import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree.{
  Val,
  ValBoolean,
  ValDate,
  ValDateTime,
  ValDayTimeDuration,
  ValError,
  ValLocalDateTime,
  ValLocalTime,
  ValNull,
  ValNumber,
  ValString,
  ValTime,
  ValYearMonthDuration,
  ZonedTime
}
import org.camunda.feel.{
  Date,
  YearMonthDuration,
  dateFormatter,
  dateTimeFormatter,
  isDayTimeDuration,
  isLocalDateTime,
  isOffsetDateTime,
  isOffsetTime,
  isValidDate,
  isYearMonthDuration,
  localDateTimeFormatter,
  localTimeFormatter,
  logger,
  stringToDate,
  stringToDateTime,
  stringToDayTimeDuration,
  stringToLocalDateTime,
  stringToLocalTime,
  stringToNumber,
  stringToYearMonthDuration
}

import java.math.BigDecimal
import java.time.{LocalDate, LocalTime, Period, ZoneOffset}
import java.util.regex.Pattern
import scala.util.Try

object ConversionBuiltinFunctions {

  def functions = Map(
    "date" -> List(dateFunction, dateFunction3),
    "date and time" -> List(dateTime, dateTime2),
    "time" -> List(timeFunction, timeFunction3, timeFunction4),
    "number" -> List(numberFunction, numberFunction2, numberFunction3),
    "string" -> List(stringFunction),
    "duration" -> List(durationFunction),
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
    invoke = {
      case List(ValNumber(year), ValNumber(month), ValNumber(day)) =>
        Try {
          ValDate(LocalDate.of(year.intValue, month.intValue, day.intValue))
        }.getOrElse {
          logger.warn(
            s"Failed to parse date from: year=$year, month=$month, day=$day");
          ValNull
        }
    }
  )

  private def dateTime =
    builtinFunction(params = List("from"), invoke = {
      case List(ValString(from)) => parseDateTime(from)
    })

  private def dateTime2 = builtinFunction(
    params = List("date", "time"),
    invoke = {
      case List(ValDate(date), ValLocalTime(time)) =>
        ValLocalDateTime(date.atTime(time))
      case List(ValDate(date), ValTime(time)) =>
        ValDateTime(time.withDate(date))
      case List(ValLocalDateTime(dateTime), ValLocalTime(time)) =>
        ValLocalDateTime(dateTime.toLocalDate().atTime(time))
      case List(ValLocalDateTime(dateTime), ValTime(time)) =>
        ValDateTime(time.withDate(dateTime.toLocalDate()))
      case List(ValDateTime(dateTime), ValLocalTime(time)) =>
        ValLocalDateTime(dateTime.toLocalDate().atTime(time))
      case List(ValDateTime(dateTime), ValTime(time)) =>
        ValDateTime(time.withDate(dateTime.toLocalDate()))
    }
  )

  private def timeFunction = builtinFunction(
    params = List("from"),
    invoke = {
      case List(ValString(from))        => parseTime(from)
      case List(ValLocalDateTime(from)) => ValLocalTime(from.toLocalTime())
      case List(ValDateTime(from))      => ValTime(ZonedTime.of(from))
      case List(ValDate(from)) =>
        ValTime(ZonedTime.of(LocalTime.MIDNIGHT, ZoneOffset.UTC))
    }
  )

  private def timeFunction3 = builtinFunction(
    params = List("hour", "minute", "second"),
    invoke = {
      case List(ValNumber(hour), ValNumber(minute), ValNumber(second)) =>
        Try {
          val nanos = second.bigDecimal
            .remainder(BigDecimal.ONE)
            .movePointRight(9)
            .intValue
          ValLocalTime(
            LocalTime
              .of(hour.intValue, minute.intValue, second.intValue, nanos))
        }.getOrElse {
          logger.warn(
            s"Failed to parse local-time from: hour=$hour, minute=$minute, second=$second");
          ValNull
        }
    }
  )

  private def timeFunction4 = builtinFunction(
    params = List("hour", "minute", "second", "offset"),
    invoke = {
      case List(ValNumber(hour),
                ValNumber(minute),
                ValNumber(second),
                ValDayTimeDuration(offset)) =>
        Try {
          val nanos = second.bigDecimal
            .remainder(BigDecimal.ONE)
            .movePointRight(9)
            .intValue
          val localTime =
            LocalTime.of(hour.intValue, minute.intValue, second.intValue, nanos)
          val zonedOffset = ZoneOffset.ofTotalSeconds(offset.getSeconds.toInt)

          ValTime(ZonedTime.of(localTime, zonedOffset))
        }.getOrElse {
          logger.warn(
            s"Failed to parse time from: hour=$hour, minute=$minute, second=$second, offset=$offset");
          ValNull
        }
      case List(ValNumber(hour),
                ValNumber(minute),
                ValNumber(second),
                ValNull) =>
        Try {
          ValLocalTime(
            LocalTime.of(hour.intValue, minute.intValue, second.intValue))
        }.getOrElse {
          logger.warn(
            s"Failed to parse local-time from: hour=$hour, minute=$minute, second=$second");
          ValNull
        }
    }
  )

  private def numberFunction =
    builtinFunction(params = List("from"), invoke = {
      case List(ValString(from)) => ValNumber(from)
    })

  private def numberFunction2 = builtinFunction(
    params = List("from", "grouping separator"),
    invoke = {
      case List(ValString(from), ValString(grouping))
          if (isValidGroupingSeparator(grouping)) =>
        ValNumber(from.replace(grouping, ""))
      case List(ValString(from), ValString(grouping)) =>
        ValError(
          s"illegal argument for grouping. Must be one of ' ', ',' or '.'")
    }
  )

  private def numberFunction3 = builtinFunction(
    params = List("from", "grouping separator", "decimal separator"),
    invoke = {
      case List(ValString(from), ValString(grouping), ValString(decimal))
          if (isValidGroupingSeparator(grouping) && isValidDecimalSeparator(
            decimal) && grouping != decimal) =>
        ValNumber(from.replace(grouping, "").replace(decimal, "."))
      case List(ValString(from), ValNull, ValString(decimal))
          if isValidDecimalSeparator(decimal) =>
        ValNumber(from.replace(decimal, "."))
      case List(ValString(from), ValString(grouping), ValNull)
          if isValidGroupingSeparator(grouping) =>
        ValNumber(from.replace(grouping, ""))
      case List(ValString(from), ValString(grouping), ValString(decimal)) =>
        ValError(
          s"illegal arguments for grouping or decimal. Must be one of ' ' (grouping only), ',' or '.'")
    }
  )

  private def isValidGroupingSeparator(separator: String) =
    separator == " " || separator == "," || separator == "."

  private def isValidDecimalSeparator(separator: String) =
    separator == "," || separator == "."

  private lazy val dateTimeOffsetZoneIdPattern =
    Pattern.compile("(.*)([+-]\\d{2}:\\d{2}|Z)(@.*)")

  private def stringFunction = builtinFunction(
    params = List("from"),
    invoke = {
      case List(ValString(from))  => ValString(from)
      case List(ValBoolean(from)) => ValString(from.toString)
      case List(ValNumber(from))  => ValString(from.toString)
      case List(ValDate(from))    => ValString(from.format(dateFormatter))
      case List(ValLocalTime(from)) =>
        ValString(from.format(localTimeFormatter))
      case List(ValTime(from)) => ValString(from.format)
      case List(ValLocalDateTime(from)) =>
        ValString(from.format(localDateTimeFormatter))
      case List(ValDateTime(from)) => {
        val formattedDateTime = from.format(dateTimeFormatter)
        // remove offset-id if zone-id is present
        val dateTimeWithOffsetOrZoneId = dateTimeOffsetZoneIdPattern
          .matcher(formattedDateTime)
          .replaceAll("$1$3")
        ValString(dateTimeWithOffsetOrZoneId)
      }
      case List(ValYearMonthDuration(from)) => ValString(from.toString)
      case List(duration: ValDayTimeDuration)   => ValString(duration.toString)
    }
  )

  private def durationFunction =
    builtinFunction(params = List("from"), invoke = {
      case List(ValString(from)) => parseDuration(from)
    })

  private def durationFunction2 = builtinFunction(
    params = List("from", "to"),
    invoke = {
      case List(ValDate(from), ValDate(to)) =>
        ValYearMonthDuration(Period.between(from, to).withDays(0).normalized)
      case List(ValLocalDateTime(from), ValLocalDateTime(to)) =>
        ValYearMonthDuration(
          Period
            .between(from.toLocalDate, to.toLocalDate)
            .withDays(0)
            .normalized)
      case List(ValDateTime(from), ValDateTime(to)) =>
        ValYearMonthDuration(
          Period
            .between(from.toLocalDate, to.toLocalDate)
            .withDays(0)
            .normalized)
      case List(ValDateTime(from), ValLocalDateTime(to)) =>
        ValYearMonthDuration(
          Period
            .between(from.toLocalDate, to.toLocalDate)
            .withDays(0)
            .normalized)
      case List(ValLocalDateTime(from), ValDateTime(to)) =>
        ValYearMonthDuration(
          Period
            .between(from.toLocalDate, to.toLocalDate)
            .withDays(0)
            .normalized)
      case List(ValDate(from), ValDateTime(to)) =>
        ValYearMonthDuration(
          Period.between(from, to.toLocalDate()).withDays(0).normalized)
      case List(ValDate(from), ValLocalDateTime(to)) =>
        ValYearMonthDuration(
          Period.between(from, to.toLocalDate()).withDays(0).normalized)
      case List(ValDateTime(from), ValDate(to)) =>
        ValYearMonthDuration(
          Period.between(from.toLocalDate(), to).withDays(0).normalized)
      case List(ValLocalDateTime(from), ValDate(to)) =>
        ValYearMonthDuration(
          Period.between(from.toLocalDate(), to).withDays(0).normalized)
    }
  )

  private def parseDate(d: String): Val = {
    if (isValidDate(d)) {
      Try(ValDate(d)).getOrElse {
        logger.warn(s"Failed to parse date from '$d'");
        ValNull
      }
    } else {
      logger.warn(s"Failed to parse date from '$d'");
      ValNull
    }
  }

  private def parseTime(t: String): Val = {
    if (isOffsetTime(t)) {
      Try(ValTime(t)).getOrElse {
        logger.warn(s"Failed to parse time from '$t'");
        ValNull
      }
    } else {
      Try(ValLocalTime(t)).getOrElse {
        logger.warn(s"Failed to parse local-time from '$t'");
        ValNull
      }
    }
  }

  private def parseDateTime(dt: String): Val = {
    if (isValidDate(dt)) {
      Try(ValLocalDateTime((dt: Date).atTime(0, 0))).getOrElse {
        logger.warn(s"Failed to parse date(-time) from '$dt'");
        ValNull
      }
    } else if (isOffsetDateTime(dt)) {
      Try(ValDateTime(dt)).getOrElse {
        logger.warn(s"Failed to parse date-time from '$dt'");
        ValNull
      }
    } else if (isLocalDateTime(dt)) {
      Try(ValLocalDateTime(dt)).getOrElse {
        logger.warn(s"Failed to parse local-date-time from '$dt'");
        ValNull
      }
    } else {
      logger.warn(s"Failed to parse date-time from '$dt'");
      ValNull
    }
  }

  private def parseDuration(d: String): Val = {
    if (isYearMonthDuration(d)) {
      Try(ValYearMonthDuration((d: YearMonthDuration).normalized)).getOrElse {
        logger.warn(s"Failed to parse year-month-duration from '$d'");
        ValNull
      }
    } else if (isDayTimeDuration(d)) {
      Try(ValDayTimeDuration(d)).getOrElse {
        logger.warn(s"Failed to parse day-time-duration from '$d'");
        ValNull
      }
    } else {
      logger.warn(s"Failed to parse duration from '$d'");
      ValNull
    }
  }

}
