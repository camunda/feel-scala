package org.camunda.feel.parser

import org.camunda.feel._

import org.scalatest.FlatSpec
import org.scalatest.Matchers

/**
 * @author Philipp Ossler
 */
class ParseSimpleExpressionTest extends FlatSpec with Matchers {
  
  val parser = new FeelParser
  
  "A parser for simple expression" should "parse number" in {
    
    parse("3") should be(ConstNumber(3))
    parse("3.2") should be(ConstNumber(3.2))
    parse(".2") should be(ConstNumber(.2))
  }
  
  it should "parse a boolean" in {

    parse("true") should be(ConstBool(true))
    parse("false") should be(ConstBool(false))
  }

  it should "parse a date" in {

    parse("""date("2015-09-18")""") should be(ConstDate("2015-09-18"))
  }
  
  private def parse(expression: String): Exp = {
    val result = parser.parseSimpleExpression(expression)
    result.get
  }
  
}