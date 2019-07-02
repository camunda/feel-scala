package org.camunda.feel.parser

import org.camunda.feel._
import org.camunda.feel.parser.FeelParser._

import org.scalatest.FlatSpec
import org.scalatest.Matchers

/**
  * @author Philipp Ossler
  */
class ParserUnaryTest extends FlatSpec with Matchers {

  "The input value" should "be equal to a number" in {

    parse("3") should be(InputEqualTo(ConstNumber(3)))
    parse("3.2") should be(InputEqualTo(ConstNumber(3.2)))
    parse(".2") should be(InputEqualTo(ConstNumber(.2)))
    parse("-1") should be(InputEqualTo(ConstNumber(-1)))
  }

  it should "be equal to a boolean" in {

    parse("true") should be(InputEqualTo(ConstBool(true)))
    parse("false") should be(InputEqualTo(ConstBool(false)))
  }

  it should "be equal to a string" in {

    parse(""" "abc" """) should be(InputEqualTo(ConstString("abc")))
  }

  it should "be equal to a date" in {

    parse("""date("2015-09-18")""") should be(
      InputEqualTo(ConstDate("2015-09-18")))
  }

  it should "be equal to a time" in {

    parse("""time("10:31:10")""") should be(
      InputEqualTo(ConstLocalTime("10:31:10")))
  }

  it should "be equal to a time with offset" in {

    parse("""time("10:31:10+01:00")""") should be(
      InputEqualTo(ConstTime("10:31:10+01:00")))
    parse("""time("10:31:10-02:00")""") should be(
      InputEqualTo(ConstTime("10:31:10-02:00")))
  }

  it should "be equal to a date-time" in {

    parse("""date and time("2015-09-18T10:31:10")""") should be(
      InputEqualTo(ConstLocalDateTime("2015-09-18T10:31:10")))
  }

  it should "be equal to a date-time with offset" in {

    parse("""date and time("2015-09-18T10:31:10+01:00")""") should be(
      InputEqualTo(ConstDateTime("2015-09-18T10:31:10+01:00")))
    parse("""date and time("2015-09-18T10:31:10-02:00")""") should be(
      InputEqualTo(ConstDateTime("2015-09-18T10:31:10-02:00")))
  }

  it should "be equal to a day-time-duration" in {

    parse("""duration("P4DT2H")""") should be(
      InputEqualTo(ConstDayTimeDuration("P4DT2H")))
  }

  it should "be equal to a year-month-duration" in {

    parse("""duration("P2Y1M")""") should be(
      InputEqualTo(ConstYearMonthDuration("P2Y1M")))
  }

  it should "be less than a number" in {
    parse("< 3") should be(InputLessThan(ConstNumber(3)))
  }

  it should "be less than or equal to a number" in {
    parse("<= 3") should be(InputLessOrEqual(ConstNumber(3)))
  }

  it should "be greater than a number" in {
    parse("> 3") should be(InputGreaterThan(ConstNumber(3)))
  }

  it should "be greater or equal to a nu,ber" in {
    parse(">= 3") should be(InputGreaterOrEqual(ConstNumber(3)))
  }

  it should "be less than a date" in {
    parse("""< date("2015-09-18")""") should be(
      InputLessThan(ConstDate("2015-09-18")))
  }

  it should "be less than a time" in {
    parse("""< time("15:41:10")""") should be(
      InputLessThan(ConstLocalTime("15:41:10")))
  }

  it should "be less than a duration" in {
    parse("""< duration("P1D")""") should be(
      InputLessThan(ConstDayTimeDuration("P1D")))
  }

  it should "be in open interval '(2..4)'" in {

    parse("(2..4)") should be(
      Interval(start = OpenIntervalBoundary(ConstNumber(2)),
               end = OpenIntervalBoundary(ConstNumber(4))))
  }

  it should "be in open interval ']2..4['" in {

    parse("]2..4[") should be(
      Interval(start = OpenIntervalBoundary(ConstNumber(2)),
               end = OpenIntervalBoundary(ConstNumber(4))))
  }

  it should "be in closed interval '[2..4]'" in {

    parse("[2..4]") should be(
      Interval(start = ClosedIntervalBoundary(ConstNumber(2)),
               end = ClosedIntervalBoundary(ConstNumber(4))))
  }

  it should "be in open date interval" in {

    parse("""(date("2015-09-17")..date("2015-09-19"))""") should be(
      Interval(start = OpenIntervalBoundary(ConstDate("2015-09-17")),
               end = OpenIntervalBoundary(ConstDate("2015-09-19"))))
  }

  it should "be in closed date interval" in {

    parse("""[date("2015-09-17")..date("2015-09-19")]""") should be(
      Interval(start = ClosedIntervalBoundary(ConstDate("2015-09-17")),
               end = ClosedIntervalBoundary(ConstDate("2015-09-19"))))
  }

  it should "match one of the tests '2,3,4'" in {

    parse("2,3,4") should be(
      AtLeastOne(
        List(InputEqualTo(ConstNumber(2)),
             InputEqualTo(ConstNumber(3)),
             InputEqualTo(ConstNumber(4)))))
  }

  it should """match one of the tests '"a","b"'""" in {

    parse(""" "a","b" """) should be(
      AtLeastOne(
        List(InputEqualTo(ConstString("a")), InputEqualTo(ConstString("b")))))
  }

  it should "be equal to 'null'" in {

    parse("null") should be(InputEqualTo(ConstNull))
  }

  it should "be not equal to test" in {

    parse("not(3)") should be(Not(InputEqualTo(ConstNumber(3))))
  }

  it should "be equal to a qualified name 'var'" in {

    parse("var") should be(InputEqualTo(Ref("var")))
  }

  it should "be equal to a qualified name 'qualified.var'" in {

    parse("qualified.var") should be(
      InputEqualTo(Ref(List("qualified", "var"))))
  }

  it should "be less than a qualified name" in {

    parse("< var") should be(InputLessThan(Ref("var")))
  }

  it should "be equal to an escaped name `a b`" in {

    parse("`a b`") should be(InputEqualTo(Ref(List("a b"))))
  }

  it should "be equal to an escaped name `a-b`" in {

    parse("`a-b`") should be(InputEqualTo(Ref(List("a-b"))))
  }

  it should "be equal to an escaped name 'a b'.'c d'" in {

    parse("`a b`.`c d`") should be(InputEqualTo(Ref(List("a b", "c d"))))
  }

  "An unary test" should "be '-'" in {

    parse("-") should be(ConstBool(true))
  }

  it should "be a function invocation without parameters" in {

    parse("f()") should be(
      FunctionInvocation("f", params = PositionalFunctionParameters(List())))
  }

  it should "be a function invocation with positional parameters" in {

    parse("fib(1)") should be(
      FunctionInvocation("fib",
                         params =
                           PositionalFunctionParameters(List(ConstNumber(1)))))

    parse("""concat("in", x)""") should be(
      FunctionInvocation("concat",
                         params = PositionalFunctionParameters(
                           List(ConstString("in"), Ref("x")))))
  }

  it should "be a function invocation with named parameters" in {

    parse("f(a:1)") should be(
      FunctionInvocation("f",
                         params =
                           NamedFunctionParameters(Map("a" -> ConstNumber(1)))))

    parse("f(a:1, b:true)") should be(
      FunctionInvocation("f",
                         params = NamedFunctionParameters(
                           Map("a" -> ConstNumber(1), "b" -> ConstBool(true)))))
  }

  it should "be a function invocation with ? (input value)" in {

    parse("""starts with(?, "f")""") should be(
      FunctionInvocation("starts with",
                         params = PositionalFunctionParameters(
                           List(ConstInputValue, ConstString("f")))))
  }

  it should "has an expression as endpoint" in {

    parse("< (2 + 3)") should be(
      InputLessThan(
        Addition(
          ConstNumber(2),
          ConstNumber(3)
        )
      ))
  }

  private def parse(expression: String): Exp =
    FeelParser.parseUnaryTests(expression) match {
      case Success(exp, _) => exp
      case e: NoSuccess =>
        throw new RuntimeException(
          s"failed to parse expression '$expression':\n$e")
    }

}
