package org.camunda.feel.parser

import org.camunda.feel._

import org.scalatest.FlatSpec
import org.scalatest.Matchers

/**
 * @author Philipp Ossler
 */
class ParserSimpleUnaryTest extends FlatSpec with Matchers {

  val parser = new FeelParser

  "A parser for simple unary test" should "parse a number" in {

    parse("3") should be(Equal(ConstNumber(3)))
    parse("3.2") should be(Equal(ConstNumber(3.2)))
    parse(".2") should be(Equal(ConstNumber(.2)))
  }

  it should "parse a boolean" in {

    parse("true") should be(Equal(ConstBool(true)))
    parse("false") should be(Equal(ConstBool(false)))
  }

  it should "parse a string" in {

    parse(""" "abc" """) should be(Equal(ConstString("abc")))
  }

  it should "parse a date" in {

    parse("""date("2015-09-18")""") should be(Equal(ConstDate("2015-09-18")))
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
    parse("""< date("2015-09-18")""") should be(LessThan(ConstDate("2015-09-18")))
  }

  it should "parse '(2 .. 4)'" in {

    parse("(2 .. 4)") should be(
      Interval(
        start = OpenIntervalBoundary(ConstNumber(2)),
        end = OpenIntervalBoundary(ConstNumber(4))))
  }

  it should "parse ']2 .. 4['" in {

    parse("]2 .. 4[") should be(
      Interval(
        start = OpenIntervalBoundary(ConstNumber(2)),
        end = OpenIntervalBoundary(ConstNumber(4))))
  }

  it should "parse '[2 .. 4]'" in {

    parse("[2 .. 4]") should be(
      Interval(
        start = ClosedIntervalBoundary(ConstNumber(2)),
        end = ClosedIntervalBoundary(ConstNumber(4))))
  }

  it should """parse '(date("2015-09-17") .. date("2015-09-19"))'""" in {

    parse("""(date("2015-09-17") .. date("2015-09-19"))""") should be(
      Interval(
        start = OpenIntervalBoundary(ConstDate("2015-09-17")),
        end = OpenIntervalBoundary(ConstDate("2015-09-19"))))
  }

  it should """parse '[date("2015-09-17") .. date("2015-09-19")]'""" in {

    parse("""[date("2015-09-17") .. date("2015-09-19")]""") should be(
      Interval(
        start = ClosedIntervalBoundary(ConstDate("2015-09-17")),
        end = ClosedIntervalBoundary(ConstDate("2015-09-19"))))
  }

  it should "parse '2,3,4' (multiple tests)" in {

    parse("2,3,4") should be(
      AtLeastOne(List(
        Equal(ConstNumber(2)),
        Equal(ConstNumber(3)),
        Equal(ConstNumber(4)))))
  }

  it should """parse '"a","b"' (multiple tests)""" in {

    parse(""" "a","b" """) should be(
      AtLeastOne(List(
        Equal(ConstString("a")),
        Equal(ConstString("b")))))
  }

  private def parse(expression: String): Exp = {
    val result = parser.parseSimpleUnaryTest(expression)
    result.get
  }

}
