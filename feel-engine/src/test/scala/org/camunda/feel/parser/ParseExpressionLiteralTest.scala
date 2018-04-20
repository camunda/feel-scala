package org.camunda.feel.parser

import org.camunda.feel._
import org.camunda.feel.parser.FeelParser._

import org.scalatest.FlatSpec
import org.scalatest.Matchers

/**
 * @author Philipp Ossler
 */
class ParseExpressionLiteralTest extends FlatSpec with Matchers {

  "A literal" should "be a number" in {

    parse("3") should be(ConstNumber(3))
    parse("3.2") should be(ConstNumber(3.2))
    parse(".2") should be(ConstNumber(.2))
  }

  it should "be a string" in {

    parse(""" "a" """) should be(ConstString("a"))
  }

  it should "be a boolean" in {

    parse("true") should be(ConstBool(true))
    parse("false") should be(ConstBool(false))
  }

  it should "be a date" in {

    parse("""date("2015-09-18")""") should be(ConstDate("2015-09-18"))
  }

  it should "be a time" in {

    parse("""time("10:31:10")""") should be(ConstLocalTime("10:31:10"))
  }

  it should "be a time with offset" in {

    parse("""time("10:31:10+01:00")""") should be(ConstTime("10:31:10+01:00"))
    parse("""time("10:31:10-02:00")""") should be(ConstTime("10:31:10-02:00"))
  }
  
  it should "be a time with zone" in {

    parse("""time("10:31:10@Europe/Paris")""") should be(ConstTime("10:31:10@Europe/Paris"))
    parse("""time("10:31:10@Etc/UTC")""") should be(ConstTime("10:31:10@Etc/UTC"))
  }

  it should "be a date-time" in {

    parse("""date and time("2015-09-18T10:31:10")""") should be(ConstLocalDateTime("2015-09-18T10:31:10"))
  }

  it should "be a date-time with offset" in {

    parse("""date and time("2015-09-18T10:31:10+01:00")""") should be(ConstDateTime("2015-09-18T10:31:10+01:00"))
    parse("""date and time("2015-09-18T10:31:10-02:00")""") should be(ConstDateTime("2015-09-18T10:31:10-02:00"))
  }

  it should "be a year-month-duration" in {

    parse("""duration("P1Y2M")""") should be(ConstYearMonthDuration("P1Y2M"))
  }

  it should "be a day-time-duration" in {

    parse("""duration("P1DT2H3M4S")""") should be(ConstDayTimeDuration("P1DT2H3M4S"))
  }

  it should "be a context" in {

    parse("{}") should be(ConstContext(List()))

    parse("{ a : 1 }") should be(ConstContext(List(
        ("a" -> ConstNumber(1)) )))

    parse("{ a:1, b:true }") should be(ConstContext(List(
        ("a" -> ConstNumber(1)),
        ("b" -> ConstBool(true)) )))
  }

  it should "be a list" in {

    parse("[]") should be(ConstList(List()))

    parse("[1]") should be(ConstList( List(ConstNumber(1)) ))

    parse("[1, 2]") should be(ConstList(List(
        ConstNumber(1),
        ConstNumber(2) )))
  }

  private def parse(expression: String): Exp =
    FeelParser.parseExpression(expression) match {
      case Success(exp, _) => exp
      case e: NoSuccess => throw new RuntimeException(s"failed to parse expression '$expression':\n$e")
    }

}
