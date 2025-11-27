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

import org.camunda.feel.impl.{EvaluationResultMatchers, FeelEngineTest}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** @author
  *   Philipp Ossler
  */
class InterpreterUnaryTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

  "A number" should "compare with '<'" in {

    evaluateUnaryTests("< 3", 2) should returnResult(true)
    evaluateUnaryTests("< 3", 3) should returnResult(false)
    evaluateUnaryTests("< 3", 4) should returnResult(false)
  }

  it should "compare with '<='" in {

    evaluateUnaryTests("<= 3", 2) should returnResult(true)
    evaluateUnaryTests("<= 3", 3) should returnResult(true)
    evaluateUnaryTests("<= 3", 4) should returnResult(false)
  }

  it should "compare with '>'" in {

    evaluateUnaryTests("> 3", 2) should returnResult(false)
    evaluateUnaryTests("> 3", 3) should returnResult(false)
    evaluateUnaryTests("> 3", 4) should returnResult(true)
  }

  it should "compare with '>='" in {

    evaluateUnaryTests(">= 3", 2) should returnResult(false)
    evaluateUnaryTests(">= 3", 3) should returnResult(true)
    evaluateUnaryTests(">= 3", 4) should returnResult(true)
  }

  it should "be equal to another number" in {

    evaluateUnaryTests("3", 2) should returnResult(false)
    evaluateUnaryTests("3", 3) should returnResult(true)

    evaluateUnaryTests("-1", -1) should returnResult(true)
    evaluateUnaryTests("-1", 0) should returnResult(false)
  }

  it should "be in interval '(2..4)'" in {

    evaluateUnaryTests("(2..4)", 2) should returnResult(false)
    evaluateUnaryTests("(2..4)", 3) should returnResult(true)
    evaluateUnaryTests("(2..4)", 4) should returnResult(false)
  }

  it should "be in interval '[2..4]'" in {

    evaluateUnaryTests("[2..4]", 2) should returnResult(true)
    evaluateUnaryTests("[2..4]", 3) should returnResult(true)
    evaluateUnaryTests("[2..4]", 4) should returnResult(true)
  }

  it should "be in one of two intervals (disjunction)" in {

    evaluateUnaryTests("[1..5], [6..10]", 3) should returnResult(true)
    evaluateUnaryTests("[1..5], [6..10]", 6) should returnResult(true)
    evaluateUnaryTests("[1..5], [6..10]", 11) should returnResult(false)
  }

  it should "be in '2,3'" in {

    evaluateUnaryTests("2,3", 2) should returnResult(true)
    evaluateUnaryTests("2,3", 3) should returnResult(true)
    evaluateUnaryTests("2,3", 4) should returnResult(false)
  }

  it should "be not equal 'not(3)'" in {

    evaluateUnaryTests("not(3)", 2) should returnResult(true)
    evaluateUnaryTests("not(3)", 3) should returnResult(false)
    evaluateUnaryTests("not(3)", 4) should returnResult(true)
  }

  it should "be not in 'not(2,3)'" in {

    evaluateUnaryTests("not(2,3)", 2) should returnResult(false)
    evaluateUnaryTests("not(2,3)", 3) should returnResult(false)
    evaluateUnaryTests("not(2,3)", 4) should returnResult(true)
  }

  it should "compare to a variable (qualified name)" in {

    evaluateUnaryTests("var", 2, Map("var" -> 3)) should returnResult(false)
    evaluateUnaryTests("var", 3, Map("var" -> 3)) should returnResult(true)

    evaluateUnaryTests("< var", 2, Map("var" -> 3)) should returnResult(true)
    evaluateUnaryTests("< var", 3, Map("var" -> 3)) should returnResult(false)
  }

  it should "compare to a field of a bean" in {

    case class A(b: Int)

    evaluateUnaryTests("a.b", 3, Map("a" -> A(3))) should returnResult(true)
    evaluateUnaryTests("a.b", 3, Map("a" -> A(4))) should returnResult(false)

    evaluateUnaryTests("< a.b", 3, Map("a" -> A(4))) should returnResult(true)
    evaluateUnaryTests("< a.b", 3, Map("a" -> A(2))) should returnResult(false)
  }

  it should "compare to null" in {
    evaluateUnaryTests("3", inputValue = null) should returnResult(false)
  }

  it should "compare null with less/greater than" in {
    evaluateUnaryTests("< 3", inputValue = null) should returnNull()
    evaluateUnaryTests("<= 3", inputValue = null) should returnNull()
    evaluateUnaryTests("> 3", inputValue = null) should returnNull()
    evaluateUnaryTests(">= 3", inputValue = null) should returnNull()
  }

  it should "compare null with interval" in {
    evaluateUnaryTests("(0..10)", inputValue = null) should returnNull()
  }

  "A string" should "be equal to another string" in {

    evaluateUnaryTests(""" "b" """, "a") should returnResult(false)
    evaluateUnaryTests(""" "b" """, "b") should returnResult(true)
  }

  it should "compare to null" in {

    evaluateUnaryTests(""" "a" """, inputValue = null) should returnResult(false)
  }

  it should """be in '"a","b"' """ in {

    evaluateUnaryTests(""" "a","b" """, "a") should returnResult(true)
    evaluateUnaryTests(""" "a","b" """, "b") should returnResult(true)
    evaluateUnaryTests(""" "a","b" """, "c") should returnResult(false)
  }

  "A boolean" should "be equal to another boolean" in {

    evaluateUnaryTests("true", false) should returnResult(false)
    evaluateUnaryTests("false", true) should returnResult(false)

    evaluateUnaryTests("false", false) should returnResult(true)
    evaluateUnaryTests("true", true) should returnResult(true)
  }

  it should "compare to null" in {

    evaluateUnaryTests("true", inputValue = null) should returnResult(false)
    evaluateUnaryTests("false", inputValue = null) should returnResult(false)
  }

  it should "compare to a boolean comparison (numeric)" in {

    evaluateUnaryTests("1 < 2", true) should returnResult(true)
    evaluateUnaryTests("2 < 1", true) should returnResult(false)
  }

  it should "compare to a boolean comparison (string)" in {

    evaluateUnaryTests(""" "a" = "a" """, true) should returnResult(true)
    evaluateUnaryTests(""" "a" = "b" """, true) should returnResult(false)
  }

  it should "compare to a conjunction (and)" in {
    // it is uncommon to use a conjunction in a unary-tests but the engine should be able to parse
    evaluateUnaryTests("true and true", true) should returnResult(true)
    evaluateUnaryTests("false and true", true) should returnResult(false)

    evaluateUnaryTests("true and null", true) should returnResult(false)
    evaluateUnaryTests("false and null", true) should returnResult(false)

    evaluateUnaryTests("""true and "otherwise" """, true) should returnResult(false)
    evaluateUnaryTests("""false and "otherwise" """, true) should returnResult(false)
  }

  it should "compare to a disjunction (or)" in {
    // it is uncommon to use a disjunction in a unary-tests but the engine should be able to parse
    evaluateUnaryTests("true or true", true) should returnResult(true)
    evaluateUnaryTests("false or true", true) should returnResult(true)
    evaluateUnaryTests("false or false", true) should returnResult(false)

    evaluateUnaryTests("true or null", true) should returnResult(true)
    evaluateUnaryTests("false or null", true) should returnResult(false)

    evaluateUnaryTests("""true or "otherwise" """, true) should returnResult(true)
    evaluateUnaryTests("""false or "otherwise" """, true) should returnResult(false)
  }

  "A date" should "compare with '<'" in {

    evaluateUnaryTests("""< date("2015-09-18")""", date("2015-09-17")) should returnResult(true)
    evaluateUnaryTests("""< date("2015-09-18")""", date("2015-09-18")) should returnResult(false)
    evaluateUnaryTests("""< date("2015-09-18")""", date("2015-09-19")) should returnResult(false)
  }

  it should "compare with '<='" in {

    evaluateUnaryTests("""<= date("2015-09-18")""", date("2015-09-17")) should returnResult(true)
    evaluateUnaryTests("""<= date("2015-09-18")""", date("2015-09-18")) should returnResult(true)
    evaluateUnaryTests("""<= date("2015-09-18")""", date("2015-09-19")) should returnResult(false)
  }

  it should "compare with '>'" in {

    evaluateUnaryTests("""> date("2015-09-18")""", date("2015-09-17")) should returnResult(false)
    evaluateUnaryTests("""> date("2015-09-18")""", date("2015-09-18")) should returnResult(false)
    evaluateUnaryTests("""> date("2015-09-18")""", date("2015-09-19")) should returnResult(true)
  }

  it should "compare with '>='" in {

    evaluateUnaryTests(""">= date("2015-09-18")""", date("2015-09-17")) should returnResult(false)
    evaluateUnaryTests(""">= date("2015-09-18")""", date("2015-09-18")) should returnResult(true)
    evaluateUnaryTests(""">= date("2015-09-18")""", date("2015-09-19")) should returnResult(true)
  }

  it should "be equal to another date" in {

    evaluateUnaryTests("""date("2015-09-18")""", date("2015-09-17")) should returnResult(false)
    evaluateUnaryTests("""date("2015-09-18")""", date("2015-09-18")) should returnResult(true)
  }

  it should """be in interval '(date("2015-09-17")..date("2015-09-19")]'""" in {

    evaluateUnaryTests(
      """(date("2015-09-17")..date("2015-09-19"))""",
      date("2015-09-17")
    ) should returnResult(false)
    evaluateUnaryTests(
      """(date("2015-09-17")..date("2015-09-19"))""",
      date("2015-09-18")
    ) should returnResult(true)
    evaluateUnaryTests(
      """(date("2015-09-17")..date("2015-09-19"))""",
      date("2015-09-19")
    ) should returnResult(false)
  }

  it should """be in interval '[date("2015-09-17")..date("2015-09-19")]'""" in {

    evaluateUnaryTests(
      """[date("2015-09-17")..date("2015-09-19")]""",
      date("2015-09-17")
    ) should returnResult(true)
    evaluateUnaryTests(
      """[date("2015-09-17")..date("2015-09-19")]""",
      date("2015-09-18")
    ) should returnResult(true)
    evaluateUnaryTests(
      """[date("2015-09-17")..date("2015-09-19")]""",
      date("2015-09-19")
    ) should returnResult(true)
  }

  "A time" should "compare with '<'" in {

    evaluateUnaryTests("""< time("10:00:00")""", localTime("08:31:14")) should returnResult(true)
    evaluateUnaryTests("""< time("10:00:00")""", localTime("10:10:00")) should returnResult(false)
    evaluateUnaryTests("""< time("10:00:00")""", localTime("11:31:14")) should returnResult(false)

    evaluateUnaryTests("""< time("11:00:00+01:00")""", time("10:00:00+01:00")) should returnResult(
      true
    )
    evaluateUnaryTests("""< time("10:00:00+01:00")""", time("10:00:00+01:00")) should returnResult(
      false
    )
  }

  it should "be equal to another time" in {

    evaluateUnaryTests("""time("10:00:00")""", localTime("08:31:14")) should returnResult(false)
    evaluateUnaryTests("""time("08:31:14")""", localTime("08:31:14")) should returnResult(true)

    evaluateUnaryTests("""time("10:00:00+02:00")""", time("10:00:00+01:00")) should returnResult(
      false
    )
    evaluateUnaryTests("""time("11:00:00+02:00")""", time("10:00:00+01:00")) should returnResult(
      false
    )
    evaluateUnaryTests("""time("10:00:00+01:00")""", time("10:00:00+01:00")) should returnResult(
      true
    )
  }

  it should """be in interval '[time("08:00:00")..time("10:00:00")]'""" in {

    evaluateUnaryTests(
      """[time("08:00:00")..time("10:00:00")]""",
      localTime("07:45:10")
    ) should returnResult(false)
    evaluateUnaryTests(
      """[time("08:00:00")..time("10:00:00")]""",
      localTime("09:15:20")
    ) should returnResult(true)
    evaluateUnaryTests(
      """[time("08:00:00")..time("10:00:00")]""",
      localTime("11:30:30")
    ) should returnResult(false)

    evaluateUnaryTests(
      """[time("08:00:00+01:00")..time("10:00:00+01:00")]""",
      time("11:30:00+01:00")
    ) should returnResult(false)
    evaluateUnaryTests(
      """[time("08:00:00+01:00")..time("10:00:00+01:00")]""",
      time("09:30:00+01:00")
    ) should returnResult(true)
  }

  "A date-time" should "compare with '<'" in {

    evaluateUnaryTests(
      """< date and time("2015-09-17T10:00:00")""",
      localDateTime("2015-09-17T08:31:14")
    ) should returnResult(true)
    evaluateUnaryTests(
      """< date and time("2015-09-17T10:00:00")""",
      localDateTime("2015-09-17T10:10:00")
    ) should returnResult(false)
    evaluateUnaryTests(
      """< date and time("2015-09-17T10:00:00")""",
      localDateTime("2015-09-17T11:31:14")
    ) should returnResult(false)

    evaluateUnaryTests(
      """< date and time("2015-09-17T12:00:00+01:00")""",
      dateTime("2015-09-17T10:00:00+01:00")
    ) should returnResult(true)
    evaluateUnaryTests(
      """< date and time("2015-09-17T09:00:00+01:00")""",
      dateTime("2015-09-17T10:00:00+01:00")
    ) should returnResult(false)
  }

  it should "be equal to another date-time" in {

    evaluateUnaryTests(
      """date and time("2015-09-17T10:00:00")""",
      localDateTime("2015-09-17T08:31:14")
    ) should returnResult(false)
    evaluateUnaryTests(
      """date and time("2015-09-17T08:31:14")""",
      localDateTime("2015-09-17T08:31:14")
    ) should returnResult(true)

    evaluateUnaryTests(
      """date and time("2015-09-17T09:30:00+01:00")""",
      dateTime("2015-09-17T08:30:00+01:00")
    ) should returnResult(false)
    evaluateUnaryTests(
      """date and time("2015-09-17T08:30:00+02:00")""",
      dateTime("2015-09-17T08:30:00+01:00")
    ) should returnResult(false)
    evaluateUnaryTests(
      """date and time("2015-09-17T08:30:00+01:00")""",
      dateTime("2015-09-17T08:30:00+01:00")
    ) should returnResult(true)
  }

  it should """be in interval '[dante and time("2015-09-17T08:00:00")..date and time("2015-09-17T10:00:00")]'""" in {

    evaluateUnaryTests(
      """[date and time("2015-09-17T08:00:00")..date and time("2015-09-17T10:00:00")]""",
      localDateTime("2015-09-17T07:45:10")
    ) should returnResult(false)
    evaluateUnaryTests(
      """[date and time("2015-09-17T08:00:00")..date and time("2015-09-17T10:00:00")]""",
      localDateTime("2015-09-17T09:15:20")
    ) should returnResult(true)
    evaluateUnaryTests(
      """[date and time("2015-09-17T08:00:00")..date and time("2015-09-17T10:00:00")]""",
      localDateTime("2015-09-17T11:30:30")
    ) should returnResult(false)

    evaluateUnaryTests(
      """[date and time("2015-09-17T09:00:00+01:00")..date and time("2015-09-17T10:00:00+01:00")]""",
      dateTime("2015-09-17T08:30:00+01:00")
    ) should returnResult(false)
    evaluateUnaryTests(
      """[date and time("2015-09-17T08:00:00+01:00")..date and time("2015-09-17T10:00:00+01:00")]""",
      dateTime("2015-09-17T08:30:00+01:00")
    ) should returnResult(true)
  }

  "A year-month-duration" should "compare with '<'" in {

    evaluateUnaryTests("""< duration("P2Y")""", yearMonthDuration("P1Y")) should returnResult(true)
    evaluateUnaryTests("""< duration("P1Y")""", yearMonthDuration("P1Y")) should returnResult(false)
    evaluateUnaryTests("""< duration("P1Y")""", yearMonthDuration("P1Y2M")) should returnResult(
      false
    )
  }

  it should "be equal to another duration" in {

    evaluateUnaryTests("""duration("P1Y3M")""", yearMonthDuration("P1Y4M")) should returnResult(
      false
    )
    evaluateUnaryTests("""duration("P1Y4M")""", yearMonthDuration("P1Y4M")) should returnResult(
      true
    )
  }

  it should """be in interval '[duration("P1Y")..duration("P2Y")]'""" in {

    evaluateUnaryTests(
      """[duration("P1Y")..duration("P2Y")]""",
      yearMonthDuration("P6M")
    ) should returnResult(false)
    evaluateUnaryTests(
      """[duration("P1Y")..duration("P2Y")]""",
      yearMonthDuration("P1Y8M")
    ) should returnResult(true)
    evaluateUnaryTests(
      """[duration("P1Y")..duration("P2Y")]""",
      yearMonthDuration("P2Y1M")
    ) should returnResult(false)
  }

  "A day-time-duration" should "compare with '<'" in {

    evaluateUnaryTests("""< duration("P2DT4H")""", dayTimeDuration("P1DT4H")) should returnResult(
      true
    )
    evaluateUnaryTests("""< duration("P2DT4H")""", dayTimeDuration("P2DT4H")) should returnResult(
      false
    )
    evaluateUnaryTests("""< duration("P2DT4H")""", dayTimeDuration("P2DT8H")) should returnResult(
      false
    )
  }

  it should "be equal to another duration" in {

    evaluateUnaryTests("""duration("P2DT4H")""", dayTimeDuration("P1DT4H")) should returnResult(
      false
    )
    evaluateUnaryTests("""duration("P2DT4H")""", dayTimeDuration("P2DT4H")) should returnResult(
      true
    )
  }

  it should """be in interval '[duration("P1D")..duration("P2D")]'""" in {

    evaluateUnaryTests(
      """[duration("P1D")..duration("P2D")]""",
      dayTimeDuration("PT4H")
    ) should returnResult(false)
    evaluateUnaryTests(
      """[duration("P1D")..duration("P2D")]""",
      dayTimeDuration("P1DT4H")
    ) should returnResult(true)
    evaluateUnaryTests(
      """[duration("P1D")..duration("P2D")]""",
      dayTimeDuration("P2DT4H")
    ) should returnResult(false)
  }

  "A list" should "be equal to another list" in {

    evaluateUnaryTests("[]", List.empty) should returnResult(true)
    evaluateUnaryTests("[1,2]", List(1, 2)) should returnResult(true)

    evaluateUnaryTests("[]", List(1, 2)) should returnResult(false)
    evaluateUnaryTests("[1]", List(1, 2)) should returnResult(false)
    evaluateUnaryTests("[2,1]", List(1, 2)) should returnResult(false)
    evaluateUnaryTests("[1,2,3]", List(1, 2)) should returnResult(false)
  }

  it should "be checked in an every expression" in {
    evaluateUnaryTests("every x in ? satisfies x > 3", List(1, 2, 3)) should returnResult(false)
    evaluateUnaryTests("every x in ? satisfies x > 3", List(4, 5, 6)) should returnResult(true)
  }

  it should "be checked in a some expression" in {
    evaluateUnaryTests("some x in ? satisfies x > 4", List(1, 2, 3)) should returnResult(false)
    evaluateUnaryTests("some x in ? satisfies x > 4", List(4, 5, 6)) should returnResult(true)
  }

  "A context" should "be equal to another context" in {

    evaluateUnaryTests("{}", Map.empty) should returnResult(true)
    evaluateUnaryTests("{x:1}", Map("x" -> 1)) should returnResult(true)

    evaluateUnaryTests("{}", Map("x" -> 1)) should returnResult(false)
    evaluateUnaryTests("{x:2}", Map("x" -> 1)) should returnResult(false)
    evaluateUnaryTests("{y:1}", Map("x" -> 1)) should returnResult(false)
    evaluateUnaryTests("{x:1,y:2}", Map("x" -> 1)) should returnResult(false)
  }

  "An empty expression ('-')" should "be always true" in {

    evaluateUnaryTests("-", None) should returnResult(true)
  }

  "A null expression" should "compare to null" in {

    evaluateUnaryTests("null", 1) should returnResult(false)
    evaluateUnaryTests("null", true) should returnResult(false)
    evaluateUnaryTests("null", "a") should returnResult(false)

    evaluateUnaryTests("null", inputValue = null) should returnResult(true)
  }

  "A function" should "be invoked with the special variable '?'" in {

    evaluateUnaryTests(""" starts with(?, "f") """, "foo") should returnResult(true)
    evaluateUnaryTests(""" starts with(?, "b") """, "foo") should returnResult(false)
  }

  it should "be invoked as endpoint" in {

    evaluateUnaryTests("< max(1,2,3)", 2) should returnResult(true)
    evaluateUnaryTests("< min(1,2,3)", 2) should returnResult(false)
  }

  it should "be invoked with the special variable '?' for a parameter with ANY type" in {

    evaluateUnaryTests("list contains([481, 485, 551, 483], ?)", 481) should returnResult(true)
    evaluateUnaryTests("list contains([481, 485, 551, 483], ?)", 999) should returnResult(false)
  }

  "A unary-tests expression" should "return true if it evaluates to a value that is equal to the implicit value" in {

    evaluateUnaryTests("5", 5) should returnResult(true)
    evaluateUnaryTests("2 + 3", 5) should returnResult(true)
    evaluateUnaryTests("x", 5, Map("x" -> 5)) should returnResult(true)
  }

  it should "return false if it evaluates to a value that is not equal to the implicit value" in {

    evaluateUnaryTests("3", 5) should returnResult(false)
    evaluateUnaryTests("1 + 2", 5) should returnResult(false)
    evaluateUnaryTests("x", 5, Map("x" -> 3)) should returnResult(false)
  }

  it should "return null if it evaluates to a value that has a different type than the implicit value" in {

    evaluateUnaryTests(""" @"2024-08-19" """, 5) should returnNull()
  }

  it should "return true if it evaluates to a list that contains the implicit value" in {

    evaluateUnaryTests("[4,5,6]", 5) should returnResult(true)
    evaluateUnaryTests("concatenate([1,2,3], [4,5,6])", 5) should returnResult(true)
    evaluateUnaryTests("x", 5, Map("x" -> List(4, 5, 6))) should returnResult(true)
  }

  it should "return false if it evaluates to a list that doesn't contain the implicit value" in {

    evaluateUnaryTests("[1,2,3]", 5) should returnResult(false)
    evaluateUnaryTests("concatenate([1,2], [3])", 5) should returnResult(false)
    evaluateUnaryTests("x", 5, Map("x" -> List(1, 2, 3))) should returnResult(false)
  }

  it should "return true if it evaluates to true when the implicit value is applied to it" in {

    evaluateUnaryTests("< 10", 5) should returnResult(true)
    evaluateUnaryTests("[1..10]", 5) should returnResult(true)
    evaluateUnaryTests("> x", 5, Map("x" -> 3)) should returnResult(true)
  }

  it should "return false if it evaluates to false when the implicit value is applied to it" in {

    evaluateUnaryTests("< 3", 5) should returnResult(false)
    evaluateUnaryTests("[1..3]", 5) should returnResult(false)
    evaluateUnaryTests("> x", 5, Map("x" -> 10)) should returnResult(false)
  }

  it should "return null if it evaluates to null when the implicit value is applied to it" in {

    evaluateUnaryTests(""" < @"2024-08-19" """, 5) should returnNull()
    evaluateUnaryTests(""" < @"2024-08-19" """, inputValue = null) should returnNull()
  }

  it should "return true if it evaluates to true when the implicit value is assigned to the special variable '?'" in {

    evaluateUnaryTests("odd(?)", 5) should returnResult(true)
    evaluateUnaryTests("abs(?) < 10", 5) should returnResult(true)
    evaluateUnaryTests("? > x", 5, Map("x" -> 3)) should returnResult(true)
  }

  it should "return false if it evaluates to false when the implicit value is assigned to the special variable '?'" in {

    evaluateUnaryTests("even(?)", 5) should returnResult(false)
    evaluateUnaryTests("abs(?) < 3", 5) should returnResult(false)
    evaluateUnaryTests("? > x", 5, Map("x" -> 10)) should returnResult(false)
  }

  it should "return null if it evaluates to a value that is not a boolean when the implicit value is assigned to the special variable '?'" in {

    evaluateUnaryTests("abs(?)", 5) should returnNull()
    evaluateUnaryTests("?", 5) should returnNull()
    evaluateUnaryTests("? + not_existing", 5) should returnNull()
  }

  it should "return true if it evaluates to null and the implicit value is null" in {

    evaluateUnaryTests("null", inputValue = null) should returnResult(true)
    evaluateUnaryTests("2 + not_existing", inputValue = null) should returnResult(true)
    evaluateUnaryTests("not_existing", inputValue = null) should returnResult(true)
  }

  it should "return false if it evaluates to null and the implicit value is not null" in {

    evaluateUnaryTests("null", 5) should returnResult(false)
    evaluateUnaryTests("2 + not_existing", 5) should returnResult(false)
    evaluateUnaryTests("not_existing", 5) should returnResult(false)
  }

  it should "return true if it evaluates to true when null is assigned to the special variable '?'" in {

    evaluateUnaryTests("? = null", inputValue = null) should returnResult(true)
    evaluateUnaryTests("odd(?) or ? = null", inputValue = null) should returnResult(true)
  }

  it should "return false if it evaluates to false when null is assigned to the special variable '?'" in {

    evaluateUnaryTests("? != null", inputValue = null) should returnResult(false)
    evaluateUnaryTests("odd(?) and ? != null", inputValue = null) should returnResult(false)
  }

  it should "return null if it evaluates to null when null is assigned to the special variable '?'" in {

    evaluateUnaryTests("? < 10", inputValue = null) should returnNull()
    evaluateUnaryTests("odd(?)", inputValue = null) should returnNull()
    evaluateUnaryTests("5 < ? and ? < 10", inputValue = null) should returnNull()
    evaluateUnaryTests("5 < ? or ? < 10", inputValue = null) should returnNull()
  }

  it should "return true if it evaluates to true" in {

    evaluateUnaryTests("x", inputValue = 3, variables = Map("x" -> true)) should returnResult(true)
    evaluateUnaryTests("4 < 10", 3) should returnResult(true)
    evaluateUnaryTests("even(4)", 3) should returnResult(true)
    evaluateUnaryTests("list contains([1,2,3], 3)", 3) should returnResult(true)
  }

  it should "return false if it evaluates to false" in {

    evaluateUnaryTests(
      expression = "x",
      inputValue = 3,
      variables = Map("x" -> false)
    ) should returnResult(false)
    evaluateUnaryTests(expression = "4 > 10", 3) should returnResult(false)
    evaluateUnaryTests(expression = "odd(4)", 3) should returnResult(false)
    evaluateUnaryTests(expression = "list contains([1,2], 3)", 3) should returnResult(false)
  }

  "A negation" should "return true if it evaluates to a value that is not equal to the implicit value" in {

    evaluateUnaryTests("not(1)", 3) should returnResult(true)
    evaluateUnaryTests(""" not("a") """, "b") should returnResult(true)
  }

  it should "return false if it evaluates to a value that is equal to the implicit value" in {

    evaluateUnaryTests("not(3)", 3) should returnResult(false)
    evaluateUnaryTests(""" not("b") """, "b") should returnResult(false)
  }

  it should "return null if it evaluates to a value that has a different type than the implicit value" in {

    evaluateUnaryTests("not(1)", "b") should returnNull()
    evaluateUnaryTests(""" not("a") """, 2) should returnNull()
  }

  it should "return true if it evaluates to false when the implicit value is applied to it" in {

    evaluateUnaryTests("not(< 3)", 5) should returnResult(true)
    evaluateUnaryTests("not([1..3])", 5) should returnResult(true)
    evaluateUnaryTests("not(> x)", inputValue = 5, variables = Map("x" -> 10)) should returnResult(
      true
    )
  }

  it should "return false if it evaluates to true when the implicit value is applied to it" in {

    evaluateUnaryTests("not(< 10)", 5) should returnResult(false)
    evaluateUnaryTests("not([1..10])", 5) should returnResult(false)
    evaluateUnaryTests("not(> x)", inputValue = 5, variables = Map("x" -> 3)) should returnResult(
      false
    )
  }

  it should "return null if it evaluates to null when the implicit value is applied to it" in {

    evaluateUnaryTests("not(< 3)", "a") should returnNull()
    evaluateUnaryTests("not(< 3)", inputValue = null) should returnNull()
  }

  it should "return true if it evaluates to false" in {

    evaluateUnaryTests("not(x)", inputValue = 3, variables = Map("x" -> false)) should returnResult(
      true
    )
    evaluateUnaryTests("not(4 > 10)", 3) should returnResult(true)
    evaluateUnaryTests("not(odd(4))", 3) should returnResult(true)
    evaluateUnaryTests("not(list contains([1,2], 3))", 3) should returnResult(true)
  }

  it should "return false if it evaluates to true" in {

    evaluateUnaryTests("not(x)", inputValue = 3, variables = Map("x" -> true)) should returnResult(
      false
    )
    evaluateUnaryTests("not(4 < 10)", 3) should returnResult(false)
    evaluateUnaryTests("not(even(4))", 3) should returnResult(false)
    evaluateUnaryTests("not(list contains([1,2,3], 3))", 3) should returnResult(false)
  }

  it should "return true if it evaluates to null and the implicit value is not null" in {

    evaluateUnaryTests("not(null)", 5) should returnResult(true)
    evaluateUnaryTests("not(not_existing)", 5) should returnResult(true)
  }

  it should "return false if it evaluates to null and the implicit value is null" in {

    evaluateUnaryTests("not(null)", inputValue = null) should returnResult(false)
    evaluateUnaryTests("not(not_existing)", inputValue = null) should returnResult(false)
  }

  it should "return true if a disjunction evaluates to false" in {

    evaluateUnaryTests("not(2,3)", 5) should returnResult(true)
    evaluateUnaryTests("not(< 3, > 10)", 5) should returnResult(true)
    evaluateUnaryTests("not([0..3], [10..20])", 5) should returnResult(true)
  }

  it should "return false if a disjunction evaluates to true" in {

    evaluateUnaryTests("not(2,3)", 3) should returnResult(false)
    evaluateUnaryTests("not(< 3, > 10)", 1) should returnResult(false)
    evaluateUnaryTests("not([0..3], [10..20])", 1) should returnResult(false)
  }

  it should "return null if a disjunction evaluates to null" in {

    evaluateUnaryTests("not(2,3)", "a") should returnNull()
    evaluateUnaryTests("not(< 3, > 10)", "a") should returnNull()
    evaluateUnaryTests("not([0..3], [10..20])", "a") should returnNull()
  }

}
