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

import org.camunda.feel.impl.EvaluationResultMatchers.returnResult

import java.time.{LocalDate, LocalDateTime, LocalTime, ZoneId, ZonedDateTime}
import org.camunda.feel.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree.{ValDate, ValDateTime, ValDayTimeDuration, ValError, ValLocalDateTime, ValNull, ValNumber, ValString, ValYearMonthDuration}
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import org.camunda.feel.{stringToDayTimeDuration, stringToYearMonthDuration}

class BuiltinTemporalFunctionsTest
    extends AnyFlatSpec
    with Matchers
    with FeelIntegrationTest
    with BeforeAndAfter {

  private val now = ZonedDateTime.of(
    LocalDate.parse("2020-07-31"),
    LocalTime.parse("14:27:30"),
    ZoneId.of("Europe/Berlin")
  )

  private val now2 = ZonedDateTime.of(
    LocalDate.parse("2020-07-31"),
    LocalTime.parse("14:27:30.123456"),
    ZoneId.of("Europe/Berlin")
  )

  private val now3 = ZonedDateTime.of(
    LocalDate.parse("2020-07-31"),
    LocalTime.parse("14:27:30.123"),
    ZoneId.of("Europe/Berlin")
  )

  private val date          = "date(2019,9,17)"
  private val localDateTime = """date and time("2019-09-17T14:30:00")"""
  private val dateTime      =
    """date and time("2019-09-17T14:30:00.123456@Europe/Berlin")"""

  "The now() function" should "return the current date-time" in withClock { clock =>
    clock.currentTime(now)
    eval(""" now() """) should be(ValDateTime(now))
  }

  "The today() function" should "return the current date" in withClock { clock =>
    clock.currentTime(now)
    eval(""" today() """) should be(ValDate(now.toLocalDate))
  }

  "The to unix timestamp() function" should "return the current timestamp" in withClock { clock =>
    clock.currentTime(now)
    eval(""" to unix timestamp() """) should be(ValNumber(1596198450))
  }

  "The to unix timestamp() function" should "return the timestamp of a given date time" in  {
    eval(s"to unix timestamp($dateTime)") should be(ValNumber(1568723400))
    eval(s"to unix timestamp($localDateTime)") should be(ValNumber(1568730600))
    eval(""" to unix timestamp(date and time ("2020-07-31T14:27:30.123456@Europe/Berlin")) """) should be(ValNumber(1596198450))
    eval(""" to unix timestamp(@"2023-06-09T16:18:41Z") """) should be(ValNumber(1686327521))
    eval(""" to unix timestamp(now()) """) should not be ValNull
  }

  "The to unix timestampMilli() function" should "return the current timestamp in milliseconds" in withClock { clock =>
    clock.currentTime(now2)
    eval(""" to unix timestampMilli() """) should be(ValString("1596198450123"))
  }

  "The to unix timestampMilli() function" should "return the timestamp in milliseconds of a given date time" in  {
    eval(s"to unix timestampMilli($dateTime)") should be(ValString("1568723400123"))
    eval(s"to unix timestampMilli($localDateTime)") should be(ValString("1568730600000"))
    eval(""" to unix timestampMilli(date and time ("2020-07-31T14:27:30.123456@Europe/Berlin")) """) should be(ValString("1596198450123"))
    eval(""" to unix timestampMilli(@"2023-06-09T16:18:41Z") """) should be(ValString("1686327521000"))
    eval(""" to unix timestampMilli(@"2023-06-09T16:18:41@Europe/Berlin") """) should be(ValString("1686320321000"))
    eval(""" to unix timestampMilli(now()) """) should not be ValNull
  }

  "The from unix timestamp() function" should "return the date time with or without milliseconds  of the UTC or a given time zone" in {

    eval("""  from unix timestamp("1568730600") """) should be(ValLocalDateTime(LocalDateTime.parse("2019-09-17T14:30:00")))
    eval("""  from unix timestamp("1568730600","") """) should be(ValLocalDateTime(LocalDateTime.parse("2019-09-17T14:30:00")))
    eval("""  from unix timestamp("1686327521","UTC") """) should be(ValLocalDateTime(LocalDateTime.parse("2023-06-09T16:18:41")))
    eval("""  from unix timestamp("1596198450","Europe/Berlin") """) should be(ValDateTime(now))

    eval("""  from unix timestamp(to unix timestampMilli(@"2023-06-09T16:18:41Z")) """) should be(ValLocalDateTime(LocalDateTime.parse("2023-06-09T16:18:41")))

    eval("""  from unix timestamp("1596198450123","Europe/Berlin") """) should be(ValDateTime(now3))
    eval("""  from unix timestamp(to unix timestampMilli(@"2020-07-31T14:27:30.123@Europe/Berlin"),"Europe/Berlin") """) should be(ValDateTime(now3))
    eval("""  from unix timestamp(to unix timestampMilli(@"2023-06-09T16:18:41.123Z"),"") """) should be(ValLocalDateTime(LocalDateTime.parse("2023-06-09T16:18:41.123")))
  }

  "The day of year() function" should "return the day within the year" in {

    eval(s"day of year($date)") should be(ValNumber(260))
    eval(s"day of year($localDateTime) ") should be(ValNumber(260))
    eval(s"day of year($dateTime) ") should be(ValNumber(260))

    eval(s""" day of year(date("2019-12-31")) """) should be(ValNumber(365))
    eval(s""" day of year(date("2020-12-31")) """) should be(ValNumber(366))
  }

  "The day of week() function" should "return the day of the week" in {

    eval(s"day of week($date)") should be(ValString("Tuesday"))
    eval(s"day of week($localDateTime)") should be(ValString("Tuesday"))
    eval(s"day of week($dateTime)") should be(ValString("Tuesday"))
  }

  "The month of year() function" should "return the month of the year" in {

    eval(s"month of year($date)") should be(ValString("September"))
    eval(s"month of year($localDateTime)") should be(ValString("September"))
    eval(s"month of year($dateTime)") should be(ValString("September"))
  }

  "The week of year() function" should "return the number of week within the year" in {

    eval(s"week of year($date)") should be(ValNumber(38))
    eval(s"week of year($localDateTime)") should be(ValNumber(38))
    eval(s"week of year($dateTime)") should be(ValNumber(38))

    eval(""" week of year(date(2003,12,29)) """) should be(ValNumber(1))
    eval(""" week of year(date(2004,1,4)) """) should be(ValNumber(1))
    eval(""" week of year(date(2005,1,1)) """) should be(ValNumber(53))
    eval(""" week of year(date(2005,1,3)) """) should be(ValNumber(1))
    eval(""" week of year(date(2005,1,9)) """) should be(ValNumber(1))
  }

  "A abs() function" should "return the absolute value of a days-time-duration" in {
    eval(""" abs(duration("PT5H")) """) should be(ValDayTimeDuration("PT5H"))
    eval(""" abs(duration("-PT5H")) """) should be(ValDayTimeDuration("PT5H"))
  }

  it should "return the absolute value of a years-months-duration" in {
    eval(""" abs(duration("P2M")) """) should be(ValYearMonthDuration("P2M"))
    eval(""" abs(duration("-P2M")) """) should be(ValYearMonthDuration("P2M"))
  }

  "A last day of month() function" should "return the the last day of the month" in {
    eval(""" last day of month(date(2022,10,17)) """) should be(
      ValDate(LocalDate.parse("2022-10-31"))
    )

    eval(s"last day of month($date)") should be(ValDate(LocalDate.parse("2019-09-30")))
    eval(s"last day of month($localDateTime)") should be(ValDate(LocalDate.parse("2019-09-30")))
    eval(s"last day of month($dateTime)") should be(ValDate(LocalDate.parse("2019-09-30")))
  }

  it should "take the leap years into account" in {
    eval(""" last day of month(date("2022-02-01")) """) should be(
      ValDate(LocalDate.parse("2022-02-28"))
    )

    eval(""" last day of month(date("2024-02-01")) """) should be(
      ValDate(LocalDate.parse("2024-02-29"))
    )
  }

}
