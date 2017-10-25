package org.camunda.feel.parser

import org.camunda.feel._
import org.camunda.feel.parser.FeelParser._

import org.scalatest.FlatSpec
import org.scalatest.Matchers

/**
 * @author Philipp Ossler
 */
class ParseSimpleExpressionTest extends FlatSpec with Matchers {

  "A parser for simple expression" should "parse number" in {

    parse("3") should be(ConstNumber(3))
    parse("3.2") should be(ConstNumber(3.2))
    parse(".2") should be(ConstNumber(.2))
  }

  it should "parse a string" in {

    parse(""" "a" """) should be(ConstString("a"))
  }

  it should "parse a qualified name" in {

    parse("b") should be(Ref("b"))
  }

  it should "parse a boolean" in {

    parse("true") should be(ConstBool(true))
    parse("false") should be(ConstBool(false))
  }

  it should "parse a time" in {

    parse("""time("10:31:10")""") should be(ConstLocalTime("10:31:10"))
  }

  it should "parse a time with offset" in {

    parse("""time("10:31:10+01:00")""") should be(ConstTime("10:31:10+01:00"))
    parse("""time("10:31:10-02:00")""") should be(ConstTime("10:31:10-02:00"))
  }

  it should "parse a date" in {

    parse("""date("2015-09-18")""") should be(ConstDate("2015-09-18"))
  }

  it should "parse a date-time" in {

    parse("""date and time("2015-09-18T10:31:10")""") should be(ConstLocalDateTime("2015-09-18T10:31:10"))
  }

  it should "parse a date-time with offset" in {

    parse("""date and time("2015-09-18T10:31:10+01:00")""") should be(ConstDateTime("2015-09-18T10:31:10+01:00"))
    parse("""date and time("2015-09-18T10:31:10-02:00")""") should be(ConstDateTime("2015-09-18T10:31:10-02:00"))
  }

  it should "parse a year-month-duration" in {

    parse("""duration("P1Y")""") should be(ConstYearMonthDuration("P1Y"))
  }

  it should "parse a day-time-duration" in {

    parse("""duration("P1D")""") should be(ConstDayTimeDuration("P1D"))
  }

  it should "ignore an one line comment '// ...'" in {

    parse("""duration("P1D") // one day""") should be(ConstDayTimeDuration("P1D"))
  }

  it should "ignore a multi line comment '/* ... */'" in {

    parse("""duration("P1DT4H") /*
                one day and 4 hours
             */ """)
  }

  it should "parse an addition" in {

    // numeric addition
    parse("2+3") should be(Addition(ConstNumber(2), ConstNumber(3)))
  }

  it should "parse a substraction" in {

    // numeric subtraction
    parse("3-2") should be(Subtraction(ConstNumber(3), ConstNumber(2)))
  }

  it should "parse a multiplication" in {

    parse("2*4") should be(Multiplication(ConstNumber(2), ConstNumber(4)))
  }

  it should "parse a division" in {

    parse("4/2") should be(Division(ConstNumber(4), ConstNumber(2)))
  }

  it should "parse an exponentiation" in {

    parse("2**4") should be(Exponentiation(ConstNumber(2), ConstNumber(4)))
  }

  it should "parse an arithmetic negation" in {

    parse("-4") should be(ArithmeticNegation(ConstNumber(4)))
  }

  private def parse(expression: String): Exp =
    FeelParser.parseSimpleExpression(expression) match {
      case Success(exp, _) => exp
      case e: NoSuccess => throw new RuntimeException(s"failed to parse expression '$expression':\n$e")
    }

}
