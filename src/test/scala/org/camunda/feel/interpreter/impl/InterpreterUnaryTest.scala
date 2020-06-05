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
package org.camunda.feel.interpreter.impl

import org.scalatest.{FlatSpec, Matchers}
import org.camunda.feel.syntaxtree._

/**
  * @author Philipp Ossler
  */
class InterpreterUnaryTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A number" should "compare with '<'" in {

    evalUnaryTests(2, "< 3") should be(ValBoolean(true))
    evalUnaryTests(3, "< 3") should be(ValBoolean(false))
    evalUnaryTests(4, "< 3") should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    evalUnaryTests(2, "<= 3") should be(ValBoolean(true))
    evalUnaryTests(3, "<= 3") should be(ValBoolean(true))
    evalUnaryTests(4, "<= 3") should be(ValBoolean(false))
  }

  it should "compare with '>'" in {

    evalUnaryTests(2, "> 3") should be(ValBoolean(false))
    evalUnaryTests(3, "> 3") should be(ValBoolean(false))
    evalUnaryTests(4, "> 3") should be(ValBoolean(true))
  }

  it should "compare with '>='" in {

    evalUnaryTests(2, ">= 3") should be(ValBoolean(false))
    evalUnaryTests(3, ">= 3") should be(ValBoolean(true))
    evalUnaryTests(4, ">= 3") should be(ValBoolean(true))
  }

  it should "be equal to another number" in {

    evalUnaryTests(2, "3") should be(ValBoolean(false))
    evalUnaryTests(3, "3") should be(ValBoolean(true))

    evalUnaryTests(-1, "-1") should be(ValBoolean(true))
    evalUnaryTests(0, "-1") should be(ValBoolean(false))
  }

  it should "be in interval '(2..4)'" in {

    evalUnaryTests(2, "(2..4)") should be(ValBoolean(false))
    evalUnaryTests(3, "(2..4)") should be(ValBoolean(true))
    evalUnaryTests(4, "(2..4)") should be(ValBoolean(false))
  }

  it should "be in interval '[2..4]'" in {

    evalUnaryTests(2, "[2..4]") should be(ValBoolean(true))
    evalUnaryTests(3, "[2..4]") should be(ValBoolean(true))
    evalUnaryTests(4, "[2..4]") should be(ValBoolean(true))
  }

  it should "be in '2,3'" in {

    evalUnaryTests(2, "2,3") should be(ValBoolean(true))
    evalUnaryTests(3, "2,3") should be(ValBoolean(true))
    evalUnaryTests(4, "2,3") should be(ValBoolean(false))
  }

  it should "be not equal 'not(3)'" in {

    evalUnaryTests(2, "not(3)") should be(ValBoolean(true))
    evalUnaryTests(3, "not(3)") should be(ValBoolean(false))
    evalUnaryTests(4, "not(3)") should be(ValBoolean(true))
  }

  it should "be not in 'not(2,3)'" in {

    evalUnaryTests(2, "not(2,3)") should be(ValBoolean(false))
    evalUnaryTests(3, "not(2,3)") should be(ValBoolean(false))
    evalUnaryTests(4, "not(2,3)") should be(ValBoolean(true))
  }

  it should "compare to a variable (qualified name)" in {

    evalUnaryTests(2, "var", Map("var" -> 3)) should be(ValBoolean(false))
    evalUnaryTests(3, "var", Map("var" -> 3)) should be(ValBoolean(true))

    evalUnaryTests(2, "< var", Map("var" -> 3)) should be(ValBoolean(true))
    evalUnaryTests(3, "< var", Map("var" -> 3)) should be(ValBoolean(false))
  }

  it should "compare to a field of a bean" in {

    class A(val b: Int)

    evalUnaryTests(3, "a.b", Map("a" -> new A(3))) should be(ValBoolean(true))
    evalUnaryTests(3, "a.b", Map("a" -> new A(4))) should be(ValBoolean(false))

    evalUnaryTests(3, "< a.b", Map("a" -> new A(4))) should be(ValBoolean(true))
    evalUnaryTests(3, "< a.b", Map("a" -> new A(2))) should be(
      ValBoolean(false))
  }

  it should "compare to null" in {

    evalUnaryTests(null, "3") should be(ValBoolean(false))
    evalUnaryTests(null, "< 3") should be(ValBoolean(false))
    evalUnaryTests(null, "> 3") should be(ValBoolean(false))
    evalUnaryTests(null, "(0..10)") should be(ValBoolean(false))
  }

  "A string" should "be equal to another string" in {

    evalUnaryTests("a", """ "b" """) should be(ValBoolean(false))
    evalUnaryTests("b", """ "b" """) should be(ValBoolean(true))
  }

  it should "compare to null" in {

    evalUnaryTests(null, """ "a" """) should be(ValBoolean(false))
  }

  it should """be in '"a","b"' """ in {

    evalUnaryTests("a", """ "a","b" """) should be(ValBoolean(true))
    evalUnaryTests("b", """ "a","b" """) should be(ValBoolean(true))
    evalUnaryTests("c", """ "a","b" """) should be(ValBoolean(false))
  }

  "A boolean" should "be equal to another boolean" in {

    evalUnaryTests(false, "true") should be(ValBoolean(false))
    evalUnaryTests(true, "false") should be(ValBoolean(false))

    evalUnaryTests(false, "false") should be(ValBoolean(true))
    evalUnaryTests(true, "true") should be(ValBoolean(true))
  }

  it should "compare to null" in {

    evalUnaryTests(null, "true") should be(ValBoolean(false))
    evalUnaryTests(null, "false") should be(ValBoolean(false))
  }

  "A date" should "compare with '<'" in {

    evalUnaryTests(date("2015-09-17"), """< date("2015-09-18")""") should be(
      ValBoolean(true))
    evalUnaryTests(date("2015-09-18"), """< date("2015-09-18")""") should be(
      ValBoolean(false))
    evalUnaryTests(date("2015-09-19"), """< date("2015-09-18")""") should be(
      ValBoolean(false))
  }

  it should "compare with '<='" in {

    evalUnaryTests(date("2015-09-17"), """<= date("2015-09-18")""") should be(
      ValBoolean(true))
    evalUnaryTests(date("2015-09-18"), """<= date("2015-09-18")""") should be(
      ValBoolean(true))
    evalUnaryTests(date("2015-09-19"), """<= date("2015-09-18")""") should be(
      ValBoolean(false))
  }

  it should "compare with '>'" in {

    evalUnaryTests(date("2015-09-17"), """> date("2015-09-18")""") should be(
      ValBoolean(false))
    evalUnaryTests(date("2015-09-18"), """> date("2015-09-18")""") should be(
      ValBoolean(false))
    evalUnaryTests(date("2015-09-19"), """> date("2015-09-18")""") should be(
      ValBoolean(true))
  }

  it should "compare with '>='" in {

    evalUnaryTests(date("2015-09-17"), """>= date("2015-09-18")""") should be(
      ValBoolean(false))
    evalUnaryTests(date("2015-09-18"), """>= date("2015-09-18")""") should be(
      ValBoolean(true))
    evalUnaryTests(date("2015-09-19"), """>= date("2015-09-18")""") should be(
      ValBoolean(true))
  }

  it should "be equal to another date" in {

    evalUnaryTests(date("2015-09-17"), """date("2015-09-18")""") should be(
      ValBoolean(false))
    evalUnaryTests(date("2015-09-18"), """date("2015-09-18")""") should be(
      ValBoolean(true))
  }

  it should """be in interval '(date("2015-09-17")..date("2015-09-19")]'""" in {

    evalUnaryTests(date("2015-09-17"),
                   """(date("2015-09-17")..date("2015-09-19"))""") should be(
      ValBoolean(false))
    evalUnaryTests(date("2015-09-18"),
                   """(date("2015-09-17")..date("2015-09-19"))""") should be(
      ValBoolean(true))
    evalUnaryTests(date("2015-09-19"),
                   """(date("2015-09-17")..date("2015-09-19"))""") should be(
      ValBoolean(false))
  }

  it should """be in interval '[date("2015-09-17")..date("2015-09-19")]'""" in {

    evalUnaryTests(date("2015-09-17"),
                   """[date("2015-09-17")..date("2015-09-19")]""") should be(
      ValBoolean(true))
    evalUnaryTests(date("2015-09-18"),
                   """[date("2015-09-17")..date("2015-09-19")]""") should be(
      ValBoolean(true))
    evalUnaryTests(date("2015-09-19"),
                   """[date("2015-09-17")..date("2015-09-19")]""") should be(
      ValBoolean(true))
  }

  "A time" should "compare with '<'" in {

    evalUnaryTests(localTime("08:31:14"), """< time("10:00:00")""") should be(
      ValBoolean(true))
    evalUnaryTests(localTime("10:10:00"), """< time("10:00:00")""") should be(
      ValBoolean(false))
    evalUnaryTests(localTime("11:31:14"), """< time("10:00:00")""") should be(
      ValBoolean(false))

    evalUnaryTests(time("10:00:00+01:00"), """< time("11:00:00+01:00")""") should be(
      ValBoolean(true))
    evalUnaryTests(time("10:00:00+01:00"), """< time("10:00:00+01:00")""") should be(
      ValBoolean(false))
  }

  it should "be equal to another time" in {

    evalUnaryTests(localTime("08:31:14"), """time("10:00:00")""") should be(
      ValBoolean(false))
    evalUnaryTests(localTime("08:31:14"), """time("08:31:14")""") should be(
      ValBoolean(true))

    evalUnaryTests(time("10:00:00+01:00"), """time("10:00:00+02:00")""") should be(
      ValBoolean(false))
    evalUnaryTests(time("10:00:00+01:00"), """time("11:00:00+02:00")""") should be(
      ValBoolean(false))
    evalUnaryTests(time("10:00:00+01:00"), """time("10:00:00+01:00")""") should be(
      ValBoolean(true))
  }

  it should """be in interval '[time("08:00:00")..time("10:00:00")]'""" in {

    evalUnaryTests(localTime("07:45:10"),
                   """[time("08:00:00")..time("10:00:00")]""") should be(
      ValBoolean(false))
    evalUnaryTests(localTime("09:15:20"),
                   """[time("08:00:00")..time("10:00:00")]""") should be(
      ValBoolean(true))
    evalUnaryTests(localTime("11:30:30"),
                   """[time("08:00:00")..time("10:00:00")]""") should be(
      ValBoolean(false))

    evalUnaryTests(
      time("11:30:00+01:00"),
      """[time("08:00:00+01:00")..time("10:00:00+01:00")]""") should be(
      ValBoolean(false))
    evalUnaryTests(
      time("09:30:00+01:00"),
      """[time("08:00:00+01:00")..time("10:00:00+01:00")]""") should be(
      ValBoolean(true))
  }

  "A date-time" should "compare with '<'" in {

    evalUnaryTests(localDateTime("2015-09-17T08:31:14"),
                   """< date and time("2015-09-17T10:00:00")""") should be(
      ValBoolean(true))
    evalUnaryTests(localDateTime("2015-09-17T10:10:00"),
                   """< date and time("2015-09-17T10:00:00")""") should be(
      ValBoolean(false))
    evalUnaryTests(localDateTime("2015-09-17T11:31:14"),
                   """< date and time("2015-09-17T10:00:00")""") should be(
      ValBoolean(false))

    evalUnaryTests(
      dateTime("2015-09-17T10:00:00+01:00"),
      """< date and time("2015-09-17T12:00:00+01:00")""") should be(
      ValBoolean(true))
    evalUnaryTests(
      dateTime("2015-09-17T10:00:00+01:00"),
      """< date and time("2015-09-17T09:00:00+01:00")""") should be(
      ValBoolean(false))
  }

  it should "be equal to another date-time" in {

    evalUnaryTests(localDateTime("2015-09-17T08:31:14"),
                   """date and time("2015-09-17T10:00:00")""") should be(
      ValBoolean(false))
    evalUnaryTests(localDateTime("2015-09-17T08:31:14"),
                   """date and time("2015-09-17T08:31:14")""") should be(
      ValBoolean(true))

    evalUnaryTests(dateTime("2015-09-17T08:30:00+01:00"),
                   """date and time("2015-09-17T09:30:00+01:00")""") should be(
      ValBoolean(false))
    evalUnaryTests(dateTime("2015-09-17T08:30:00+01:00"),
                   """date and time("2015-09-17T08:30:00+02:00")""") should be(
      ValBoolean(false))
    evalUnaryTests(dateTime("2015-09-17T08:30:00+01:00"),
                   """date and time("2015-09-17T08:30:00+01:00")""") should be(
      ValBoolean(true))
  }

  it should """be in interval '[dante and time("2015-09-17T08:00:00")..date and time("2015-09-17T10:00:00")]'""" in {

    evalUnaryTests(
      localDateTime("2015-09-17T07:45:10"),
      """[date and time("2015-09-17T08:00:00")..date and time("2015-09-17T10:00:00")]""") should be(
      ValBoolean(false))
    evalUnaryTests(
      localDateTime("2015-09-17T09:15:20"),
      """[date and time("2015-09-17T08:00:00")..date and time("2015-09-17T10:00:00")]""") should be(
      ValBoolean(true))
    evalUnaryTests(
      localDateTime("2015-09-17T11:30:30"),
      """[date and time("2015-09-17T08:00:00")..date and time("2015-09-17T10:00:00")]""") should be(
      ValBoolean(false))

    evalUnaryTests(
      dateTime("2015-09-17T08:30:00+01:00"),
      """[date and time("2015-09-17T09:00:00+01:00")..date and time("2015-09-17T10:00:00+01:00")]""") should be(
      ValBoolean(false))
    evalUnaryTests(
      dateTime("2015-09-17T08:30:00+01:00"),
      """[date and time("2015-09-17T08:00:00+01:00")..date and time("2015-09-17T10:00:00+01:00")]""") should be(
      ValBoolean(true))
  }

  "A year-month-duration" should "compare with '<'" in {

    evalUnaryTests(yearMonthDuration("P1Y"), """< duration("P2Y")""") should be(
      ValBoolean(true))
    evalUnaryTests(yearMonthDuration("P1Y"), """< duration("P1Y")""") should be(
      ValBoolean(false))
    evalUnaryTests(yearMonthDuration("P1Y2M"), """< duration("P1Y")""") should be(
      ValBoolean(false))
  }

  it should "be equal to another duration" in {

    evalUnaryTests(yearMonthDuration("P1Y4M"), """duration("P1Y3M")""") should be(
      ValBoolean(false))
    evalUnaryTests(yearMonthDuration("P1Y4M"), """duration("P1Y4M")""") should be(
      ValBoolean(true))
  }

  it should """be in interval '[duration("P1Y")..duration("P2Y")]'""" in {

    evalUnaryTests(yearMonthDuration("P6M"),
                   """[duration("P1Y")..duration("P2Y")]""") should be(
      ValBoolean(false))
    evalUnaryTests(yearMonthDuration("P1Y8M"),
                   """[duration("P1Y")..duration("P2Y")]""") should be(
      ValBoolean(true))
    evalUnaryTests(yearMonthDuration("P2Y1M"),
                   """[duration("P1Y")..duration("P2Y")]""") should be(
      ValBoolean(false))
  }

  "A day-time-duration" should "compare with '<'" in {

    evalUnaryTests(dayTimeDuration("P1DT4H"), """< duration("P2DT4H")""") should be(
      ValBoolean(true))
    evalUnaryTests(dayTimeDuration("P2DT4H"), """< duration("P2DT4H")""") should be(
      ValBoolean(false))
    evalUnaryTests(dayTimeDuration("P2DT8H"), """< duration("P2DT4H")""") should be(
      ValBoolean(false))
  }

  it should "be equal to another duration" in {

    evalUnaryTests(dayTimeDuration("P1DT4H"), """duration("P2DT4H")""") should be(
      ValBoolean(false))
    evalUnaryTests(dayTimeDuration("P2DT4H"), """duration("P2DT4H")""") should be(
      ValBoolean(true))
  }

  it should """be in interval '[duration("P1D")..duration("P2D")]'""" in {

    evalUnaryTests(dayTimeDuration("PT4H"),
                   """[duration("P1D")..duration("P2D")]""") should be(
      ValBoolean(false))
    evalUnaryTests(dayTimeDuration("P1DT4H"),
                   """[duration("P1D")..duration("P2D")]""") should be(
      ValBoolean(true))
    evalUnaryTests(dayTimeDuration("P2DT4H"),
                   """[duration("P1D")..duration("P2D")]""") should be(
      ValBoolean(false))
  }

  "An empty expression ('-')" should "be always true" in {

    evalUnaryTests(None, "-") should be(ValBoolean(true))
  }

  "A null expression" should "compare to null" in {

    evalUnaryTests(1, "null") should be(ValBoolean(false))
    evalUnaryTests(true, "null") should be(ValBoolean(false))
    evalUnaryTests("a", "null") should be(ValBoolean(false))

    evalUnaryTests(null, "null") should be(ValBoolean(true))
  }

  "A function" should "be invoked with ? (input value)" in {

    evalUnaryTests("foo", """ starts with(?, "f") """) should be(
      ValBoolean(true))
    evalUnaryTests("foo", """ starts with(?, "b") """) should be(
      ValBoolean(false))
  }

  it should "be invoked as endpoint" in {

    evalUnaryTests(2, "< max(1,2,3)") should be(ValBoolean(true))
    evalUnaryTests(2, "< min(1,2,3)") should be(ValBoolean(false))
  }

  "An expression" should "be compared with equals" in {

    evalUnaryTests(2, """number("2")""") should be(ValBoolean(true))
  }

  it should "be compared with a boolean" in {

    evalUnaryTests(false, """(5 < 4)""") should be(ValBoolean(true))
    evalUnaryTests(true, """(5 < 4)""") should be(ValBoolean(false))
  }

  it should "be compared to literal" in {

    evalUnaryTests(date("2019-08-12"),
                   """ date(now) """,
                   Map("now" -> "2019-08-12")) should be(ValBoolean(true))

    evalUnaryTests(date("2019-08-12"),
                   """ date(now) """,
                   Map("now" -> "2019-08-13")) should be(ValBoolean(false))
  }

  it should "be compared with a list" in {

    evalUnaryTests(2, """[1,2,3]""") should be(ValBoolean(true))
    evalUnaryTests(4, """[1,2,3]""") should be(ValBoolean(false))
  }

}
