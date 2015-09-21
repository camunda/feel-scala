package org.camunda.feel.parser

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.joda.time.LocalDate
import com.github.nscala_time.time.Imports._
import org.camunda.feel.types.OpenIntervalBoundary
import org.camunda.feel.types.ClosedIntervalBoundary


/**
 * @author Philipp Ossler
 */
class ParserTest extends FlatSpec with Matchers {
  
  val parser = new FeelParser
  
  "A parser" should "parse a number" in {
    
    parser.parse("3").get should be (ConstNumber(3))
    parser.parse("3.2").get should be (ConstNumber(3.2))
    parser.parse(".2").get should be (ConstNumber(.2))
  }
  
  it should "parse a boolean" in {
    
    parser.parse("true").get should be (ConstBool(true))
    parser.parse("false").get should be (ConstBool(false))
  }
  
  it should "parse a date" in {
    
    parser.parse("""date("2015-09-18")""").get should be (ConstDate(LocalDate.parse("2015-09-18")))
  }
  
  it should "parse '< 3'" in {
    parser.parse("< 3").get should be (LessThan(ConstNumber(3)))
  }
  
  it should "parse '<= 3'" in {
    parser.parse("<= 3").get should be (LessOrEqual(ConstNumber(3)))
  }
  
  it should "parse '> 3'" in {
    parser.parse("> 3").get should be (GreaterThan(ConstNumber(3)))
  }
  
  it should "parse '>= 3'" in {
    parser.parse(">= 3").get should be (GreaterOrEqual(ConstNumber(3)))
  }
  
  it should """parse '< date("2015-09-18")'""" in {
    parser.parse("""< date("2015-09-18")""").get should be (LessThan(ConstDate(LocalDate.parse("2015-09-18"))))
  }
  
  it should "parse '(2 .. 4)'" in {
    val interval = Interval(start = OpenIntervalBoundary(ConstNumber(2)), end = OpenIntervalBoundary(ConstNumber(4)))
    
    parser.parse("(2 .. 4)").get should be (interval)
  }
  
  it should "parse ']2 .. 4['" in {
    val interval = Interval(start = OpenIntervalBoundary(ConstNumber(2)), end = OpenIntervalBoundary(ConstNumber(4)))
    
    parser.parse("]2 .. 4[").get should be (interval)
  }
 
  it should "parse '[2 .. 4]'" in {
    val interval = Interval(start = ClosedIntervalBoundary(ConstNumber(2)), end = ClosedIntervalBoundary(ConstNumber(4)))
    
    parser.parse("[2 .. 4]").get should be (interval)
  }
}
