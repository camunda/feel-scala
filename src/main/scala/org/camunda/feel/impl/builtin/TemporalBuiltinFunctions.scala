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

import org.camunda.feel.{Date, FeelEngineClock}
import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree.{Val, ValBoolean, ValDate, ValDateTime, ValFunction, ValLocalDateTime, ValNull, ValNumber, ValString}

import java.time.{Instant, LocalDateTime, ZoneId, ZoneOffset, ZonedDateTime}
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

class TemporalBuiltinFunctions(clock: FeelEngineClock) {

  def functions = Map(
    "now"                    -> List(nowFunction),
    "today"                  -> List(todayFunction),
    "day of year"            -> List(dateTimeFunction(getDayOfYear)),
    "day of week"            -> List(dateTimeFunction(getDayOfWeek)),
    "month of year"          -> List(dateTimeFunction(getMonthOfYear)),
    "week of year"           -> List(dateTimeFunction(getWeekOfYear)),
    "last day of month"      -> List(dateTimeFunction(getLastDayOfMonth)),
    "to unix timestamp"      -> List(timestampFunction,timestampFunction2),
    "to unix timestampMilli" -> List(timestampMilliFunction,timestampMilliFunction2),
    "from unix timestamp"    -> List(timestampFunction3)
  )

  private def nowFunction = builtinFunction(
    params = List.empty,
    invoke = { case _ =>
      val now = clock.getCurrentTime
      ValDateTime(now)
    }
  )

  private def todayFunction = builtinFunction(
    params = List.empty,
    invoke = { case _ =>
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

  private def getDayOfYear(date: Date): ValNumber = {
    val dayOfYear = date.getDayOfYear
    ValNumber(dayOfYear)
  }

  private def getDayOfWeek(date: Date): ValString = {
    val dayOfWeek   = date.getDayOfWeek
    val displayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    ValString(displayName)
  }

  private def getMonthOfYear(date: Date): ValString = {
    val month       = date.getMonth
    val displayName = month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    ValString(displayName)
  }

  private def getWeekOfYear(date: Date): ValNumber = {
    val temporalField = WeekFields.ISO.weekOfWeekBasedYear()
    val weekOfYear    = date.get(temporalField)
    ValNumber(weekOfYear)
  }

  private def getLastDayOfMonth(date: Date): ValDate = {
    ValDate(date.`with`(TemporalAdjusters.lastDayOfMonth()))
  }

  private def timestampFunction = builtinFunction(
    params = List.empty,
    invoke = { case _ =>
      val timestamp = clock.getCurrentTime.toEpochSecond
      ValNumber(timestamp)
    }
  )

  private def timestampFunction2 = builtinFunction(
    params = List("datetime"),
    invoke = {
      case List(ValDateTime(datetime))        =>
        val timestamp = datetime.toEpochSecond
        ValNumber(timestamp)
      case List(ValLocalDateTime(datetime))   =>
        val timestamp = datetime.toInstant(ZoneOffset.UTC).getEpochSecond
        ValNumber(timestamp)
      case _                                  => ValNull

    }
  )

  private def timestampFunction3 = builtinFunction(
    params = List("timestamp","zoneId"),
    invoke = {

      case List(ValString(timestamp),ValString(zoneId))    =>
        System.out.println("timestamp---"+timestamp+"---"+zoneId)
        var instant = Instant.ofEpochSecond(timestamp.toLong)
        if (timestamp.length() == 10) {
          instant = Instant.ofEpochSecond(timestamp.toLong)
        } else if (timestamp.length() == 13){
          instant = Instant.ofEpochMilli(timestamp.toLong)
        }
        if(!"UTC".equals(zoneId) && zoneId.nonEmpty) {
          val localDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of(zoneId))
          System.out.println(localDateTime)
          ValDateTime(localDateTime)
        }else {
          val localDateTime = instant.atZone(ZoneId.of("UTC")).toLocalDateTime
          System.out.println(localDateTime)
          ValLocalDateTime(localDateTime)
        }
      case _                                               => ValNull

    }
  )

  private def timestampMilliFunction = builtinFunction(
    params = List.empty,
    invoke = { case _ =>
      val timestamp =  clock.getCurrentTime.toInstant.toEpochMilli
      ValString(timestamp.toString)
    }
  )

  private def timestampMilliFunction2 = builtinFunction(
    params = List("datetime"),
    invoke = {
      case List(ValDateTime(datetime))        =>
        val timestamp =  datetime.toInstant.toEpochMilli
        ValString(timestamp.toString)
      case List(ValLocalDateTime(datetime))   =>
        val timestamp =  datetime.toInstant(ZoneOffset.UTC).toEpochMilli
        ValString(timestamp.toString)
      case _                                  =>ValNull
    }
  )

}
