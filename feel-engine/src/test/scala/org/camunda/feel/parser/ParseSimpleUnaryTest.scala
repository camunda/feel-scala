package org.camunda.feel.parser

import org.camunda.feel._
import org.camunda.feel.parser.FeelParser._

import org.scalatest.FlatSpec
import org.scalatest.Matchers

/**
 * @author Philipp Ossler
 */
class ParserUnaryTest extends FlatSpec with Matchers {

  "A parser for unary test" should "parse a number" in {

    parse("3") should be(InputEqualTo(ConstNumber(3)))
    parse("3.2") should be(InputEqualTo(ConstNumber(3.2)))
    parse(".2") should be(InputEqualTo(ConstNumber(.2)))
  }

  it should "parse a boolean" in {

    parse("true") should be(InputEqualTo(ConstBool(true)))
    parse("false") should be(InputEqualTo(ConstBool(false)))
  }

  it should "parse a string" in {

    parse(""" "abc" """) should be(InputEqualTo(ConstString("abc")))
  }

  it should "parse a date" in {

    parse("""date("2015-09-18")""") should be(InputEqualTo(ConstDate("2015-09-18")))
  }

  it should "parse a time" in {

    parse("""time("10:31:10")""") should be(InputEqualTo(ConstLocalTime("10:31:10")))
  }

  it should "parse a time with offset" in {

    parse("""time("10:31:10+01:00")""") should be(InputEqualTo(ConstTime("10:31:10+01:00")))
    parse("""time("10:31:10-02:00")""") should be(InputEqualTo(ConstTime("10:31:10-02:00")))
  }

  it should "parse a date-time" in {

    parse("""date and time("2015-09-18T10:31:10")""") should be(InputEqualTo(ConstLocalDateTime("2015-09-18T10:31:10")))
  }

  it should "parse a date-time with offset" in {

    parse("""date and time("2015-09-18T10:31:10+01:00")""") should be(InputEqualTo(ConstDateTime("2015-09-18T10:31:10+01:00")))
    parse("""date and time("2015-09-18T10:31:10-02:00")""") should be(InputEqualTo(ConstDateTime("2015-09-18T10:31:10-02:00")))
  }

  it should "parse a day-time-duration" in {

    parse("""duration("P4DT2H")""") should be(InputEqualTo(ConstDayTimeDuration("P4DT2H")))
  }

  it should "parse a year-month-duration" in {

    parse("""duration("P2Y1M")""") should be(InputEqualTo(ConstYearMonthDuration("P2Y1M")))
  }

  it should "parse '< 3'" in {
    parse("< 3") should be(InputLessThan(ConstNumber(3)))
  }

  it should "parse '<= 3'" in {
    parse("<= 3") should be(InputLessOrEqual(ConstNumber(3)))
  }

  it should "parse '> 3'" in {
    parse("> 3") should be(InputGreaterThan(ConstNumber(3)))
  }

  it should "parse '>= 3'" in {
    parse(">= 3") should be(InputGreaterOrEqual(ConstNumber(3)))
  }

  it should """parse '< date("2015-09-18")'""" in {
    parse("""< date("2015-09-18")""") should be(InputLessThan(ConstDate("2015-09-18")))
  }

  it should """parse '< time("15:41:10")'""" in {
    parse("""< time("15:41:10")""") should be(InputLessThan(ConstLocalTime("15:41:10")))
  }

  it should """parse '< duration("P1D")'""" in {
    parse("""< duration("P1D")""") should be(InputLessThan(ConstDayTimeDuration("P1D")))
  }

  it should "parse '(2..4)'" in {

    parse("(2..4)") should be(
      Interval(
        start = OpenIntervalBoundary(ConstNumber(2)),
        end = OpenIntervalBoundary(ConstNumber(4))))
  }

  it should "parse ']2..4['" in {

    parse("]2..4[") should be(
      Interval(
        start = OpenIntervalBoundary(ConstNumber(2)),
        end = OpenIntervalBoundary(ConstNumber(4))))
  }

  it should "parse '[2..4]'" in {

    parse("[2..4]") should be(
      Interval(
        start = ClosedIntervalBoundary(ConstNumber(2)),
        end = ClosedIntervalBoundary(ConstNumber(4))))
  }

  it should """parse '(date("2015-09-17")..date("2015-09-19"))'""" in {

    parse("""(date("2015-09-17")..date("2015-09-19"))""") should be(
      Interval(
        start = OpenIntervalBoundary(ConstDate("2015-09-17")),
        end = OpenIntervalBoundary(ConstDate("2015-09-19"))))
  }

  it should """parse '[date("2015-09-17")..date("2015-09-19")]'""" in {

    parse("""[date("2015-09-17")..date("2015-09-19")]""") should be(
      Interval(
        start = ClosedIntervalBoundary(ConstDate("2015-09-17")),
        end = ClosedIntervalBoundary(ConstDate("2015-09-19"))))
  }

  it should "parse '2,3,4' (multiple tests)" in {

    parse("2,3,4") should be(
      AtLeastOne(List(
        InputEqualTo(ConstNumber(2)),
        InputEqualTo(ConstNumber(3)),
        InputEqualTo(ConstNumber(4)))))
  }

  it should """parse '"a","b"' (multiple tests)""" in {

    parse(""" "a","b" """) should be(
      AtLeastOne(List(
        InputEqualTo(ConstString("a")),
        InputEqualTo(ConstString("b")))))
  }

  it should "parse '-'" in {

    parse("-") should be(ConstBool(true))
  }

  it should "parse 'null'" in {

    parse("null") should be(InputEqualTo(ConstNull))
  }

  it should "parse 'not(3)' (negation)" in {

    parse("not(3)") should be(
      Not(
        InputEqualTo(ConstNumber(3))))
  }

  it should "parse 'var' (qualified name)" in {

    parse("var") should be (InputEqualTo(Ref("var")))
  }

  it should "parse 'qualified.var' (qualified name)" in {

    parse("qualified.var") should be (InputEqualTo(Ref( List("qualified","var") )))
  }

  it should "parse '< var'" in {

    parse("< var") should be (InputLessThan(Ref("var")))
  }

   private def parse(expression: String): Exp =
    FeelParser.parseUnaryTests(expression) match {
      case Success(exp, _) => exp
      case e: NoSuccess => throw new RuntimeException(s"failed to parse expression '$expression':\n$e")
    }

}
