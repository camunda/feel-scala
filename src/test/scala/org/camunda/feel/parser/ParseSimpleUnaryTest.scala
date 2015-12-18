package org.camunda.feel.parser

import org.camunda.feel._

import org.scalatest.FlatSpec
import org.scalatest.Matchers

/**
 * @author Philipp Ossler
 */
class ParserSimpleUnaryTest extends FlatSpec with Matchers {

  "A parser for simple unary test" should "parse a number" in {

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
    
    parse("""time("15:41:10")""") should be(InputEqualTo(ConstTime("15:41:10")))
  }
  
  it should "parse a duration (days and time)" in {
    
    parse("""duration("P4DT2H")""") should be(InputEqualTo(ConstDuration("P4DT2H")))
  }
  
  it should "parse a duration (years and months)" in {
    
    parse("""duration("P2Y1M")""") should be(InputEqualTo(ConstDuration("P2Y1M")))
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
    parse("""< time("15:41:10")""") should be(InputLessThan(ConstTime("15:41:10")))
  }
  
  it should """parse '< duration("P1D")'""" in {
    parse("""< duration("P1D")""") should be(InputLessThan(ConstDuration("P1D")))
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

  it should "parse 'not(3)' (negation)" in {

    parse("not(3)") should be(
      Not(
        InputEqualTo(ConstNumber(3))))
  }
  
  it should "parse 'var' (qualified name)" in {
    
    parse("var") should be (InputEqualTo(Ref("var")))
  }
  
  it should "parse 'qualified.var' (qualified name)" in {
    
    parse("qualified.var") should be (InputEqualTo(Ref("qualified.var")))
  }
  
  it should "parse '< var'" in {
    
    parse("< var") should be (InputLessThan(Ref("var")))
  }

  private def parse(expression: String): Exp = {
    val result = FeelParser.parseSimpleUnaryTest(expression)
    result.get
  }

}
