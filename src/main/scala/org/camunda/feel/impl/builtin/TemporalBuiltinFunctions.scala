package org.camunda.feel.impl.builtin

import org.camunda.feel.{Date, FeelEngineClock}
import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree.{
  Val,
  ValDate,
  ValDateTime,
  ValFunction,
  ValLocalDateTime,
  ValNumber,
  ValString
}
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale
import java.time.ZonedDateTime
import java.time.LocalDateTime

class TemporalBuiltinFunctions(clock: FeelEngineClock) {

  def functions = Map(
    "now" -> List(nowFunction),
    "today" -> List(todayFunction),
    "day of year" -> List(dateTimeFunction(getDayOfYear)),
    "day of week" -> List(dateTimeFunction(getDayOfWeek)),
    "month of year" -> List(dateTimeFunction(getMonthOfYear)),
    "week of year" -> List(dateTimeFunction(getWeekOfYear)),
    "datetime and zone" -> List(dateTimeFunctionWithZone(getDateTimeInZone))
  )

  private def nowFunction = builtinFunction(
    params = List.empty,
    invoke = {
      case _ =>
        val now = clock.getCurrentTime
        ValDateTime(now)
    }
  )

  private def todayFunction = builtinFunction(
    params = List.empty,
    invoke = {
      case _ =>
        val today = clock.getCurrentTime.toLocalDate
        ValDate(today)
    }
  )

  private def dateTimeFunction(function: Date => Val): ValFunction =
    builtinFunction(
      params = List("date"),
      invoke = {
        case List(ValDate(date))          => function(date)
        case List(ValDateTime(date))      => function(date.toLocalDate)
        case List(ValLocalDateTime(date)) => function(date.toLocalDate)
      }
    )

  private def dateTimeFunctionWithZone(
      function: (Date, String) => Val): ValFunction =
    builtinFunction(
      params = List("date", "zone"),
      invoke = {
        case List(ValDate(date), ValString(zone)) =>
          function(date, zone)
      }
    )

  private def getDayOfYear(date: Date): ValNumber = {
    val dayOfYear = date.getDayOfYear
    ValNumber(dayOfYear)
  }

  private def getDayOfWeek(date: Date): ValString = {
    val dayOfWeek = date.getDayOfWeek
    val displayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    ValString(displayName)
  }

  private def getMonthOfYear(date: Date): ValString = {
    val month = date.getMonth
    val displayName = month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    ValString(displayName)
  }

  private def getWeekOfYear(date: Date): ValNumber = {
    val temporalField = WeekFields.ISO.weekOfWeekBasedYear()
    val weekOfYear = date.get(temporalField)
    ValNumber(weekOfYear)
  }

  private def getDateTimeInZone(date: Date, zone: String): ValDateTime = {
    ValDateTime(date.toInstant().atZone(ZoneId.of(zone)))
  }

}
