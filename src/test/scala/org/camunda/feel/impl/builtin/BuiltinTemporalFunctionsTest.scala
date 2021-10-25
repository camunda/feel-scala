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

import java.time.{LocalDate, LocalTime, ZoneId, ZonedDateTime}

import org.camunda.feel.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree.{ValDate, ValDateTime, ValNumber, ValString}
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

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

  private val date = "date(2019,9,17)"
  private val localDateTime = """date and time("2019-09-17T14:30:00")"""
  private val dateTime =
    """date and time("2019-09-17T14:30:00@Europe/Berlin")"""

  "The now() function" should "return the current date-time" in withClock {
    clock =>
      clock.currentTime(now)
      eval(""" now() """) should be(ValDateTime(now))
  }

  "The today() function" should "return the current date" in withClock {
    clock =>
      clock.currentTime(now)
      eval(""" today() """) should be(ValDate(now.toLocalDate))
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
}
