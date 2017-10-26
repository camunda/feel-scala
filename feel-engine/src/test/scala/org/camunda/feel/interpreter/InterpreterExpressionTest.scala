package org.camunda.feel.interpreter

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._

/**
 * @author Philipp Ossler
 */
class InterpreterExpressionTest extends FlatSpec with Matchers with FeelIntegrationTest {

  "An expression" should "be a number" in {

    eval("2") should be(ValNumber(2))
  }

  it should "be a string" in {

    eval(""" "a" """) should be(ValString("a"))
  }

  it should "be a boolean" in {

    eval("true") should be(ValBoolean(true))
  }

  it should "be null" in {

    eval("null") should be(ValNull)
  }

  it should "be an if-then-else" in {

    val exp = """ if (x < 5) then "low" else "high" """

    eval(exp, Map("x" -> 2)) should be(ValString("low"))
    eval(exp, Map("x" -> 7)) should be(ValString("high"))
    
    eval(exp, Map("x" -> "foo")) should be(ValString("high"))
  }

  it should "be a simple positive unary test" in {

    eval("< 3", Map(RootContext.defaultInputVariable -> 2)) should be(ValBoolean(true))

    eval("(2 .. 4)", Map(RootContext.defaultInputVariable -> 5)) should be(ValBoolean(false))
  }

  it should "be an instance of" in {

    eval("x instance of number", Map("x" -> 1)) should be(ValBoolean(true))
    eval("x instance of number", Map("x" -> "NaN")) should be(ValBoolean(false))

    eval("x instance of boolean", Map("x" -> true)) should be(ValBoolean(true))
    eval("x instance of boolean", Map("x" -> 0)) should be(ValBoolean(false))

    eval("x instance of string", Map("x" -> "yes")) should be(ValBoolean(true))
    eval("x instance of string", Map("x" -> 0)) should be(ValBoolean(false))
  }

  it should "be a context" in {

    eval("{ a : 1 }") should be(ValContext(DefaultContext(Map("a" -> ValNumber(1)))))

    eval("""{ a:1, b:"foo" }""") should be(ValContext(DefaultContext(Map("a" -> ValNumber(1), "b" -> ValString("foo")))))

    // nested
    eval("{ a : { b : 1 } }") should be(ValContext(DefaultContext(Map("a" -> ValContext(DefaultContext(Map("b" -> ValNumber(1))))))))

  }

  it should "be a list" in {

    eval("[1]") should be(ValList(List( ValNumber(1) )))

    eval("[1,2]") should be(ValList(List(
        ValNumber(1),
        ValNumber(2) )))

    // nested
    eval("[ [1], [2] ]") should be(ValList(List(
        ValList(List(ValNumber(1))),
        ValList(List(ValNumber(2))) )))
  }

  "Null" should "compare to null" in {

    eval("null = null") should be(ValBoolean(true))
    eval("null != null") should be(ValBoolean(false))
  }

  "A number" should "add to '4'" in {

    eval("2+4") should be(ValNumber(6))
  }

  it should "add to '4' and '6'" in {

    eval("2+4+6") should be(ValNumber(12))
  }

  it should "add to not-a-number" in {

    eval("2 + true") should be(ValNull)
    eval("false + 3") should be(ValNull)
  }

  it should "subtract from '2'" in {

    eval("4-2") should be(ValNumber(2))
  }

  it should "subtract to not-a-number" in {

    eval("2 - true") should be(ValNull)
    eval("false - 3") should be(ValNull)
  }

  it should "add and subtract" in {

    eval("2+4-3+1") should be(ValNumber(4))
  }

  it should "multiply by '3'" in {

    eval("3*3") should be(ValNumber(9))
  }

  it should "multiply to not-a-number" in {

    eval("2 * true") should be(ValNull)
    eval("false * 3") should be(ValNull)
  }

  it should "divide by '4'" in {

    eval("8/4") should be(ValNumber(2))
  }

  it should "be null if divide by zero" in {

    eval("2 / 0") should be(ValNull)
  }

  it should "divide to not-a-number" in {

    eval("2 / true") should be(ValNull)
    eval("false / 3") should be(ValNull)
  }

  it should "multiply and divide" in {

    eval("3*4/2*5") should be(ValNumber(30))
  }

  it should "exponentiate by '3'" in {

    eval("2**3") should be(ValNumber(8))
  }

  it should "exponentiate twice" in {
    // all operators are left associative
    eval("2**2**3") should be(ValNumber(64))
  }

  it should "negate" in {

    eval("-2") should be(ValNumber(-2))
  }

  it should "negate and multiply" in {

    eval("2 * -3") should be(ValNumber(-6))
  }

  it should "add and multiply" in {

    eval("2 + 3 * 4") should be(ValNumber(14))

    eval("2 * 3 + 4") should be(ValNumber(10))
  }

  it should "multiply and exponentiate" in {

    eval("2**3 * 4") should be(ValNumber(32))

    eval("3 * 4**2") should be(ValNumber(48))
  }

  it should "compare with '='" in {

    eval("x=2", Map("x" -> 2)) should be(ValBoolean(true))
    eval("x=2", Map("x" -> 3)) should be(ValBoolean(false))

    eval("(x * 2) = 4", Map("x" -> 2)) should be(ValBoolean(true))
    eval("(x * 2) = 4", Map("x" -> 3)) should be(ValBoolean(false))
  }

  it should "compare with '!='" in {

    eval("x!=2", Map("x" -> 2)) should be(ValBoolean(false))
    eval("x!=2", Map("x" -> 3)) should be(ValBoolean(true))
  }

  it should "compare with '<'" in {

    eval("x<2", Map("x" -> 1)) should be(ValBoolean(true))
    eval("x<2", Map("x" -> 2)) should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval("x<=2", Map("x" -> 2)) should be(ValBoolean(true))
    eval("x<=2", Map("x" -> 3)) should be(ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval("x>2", Map("x" -> 2)) should be(ValBoolean(false))
    eval("x>2", Map("x" -> 3)) should be(ValBoolean(true))
  }

  it should "compare with '>='" in {

    eval("x>=2", Map("x" -> 2)) should be(ValBoolean(true))
    eval("x>=2", Map("x" -> 1)) should be(ValBoolean(false))
  }

  it should "compare with null" in {

    eval("2 = null") should be(ValBoolean(false))
    eval("null = 2") should be(ValBoolean(false))
    eval("null != 2") should be(ValBoolean(true))

    eval("2 > null") shouldBe a [ValError]
    eval("null < 2") shouldBe a [ValError]
  }

  it should "compare with 'between _ and _'" in {

    eval("x between 2 and 4", Map("x" -> 1)) should be (ValBoolean(false))
    eval("x between 2 and 4", Map("x" -> 2)) should be (ValBoolean(true))
    eval("x between 2 and 4", Map("x" -> 3)) should be (ValBoolean(true))
    eval("x between 2 and 4", Map("x" -> 4)) should be (ValBoolean(true))
    eval("x between 2 and 4", Map("x" -> 5)) should be (ValBoolean(false))
  }

  it should "compare with 'in'" in {

    eval("x in < 2", Map("x" -> 1)) should be(ValBoolean(true))
    eval("x in < 2", Map("x" -> 2)) should be(ValBoolean(false))

    eval("x in (2 .. 4)", Map("x" -> 3)) should be (ValBoolean(true))
    eval("x in (2 .. 4)", Map("x" -> 4)) should be (ValBoolean(false))

    eval("x in (2,4,6)", Map("x" -> 4)) should be(ValBoolean(true))
    eval("x in (2,4,6)", Map("x" -> 5)) should be(ValBoolean(false))

    eval("3 in (2 .. 4)") should be(ValBoolean(true))
    eval("4 in (2 .. 4)") should be(ValBoolean(false))
  }

  it should "be null if nAn" in {

    eval("x", Map("x" -> Double.NaN)) should be(ValNull)
  }

  it should "be null if infinity" in {

    eval("x", Map("x" -> Double.PositiveInfinity)) should be(ValNull)
    eval("x", Map("x" -> Double.NegativeInfinity)) should be(ValNull)
  }

  "A string" should "concatenates to another String" in {

    eval(""" "a" + "b" """) should be(ValString("ab"))
  }

  it should "compare with '='" in {

    eval(""" "a" = "a" """) should be(ValBoolean(true))
    eval(""" "a" = "b" """) should be(ValBoolean(false))
  }

  it should "compare with '!='" in {

    eval(""" "a" != "a" """) should be(ValBoolean(false))
    eval(""" "a" != "b" """) should be(ValBoolean(true))
  }

  it should "compare with '<'" in {

    eval(""" "a" < "b" """) should be(ValBoolean(true))
    eval(""" "b" < "a" """) should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(""" "a" <= "a" """) should be(ValBoolean(true))
    eval(""" "b" <= "a" """) should be(ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(""" "b" > "a" """) should be(ValBoolean(true))
    eval(""" "a" > "b" """) should be(ValBoolean(false))
  }

  it should "compare with '>='" in {

    eval(""" "b" >= "b" """) should be(ValBoolean(true))
    eval(""" "a" >= "b" """) should be(ValBoolean(false))
  }

  it should "compare with null" in {

    eval(""" "a" = null """) should be(ValBoolean(false))
    eval(""" null = "a" """) should be(ValBoolean(false))
    eval(""" "a" != null """) should be(ValBoolean(true))
  }

  "A boolean" should "compare with '='" in {

    eval("true = true") should be(ValBoolean(true))
    eval("true = false") should be(ValBoolean(false))
  }

  it should "compare with null" in {

    eval(""" true = null """) should be(ValBoolean(false))
    eval(""" null = false """) should be(ValBoolean(false))
    eval(""" true != null """) should be(ValBoolean(true))
    eval(""" null != false """) should be(ValBoolean(true))
  }

  it should "be in conjunction" in {

    eval("true and true") should be(ValBoolean(true))
    eval("true and false") should be(ValBoolean(false))

    eval("true and true and false") should be(ValBoolean(false))

    eval("true and 2") should be(ValNull)
    eval("false and 2") should be(ValBoolean(false))

    eval("2 and true") should be(ValNull)
    eval("2 and false") should be(ValBoolean(false))

    eval("2 and 4") should be(ValNull)
  }

  it should "be in disjunction" in {

    eval("false or true") should be(ValBoolean(true))
    eval("false or false") should be(ValBoolean(false))

    eval("false or false or true") should be(ValBoolean(true))

    eval("true or 2") should be(ValBoolean(true))
    eval("false or 2") should be(ValNull)

    eval("2 or true") should be(ValBoolean(true))
    eval("2 or false") should be(ValNull)

    eval("2 or 4") should be(ValNull)
  }

  it should "negate" in {

    eval("not(true)") should be(ValBoolean(false))
    eval("not(false)") should be(ValBoolean(true))

    eval("not(2)") should be(ValNull)
  }

  "A time" should "subtract from another time" in {

    eval(""" time("10:30:00") - time("09:00:00") """) should be(ValDayTimeDuration("PT1H30M"))

    eval(""" time("09:00:00") - time("10:00:00") """) should be(ValDayTimeDuration("PT-1H"))

    eval(""" time("12:00:00+01:00") - time("10:00:00+01:00") """) should be(ValDayTimeDuration("PT2H"))
  }

  it should "compare with '='" in {

    eval(""" time("10:00:00") = time("10:00:00") """) should be(ValBoolean(true))
    eval(""" time("10:00:00") = time("10:30:00") """) should be(ValBoolean(false))

    eval(""" time("10:00:00+01:00") = time("10:30:00+01:00") """) should be(ValBoolean(false))
    eval(""" time("10:00:00+01:00") = time("10:00:00+02:00") """) should be(ValBoolean(false))
    eval(""" time("10:00:00+01:00") = time("10:00:00+01:00") """) should be(ValBoolean(true))
  }

  it should "compare with '!='" in {

    eval(""" time("10:00:00") != time("10:00:00") """) should be(ValBoolean(false))
    eval(""" time("10:00:00") != time("22:00:00") """) should be(ValBoolean(true))

    eval(""" time("10:00:00+01:00") != time("10:00:00+01:00") """) should be(ValBoolean(false))
    eval(""" time("10:00:00+01:00") != time("22:00:00+01:00") """) should be(ValBoolean(true))
  }

  it should "compare with '<'" in {

    eval(""" time("10:00:00") < time("11:00:00") """) should be(ValBoolean(true))
    eval(""" time("10:00:00") < time("10:00:00") """) should be(ValBoolean(false))

    eval(""" time("10:00:00+01:00") < time("11:00:00+01:00") """) should be(ValBoolean(true))
    eval(""" time("10:00:00+01:00") < time("10:00:00+01:00") """) should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(""" time("10:00:00") <= time("10:00:00") """) should be(ValBoolean(true))
    eval(""" time("10:00:01") <= time("10:00:00") """) should be(ValBoolean(false))

    eval(""" time("10:00:00+01:00") <= time("10:00:00+01:00") """) should be(ValBoolean(true))
    eval(""" time("11:00:00+01:00") <= time("10:00:00+01:00") """) should be(ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(""" time("11:00:00") > time("11:00:00") """) should be(ValBoolean(false))
    eval(""" time("10:15:00") > time("10:00:00") """) should be(ValBoolean(true))

    eval(""" time("11:00:00+01:00") > time("11:00:00+01:00") """) should be(ValBoolean(false))
    eval(""" time("10:15:00+01:00") > time("10:00:00+01:00") """) should be(ValBoolean(true))
  }

  it should "compare with '>='" in {

    eval(""" time("11:00:00") >= time("11:00:00") """) should be(ValBoolean(true))
    eval(""" time("09:00:00") >= time("11:15:00") """) should be(ValBoolean(false))

    eval(""" time("11:00:00+01:00") >= time("11:00:00+01:00") """) should be(ValBoolean(true))
    eval(""" time("09:00:00+01:00") >= time("11:15:00+01:00") """) should be(ValBoolean(false))
  }

  it should "compare with 'between _ and _'" in {

    eval(""" time("08:30:00") between time("08:00:00") and time("10:00:00") """) should be (ValBoolean(true))
    eval(""" time("08:30:00") between time("09:00:00") and time("10:00:00") """) should be (ValBoolean(false))

    eval(""" time("08:30:00+01:00") between time("08:00:00+01:00") and time("10:00:00+01:00") """) should be (ValBoolean(true))
    eval(""" time("08:30:00+01:00") between time("09:00:00+01:00") and time("10:00:00+01:00") """) should be (ValBoolean(false))
  }

  "A date" should "compare with '='" in {

    eval(""" date("2017-01-10") = date("2017-01-10") """) should be(ValBoolean(true))
    eval(""" date("2017-01-10") = date("2017-01-11") """) should be(ValBoolean(false))
  }

  it should "compare with '!='" in {

    eval(""" date("2017-01-10") != date("2017-01-10") """) should be(ValBoolean(false))
    eval(""" date("2017-01-10") != date("2017-02-10") """) should be(ValBoolean(true))
  }

  it should "compare with '<'" in {

    eval(""" date("2016-01-10") < date("2017-01-10") """) should be(ValBoolean(true))
    eval(""" date("2017-01-10") < date("2017-01-10") """) should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(""" date("2017-01-10") <= date("2017-01-10") """) should be(ValBoolean(true))
    eval(""" date("2017-01-20") <= date("2017-01-10") """) should be(ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(""" date("2017-01-10") > date("2017-01-10") """) should be(ValBoolean(false))
    eval(""" date("2017-02-17") > date("2017-01-10") """) should be(ValBoolean(true))
  }

  it should "compare with '>='" in {

    eval(""" date("2017-01-10") >= date("2017-01-10") """) should be(ValBoolean(true))
    eval(""" date("2017-01-10") >= date("2018-01-10") """) should be(ValBoolean(false))
  }

  it should "compare with 'between _ and _'" in {

    eval(""" date("2017-01-10") between date("2017-01-01") and date("2018-01-10") """) should be (ValBoolean(true))
    eval(""" date("2017-01-10") between date("2017-02-01") and date("2017-03-01") """) should be (ValBoolean(false))
  }

  "A date-time" should "subtract from another date-time" in {

    eval(""" date and time("2017-01-10T10:30:00") - date and time("2017-01-01T10:00:00") """) should be(ValDayTimeDuration("P9DT30M"))
    eval(""" date and time("2017-01-10T10:00:00") - date and time("2017-01-10T10:30:00") """) should be(ValDayTimeDuration("PT-30M"))

    eval(""" date and time("2017-01-10T10:30:00+01:00") - date and time("2017-01-01T10:00:00+01:00") """) should be(ValDayTimeDuration("P9DT30M"))
    eval(""" date and time("2017-01-10T10:00:00+01:00") - date and time("2017-01-10T10:30:00+01:00") """) should be(ValDayTimeDuration("PT-30M"))
  }

  it should "compare with '='" in {

    eval(""" date and time("2017-01-10T10:30:00") = date and time("2017-01-10T10:30:00") """) should be(ValBoolean(true))
    eval(""" date and time("2017-01-10T10:30:00") = date and time("2017-01-10T14:00:00") """) should be(ValBoolean(false))

    eval(""" date and time("2017-01-10T10:30:00+01:00") = date and time("2017-01-10T10:30:00+01:00") """) should be(ValBoolean(true))
    eval(""" date and time("2017-01-10T10:30:00+01:00") = date and time("2017-01-10T14:00:00+01:00") """) should be(ValBoolean(false))
  }

  it should "compare with '!='" in {

    eval(""" date and time("2017-01-10T10:30:00") != date and time("2017-01-10T10:30:00") """) should be(ValBoolean(false))
    eval(""" date and time("2017-01-10T10:30:00") != date and time("2017-01-11T10:30:00") """) should be(ValBoolean(true))

    eval(""" date and time("2017-01-10T10:30:00+01:00") != date and time("2017-01-10T10:30:00+01:00") """) should be(ValBoolean(false))
    eval(""" date and time("2017-01-10T10:30:00+01:00") != date and time("2017-01-11T10:30:00+01:00") """) should be(ValBoolean(true))
  }

  it should "compare with '<'" in {

    eval(""" date and time("2017-01-10T10:30:00") < date and time("2017-02-10T10:00:00") """) should be(ValBoolean(true))
    eval(""" date and time("2017-01-10T10:30:00") < date and time("2017-01-10T10:30:00") """) should be(ValBoolean(false))

    eval(""" date and time("2017-01-10T10:30:00+01:00") < date and time("2017-02-10T10:00:00+01:00") """) should be(ValBoolean(true))
    eval(""" date and time("2017-01-10T10:30:00+01:00") < date and time("2017-01-10T10:30:00+01:00") """) should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(""" date and time("2017-01-10T10:30:00") <= date and time("2017-01-10T10:30:00") """) should be(ValBoolean(true))
    eval(""" date and time("2017-02-10T10:00:00") <= date and time("2017-01-10T10:30:00") """) should be(ValBoolean(false))

    eval(""" date and time("2017-01-10T10:30:00+01:00") <= date and time("2017-01-10T10:30:00+01:00") """) should be(ValBoolean(true))
    eval(""" date and time("2017-02-10T10:00:00+01:00") <= date and time("2017-01-10T10:30:00+01:00") """) should be(ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(""" date and time("2017-01-10T10:30:00") > date and time("2017-01-10T10:30:00") """) should be(ValBoolean(false))
    eval(""" date and time("2018-01-10T10:30:00") > date and time("2017-01-10T10:30:00") """) should be(ValBoolean(true))

    eval(""" date and time("2017-01-10T10:30:00+01:00") > date and time("2017-01-10T10:30:00+01:00") """) should be(ValBoolean(false))
    eval(""" date and time("2018-01-10T10:30:00+01:00") > date and time("2017-01-10T10:30:00+01:00") """) should be(ValBoolean(true))
  }

  it should "compare with '>='" in {

    eval(""" date and time("2017-01-10T10:30:00") >= date and time("2017-01-10T10:30:00") """) should be(ValBoolean(true))
    eval(""" date and time("2017-01-10T10:30:00") >= date and time("2017-01-10T10:30:01") """) should be(ValBoolean(false))

    eval(""" date and time("2017-01-10T10:30:00+01:00") >= date and time("2017-01-10T10:30:00+01:00") """) should be(ValBoolean(true))
    eval(""" date and time("2017-01-10T10:30:00+01:00") >= date and time("2017-01-10T10:30:01+01:00") """) should be(ValBoolean(false))
  }

  it should "compare with 'between _ and _'" in {

    eval(""" date and time("2017-01-10T10:30:00") between date and time("2017-01-10T09:00:00") and date and time("2017-01-10T14:00:00") """) should be (ValBoolean(true))
    eval(""" date and time("2017-01-10T10:30:00") between date and time("2017-01-10T11:00:00") and date and time("2017-01-11T08:00:00") """) should be (ValBoolean(false))

    eval(""" date and time("2017-01-10T10:30:00+01:00") between date and time("2017-01-10T09:00:00+01:00") and date and time("2017-01-10T14:00:00+01:00") """) should be (ValBoolean(true))
    eval(""" date and time("2017-01-10T10:30:00+01:00") between date and time("2017-01-10T11:00:00+01:00") and date and time("2017-01-11T08:00:00+01:00") """) should be (ValBoolean(false))
  }

  "A year-month-duration" should "add to year-month-duration" in {

    eval(""" duration("P2M") + duration("P3M") """) should be(ValYearMonthDuration("P5M"))
    eval(""" duration("P1Y") + duration("P6M") """) should be(ValYearMonthDuration("P1Y6M"))
  }

  it should "add to date-time" in {

    eval(""" duration("P1M") + date and time("2017-01-10T10:30:00") """) should be(ValLocalDateTime("2017-02-10T10:30:00"))
    eval(""" date and time("2017-01-10T10:30:00") + duration("P1Y") """) should be(ValLocalDateTime("2018-01-10T10:30:00"))

    eval(""" duration("P1M") + date and time("2017-01-10T10:30:00+01:00") """) should be(ValDateTime("2017-02-10T10:30:00+01:00"))
    eval(""" date and time("2017-01-10T10:30:00+01:00") + duration("P1Y") """) should be(ValDateTime("2018-01-10T10:30:00+01:00"))
  }

  it should "subtract from year-month-duration" in {

    eval(""" duration("P1Y") - duration("P3M") """) should be(ValYearMonthDuration("P9M"))
    eval(""" duration("P5M") - duration("P6M") """) should be(ValYearMonthDuration("P-1M"))
  }

  it should "subtract from date-time" in {

    eval(""" date and time("2017-01-10T10:30:00") - duration("P1M") """) should be(ValLocalDateTime("2016-12-10T10:30:00"))

    eval(""" date and time("2017-01-10T10:30:00+01:00") - duration("P1M") """) should be(ValDateTime("2016-12-10T10:30:00+01:00"))
  }

  it should "multiply by '3'" in {

    eval(""" duration("P1M") * 3 """) should be(ValYearMonthDuration("P3M"))
    eval(""" 3 * duration("P2Y") """) should be(ValYearMonthDuration("P6Y"))
  }

  it should "divide by '4'" in {

    eval(""" duration("P1Y") / 2 """) should be(ValYearMonthDuration("P6M"))
  }

  it should "compare with '='" in {

    eval(""" duration("P2M") = duration("P2M") """) should be(ValBoolean(true))
    eval(""" duration("P2M") = duration("P4M") """) should be(ValBoolean(false))
  }

  it should "compare with '!='" in {

    eval(""" duration("P2M") != duration("P2M") """) should be(ValBoolean(false))
    eval(""" duration("P2M") != duration("P1Y") """) should be(ValBoolean(true))
  }

  it should "compare with '<'" in {

    eval(""" duration("P2M") < duration("P3M") """) should be(ValBoolean(true))
    eval(""" duration("P2M") < duration("P2M") """) should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(""" duration("P2M") <= duration("P2M") """) should be(ValBoolean(true))
    eval(""" duration("P1Y2M") <= duration("P2M") """) should be(ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(""" duration("P2M") > duration("P2M") """) should be(ValBoolean(false))
    eval(""" duration("P2M") > duration("P1M") """) should be(ValBoolean(true))
  }

  it should "compare with '>='" in {

    eval(""" duration("P2M") >= duration("P2M") """) should be(ValBoolean(true))
    eval(""" duration("P2M") >= duration("P5M") """) should be(ValBoolean(false))
  }

  it should "compare with 'between _ and _'" in {

    eval(""" duration("P3M") between duration("P2M") and duration("P6M") """) should be (ValBoolean(true))
    eval(""" duration("P1Y") between duration("P2M") and duration("P6M") """) should be (ValBoolean(false))
  }

  "A day-time-duration" should "add to day-time-duration" in {

    eval(""" duration("PT4H") + duration("PT2H") """) should be(ValDayTimeDuration("PT6H"))
    eval(""" duration("P1D") + duration("PT6H") """) should be(ValDayTimeDuration("P1DT6H"))
  }

  it should "add to date-time" in {

    eval(""" duration("PT1H") + date and time("2017-01-10T10:30:00") """) should be(ValLocalDateTime("2017-01-10T11:30:00"))
    eval(""" date and time("2017-01-10T10:30:00") + duration("P1D") """) should be(ValLocalDateTime("2017-01-11T10:30:00"))

    eval(""" duration("PT1H") + date and time("2017-01-10T10:30:00+01:00") """) should be(ValDateTime("2017-01-10T11:30:00+01:00"))
    eval(""" date and time("2017-01-10T10:30:00+01:00") + duration("P1D") """) should be(ValDateTime("2017-01-11T10:30:00+01:00"))
  }

  it should "add to time" in {

    eval(""" duration("PT1H") + time("10:30:00") """) should be(ValLocalTime("11:30:00"))
    eval(""" time("10:30:00") + duration("P1D") """) should be(ValLocalTime("10:30:00"))

    eval(""" duration("PT1H") + time("10:30:00+01:00") """) should be(ValTime("11:30:00+01:00"))
    eval(""" time("10:30:00+01:00") + duration("P1D") """) should be(ValTime("10:30:00+01:00"))
  }

  it should "subtract from day-time-duration" in {

    eval(""" duration("PT6H") - duration("PT2H") """) should be(ValDayTimeDuration("PT4H"))
    eval(""" duration("PT22H") - duration("P1D") """) should be(ValDayTimeDuration("PT-2H"))
  }

  it should "subtract from date-time" in {

    eval(""" date and time("2017-01-10T10:30:00") - duration("PT1H") """) should be(ValLocalDateTime("2017-01-10T09:30:00"))

    eval(""" date and time("2017-01-10T10:30:00+01:00") - duration("PT1H") """) should be(ValDateTime("2017-01-10T09:30:00+01:00"))
  }

  it should "subtract from time" in {

    eval(""" time("10:30:00") - duration("PT1H") """) should be(ValLocalTime("09:30:00"))

    eval(""" time("10:30:00+01:00") - duration("PT1H") """) should be(ValTime("09:30:00+01:00"))
  }

  it should "multiply by '3'" in {

    eval(""" duration("PT2H") * 3 """) should be(ValDayTimeDuration("PT6H"))
    eval(""" 3 * duration("P1D") """) should be(ValDayTimeDuration("P3D"))
  }

  it should "divide by '4'" in {

    eval(""" duration("P1D") / 4 """) should be(ValDayTimeDuration("PT6H"))
  }

  it should "compare with '='" in {

    eval(""" duration("PT6H") = duration("PT6H") """) should be(ValBoolean(true))
    eval(""" duration("PT6H") = duration("PT2H") """) should be(ValBoolean(false))
  }

  it should "compare with '!='" in {

    eval(""" duration("PT6H") != duration("PT6H") """) should be(ValBoolean(false))
    eval(""" duration("PT6H") != duration("P1D") """) should be(ValBoolean(true))
  }

  it should "compare with '<'" in {

    eval(""" duration("PT6H") < duration("PT12H") """) should be(ValBoolean(true))
    eval(""" duration("PT6H") < duration("PT6H") """) should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(""" duration("PT6H") <= duration("PT6H") """) should be(ValBoolean(true))
    eval(""" duration("PT6H") <= duration("PT1H") """) should be(ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(""" duration("PT6H") > duration("PT6H") """) should be(ValBoolean(false))
    eval(""" duration("P1D") > duration("PT6H") """) should be(ValBoolean(true))
  }

  it should "compare with '>='" in {

    eval(""" duration("PT6H") >= duration("PT6H") """) should be(ValBoolean(true))
    eval(""" duration("PT6H") >= duration("PT6H1M") """) should be(ValBoolean(false))
  }

  it should "compare with 'between _ and _'" in {

    eval(""" duration("PT8H") between duration("PT6H") and duration("PT12H") """) should be (ValBoolean(true))
    eval(""" duration("PT2H") between duration("PT6H") and duration("PT12H") """) should be (ValBoolean(false))
  }

  "A function definition" should "be interpeted" in {

    val function = eval("function(x) x + 1")

    function shouldBe a [ValFunction]
    function.asInstanceOf[ValFunction].params should be(List("x"))
  }

  it should "be invoked without parameter" in {

    val functions = Map("f" -> eval("""function() "invoked" """).asInstanceOf[ValFunction])

    eval("f()", functions = functions) should be(ValString("invoked"))
  }

  it should "be invoked with one positional parameter" in {

    val functions = Map("f" -> eval("function(x) x + 1").asInstanceOf[ValFunction])

    eval("f(1)", functions = functions) should be(ValNumber(2))
    eval("f(2)", functions = functions) should be(ValNumber(3))
  }

  it should "be invoked with positional parameters" in {

    val functions = Map("add" -> eval("function(x,y) x + y").asInstanceOf[ValFunction])

    eval("add(1,2)", functions = functions) should be(ValNumber(3))
    eval("add(2,3)", functions = functions) should be(ValNumber(5))
  }

  it should "be invoked with one named parameter" in {

    val functions = Map("f" -> eval("function(x) x + 1").asInstanceOf[ValFunction])

    eval("f(x:1)", functions = functions) should be(ValNumber(2))
    eval("f(x:2)", functions = functions) should be(ValNumber(3))
  }

  it should "be invoked with named parameters" in {

    val functions = Map("sub" -> eval("function(x,y) x - y").asInstanceOf[ValFunction])

    eval("sub(x:4,y:2)", functions = functions) should be(ValNumber(2))
    eval("sub(y:2,x:4)", functions = functions) should be(ValNumber(2))
  }

  it should "be invoked with an expression as parameter" in {

    val functions = Map("f" -> eval("function(x) x + 1").asInstanceOf[ValFunction])

    eval("f(2 + 3)", functions = functions) should be(ValNumber(6))
  }

  it should "be invoked as parameter of another function" in {

    val functions = Map(
      "a" -> eval("function(x) x + 1").asInstanceOf[ValFunction],
      "b" -> eval("function(x) x + 2").asInstanceOf[ValFunction]
    )

    eval("a(b(1))", functions = functions) should be(ValNumber(4))
  }

  it should "fail to invoke with wrong number of parameters" in {

    val functions = Map("f" -> eval("function(x,y) true").asInstanceOf[ValFunction])

    eval("f()", functions = functions) should be(ValError("no function found with name 'f' and 0 parameters"))
    eval("f(1)", functions = functions) should be(ValError("no function found with name 'f' and 1 parameters"))

    eval("f(x:1,z:3)", functions = functions) should be(ValError("no function found with name 'f' and parameters: x,z"))
    eval("f(x:1,y:2,z:3)", functions = functions) should be(ValError("no function found with name 'f' and parameters: x,y,z"))
  }

  it should "replace not set parameters with null" in {

    val functions = Map("f" -> eval("""
      function(x,y)
        if x = null
        then "x"
        else if y = null
        then "y"
        else "ok"
        """).asInstanceOf[ValFunction])

    eval("f(x:1)", functions = functions) should be(ValString("y"))
    eval("f(y:1)", functions = functions) should be(ValString("x"))
    eval("f(x:1,y:1)", functions = functions) should be(ValString("ok"))
  }

  "An external java function definition" should "be invoked with one double parameter" in {

    val functions = Map("cos" -> eval(""" function(angle) external { java: { class: "java.lang.Math", method_signature: "cos(double)" } } """).asInstanceOf[ValFunction])

    eval("cos(0)", functions = functions) should be(ValNumber(1))
    eval("cos(1)", functions = functions) should be(ValNumber( Math.cos(1) ))
  }

  it should "be invoked with two int parameters" in {

    val functions = Map("max" -> eval(""" function(x,y) external { java: { class: "java.lang.Math", method_signature: "max(int, int)" } } """).asInstanceOf[ValFunction])

    eval("max(1,2)", functions = functions) should be(ValNumber(2))
  }

  it should "be invoked with one long parameters" in {

    val functions = Map("abs" -> eval(""" function(a) external { java: { class: "java.lang.Math", method_signature: "abs(long)" } } """).asInstanceOf[ValFunction])

    eval("abs(-1)", functions = functions) should be(ValNumber(1))
  }

  it should "be invoked with one float parameters" in {

    val functions = Map("round" -> eval(""" function(a) external { java: { class: "java.lang.Math", method_signature: "round(float)" } } """).asInstanceOf[ValFunction])

    eval("round(3.2)", functions = functions) should be(ValNumber(3))
  }

  "A list" should "be checked with 'some'" in {

    eval("some x in [1,2,3] satisfies x > 2") should be(ValBoolean(true))
    eval("some x in [1,2,3] satisfies x > 3") should be(ValBoolean(false))

    eval("some x in xs satisfies x > 2", Map("xs" -> List(1,2,3))) should be(ValBoolean(true))
    eval("some x in xs satisfies x > 2", Map("xs" -> List(1,2))) should be(ValBoolean(false))

    eval("some x in [1,2], y in [2,3] satisfies x < y") should be(ValBoolean(true))
    eval("some x in [1,2], y in [1,1] satisfies x < y") should be(ValBoolean(false))
  }

  it should "be checked with 'every'" in {

    eval("every x in [1,2,3] satisfies x >= 1") should be(ValBoolean(true))
    eval("every x in [1,2,3] satisfies x >= 2") should be(ValBoolean(false))

    eval("every x in xs satisfies x >= 1", Map("xs" -> List(1,2,3))) should be(ValBoolean(true))
    eval("every x in xs satisfies x >= 1", Map("xs" -> List(0,1,2,3))) should be(ValBoolean(false))

    eval("every x in [1,2], y in [3,4] satisfies x < y") should be(ValBoolean(true))
    eval("every x in [1,2], y in [2,3] satisfies x < y") should be(ValBoolean(false))
  }

  it should "be processed in a for-expression" in {

    eval("for x in [1,2] return x * 2") should be(ValList(List(
        ValNumber(2),
        ValNumber(4) )))

    eval("for x in [1,2], y in [3,4] return x * y") should be(ValList(List(
        ValNumber(3),
        ValNumber(4),
        ValNumber(6),
        ValNumber(8) )))

    eval("for x in xs return x * 2", Map("xs" -> List(1,2))) should be(ValList(List(
        ValNumber(2),
        ValNumber(4) )))
  }

  it should "be filtered" in {

    eval("[1,2,3,4][item > 2]") should be(ValList(List(
        ValNumber(3), ValNumber(4))))

    eval("xs [item > 2]", Map("xs" -> List(1,2,3,4))) should be(ValList(List(
        ValNumber(3), ValNumber(4))))
  }

  "A context" should "be accessed" in {

    eval("ctx.a", Map("ctx" -> Map("a" -> 1))) should be(ValNumber(1))

    eval("{ a: 1 }.a") should be(ValNumber(1))
  }

  it should "be accessed in a nested context" in {

    eval("{ a: { b:1 } }.a") should be(ValContext(DefaultContext(Map(
        "b" -> ValNumber(1))) ))

    eval("{ a: { b:1 } }.a.b") should be(ValNumber(1))
  }

  it should "be accessed in a list" in {

    eval("[ {a:1, b:2}, {a:3, b:4} ].a") should be(ValList(List(
        ValNumber(1), ValNumber(3) )))
  }

  it should "be accessed in same context" in {

    eval("{ a:1, b:(a+1), c:(b+1)}.c") should be(ValNumber(3))

    eval("{ a: { b: 1 }, c: (1 + a.b) }.c") should be(ValNumber(2))
  }

  it should "be filtered in a list" in {

    eval("[ {a:1, b:2}, {a:3, b:4} ][a > 2]") should be(ValList(List(
        ValContext(DefaultContext(Map(
            "a" -> ValNumber(3),
            "b" -> ValNumber(4) )) ))))
  }

  it should "access a variable in a nested context" in {

      eval("{ a:1, b:{ c:a+2 } }.b.c") should be(ValNumber(3))
  }

  it should "not override variables of nested context" in {

      eval("{ a:1, b:{ a:2, c:a+3 } }.b.c") should be(ValNumber(5))
  }

  "A bean" should "be handled as context" in {

    class A {
      val b: Int = 0
      val c: Int = 1
      def foo() = "foo"
      def getBar() = "bar"
      def incr(x: Int) = x + 1
    }

    val obj = new A
    val c = eval("a", Map("a" -> obj))

    c should be(ValContext(ObjectContext(obj)))
    c.asInstanceOf[ValContext].context.variable("b") shouldBe a [ValNumber]
    c.asInstanceOf[ValContext].context.variable("foo") shouldBe a [ValString]
    c.asInstanceOf[ValContext].context.variable("bar") shouldBe a [ValString]
    c.asInstanceOf[ValContext].context.function("foo", 0) shouldBe a [ValFunction]
    c.asInstanceOf[ValContext].context.function("getBar", 0) shouldBe a [ValFunction]
    c.asInstanceOf[ValContext].context.function("incr", 1) shouldBe a [ValFunction]
    c.asInstanceOf[ValContext].context.function("incr", Set("x")) shouldBe a [ValFunction]
  }

  it should "access a field" in {

    class A(val b: Int)

    eval("a.b", Map("a" -> new A(2))) should be(ValNumber(2))

  }

  it should "access a getter method as field" in {

    class A(b: Int) { def getFoo() = b + 1 }

    eval("a.foo", Map("a" -> new A(2))) should be(ValNumber(3))

  }

  it should "invoke a method without arguments" in {

    class A { def foo() = "foo" }

    eval("a.foo()", Map("a" -> new A())) should be(ValString("foo"))

  }

  it should "invoke a method with one argument" in {

    class A { def incr(x: Int) = x + 1 }

    eval("a.incr(1)", Map("a" -> new A())) should be(ValNumber(2))

  }

  it should "access a nullable field" in {

     class A(val a: String, val b: String)

     eval(""" a.a = null """, Map("a" -> new A("not null", null))) should be(ValBoolean(false))
     eval(""" a.b = null """, Map("a" -> new A("not null", null))) should be(ValBoolean(true))
     eval(""" null = a.a """, Map("a" -> new A("not null", null))) should be(ValBoolean(false))
     eval(""" null = a.b""", Map("a" -> new A("not null", null))) should be(ValBoolean(true))
     eval(""" a.a = a.b """, Map("a" -> new A("not null", "not null"))) should be(ValBoolean(true))
     eval(""" a.a = a.b """, Map("a" -> new A("not null", null))) should be(ValBoolean(false))
     eval(""" a.a = a.b """, Map("a" -> new A(null, "not null"))) should be(ValBoolean(false))
     eval(""" a.a = a.b """, Map("a" -> new A(null, null))) should be(ValBoolean(true))
   }

}
