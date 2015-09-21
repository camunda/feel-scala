package org.camunda.feel.parser

import org.scalatest.FlatSpec
import org.scalatest.Matchers


/**
 * @author Philipp Ossler
 */
class ParserTest extends FlatSpec with Matchers {
  
  val parser = new FeelParser
  
  "A parser" should "parse '< 3'" in {
    parser.parse("< 3").get should be (LessThan(ConstNumber(3)))
  }
  
  it should "not parse '< NaN'" in {
    parser.parse("< NaN").successful should be (false) 
  }
  
  it should "not parse '< true'" in {
    parser.parse("< true").successful should be (false)
  }
  
  it should "parse '<= 3'" in {
    parser.parse("<= 3").get should be (LessOrEqual(ConstNumber(3)))
  }
  
  it should "parse '> 3'" in {
    parser.parse("> 3").get should be (GreaterThat(ConstNumber(3)))
  }
  
  it should "parse '>= 3'" in {
    parser.parse(">= 3").get should be (GreaterOrEqual(ConstNumber(3)))
  }
  
}
