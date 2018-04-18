package org.camunda.feel.interpreter

import org.camunda.feel._
import org.camunda.feel.parser.FeelParser

import org.scalatest.FlatSpec
import org.scalatest.Matchers

/**
 * @author Philipp Ossler
 */
class InterpreterUnaryTest extends FlatSpec with Matchers {

  val interpreter = new FeelInterpreter

  "A number" should "compare with '<'" in {

    eval(2, "< 3") should be(ValBoolean(true))
    eval(3, "< 3") should be(ValBoolean(false))
    eval(4, "< 3") should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(2, "<= 3") should be(ValBoolean(true))
    eval(3, "<= 3") should be(ValBoolean(true))
    eval(4, "<= 3") should be(ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(2, "> 3") should be(ValBoolean(false))
    eval(3, "> 3") should be(ValBoolean(false))
    eval(4, "> 3") should be(ValBoolean(true))
  }

  it should "compare with '>='" in {

    eval(2, ">= 3") should be(ValBoolean(false))
    eval(3, ">= 3") should be(ValBoolean(true))
    eval(4, ">= 3") should be(ValBoolean(true))
  }

  it should "be equal to another number" in {

    eval(2, "3") should be (ValBoolean(false))
    eval(3, "3") should be (ValBoolean(true))
  }

  it should "compare to null" in {

    eval(null, "3") should be (ValBoolean(false))
  }

  it should "be in interval '(2..4)'" in {

    eval(2, "(2..4)") should be(ValBoolean(false))
    eval(3, "(2..4)") should be(ValBoolean(true))
    eval(4, "(2..4)") should be(ValBoolean(false))
  }

  it should "be in interval '[2..4]'" in {

    eval(2, "[2..4]") should be(ValBoolean(true))
    eval(3, "[2..4]") should be(ValBoolean(true))
    eval(4, "[2..4]") should be(ValBoolean(true))
  }

  it should "be in '2,3'" in {

    eval(2, "2,3") should be (ValBoolean(true))
    eval(3, "2,3") should be (ValBoolean(true))
    eval(4, "2,3") should be (ValBoolean(false))
  }

  it should "be not equal 'not(3)'" in {

    eval(2, "not(3)") should be (ValBoolean(true))
    eval(3, "not(3)") should be (ValBoolean(false))
    eval(4, "not(3)") should be (ValBoolean(true))
  }

  it should "be not in 'not(2,3)'" in {

    eval(2, "not(2,3)") should be (ValBoolean(false))
    eval(3, "not(2,3)") should be (ValBoolean(false))
    eval(4, "not(2,3)") should be (ValBoolean(true))
  }

  it should "compare to a variable (qualified name)" in {

    eval(2,"var", Map("var" -> 3)) should be (ValBoolean(false))
    eval(3,"var", Map("var" -> 3)) should be (ValBoolean(true))

    eval(2,"< var", Map("var" -> 3)) should be (ValBoolean(true))
    eval(3,"< var", Map("var" -> 3)) should be (ValBoolean(false))
  }

  it should "compare to a field of a bean" in {

    class A(val b: Int)

    eval(3, "a.b", Map("a" -> new A(3))) should be(ValBoolean(true))
    eval(3, "a.b", Map("a" -> new A(4))) should be(ValBoolean(false))

    eval(3, "< a.b", Map("a" -> new A(4))) should be(ValBoolean(true))
    eval(3, "< a.b", Map("a" -> new A(2))) should be(ValBoolean(false))
  }

  "A string" should "be equal to another string" in {

    eval("a", """ "b" """) should be (ValBoolean(false))
    eval("b", """ "b" """) should be (ValBoolean(true))
  }

  it should "compare to null" in {

    eval(null, """ "a" """) should be (ValBoolean(false))
  }

  it should """be in '"a","b"' """ in {

    eval("a", """ "a","b" """) should be (ValBoolean(true))
    eval("b", """ "a","b" """) should be (ValBoolean(true))
    eval("c", """ "a","b" """) should be (ValBoolean(false))
  }

  "A boolean" should "be equal to another boolean" in {

    eval(false, "true") should be (ValBoolean(false))
    eval(true, "false") should be (ValBoolean(false))

    eval(false, "false") should be (ValBoolean(true))
    eval(true, "true") should be (ValBoolean(true))
  }

  it should "compare to null" in {

    eval(null, "true") should be (ValBoolean(false))
    eval(null, "false") should be (ValBoolean(false))
  }

  "A date" should "compare with '<'" in {

    eval(date("2015-09-17"), """< date("2015-09-18")""") should be(ValBoolean(true))
    eval(date("2015-09-18"), """< date("2015-09-18")""") should be(ValBoolean(false))
    eval(date("2015-09-19"), """< date("2015-09-18")""") should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(date("2015-09-17"), """<= date("2015-09-18")""") should be(ValBoolean(true))
    eval(date("2015-09-18"), """<= date("2015-09-18")""") should be(ValBoolean(true))
    eval(date("2015-09-19"), """<= date("2015-09-18")""") should be(ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(date("2015-09-17"), """> date("2015-09-18")""") should be(ValBoolean(false))
    eval(date("2015-09-18"), """> date("2015-09-18")""") should be(ValBoolean(false))
    eval(date("2015-09-19"), """> date("2015-09-18")""") should be(ValBoolean(true))
  }

  it should "compare with '>='" in {

    eval(date("2015-09-17"), """>= date("2015-09-18")""") should be(ValBoolean(false))
    eval(date("2015-09-18"), """>= date("2015-09-18")""") should be(ValBoolean(true))
    eval(date("2015-09-19"), """>= date("2015-09-18")""") should be(ValBoolean(true))
  }

  it should "be equal to another date" in {

    eval(date("2015-09-17"), """date("2015-09-18")""") should be (ValBoolean(false))
    eval(date("2015-09-18"), """date("2015-09-18")""") should be (ValBoolean(true))
  }

  it should """be in interval '(date("2015-09-17")..date("2015-09-19")]'""" in {

    eval(date("2015-09-17"), """(date("2015-09-17")..date("2015-09-19"))""") should be(ValBoolean(false))
    eval(date("2015-09-18"), """(date("2015-09-17")..date("2015-09-19"))""") should be(ValBoolean(true))
    eval(date("2015-09-19"), """(date("2015-09-17")..date("2015-09-19"))""") should be(ValBoolean(false))
  }

  it should """be in interval '[date("2015-09-17")..date("2015-09-19")]'""" in {

    eval(date("2015-09-17"), """[date("2015-09-17")..date("2015-09-19")]""") should be(ValBoolean(true))
    eval(date("2015-09-18"), """[date("2015-09-17")..date("2015-09-19")]""") should be(ValBoolean(true))
    eval(date("2015-09-19"), """[date("2015-09-17")..date("2015-09-19")]""") should be(ValBoolean(true))
  }

  "A time" should "compare with '<'" in {

    eval(localTime("08:31:14"), """< time("10:00:00")""") should be(ValBoolean(true))
    eval(localTime("10:10:00"), """< time("10:00:00")""") should be(ValBoolean(false))
    eval(localTime("11:31:14"), """< time("10:00:00")""") should be(ValBoolean(false))

    eval(time("10:00:00+01:00"), """< time("11:00:00+01:00")""") should be(ValBoolean(true))
    eval(time("10:00:00+01:00"), """< time("10:00:00+01:00")""") should be(ValBoolean(false))
  }

  it should "be equal to another time" in {

    eval(localTime("08:31:14"), """time("10:00:00")""") should be(ValBoolean(false))
    eval(localTime("08:31:14"), """time("08:31:14")""") should be(ValBoolean(true))

    eval(time("10:00:00+01:00"), """time("10:00:00+02:00")""") should be(ValBoolean(false))
    eval(time("10:00:00+01:00"), """time("11:00:00+02:00")""") should be(ValBoolean(false))
    eval(time("10:00:00+01:00"), """time("10:00:00+01:00")""") should be(ValBoolean(true))
  }

  it should """be in interval '[time("08:00:00")..time("10:00:00")]'""" in {

    eval(localTime("07:45:10"), """[time("08:00:00")..time("10:00:00")]""") should be(ValBoolean(false))
    eval(localTime("09:15:20"), """[time("08:00:00")..time("10:00:00")]""") should be(ValBoolean(true))
    eval(localTime("11:30:30"), """[time("08:00:00")..time("10:00:00")]""") should be(ValBoolean(false))

    eval(time("11:30:00+01:00"), """[time("08:00:00+01:00")..time("10:00:00+01:00")]""") should be(ValBoolean(false))
    eval(time("09:30:00+01:00"), """[time("08:00:00+01:00")..time("10:00:00+01:00")]""") should be(ValBoolean(true))
  }

  "A date-time" should "compare with '<'" in {

    eval(localDateTime("2015-09-17T08:31:14"), """< date and time("2015-09-17T10:00:00")""") should be(ValBoolean(true))
    eval(localDateTime("2015-09-17T10:10:00"), """< date and time("2015-09-17T10:00:00")""") should be(ValBoolean(false))
    eval(localDateTime("2015-09-17T11:31:14"), """< date and time("2015-09-17T10:00:00")""") should be(ValBoolean(false))

    eval(dateTime("2015-09-17T10:00:00+01:00"), """< date and time("2015-09-17T12:00:00+01:00")""") should be(ValBoolean(true))
    eval(dateTime("2015-09-17T10:00:00+01:00"), """< date and time("2015-09-17T09:00:00+01:00")""") should be(ValBoolean(false))
  }

  it should "be equal to another date-time" in {

    eval(localDateTime("2015-09-17T08:31:14"), """date and time("2015-09-17T10:00:00")""") should be(ValBoolean(false))
    eval(localDateTime("2015-09-17T08:31:14"), """date and time("2015-09-17T08:31:14")""") should be(ValBoolean(true))

    eval(dateTime("2015-09-17T08:30:00+01:00"), """date and time("2015-09-17T09:30:00+01:00")""") should be(ValBoolean(false))
    eval(dateTime("2015-09-17T08:30:00+01:00"), """date and time("2015-09-17T08:30:00+02:00")""") should be(ValBoolean(false))
    eval(dateTime("2015-09-17T08:30:00+01:00"), """date and time("2015-09-17T08:30:00+01:00")""") should be(ValBoolean(true))
  }

  it should """be in interval '[dante and time("2015-09-17T08:00:00")..date and time("2015-09-17T10:00:00")]'""" in {

    eval(localDateTime("2015-09-17T07:45:10"), """[date and time("2015-09-17T08:00:00")..date and time("2015-09-17T10:00:00")]""") should be(ValBoolean(false))
    eval(localDateTime("2015-09-17T09:15:20"), """[date and time("2015-09-17T08:00:00")..date and time("2015-09-17T10:00:00")]""") should be(ValBoolean(true))
    eval(localDateTime("2015-09-17T11:30:30"), """[date and time("2015-09-17T08:00:00")..date and time("2015-09-17T10:00:00")]""") should be(ValBoolean(false))

    eval(dateTime("2015-09-17T08:30:00+01:00"), """[date and time("2015-09-17T09:00:00+01:00")..date and time("2015-09-17T10:00:00+01:00")]""") should be(ValBoolean(false))
    eval(dateTime("2015-09-17T08:30:00+01:00"), """[date and time("2015-09-17T08:00:00+01:00")..date and time("2015-09-17T10:00:00+01:00")]""") should be(ValBoolean(true))
  }

  "A year-month-duration" should "compare with '<'" in {

    eval(yearMonthDuration("P1Y"), """< duration("P2Y")""") should be(ValBoolean(true))
    eval(yearMonthDuration("P1Y"), """< duration("P1Y")""") should be(ValBoolean(false))
    eval(yearMonthDuration("P1Y2M"), """< duration("P1Y")""") should be(ValBoolean(false))
  }

  it should "be equal to another duration" in {

    eval(yearMonthDuration("P1Y4M"), """duration("P1Y3M")""") should be(ValBoolean(false))
    eval(yearMonthDuration("P1Y4M"), """duration("P1Y4M")""") should be(ValBoolean(true))
  }

  it should """be in interval '[duration("P1Y")..duration("P2Y")]'""" in {

    eval(yearMonthDuration("P6M"), """[duration("P1Y")..duration("P2Y")]""") should be(ValBoolean(false))
    eval(yearMonthDuration("P1Y8M"), """[duration("P1Y")..duration("P2Y")]""") should be(ValBoolean(true))
    eval(yearMonthDuration("P2Y1M"), """[duration("P1Y")..duration("P2Y")]""") should be(ValBoolean(false))
  }

  "A day-time-duration" should "compare with '<'" in {

    eval(dayTimeDuration("P1DT4H"), """< duration("P2DT4H")""") should be(ValBoolean(true))
    eval(dayTimeDuration("P2DT4H"), """< duration("P2DT4H")""") should be(ValBoolean(false))
    eval(dayTimeDuration("P2DT8H"), """< duration("P2DT4H")""") should be(ValBoolean(false))
  }

  it should "be equal to another duration" in {

    eval(dayTimeDuration("P1DT4H"), """duration("P2DT4H")""") should be(ValBoolean(false))
    eval(dayTimeDuration("P2DT4H"), """duration("P2DT4H")""") should be(ValBoolean(true))
  }

  it should """be in interval '[duration("P1D")..duration("P2D")]'""" in {

    eval(dayTimeDuration("PT4H"), """[duration("P1D")..duration("P2D")]""") should be(ValBoolean(false))
    eval(dayTimeDuration("P1DT4H"), """[duration("P1D")..duration("P2D")]""") should be(ValBoolean(true))
    eval(dayTimeDuration("P2DT4H"), """[duration("P1D")..duration("P2D")]""") should be(ValBoolean(false))
  }

  "An empty expression ('-')" should "be always true" in {

    eval(None, "-") should be (ValBoolean(true))
  }

  "A null expression" should "compare to null" in {

    eval(1, "null") should be(ValBoolean(false))
    eval(true, "null") should be(ValBoolean(false))
    eval("a", "null") should be(ValBoolean(false))

    eval(null, "null") should be(ValBoolean(true))
  }

  "A function" should "be invoked as test" in {

    val startsWithFunction  = ValFunction(
        params = List("x", "y"),
        invoke = { case List(ValString(x), ValString(y)) => ValBoolean( x.startsWith(y) ) })

    eval("foo", """ startsWith(?, "f") """, functions = Map("startsWith" -> startsWithFunction)) should be(ValBoolean(true))
    eval("foo", """ startsWith(?, "b") """, functions = Map("startsWith" -> startsWithFunction)) should be(ValBoolean(false))
  }

  it should "be invoked as end point" in {

    val function = ValFunction(
      params = List("x"),
      invoke = { case List(ValNumber(x)) => ValNumber(x + 1) }
    )

    eval(2, "< f(2)", functions = Map("f" -> function)) should be(ValBoolean(true))
    eval(3, "< f(2)", functions = Map("f" -> function)) should be(ValBoolean(false))

    eval(3, "[f(1)..f(2)]", functions = Map("f" -> function)) should be(ValBoolean(true))
    eval(4, "[f(1)..f(2)]", functions = Map("f" -> function)) should be(ValBoolean(false))
  }
  
  "A built-in function" should "be invoked with input value" in {

    eval("foo", """ starts with(?, "f") """) should be(ValBoolean(true))
    eval("foo", """ starts with(?, "b") """) should be(ValBoolean(false))
  }

  private def eval(input: Any, expression: String, variables: Map[String, Any] = Map(), functions: Map[String, ValFunction] = Map()): Val = {
    val exp = FeelParser.parseUnaryTests(expression)
    val fs = functions.map{ case (n,f) => n -> List(f) }.toMap
    val ctx = RootContext(variables + (RootContext.defaultInputVariable -> input), fs)
    interpreter.eval(exp.get)(ctx)
  }

  private def date(date: String): Date = date

  private def localTime(time: String): LocalTime = time

  private def time(time: String): Time = time

  private def localDateTime(dateTime: String): LocalDateTime = dateTime

  private def dateTime(dateTime: String): DateTime = dateTime

  private def yearMonthDuration(duration: String): YearMonthDuration = duration

  private def dayTimeDuration(duration: String): DayTimeDuration = duration

}
