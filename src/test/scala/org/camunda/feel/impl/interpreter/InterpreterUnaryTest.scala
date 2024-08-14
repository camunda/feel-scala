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

import org.camunda.feel.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree._
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

/** @author
  *   Philipp Ossler
  */
class InterpreterUnaryTest extends AnyFlatSpec with Matchers with FeelIntegrationTest {

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

  it should "be in one of two intervals (disjunction)" in {

    evalUnaryTests(3, "[1..5], [6..10]") should be(ValBoolean(true))
    evalUnaryTests(6, "[1..5], [6..10]") should be(ValBoolean(true))
    evalUnaryTests(11, "[1..5], [6..10]") should be(ValBoolean(false))
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
    evalUnaryTests(3, "< a.b", Map("a" -> new A(2))) should be(ValBoolean(false))
  }

  it should "compare to null" in {
    evalUnaryTests(null, "3") should be(ValBoolean(false))
  }

  it should "compare null with less/greater than" in {
    evalUnaryTests(null, "< 3") should be(ValNull)
    evalUnaryTests(null, "<= 3") should be(ValNull)
    evalUnaryTests(null, "> 3") should be(ValNull)
    evalUnaryTests(null, ">= 3") should be(ValNull)
  }

  it should "compare null with interval" in {
    evalUnaryTests(null, "(0..10)") should be(ValNull)
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

  it should "compare to a boolean comparison (numeric)" in {

    evalUnaryTests(true, "1 < 2") should be(ValBoolean(true))
    evalUnaryTests(true, "2 < 1") should be(ValBoolean(false))
  }

  it should "compare to a boolean comparison (string)" in {

    evalUnaryTests(true, """ "a" = "a" """) should be(ValBoolean(true))
    evalUnaryTests(true, """ "a" = "b" """) should be(ValBoolean(false))
  }

  it should "compare to a conjunction (and)" in {
    // it is uncommon to use a conjunction in a unary-tests but the engine should be able to parse
    evalUnaryTests(true, "true and true") shouldBe ValBoolean(true)
    evalUnaryTests(true, "false and true") shouldBe ValBoolean(false)

    evalUnaryTests(true, "true and null") shouldBe ValBoolean(false)
    evalUnaryTests(true, "false and null") shouldBe ValBoolean(false)

    evalUnaryTests(true, """true and "otherwise" """) shouldBe ValBoolean(false)
    evalUnaryTests(true, """false and "otherwise" """) shouldBe ValBoolean(false)
  }

  it should "compare to a disjunction (or)" in {
    // it is uncommon to use a disjunction in a unary-tests but the engine should be able to parse
    evalUnaryTests(true, "true or true") shouldBe ValBoolean(true)
    evalUnaryTests(true, "false or true") shouldBe ValBoolean(true)
    evalUnaryTests(true, "false or false") shouldBe ValBoolean(false)

    evalUnaryTests(true, "true or null") shouldBe ValBoolean(true)
    evalUnaryTests(true, "false or null") shouldBe ValBoolean(false)

    evalUnaryTests(true, """true or "otherwise" """) shouldBe ValBoolean(true)
    evalUnaryTests(true, """false or "otherwise" """) shouldBe ValBoolean(false)
  }

  "A date" should "compare with '<'" in {

    evalUnaryTests(date("2015-09-17"), """< date("2015-09-18")""") should be(ValBoolean(true))
    evalUnaryTests(date("2015-09-18"), """< date("2015-09-18")""") should be(ValBoolean(false))
    evalUnaryTests(date("2015-09-19"), """< date("2015-09-18")""") should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    evalUnaryTests(date("2015-09-17"), """<= date("2015-09-18")""") should be(ValBoolean(true))
    evalUnaryTests(date("2015-09-18"), """<= date("2015-09-18")""") should be(ValBoolean(true))
    evalUnaryTests(date("2015-09-19"), """<= date("2015-09-18")""") should be(ValBoolean(false))
  }

  it should "compare with '>'" in {

    evalUnaryTests(date("2015-09-17"), """> date("2015-09-18")""") should be(ValBoolean(false))
    evalUnaryTests(date("2015-09-18"), """> date("2015-09-18")""") should be(ValBoolean(false))
    evalUnaryTests(date("2015-09-19"), """> date("2015-09-18")""") should be(ValBoolean(true))
  }

  it should "compare with '>='" in {

    evalUnaryTests(date("2015-09-17"), """>= date("2015-09-18")""") should be(ValBoolean(false))
    evalUnaryTests(date("2015-09-18"), """>= date("2015-09-18")""") should be(ValBoolean(true))
    evalUnaryTests(date("2015-09-19"), """>= date("2015-09-18")""") should be(ValBoolean(true))
  }

  it should "be equal to another date" in {

    evalUnaryTests(date("2015-09-17"), """date("2015-09-18")""") should be(ValBoolean(false))
    evalUnaryTests(date("2015-09-18"), """date("2015-09-18")""") should be(ValBoolean(true))
  }

  it should """be in interval '(date("2015-09-17")..date("2015-09-19")]'""" in {

    evalUnaryTests(date("2015-09-17"), """(date("2015-09-17")..date("2015-09-19"))""") should be(
      ValBoolean(false)
    )
    evalUnaryTests(date("2015-09-18"), """(date("2015-09-17")..date("2015-09-19"))""") should be(
      ValBoolean(true)
    )
    evalUnaryTests(date("2015-09-19"), """(date("2015-09-17")..date("2015-09-19"))""") should be(
      ValBoolean(false)
    )
  }

  it should """be in interval '[date("2015-09-17")..date("2015-09-19")]'""" in {

    evalUnaryTests(date("2015-09-17"), """[date("2015-09-17")..date("2015-09-19")]""") should be(
      ValBoolean(true)
    )
    evalUnaryTests(date("2015-09-18"), """[date("2015-09-17")..date("2015-09-19")]""") should be(
      ValBoolean(true)
    )
    evalUnaryTests(date("2015-09-19"), """[date("2015-09-17")..date("2015-09-19")]""") should be(
      ValBoolean(true)
    )
  }

  "A time" should "compare with '<'" in {

    evalUnaryTests(localTime("08:31:14"), """< time("10:00:00")""") should be(ValBoolean(true))
    evalUnaryTests(localTime("10:10:00"), """< time("10:00:00")""") should be(ValBoolean(false))
    evalUnaryTests(localTime("11:31:14"), """< time("10:00:00")""") should be(ValBoolean(false))

    evalUnaryTests(time("10:00:00+01:00"), """< time("11:00:00+01:00")""") should be(
      ValBoolean(true)
    )
    evalUnaryTests(time("10:00:00+01:00"), """< time("10:00:00+01:00")""") should be(
      ValBoolean(false)
    )
  }

  it should "be equal to another time" in {

    evalUnaryTests(localTime("08:31:14"), """time("10:00:00")""") should be(ValBoolean(false))
    evalUnaryTests(localTime("08:31:14"), """time("08:31:14")""") should be(ValBoolean(true))

    evalUnaryTests(time("10:00:00+01:00"), """time("10:00:00+02:00")""") should be(
      ValBoolean(false)
    )
    evalUnaryTests(time("10:00:00+01:00"), """time("11:00:00+02:00")""") should be(
      ValBoolean(false)
    )
    evalUnaryTests(time("10:00:00+01:00"), """time("10:00:00+01:00")""") should be(ValBoolean(true))
  }

  it should """be in interval '[time("08:00:00")..time("10:00:00")]'""" in {

    evalUnaryTests(localTime("07:45:10"), """[time("08:00:00")..time("10:00:00")]""") should be(
      ValBoolean(false)
    )
    evalUnaryTests(localTime("09:15:20"), """[time("08:00:00")..time("10:00:00")]""") should be(
      ValBoolean(true)
    )
    evalUnaryTests(localTime("11:30:30"), """[time("08:00:00")..time("10:00:00")]""") should be(
      ValBoolean(false)
    )

    evalUnaryTests(
      time("11:30:00+01:00"),
      """[time("08:00:00+01:00")..time("10:00:00+01:00")]"""
    ) should be(ValBoolean(false))
    evalUnaryTests(
      time("09:30:00+01:00"),
      """[time("08:00:00+01:00")..time("10:00:00+01:00")]"""
    ) should be(ValBoolean(true))
  }

  "A date-time" should "compare with '<'" in {

    evalUnaryTests(
      localDateTime("2015-09-17T08:31:14"),
      """< date and time("2015-09-17T10:00:00")"""
    ) should be(ValBoolean(true))
    evalUnaryTests(
      localDateTime("2015-09-17T10:10:00"),
      """< date and time("2015-09-17T10:00:00")"""
    ) should be(ValBoolean(false))
    evalUnaryTests(
      localDateTime("2015-09-17T11:31:14"),
      """< date and time("2015-09-17T10:00:00")"""
    ) should be(ValBoolean(false))

    evalUnaryTests(
      dateTime("2015-09-17T10:00:00+01:00"),
      """< date and time("2015-09-17T12:00:00+01:00")"""
    ) should be(ValBoolean(true))
    evalUnaryTests(
      dateTime("2015-09-17T10:00:00+01:00"),
      """< date and time("2015-09-17T09:00:00+01:00")"""
    ) should be(ValBoolean(false))
  }

  it should "be equal to another date-time" in {

    evalUnaryTests(
      localDateTime("2015-09-17T08:31:14"),
      """date and time("2015-09-17T10:00:00")"""
    ) should be(ValBoolean(false))
    evalUnaryTests(
      localDateTime("2015-09-17T08:31:14"),
      """date and time("2015-09-17T08:31:14")"""
    ) should be(ValBoolean(true))

    evalUnaryTests(
      dateTime("2015-09-17T08:30:00+01:00"),
      """date and time("2015-09-17T09:30:00+01:00")"""
    ) should be(ValBoolean(false))
    evalUnaryTests(
      dateTime("2015-09-17T08:30:00+01:00"),
      """date and time("2015-09-17T08:30:00+02:00")"""
    ) should be(ValBoolean(false))
    evalUnaryTests(
      dateTime("2015-09-17T08:30:00+01:00"),
      """date and time("2015-09-17T08:30:00+01:00")"""
    ) should be(ValBoolean(true))
  }

  it should """be in interval '[dante and time("2015-09-17T08:00:00")..date and time("2015-09-17T10:00:00")]'""" in {

    evalUnaryTests(
      localDateTime("2015-09-17T07:45:10"),
      """[date and time("2015-09-17T08:00:00")..date and time("2015-09-17T10:00:00")]"""
    ) should be(ValBoolean(false))
    evalUnaryTests(
      localDateTime("2015-09-17T09:15:20"),
      """[date and time("2015-09-17T08:00:00")..date and time("2015-09-17T10:00:00")]"""
    ) should be(ValBoolean(true))
    evalUnaryTests(
      localDateTime("2015-09-17T11:30:30"),
      """[date and time("2015-09-17T08:00:00")..date and time("2015-09-17T10:00:00")]"""
    ) should be(ValBoolean(false))

    evalUnaryTests(
      dateTime("2015-09-17T08:30:00+01:00"),
      """[date and time("2015-09-17T09:00:00+01:00")..date and time("2015-09-17T10:00:00+01:00")]"""
    ) should be(ValBoolean(false))
    evalUnaryTests(
      dateTime("2015-09-17T08:30:00+01:00"),
      """[date and time("2015-09-17T08:00:00+01:00")..date and time("2015-09-17T10:00:00+01:00")]"""
    ) should be(ValBoolean(true))
  }

  "A year-month-duration" should "compare with '<'" in {

    evalUnaryTests(yearMonthDuration("P1Y"), """< duration("P2Y")""") should be(ValBoolean(true))
    evalUnaryTests(yearMonthDuration("P1Y"), """< duration("P1Y")""") should be(ValBoolean(false))
    evalUnaryTests(yearMonthDuration("P1Y2M"), """< duration("P1Y")""") should be(ValBoolean(false))
  }

  it should "be equal to another duration" in {

    evalUnaryTests(yearMonthDuration("P1Y4M"), """duration("P1Y3M")""") should be(ValBoolean(false))
    evalUnaryTests(yearMonthDuration("P1Y4M"), """duration("P1Y4M")""") should be(ValBoolean(true))
  }

  it should """be in interval '[duration("P1Y")..duration("P2Y")]'""" in {

    evalUnaryTests(yearMonthDuration("P6M"), """[duration("P1Y")..duration("P2Y")]""") should be(
      ValBoolean(false)
    )
    evalUnaryTests(yearMonthDuration("P1Y8M"), """[duration("P1Y")..duration("P2Y")]""") should be(
      ValBoolean(true)
    )
    evalUnaryTests(yearMonthDuration("P2Y1M"), """[duration("P1Y")..duration("P2Y")]""") should be(
      ValBoolean(false)
    )
  }

  "A day-time-duration" should "compare with '<'" in {

    evalUnaryTests(dayTimeDuration("P1DT4H"), """< duration("P2DT4H")""") should be(
      ValBoolean(true)
    )
    evalUnaryTests(dayTimeDuration("P2DT4H"), """< duration("P2DT4H")""") should be(
      ValBoolean(false)
    )
    evalUnaryTests(dayTimeDuration("P2DT8H"), """< duration("P2DT4H")""") should be(
      ValBoolean(false)
    )
  }

  it should "be equal to another duration" in {

    evalUnaryTests(dayTimeDuration("P1DT4H"), """duration("P2DT4H")""") should be(ValBoolean(false))
    evalUnaryTests(dayTimeDuration("P2DT4H"), """duration("P2DT4H")""") should be(ValBoolean(true))
  }

  it should """be in interval '[duration("P1D")..duration("P2D")]'""" in {

    evalUnaryTests(dayTimeDuration("PT4H"), """[duration("P1D")..duration("P2D")]""") should be(
      ValBoolean(false)
    )
    evalUnaryTests(dayTimeDuration("P1DT4H"), """[duration("P1D")..duration("P2D")]""") should be(
      ValBoolean(true)
    )
    evalUnaryTests(dayTimeDuration("P2DT4H"), """[duration("P1D")..duration("P2D")]""") should be(
      ValBoolean(false)
    )
  }

  "A list" should "be equal to another list" in {

    evalUnaryTests(List.empty, "[]") should be(ValBoolean(true))
    evalUnaryTests(List(1, 2), "[1,2]") should be(ValBoolean(true))

    evalUnaryTests(List(1, 2), "[]") should be(ValBoolean(false))
    evalUnaryTests(List(1, 2), "[1]") should be(ValBoolean(false))
    evalUnaryTests(List(1, 2), "[2,1]") should be(ValBoolean(false))
    evalUnaryTests(List(1, 2), "[1,2,3]") should be(ValBoolean(false))
  }

  it should "be checked in an every expression" in {
    evalUnaryTests(List(1, 2, 3), "every x in ? satisfies x > 3") should be(ValBoolean(false))
    evalUnaryTests(List(4, 5, 6), "every x in ? satisfies x > 3") should be(ValBoolean(true))
  }

  it should "be checked in a some expression" in {
    evalUnaryTests(List(1, 2, 3), "some x in ? satisfies x > 4") should be(ValBoolean(false))
    evalUnaryTests(List(4, 5, 6), "some x in ? satisfies x > 4") should be(ValBoolean(true))
  }

  "A context" should "be equal to another context" in {

    evalUnaryTests(Map.empty, "{}") should be(ValBoolean(true))
    evalUnaryTests(Map("x" -> 1), "{x:1}") should be(ValBoolean(true))

    evalUnaryTests(Map("x" -> 1), "{}") should be(ValBoolean(false))
    evalUnaryTests(Map("x" -> 1), "{x:2}") should be(ValBoolean(false))
    evalUnaryTests(Map("x" -> 1), "{y:1}") should be(ValBoolean(false))
    evalUnaryTests(Map("x" -> 1), "{x:1,y:2}") should be(ValBoolean(false))
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

    evalUnaryTests("foo", """ starts with(?, "f") """) should be(ValBoolean(true))
    evalUnaryTests("foo", """ starts with(?, "b") """) should be(ValBoolean(false))
  }

  it should "be invoked as endpoint" in {

    evalUnaryTests(2, "< max(1,2,3)") should be(ValBoolean(true))
    evalUnaryTests(2, "< min(1,2,3)") should be(ValBoolean(false))
  }

  "A unary-tests expression" should "return true if it evaluates to a value that is equal to the implicit value" in {

    evalUnaryTests(5, "5") should be(ValBoolean(true))
    evalUnaryTests(5, "2 + 3") should be(ValBoolean(true))
    evalUnaryTests(5, "x", Map("x" -> 5)) should be(ValBoolean(true))
  }

  it should "return false if it evaluates to a value that is not equal to the implicit value" in {

    evalUnaryTests(5, "3") should be(ValBoolean(false))
    evalUnaryTests(5, "1 + 2") should be(ValBoolean(false))
    evalUnaryTests(5, "x", Map("x" -> 3)) should be(ValBoolean(false))
  }

  it should "return null if it evaluates to a value that has a different type than the implicit value" in {

    evalUnaryTests(5, """ @"2024-08-19" """) should be(ValNull)
  }

  it should "return true if it evaluates to a list that contains the implicit value" in {

    evalUnaryTests(5, "[4,5,6]") should be(ValBoolean(true))
    evalUnaryTests(5, "concatenate([1,2,3], [4,5,6])") should be(ValBoolean(true))
    evalUnaryTests(5, "x", Map("x" -> List(4, 5, 6))) should be(ValBoolean(true))
  }

  it should "return false if it evaluates to a list that doesn't contain the implicit value" in {

    evalUnaryTests(5, "[1,2,3]") should be(ValBoolean(false))
    evalUnaryTests(5, "concatenate([1,2], [3])") should be(ValBoolean(false))
    evalUnaryTests(5, "x", Map("x" -> List(1, 2, 3))) should be(ValBoolean(false))
  }

  it should "return true if it evaluates to true when the implicit value is applied to it" in {

    evalUnaryTests(5, "< 10") should be(ValBoolean(true))
    evalUnaryTests(5, "[1..10]") should be(ValBoolean(true))
    evalUnaryTests(5, "> x", Map("x" -> 3)) should be(ValBoolean(true))
  }

  it should "return false if it evaluates to false when the implicit value is applied to it" in {

    evalUnaryTests(5, "< 3") should be(ValBoolean(false))
    evalUnaryTests(5, "[1..3]") should be(ValBoolean(false))
    evalUnaryTests(5, "> x", Map("x" -> 10)) should be(ValBoolean(false))
  }

  it should "return null if it evaluates to null when the implicit value is applied to it" in {

    evalUnaryTests(5, """ < @"2024-08-19" """) should be(ValNull)
    evalUnaryTests(null, """ < @"2024-08-19" """) should be(ValNull)
  }

  it should "return true if it evaluates to true when the implicit value is assigned to the special variable '?'" in {

    evalUnaryTests(5, "odd(?)") should be(ValBoolean(true))
    evalUnaryTests(5, "abs(?) < 10") should be(ValBoolean(true))
    evalUnaryTests(5, "? > x", Map("x" -> 3)) should be(ValBoolean(true))
  }

  it should "return false if it evaluates to false when the implicit value is assigned to the special variable '?'" in {

    evalUnaryTests(5, "even(?)") should be(ValBoolean(false))
    evalUnaryTests(5, "abs(?) < 3") should be(ValBoolean(false))
    evalUnaryTests(5, "? > x", Map("x" -> 10)) should be(ValBoolean(false))
  }

  it should "return null if it evaluates to a value that is not a boolean when the implicit value is assigned to the special variable '?'" in {

    evalUnaryTests(5, "abs(?)") should be(ValNull)
    evalUnaryTests(5, "?") should be(ValNull)
    evalUnaryTests(5, "? + not_existing") should be(ValNull)
  }

  it should "return true if it evaluates to null and the implicit value is null" in {

    evalUnaryTests(null, "null") should be(ValBoolean(true))
    evalUnaryTests(null, "2 + not_existing") should be(ValBoolean(true))
    evalUnaryTests(null, "not_existing") should be(ValBoolean(true))
  }

  it should "return false if it evaluates to null and the implicit value is not null" in {

    evalUnaryTests(5, "null") should be(ValBoolean(false))
    evalUnaryTests(5, "2 + not_existing") should be(ValBoolean(false))
    evalUnaryTests(5, "not_existing") should be(ValBoolean(false))
  }

  it should "return true if it evaluates to true when null is assigned to the special variable '?'" in {

    evalUnaryTests(null, "? = null") should be(ValBoolean(true))
    evalUnaryTests(null, "? = null or odd(?)") should be(ValBoolean(true))
  }

  it should "return false if it evaluates to false when null is assigned to the special variable '?'" in {

    evalUnaryTests(null, "? != null") should be(ValBoolean(false))
    evalUnaryTests(null, "? != null and odd(?)") should be(ValBoolean(false))
  }

  it should "return null if it evaluates to null when null is assigned to the special variable '?'" in {

    evalUnaryTests(null, "? < 10") should be(ValNull)
    evalUnaryTests(null, "odd(?)") should be(ValNull)
    evalUnaryTests(null, "5 < ? and ? < 10") should be(ValNull)
    evalUnaryTests(null, "5 < ? or ? < 10") should be(ValNull)
  }

}
