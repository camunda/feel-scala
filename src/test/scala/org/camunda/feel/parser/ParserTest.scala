package org.camunda.feel.parser

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.joda.time.LocalDate
import com.github.nscala_time.time.Imports._

/**
 * @author Philipp Ossler
 */
class ParserTest extends FlatSpec with Matchers {

  val parser = new FeelParser

  "A parser" should "parse a number" in {

    parse("3") should be(ConstNumber(3))
    parse("3.2") should be(ConstNumber(3.2))
    parse(".2") should be(ConstNumber(.2))
  }

  it should "parse a boolean" in {

    parse("true") should be(ConstBool(true))
    parse("false") should be(ConstBool(false))
  }

  it should "parse a date" in {

    parse("""date("2015-09-18")""") should be(ConstDate(asDate("2015-09-18")))
  }

  it should "parse '< 3'" in {
    parse("< 3") should be(LessThan(ConstNumber(3)))
  }

  it should "parse '<= 3'" in {
    parse("<= 3") should be(LessOrEqual(ConstNumber(3)))
  }

  it should "parse '> 3'" in {
    parse("> 3") should be(GreaterThan(ConstNumber(3)))
  }

  it should "parse '>= 3'" in {
    parse(">= 3") should be(GreaterOrEqual(ConstNumber(3)))
  }

  it should """parse '< date("2015-09-18")'""" in {
    parse("""< date("2015-09-18")""") should be(LessThan(ConstDate(asDate("2015-09-18"))))
  }

  it should "parse '(2 .. 4)'" in {
    val interval = Interval(
      start = OpenIntervalBoundary(ConstNumber(2)),
      end = OpenIntervalBoundary(ConstNumber(4)))

    parse("(2 .. 4)") should be(interval)
  }

  it should "parse ']2 .. 4['" in {
    val interval = Interval(
      start = OpenIntervalBoundary(ConstNumber(2)),
      end = OpenIntervalBoundary(ConstNumber(4)))

    parse("]2 .. 4[") should be(interval)
  }

  it should "parse '[2 .. 4]'" in {
    val interval = Interval(
      start = ClosedIntervalBoundary(ConstNumber(2)),
      end = ClosedIntervalBoundary(ConstNumber(4)))

    parse("[2 .. 4]") should be(interval)
  }

  it should """parse '(date("2015-09-17") .. date("2015-09-19"))'""" in {
    val interval = Interval(
      start = OpenIntervalBoundary(ConstDate(asDate("2015-09-17"))),
      end = OpenIntervalBoundary(ConstDate(asDate("2015-09-19"))))

    parse("""(date("2015-09-17") .. date("2015-09-19"))""") should be(interval)
  }
  
  it should """parse '[date("2015-09-17") .. date("2015-09-19")]'""" in {
    val interval = Interval(
      start = ClosedIntervalBoundary(ConstDate(asDate("2015-09-17"))),
      end = ClosedIntervalBoundary(ConstDate(asDate("2015-09-19"))))

    parse("""[date("2015-09-17") .. date("2015-09-19")]""") should be(interval)
  }

  private def parse(expression: String): Exp = {
    val result = parser.parse(expression)
    result.get
  }

  private def asDate(date: String): LocalDate = LocalDate.parse(date)

}
