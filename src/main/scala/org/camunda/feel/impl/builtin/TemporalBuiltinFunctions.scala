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
import org.camunda.feel.syntaxtree.{
  Val,
  ValDate,
  ValDateTime,
  ValFunction,
  ValLocalDateTime,
  ValNumber,
  ValString
}

import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

class TemporalBuiltinFunctions(clock: FeelEngineClock) {

  def functions = Map(
    "now" -> List(nowFunction),
    "today" -> List(todayFunction),
    "day of year" -> List(dateTimeFunction(getDayOfYear)),
    "day of week" -> List(dateTimeFunction(getDayOfWeek)),
    "month of year" -> List(dateTimeFunction(getMonthOfYear)),
    "week of year" -> List(dateTimeFunction(getWeekOfYear)),
    "last day of month" -> List(dateTimeFunction(getLastDayOfMonth))
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

  private def getLastDayOfMonth(date: Date): ValDate = {
    ValDate(date.`with`(TemporalAdjusters.lastDayOfMonth()))
  }

}
