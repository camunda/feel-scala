package org.camunda.feel.parser

import org.camunda.feel._

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

  it should "parse a date" in {

    parse("""date("2015-09-18")""") should be(ConstDate("2015-09-18"))
  }
  
  it should "parse a time" in {
    
    parse("""time("10:31:10")""") should be(ConstTime("10:31:10"))
  }
  
  it should "parse a duration" in {
    
    parse("""duration("P1D")""") should be(ConstDuration("P1D"))
  }
  
  it should "ignore an one line comment '// ...'" in {
    
    parse("""duration("P1D") // one day""") should be(ConstDuration("P1D"))
  }
  
  it should "ignore a multi line comment '/* ... */'" in {
    
    parse("""duration("P1DT4H") /* 
                one day and 4 hours
             */ """)
  }
  
  private def parse(expression: String): Exp = {
    val result = FeelParser.parseSimpleExpression(expression)
    result.get
  }
  
}