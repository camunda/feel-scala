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

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import org.camunda.feel.syntaxtree._
import org.camunda.feel._
import org.camunda.feel.impl.FeelIntegrationTest

import java.time.ZonedDateTime
import scala.math.BigDecimal.double2bigDecimal
import scala.math.BigDecimal.int2bigDecimal

/**
  * @author Philipp
  */
class BuiltinConversionFunctionsTest
    extends AnyFlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A date() function" should "convert String" in {

    eval(""" date(x) """, Map("x" -> "2012-12-25")) should be(
      ValDate("2012-12-25"))
  }

  it should "convert Date-Time" in {

    eval(""" date( date and time("2012-12-25T11:00:00") ) """) should be(
      ValDate("2012-12-25"))

    eval(""" date( date and time("2012-12-25T11:00:00+01:00") ) """) should be(
      ValDate("2012-12-25"))
  }

  it should "convert (year,month,day)" in {

    eval(""" date(2012, 12, 25) """) should be(ValDate("2012-12-25"))
  }

  "A date and time() function" should "convert String" in {

    eval(""" date and time(x) """, Map("x" -> "2012-12-24T23:59:00")) should be(
      ValLocalDateTime("2012-12-24T23:59:00"))

    eval(""" date and time(x) """, Map("x" -> "2012-12-24T23:59:00+01:00")) should be(
      ValDateTime("2012-12-24T23:59:00+01:00"))
  }

  it should "convert (DateTime, Timezone)" in {
    eval("""date and time(@"2020-07-31T14:27:30@Europe/Berlin", "Z")""") should be(
      ValDateTime(ZonedDateTime.parse("2020-07-31T12:27:30Z")))

    eval(
      """date and time(@"2020-07-31T14:27:30@Europe/Berlin", "America/Los_Angeles")""") should be(
      ValDateTime(
        ZonedDateTime.parse("2020-07-31T05:27:30-07:00[America/Los_Angeles]")))

    eval(
      """date and time(@"2020-07-31T14:27:30", "Z")""") should be(
      ValDateTime(
        ZonedDateTime.parse("2020-07-31T14:27:30Z")))
  }

  it should "convert (Date,Time)" in {

    eval(""" date and time(date("2012-12-24"),time("T23:59:00")) """) should be(
      ValLocalDateTime("2012-12-24T23:59:00"))

    eval(""" date and time(date("2012-12-24"),time("T23:59:00+01:00")) """) should be(
      ValDateTime("2012-12-24T23:59:00+01:00"))
  }

  it should "convert (DateTime,Time)" in {

    eval(
      """ date and time(date and time("2012-12-24T10:24:00"),time("T23:59:00")) """) should be(
      ValLocalDateTime("2012-12-24T23:59:00"))
    eval(
      """ date and time(date and time("2012-12-24T10:24:00"),time("T23:59:00+01:00")) """) should be(
      ValDateTime("2012-12-24T23:59:00+01:00"))
    eval(
      """ date and time(date and time("2012-12-24T10:24:00+01:00"),time("T23:59:00")) """) should be(
      ValLocalDateTime("2012-12-24T23:59:00"))
    eval(
      """ date and time(date and time("2012-12-24T10:24:00+01:00"),time("T23:59:00+01:00")) """) should be(
      ValDateTime("2012-12-24T23:59:00+01:00"))
  }

  "A time() function" should "convert String" in {

    eval(""" time(x) """, Map("x" -> "23:59:00")) should be(
      ValLocalTime("23:59:00"))

    eval(""" time(x) """, Map("x" -> "23:59:00+01:00")) should be(
      ValTime("23:59:00+01:00"))

    eval(""" time(x) """, Map("x" -> "23:59:00@Europe/Paris")) should be(
      ValTime("23:59:00@Europe/Paris"))
  }

  it should "convert Date-Time" in {

    eval(""" time( date and time("2012-12-25T11:00:00") ) """) should be(
      ValLocalTime("11:00:00"))

    eval(""" time( date and time("2012-12-25T11:00:00+01:00") ) """) should be(
      ValTime("11:00:00+01:00"))
  }

  it should "convert (hour,minute,second)" in {

    eval(""" time(23, 59, 0) """) should be(ValLocalTime("23:59:00"))
  }

  it should "convert (hour,minute,second, offset)" in {

    eval(""" time(14, 30, 0, duration("PT1H")) """) should be(
      ValTime("14:30:00+01:00"))
  }

  "A number() function" should "convert String" in {

    eval(""" number("1500.5") """) should be(ValNumber(1500.5))
  }

  it should "convert String with Grouping Separator ' '" in {

    eval(""" number("1 500.5", " ") """) should be(ValNumber(1500.5))
  }

  it should "convert String with Grouping Separator ','" in {

    eval(""" number("1,500", ",") """) should be(ValNumber(1500))
  }

  it should "convert String with Grouping Separator '.'" in {

    eval(""" number("1.500", ".") """) should be(ValNumber(1500))
  }

  it should "convert String with Grouping ' ' and Decimal Separator '.'" in {

    eval(""" number("1 500.5", " ", ".") """) should be(ValNumber(1500.5))
  }

  it should "convert String with Grouping ' ' and Decimal Separator ','" in {

    eval(""" number("1 500,5", " ", ",") """) should be(ValNumber(1500.5))
  }

  it should "convert String with Grouping null and Decimal Separator ','" in {

    eval(""" number("1500,5", null, ",") """) should be(ValNumber(1500.5))
  }

  it should "convert String with Grouping '.' and Decimal null" in {

    eval(""" number("1.500", ".", null) """) should be(ValNumber(1500))
  }

  it should "be invoked with named parameter" in {

    eval(
      """ number(from: "1.500", grouping separator: ".", decimal separator: null) """) should be(
      ValNumber(1500))
  }

  "A string() function" should "convert Number" in {

    eval(""" string(1.1) """) should be(ValString("1.1"))
  }

  it should "convert Boolean" in {

    eval(""" string(true) """) should be(ValString("true"))
  }

  it should "convert Date" in {

    eval(""" string(date("2012-12-25")) """) should be(ValString("2012-12-25"))
  }

  it should "convert Time" in {

    eval(""" string(time("23:59:00")) """) should be(ValString("23:59:00"))
    eval(""" string(time("23:59:00+01:00")) """) should be(
      ValString("23:59:00+01:00"))
  }

  it should "convert Date-Time" in {

    eval(""" string(date and time("2012-12-25T11:00:00")) """) should be(
      ValString("2012-12-25T11:00:00"))
    eval(""" string(date and time("2012-12-25T11:00:00+02:00")) """) should be(
      ValString("2012-12-25T11:00:00+02:00"))
  }
  it should "convert zero-length days-time-duration" in {
    eval(""" string(@"-PT0S") """) should be(ValString("P0D"))
    eval(""" string(@"P0D") """) should be(ValString("P0D"))
    eval(""" string(@"PT0H") """) should be(ValString("P0D"))
    eval(""" string(@"PT0H0M") """) should be(ValString("P0D"))
    eval(""" string(@"PT0H0M0S") """) should be(ValString("P0D"))
    eval(""" string(@"P0DT0H0M0S") """) should be(ValString("P0D"))
  }
  it should "convert negative days-time-duration" in {

    eval(""" string(@"-PT1S") """) should be(ValString("-PT1S"))
    eval(""" string(@"-PT1H") """) should be(ValString("-PT1H"))
    eval(""" string(@"-PT2M30S") """) should be(ValString("-PT2M30S"))
    eval(""" string(@"-P1DT2H3M4S") """) should be(ValString("-P1DT2H3M4S"))
  }
  it should "convert days-time-duration" in {

    eval(""" string(@"PT1H") """) should be(ValString("PT1H"))
    eval(""" string(@"PT2M30S") """) should be(ValString("PT2M30S"))
    eval(""" string(@"P1DT2H3M4S") """) should be(ValString("P1DT2H3M4S"))
  }

  it should "convert zero-length years-months-duration" in {

    eval(""" string(@"P0Y") """) should be(ValString("P0Y"))
    eval(""" string(@"-P0M") """) should be(ValString("P0Y"))
    eval(""" string(@"P0Y0M") """) should be(ValString("P0Y"))
  }
  it should "convert negative years-months-duration" in {

    eval(""" string(@"-P1Y") """) should be(ValString("-P1Y"))
    eval(""" string(@"-P5M") """) should be(ValString("-P5M"))
    eval(""" string(@"-P3Y1M") """) should be(ValString("-P3Y1M"))
  }
  it should "convert years-months-duration" in {

    eval(""" string(@"P1Y") """) should be(ValString("P1Y"))
    eval(""" string(@"P2M") """) should be(ValString("P2M"))
    eval(""" string(@"P1Y2M") """) should be(ValString("P1Y2M"))
  }

  "A duration() function" should "convert day-time-String" in {

    eval(""" duration(x) """, Map("x" -> "P2DT20H14M")) should be(
      ValDayTimeDuration("P2DT20H14M"))
  }

  it should "convert day-time-String with negative duration" in {
    eval(""" duration(x) """, Map("x" -> "-PT5M")) should be(
      ValDayTimeDuration("-PT5M"))

    eval(""" duration(x) """, Map("x" -> "PT-5M")) should be(
      ValDayTimeDuration("PT-5M"))

    eval(""" duration(x) """, Map("x" -> "P-1D")) should be(
      ValDayTimeDuration("P-1D"))

    eval(""" duration(x) """, Map("x" -> "PT-2H")) should be(
      ValDayTimeDuration("PT-2H"))

    eval(""" duration(x) """, Map("x" -> "PT-3M-4S")) should be(
      ValDayTimeDuration("PT-3M-4S"))
  }

  it should "convert year-month-String" in {

    eval(""" duration(x) """, Map("x" -> "P2Y4M")) should be(
      ValYearMonthDuration("P2Y4M"))
  }

  it should "convert year-month-String with negative duration" in {
    eval(""" duration(x) """, Map("x" -> "-P1Y2M")) should be(
      ValYearMonthDuration("-P1Y2M"))

    eval(""" duration(x) """, Map("x" -> "P-1Y")) should be(
      ValYearMonthDuration("P-1Y"))

    eval(""" duration(x) """, Map("x" -> "P-2M")) should be(
      ValYearMonthDuration("P-2M"))

    eval(""" duration(x) """, Map("x" -> "P-1Y-2M")) should be(
      ValYearMonthDuration("P-1Y-2M"))
  }

  "A years and months duration(from,to) function" should "convert (Date,Date)" in {

    eval(
      """ years and months duration( date("2011-12-22"), date("2013-08-24") ) """) should be(
      ValYearMonthDuration("P1Y8M"))
    eval(
      """ years and months duration( date and time("2011-12-22T10:00:00"), date and time("2013-08-24T10:00:00") ) """) should be(
      ValYearMonthDuration("P1Y8M"))
    eval(
      """ years and months duration( date and time("2011-12-22T10:00:00+01:00"), date and time("2013-08-24T10:00:00+01:00") ) """) should be(
      ValYearMonthDuration("P1Y8M"))
  }

}
