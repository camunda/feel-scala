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
package org.camunda.feel.impl.interpreter

import org.camunda.feel._
import org.camunda.feel.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree._
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

/**
  * @author Philipp Ossler
  */
class InterpreterDateTimeExpressionTest
    extends AnyFlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A time" should "subtract from another time" in {

    eval(""" time("10:30:00") - time("09:00:00") """) should be(
      ValDayTimeDuration("PT1H30M"))

    eval(""" time("09:00:00") - time("10:00:00") """) should be(
      ValDayTimeDuration("PT-1H"))

    eval(""" time("12:00:00+01:00") - time("10:00:00+01:00") """) should be(
      ValDayTimeDuration("PT2H"))
  }

  it should "compare with '='" in {

    eval(""" time("10:00:00") = time("10:00:00") """) should be(
      ValBoolean(true))
    eval(""" time("10:00:00") = time("10:30:00") """) should be(
      ValBoolean(false))

    eval(""" time("10:00:00+01:00") = time("10:30:00+01:00") """) should be(
      ValBoolean(false))
    eval(""" time("10:00:00+01:00") = time("10:00:00+02:00") """) should be(
      ValBoolean(false))
    eval(""" time("10:00:00+01:00") = time("10:00:00+01:00") """) should be(
      ValBoolean(true))
  }

  it should "compare with '!='" in {

    eval(""" time("10:00:00") != time("10:00:00") """) should be(
      ValBoolean(false))
    eval(""" time("10:00:00") != time("22:00:00") """) should be(
      ValBoolean(true))

    eval(""" time("10:00:00+01:00") != time("10:00:00+01:00") """) should be(
      ValBoolean(false))
    eval(""" time("10:00:00+01:00") != time("22:00:00+01:00") """) should be(
      ValBoolean(true))
  }

  it should "compare with '<'" in {

    eval(""" time("10:00:00") < time("11:00:00") """) should be(
      ValBoolean(true))
    eval(""" time("10:00:00") < time("10:00:00") """) should be(
      ValBoolean(false))

    eval(""" time("10:00:00+01:00") < time("11:00:00+01:00") """) should be(
      ValBoolean(true))
    eval(""" time("10:00:00+01:00") < time("10:00:00+01:00") """) should be(
      ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(""" time("10:00:00") <= time("10:00:00") """) should be(
      ValBoolean(true))
    eval(""" time("10:00:01") <= time("10:00:00") """) should be(
      ValBoolean(false))

    eval(""" time("10:00:00+01:00") <= time("10:00:00+01:00") """) should be(
      ValBoolean(true))
    eval(""" time("11:00:00+01:00") <= time("10:00:00+01:00") """) should be(
      ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(""" time("11:00:00") > time("11:00:00") """) should be(
      ValBoolean(false))
    eval(""" time("10:15:00") > time("10:00:00") """) should be(
      ValBoolean(true))

    eval(""" time("11:00:00+01:00") > time("11:00:00+01:00") """) should be(
      ValBoolean(false))
    eval(""" time("10:15:00+01:00") > time("10:00:00+01:00") """) should be(
      ValBoolean(true))
  }

  it should "compare with '>='" in {

    eval(""" time("11:00:00") >= time("11:00:00") """) should be(
      ValBoolean(true))
    eval(""" time("09:00:00") >= time("11:15:00") """) should be(
      ValBoolean(false))

    eval(""" time("11:00:00+01:00") >= time("11:00:00+01:00") """) should be(
      ValBoolean(true))
    eval(""" time("09:00:00+01:00") >= time("11:15:00+01:00") """) should be(
      ValBoolean(false))
  }

  it should "compare with 'between _ and _'" in {

    eval(""" time("08:30:00") between time("08:00:00") and time("10:00:00") """) should be(
      ValBoolean(true))
    eval(""" time("08:30:00") between time("09:00:00") and time("10:00:00") """) should be(
      ValBoolean(false))

    eval(
      """ time("08:30:00+01:00") between time("08:00:00+01:00") and time("10:00:00+01:00") """) should be(
      ValBoolean(true))
    eval(
      """ time("08:30:00+01:00") between time("09:00:00+01:00") and time("10:00:00+01:00") """) should be(
      ValBoolean(false))
  }

  "A date" should "subtract from another date" in {

    eval(""" date("2012-12-25") - date("2012-12-24") """) should be(
      ValDayTimeDuration("P1D"))

    eval(""" date("2012-12-24") - date("2012-12-25") """) should be(
      ValDayTimeDuration("P-1D"))

    eval(""" date("2013-02-25") - date("2012-12-24") """) should be(
      ValDayTimeDuration("P63D"))
  }

  it should "compare with '='" in {

    eval(""" date("2017-01-10") = date("2017-01-10") """) should be(
      ValBoolean(true))
    eval(""" date("2017-01-10") = date("2017-01-11") """) should be(
      ValBoolean(false))
  }

  it should "compare with '!='" in {

    eval(""" date("2017-01-10") != date("2017-01-10") """) should be(
      ValBoolean(false))
    eval(""" date("2017-01-10") != date("2017-02-10") """) should be(
      ValBoolean(true))
  }

  it should "compare with '<'" in {

    eval(""" date("2016-01-10") < date("2017-01-10") """) should be(
      ValBoolean(true))
    eval(""" date("2017-01-10") < date("2017-01-10") """) should be(
      ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(""" date("2017-01-10") <= date("2017-01-10") """) should be(
      ValBoolean(true))
    eval(""" date("2017-01-20") <= date("2017-01-10") """) should be(
      ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(""" date("2017-01-10") > date("2017-01-10") """) should be(
      ValBoolean(false))
    eval(""" date("2017-02-17") > date("2017-01-10") """) should be(
      ValBoolean(true))
  }

  it should "compare with '>='" in {

    eval(""" date("2017-01-10") >= date("2017-01-10") """) should be(
      ValBoolean(true))
    eval(""" date("2017-01-10") >= date("2018-01-10") """) should be(
      ValBoolean(false))
  }

  it should "compare with 'between _ and _'" in {

    eval(
      """ date("2017-01-10") between date("2017-01-01") and date("2018-01-10") """) should be(
      ValBoolean(true))
    eval(
      """ date("2017-01-10") between date("2017-02-01") and date("2017-03-01") """) should be(
      ValBoolean(false))
  }

  "A date-time" should "subtract from another date-time" in {

    eval(
      """ date and time("2017-01-10T10:30:00") - date and time("2017-01-01T10:00:00") """) should be(
      ValDayTimeDuration("P9DT30M"))
    eval(
      """ date and time("2017-01-10T10:00:00") - date and time("2017-01-10T10:30:00") """) should be(
      ValDayTimeDuration("PT-30M"))

    eval(
      """ date and time("2017-01-10T10:30:00+01:00") - date and time("2017-01-01T10:00:00+01:00") """) should be(
      ValDayTimeDuration("P9DT30M"))
    eval(
      """ date and time("2017-01-10T10:00:00+01:00") - date and time("2017-01-10T10:30:00+01:00") """) should be(
      ValDayTimeDuration("PT-30M"))
  }

  it should "compare with '='" in {

    eval(
      """ date and time("2017-01-10T10:30:00") = date and time("2017-01-10T10:30:00") """) should be(
      ValBoolean(true))
    eval(
      """ date and time("2017-01-10T10:30:00") = date and time("2017-01-10T14:00:00") """) should be(
      ValBoolean(false))

    eval(
      """ date and time("2017-01-10T10:30:00+01:00") = date and time("2017-01-10T10:30:00+01:00") """) should be(
      ValBoolean(true))
    eval(
      """ date and time("2017-01-10T10:30:00+01:00") = date and time("2017-01-10T14:00:00+01:00") """) should be(
      ValBoolean(false))
  }

  it should "compare with '!='" in {

    eval(
      """ date and time("2017-01-10T10:30:00") != date and time("2017-01-10T10:30:00") """) should be(
      ValBoolean(false))
    eval(
      """ date and time("2017-01-10T10:30:00") != date and time("2017-01-11T10:30:00") """) should be(
      ValBoolean(true))

    eval(
      """ date and time("2017-01-10T10:30:00+01:00") != date and time("2017-01-10T10:30:00+01:00") """) should be(
      ValBoolean(false))
    eval(
      """ date and time("2017-01-10T10:30:00+01:00") != date and time("2017-01-11T10:30:00+01:00") """) should be(
      ValBoolean(true))
  }

  it should "compare with '<'" in {

    eval(
      """ date and time("2017-01-10T10:30:00") < date and time("2017-02-10T10:00:00") """) should be(
      ValBoolean(true))
    eval(
      """ date and time("2017-01-10T10:30:00") < date and time("2017-01-10T10:30:00") """) should be(
      ValBoolean(false))

    eval(
      """ date and time("2017-01-10T10:30:00+01:00") < date and time("2017-02-10T10:00:00+01:00") """) should be(
      ValBoolean(true))
    eval(
      """ date and time("2017-01-10T10:30:00+01:00") < date and time("2017-01-10T10:30:00+01:00") """) should be(
      ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(
      """ date and time("2017-01-10T10:30:00") <= date and time("2017-01-10T10:30:00") """) should be(
      ValBoolean(true))
    eval(
      """ date and time("2017-02-10T10:00:00") <= date and time("2017-01-10T10:30:00") """) should be(
      ValBoolean(false))

    eval(
      """ date and time("2017-01-10T10:30:00+01:00") <= date and time("2017-01-10T10:30:00+01:00") """) should be(
      ValBoolean(true))
    eval(
      """ date and time("2017-02-10T10:00:00+01:00") <= date and time("2017-01-10T10:30:00+01:00") """) should be(
      ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(
      """ date and time("2017-01-10T10:30:00") > date and time("2017-01-10T10:30:00") """) should be(
      ValBoolean(false))
    eval(
      """ date and time("2018-01-10T10:30:00") > date and time("2017-01-10T10:30:00") """) should be(
      ValBoolean(true))

    eval(
      """ date and time("2017-01-10T10:30:00+01:00") > date and time("2017-01-10T10:30:00+01:00") """) should be(
      ValBoolean(false))
    eval(
      """ date and time("2018-01-10T10:30:00+01:00") > date and time("2017-01-10T10:30:00+01:00") """) should be(
      ValBoolean(true))
  }

  it should "compare with '>='" in {

    eval(
      """ date and time("2017-01-10T10:30:00") >= date and time("2017-01-10T10:30:00") """) should be(
      ValBoolean(true))
    eval(
      """ date and time("2017-01-10T10:30:00") >= date and time("2017-01-10T10:30:01") """) should be(
      ValBoolean(false))

    eval(
      """ date and time("2017-01-10T10:30:00+01:00") >= date and time("2017-01-10T10:30:00+01:00") """) should be(
      ValBoolean(true))
    eval(
      """ date and time("2017-01-10T10:30:00+01:00") >= date and time("2017-01-10T10:30:01+01:00") """) should be(
      ValBoolean(false))
  }

  it should "compare with 'between _ and _'" in {

    eval(
      """ date and time("2017-01-10T10:30:00") between date and time("2017-01-10T09:00:00") and date and time("2017-01-10T14:00:00") """) should be(
      ValBoolean(true))
    eval(
      """ date and time("2017-01-10T10:30:00") between date and time("2017-01-10T11:00:00") and date and time("2017-01-11T08:00:00") """) should be(
      ValBoolean(false))

    eval(
      """ date and time("2017-01-10T10:30:00+01:00") between date and time("2017-01-10T09:00:00+01:00") and date and time("2017-01-10T14:00:00+01:00") """) should be(
      ValBoolean(true))
    eval(
      """ date and time("2017-01-10T10:30:00+01:00") between date and time("2017-01-10T11:00:00+01:00") and date and time("2017-01-11T08:00:00+01:00") """) should be(
      ValBoolean(false))
  }

  "A year-month-duration" should "add to year-month-duration" in {

    eval(""" duration("P2M") + duration("P3M") """) should be(
      ValYearMonthDuration("P5M"))
    eval(""" duration("P1Y") + duration("P6M") """) should be(
      ValYearMonthDuration("P1Y6M"))
  }

  it should "add to date-time" in {

    eval(""" duration("P1M") + date and time("2017-01-10T10:30:00") """) should be(
      ValLocalDateTime("2017-02-10T10:30:00"))
    eval(""" date and time("2017-01-10T10:30:00") + duration("P1Y") """) should be(
      ValLocalDateTime("2018-01-10T10:30:00"))

    eval(""" duration("P1M") + date and time("2017-01-10T10:30:00+01:00") """) should be(
      ValDateTime("2017-02-10T10:30:00+01:00"))
    eval(""" date and time("2017-01-10T10:30:00+01:00") + duration("P1Y") """) should be(
      ValDateTime("2018-01-10T10:30:00+01:00"))
  }

  it should "add to date" in {

    eval(""" duration("P1M") + date("2017-01-10") """) should be(
      ValDate("2017-02-10"))
    eval(""" date("2017-01-10") + duration("P1Y") """) should be(
      ValDate("2018-01-10"))
  }

  it should "subtract from year-month-duration" in {

    eval(""" duration("P1Y") - duration("P3M") """) should be(
      ValYearMonthDuration("P9M"))
    eval(""" duration("P5M") - duration("P6M") """) should be(
      ValYearMonthDuration("P-1M"))
  }

  it should "subtract from date-time" in {

    eval(""" date and time("2017-01-10T10:30:00") - duration("P1M") """) should be(
      ValLocalDateTime("2016-12-10T10:30:00"))

    eval(""" date and time("2017-01-10T10:30:00+01:00") - duration("P1M") """) should be(
      ValDateTime("2016-12-10T10:30:00+01:00"))
  }

  it should "subtract from date" in {

    eval(""" date("2017-01-10") - duration("P1M") """) should be(
      ValDate("2016-12-10"))

    eval(""" date("2017-01-10") - duration("P1Y") """) should be(
      ValDate("2016-01-10"))

    eval(""" date("2017-01-10") - duration("P1Y1M") """) should be(
      ValDate("2015-12-10"))
  }

  it should "subtract date from date" in {

    eval(""" date("2020-04-06") - date("2020-04-01") """) should be(
      ValDayTimeDuration("P5D"))
    eval(""" date("2020-04-06") - date("2020-04-01") """) should be(
      ValDayTimeDuration("PT120H"))
  }

  it should "subtract date from date as string" in {

    eval(""" date("2020-04-07") - date("2020-04-01") """) should be(
      ValDayTimeDuration("PT144H"))
  }

  it should "multiply by '3'" in {

    eval(""" duration("P1M") * 3 """) should be(ValYearMonthDuration("P3M"))
    eval(""" 3 * duration("P2Y") """) should be(ValYearMonthDuration("P6Y"))
  }

  it should "divide by '4'" in {

    eval(""" duration("P1Y") / 2 """) should be(ValYearMonthDuration("P6M"))
  }

  it should "divide by duration" in {

    eval(""" duration("P1Y") / duration("P1M") """) should be(ValNumber(12))
  }

  it should "divide by zero duration" in {

    eval(""" duration("P1Y") / duration("P0M") """) should be(ValNull)
  }

  it should "compare with '='" in {

    eval(""" duration("P2M") = duration("P2M") """) should be(ValBoolean(true))
    eval(""" duration("P2M") = duration("P4M") """) should be(ValBoolean(false))
  }

  it should "compare with '!='" in {

    eval(""" duration("P2M") != duration("P2M") """) should be(
      ValBoolean(false))
    eval(""" duration("P2M") != duration("P1Y") """) should be(ValBoolean(true))
  }

  it should "compare with '<'" in {

    eval(""" duration("P2M") < duration("P3M") """) should be(ValBoolean(true))
    eval(""" duration("P2M") < duration("P2M") """) should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(""" duration("P2M") <= duration("P2M") """) should be(ValBoolean(true))
    eval(""" duration("P1Y2M") <= duration("P2M") """) should be(
      ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(""" duration("P2M") > duration("P2M") """) should be(ValBoolean(false))
    eval(""" duration("P2M") > duration("P1M") """) should be(ValBoolean(true))
  }

  it should "compare with '>='" in {

    eval(""" duration("P2M") >= duration("P2M") """) should be(ValBoolean(true))
    eval(""" duration("P2M") >= duration("P5M") """) should be(
      ValBoolean(false))
  }

  it should "compare with 'between _ and _'" in {

    eval(""" duration("P3M") between duration("P2M") and duration("P6M") """) should be(
      ValBoolean(true))
    eval(""" duration("P1Y") between duration("P2M") and duration("P6M") """) should be(
      ValBoolean(false))
  }

  "A day-time-duration" should "add to day-time-duration" in {

    eval(""" duration("PT4H") + duration("PT2H") """) should be(
      ValDayTimeDuration("PT6H"))
    eval(""" duration("P1D") + duration("PT6H") """) should be(
      ValDayTimeDuration("P1DT6H"))
  }

  it should "add to date-time" in {

    eval(""" duration("PT1H") + date and time("2017-01-10T10:30:00") """) should be(
      ValLocalDateTime("2017-01-10T11:30:00"))
    eval(""" date and time("2017-01-10T10:30:00") + duration("P1D") """) should be(
      ValLocalDateTime("2017-01-11T10:30:00"))

    eval(""" duration("PT1H") + date and time("2017-01-10T10:30:00+01:00") """) should be(
      ValDateTime("2017-01-10T11:30:00+01:00"))
    eval(""" date and time("2017-01-10T10:30:00+01:00") + duration("P1D") """) should be(
      ValDateTime("2017-01-11T10:30:00+01:00"))
  }

  it should "add to date" in {

    eval(""" duration("PT1H") + date("2017-01-10") """) should be(
      ValDate("2017-01-10"))
    eval(""" duration("P1D") + date("2017-01-10") """) should be(
      ValDate("2017-01-11"))
    eval(""" date("2017-01-10") + duration("PT1M") """) should be(
      ValDate("2017-01-10"))
    eval(""" date("2017-01-10") + duration("P1D") """) should be(
      ValDate("2017-01-11"))
  }

  it should "add to time" in {

    eval(""" duration("PT1H") + time("10:30:00") """) should be(
      ValLocalTime("11:30:00"))
    eval(""" time("10:30:00") + duration("P1D") """) should be(
      ValLocalTime("10:30:00"))

    eval(""" duration("PT1H") + time("10:30:00+01:00") """) should be(
      ValTime("11:30:00+01:00"))
    eval(""" time("10:30:00+01:00") + duration("P1D") """) should be(
      ValTime("10:30:00+01:00"))
  }

  it should "subtract from day-time-duration" in {

    eval(""" duration("PT6H") - duration("PT2H") """) should be(
      ValDayTimeDuration("PT4H"))
    eval(""" duration("PT22H") - duration("P1D") """) should be(
      ValDayTimeDuration("PT-2H"))
  }

  it should "subtract from date-time" in {

    eval(""" date and time("2017-01-10T10:30:00") - duration("PT1H") """) should be(
      ValLocalDateTime("2017-01-10T09:30:00"))

    eval(""" date and time("2017-01-10T10:30:00+01:00") - duration("PT1H") """) should be(
      ValDateTime("2017-01-10T09:30:00+01:00"))
  }

  it should "subtract from date" in {

    eval(""" date("2017-01-10") - duration("PT1H") """) should be(
      ValDate("2017-01-09"))

    eval(""" date("2017-01-10") - duration("P1DT1H") """) should be(
      ValDate("2017-01-08"))
  }

  it should "subtract from time" in {

    eval(""" time("10:30:00") - duration("PT1H") """) should be(
      ValLocalTime("09:30:00"))

    eval(""" time("10:30:00+01:00") - duration("PT1H") """) should be(
      ValTime("09:30:00+01:00"))
  }

  it should "multiply by '3'" in {

    eval(""" duration("PT2H") * 3 """) should be(ValDayTimeDuration("PT6H"))
    eval(""" 3 * duration("P1D") """) should be(ValDayTimeDuration("P3D"))
  }

  it should "divide by '4'" in {

    eval(""" duration("P1D") / 4 """) should be(ValDayTimeDuration("PT6H"))
  }

  it should "divide by duration" in {

    eval(""" duration("P1D") / duration("PT1H") """) should be(ValNumber(24))
  }

  it should "divide by zero duration" in {

    eval(""" duration("P1D") / duration("PT0H") """) should be(ValNull)
  }

  it should "compare with '='" in {

    eval(""" duration("PT6H") = duration("PT6H") """) should be(
      ValBoolean(true))
    eval(""" duration("PT6H") = duration("PT2H") """) should be(
      ValBoolean(false))
  }

  it should "compare with '!='" in {

    eval(""" duration("PT6H") != duration("PT6H") """) should be(
      ValBoolean(false))
    eval(""" duration("PT6H") != duration("P1D") """) should be(
      ValBoolean(true))
  }

  it should "compare with '<'" in {

    eval(""" duration("PT6H") < duration("PT12H") """) should be(
      ValBoolean(true))
    eval(""" duration("PT6H") < duration("PT6H") """) should be(
      ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(""" duration("PT6H") <= duration("PT6H") """) should be(
      ValBoolean(true))
    eval(""" duration("PT6H") <= duration("PT1H") """) should be(
      ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(""" duration("PT6H") > duration("PT6H") """) should be(
      ValBoolean(false))
    eval(""" duration("P1D") > duration("PT6H") """) should be(ValBoolean(true))
  }

  it should "compare with '>='" in {

    eval(""" duration("PT6H") >= duration("PT6H") """) should be(
      ValBoolean(true))
    eval(""" duration("PT6H") >= duration("PT6H1M") """) should be(
      ValBoolean(false))
  }

  it should "compare with 'between _ and _'" in {

    eval(
      """ duration("PT8H") between duration("PT6H") and duration("PT12H") """) should be(
      ValBoolean(true))
    eval(
      """ duration("PT2H") between duration("PT6H") and duration("PT12H") """) should be(
      ValBoolean(false))
  }

}
