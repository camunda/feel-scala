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
package org.camunda.feel.api

import org.camunda.feel.context.Context.{EmptyContext, StaticContext}
import org.camunda.feel.syntaxtree.{
  ClosedRangeBoundary,
  OpenRangeBoundary,
  ValContext,
  ValDate,
  ValDateTime,
  ValDayTimeDuration,
  ValError,
  ValFatalError,
  ValFunction,
  ValList,
  ValLocalDateTime,
  ValLocalTime,
  ValNull,
  ValNumber,
  ValRange,
  ValString,
  ValTime,
  ValYearMonthDuration,
  ZonedTime
}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.{Duration, LocalDate, LocalDateTime, LocalTime, Period, ZonedDateTime}

class StringRepresentationTypeTest extends AnyFlatSpec with Matchers {

  "Null" should "return 'null'" in {
    ValNull.toString should be("null")
  }

  "A number" should "return '1'" in {
    ValNumber(1).toString should be("1")
  }

  it should "return '1.2'" in {
    ValNumber(1.2).toString should be("1.2")
  }

  "A string" should """return '"a"' """ in {
    ValString("a").toString should be("\"a\"")
  }

  "A date" should "return '2023-09-15' " in {
    val date = ValDate(LocalDate.parse("2023-09-15"))

    date.toString should be("2023-09-15")
  }

  "A time" should "return '06:41:30' " in {
    val time = ValLocalTime(LocalTime.parse("06:41:30"))

    time.toString should be("06:41:30")
  }

  it should "return '06:41:30+02:00' " in {
    val time = ValTime(ZonedTime.parse("06:41:30+02:00"))

    time.toString should be("06:41:30+02:00")
  }

  it should "return '06:41:30@Europe/Berlin' " in {
    val time = ValTime(ZonedTime.parse("06:41:30@Europe/Berlin"))

    time.toString should be("06:41:30@Europe/Berlin")
  }

  "A date and time" should "return '2023-09-15T06:41:30'" in {
    val dateTime = ValLocalDateTime(LocalDateTime.parse("2023-09-15T06:41:30"))

    dateTime.toString should be("2023-09-15T06:41:30")
  }

  it should "return '2023-09-15T06:41:30+02:00'" in {
    val dateTime = ValDateTime(ZonedDateTime.parse("2023-09-15T06:41:30+02:00"))

    dateTime.toString should be("2023-09-15T06:41:30+02:00")
  }

  it should "return '2023-09-15T06:41:30@Europe/Berlin'" in {
    val dateTime = ValDateTime(ZonedDateTime.parse("2023-09-15T06:41:30+02:00[Europe/Berlin]"))

    dateTime.toString should be("2023-09-15T06:41:30@Europe/Berlin")
  }

  "A years-months-duration" should "return 'P1Y2M' " in {
    val duration = ValYearMonthDuration(Period.parse("P1Y2M"))

    duration.toString should be("P1Y2M")
  }

  it should "return 'P1Y' " in {
    val duration = ValYearMonthDuration(Period.parse("P1Y"))

    duration.toString should be("P1Y")
  }

  it should "return 'P2M' " in {
    val duration = ValYearMonthDuration(Period.parse("P2M"))

    duration.toString should be("P2M")
  }

  it should "return normalized format " in {
    ValYearMonthDuration(Period.parse("P2Y")).toString should be("P2Y")
    ValYearMonthDuration(Period.parse("P24M")).toString should be("P2Y")

    ValYearMonthDuration(Period.parse("P25M")).toString should be("P2Y1M")
    ValYearMonthDuration(Period.parse("P35M")).toString should be("P2Y11M")
  }

  "A days-time-duration" should "return 'P1DT2H3M4S' " in {
    val duration = ValDayTimeDuration(Duration.parse("P1DT2H3M4S"))

    duration.toString should be("P1DT2H3M4S")
  }

  it should "return 'P1D' " in {
    val duration = ValDayTimeDuration(Duration.parse("P1D"))

    duration.toString should be("P1D")
  }

  it should "return 'PT2H' " in {
    val duration = ValDayTimeDuration(Duration.parse("PT2H"))

    duration.toString should be("PT2H")
  }

  it should "return 'PT3M' " in {
    val duration = ValDayTimeDuration(Duration.parse("PT3M"))

    duration.toString should be("PT3M")
  }

  it should "return 'PT4S' " in {
    val duration = ValDayTimeDuration(Duration.parse("PT4S"))

    duration.toString should be("PT4S")
  }

  it should "return normalized format " in {
    ValDayTimeDuration(Duration.parse("P5D")).toString should be("P5D")
    ValDayTimeDuration(Duration.parse("PT120H")).toString should be("P5D")
    ValDayTimeDuration(Duration.parse("PT7200M")).toString should be("P5D")
    ValDayTimeDuration(Duration.parse("PT432000S")).toString should be("P5D")

    ValDayTimeDuration(Duration.parse("PT121H")).toString should be("P5DT1H")
    ValDayTimeDuration(Duration.parse("PT7201M")).toString should be("P5DT1M")
    ValDayTimeDuration(Duration.parse("PT7261M")).toString should be("P5DT1H1M")
  }

  "A list" should "return '[1, 2]' " in {
    ValList(List(ValNumber(1), ValNumber(2))).toString should be("[1, 2]")
  }

  it should "return '[]' " in {
    ValList(List()).toString should be("[]")
  }

  "A context" should "return '{x:1, y:2}' " in {
    val context = StaticContext(variables = Map("x" -> ValNumber(1), "y" -> ValNumber(2)))

    ValContext(context).toString should be("{x:1, y:2}")
  }

  it should "return '{}' " in {
    ValContext(EmptyContext).toString should be("{}")
  }

  "A range" should "return '(1..3)' " in {
    val range = ValRange(
      start = OpenRangeBoundary(ValNumber(1)),
      end = OpenRangeBoundary(ValNumber(3))
    )

    range.toString should be("(1..3)")
  }

  it should "return '[1..3]' " in {
    val range = ValRange(
      start = ClosedRangeBoundary(ValNumber(1)),
      end = ClosedRangeBoundary(ValNumber(3))
    )

    range.toString should be("[1..3]")
  }

  "A function" should "return 'function(a, b)' " in {
    val function = ValFunction(
      params = List("a", "b"),
      invoke = _ => ValNull
    )

    function.toString should be("function(a, b)")
  }

  "An error" should """return 'error("something wrong")' """ in {
    val error = ValError("something wrong")

    error.toString should be("error(\"something wrong\")")
  }

  "A fatal error" should """return 'fatal error("something wrong")' """ in {
    val fatalError = ValFatalError("something wrong")

    fatalError.toString should be("fatal error(\"something wrong\")")
  }

}
