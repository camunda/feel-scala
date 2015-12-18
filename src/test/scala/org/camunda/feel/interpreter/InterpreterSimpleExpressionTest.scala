package org.camunda.feel.interpreter

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._
import org.camunda.feel.parser.FeelParser


/**
 * @author Philipp Ossler
 */
class InterpreterSimpleExpressionTest extends FlatSpec with Matchers {
  
  val interpreter = new FeelInterpreter
  
  "A number" should "add to '4'" in {
    
    eval("2+4") should be(ValNumber(6))
  }
  
  it should "subtract from '2'" in {
    
    eval("4-2") should be(ValNumber(2))
  }
  
  it should "multiply by '3'" in {
    
    eval("3*3") should be(ValNumber(9))
  }
  
  it should "divide by '4'" in {
    
    eval("8/4") should be(ValNumber(2))
  }
  
  it should "exponentiate by '3'" in {
    
    eval("2**3") should be(ValNumber(8))
  }
  
  it should "negate" in {
    
    eval("-2") should be(ValNumber(-2))
  }
  
  it should "compare with '='" in {
    
    eval("x=2", Map("x" -> 2)) should be(ValBoolean(true))
    eval("x=2", Map("x" -> 3)) should be(ValBoolean(false))
  }
  
  it should "compare with '!='" in {
    
    eval("x!=2", Map("x" -> 2)) should be(ValBoolean(false))
    eval("x!=2", Map("x" -> 3)) should be(ValBoolean(true))
  }
  
  it should "compare with '<'" in {
    
    eval("x<2", Map("x" -> 1)) should be(ValBoolean(true))
    eval("x<2", Map("x" -> 2)) should be(ValBoolean(false))
  }
  
  it should "compare with '<='" in {
    
    eval("x<=2", Map("x" -> 2)) should be(ValBoolean(true))
    eval("x<=2", Map("x" -> 3)) should be(ValBoolean(false))
  }
  
  it should "compare with '>'" in {
    
    eval("x>2", Map("x" -> 2)) should be(ValBoolean(false))
    eval("x>2", Map("x" -> 3)) should be(ValBoolean(true))
  }
  
  it should "compare with '>='" in {
    
    eval("x>=2", Map("x" -> 2)) should be(ValBoolean(true))
    eval("x>=2", Map("x" -> 1)) should be(ValBoolean(false))
  }
  
  private def eval(expression: String, variables: Map[String, Any] = Map()): Val = {
    val exp = FeelParser.parseExpression(expression)
    interpreter.eval(exp.get)(Context(variables))
  }

  private def date(date: String): Date = date

  private def time(time: String): Time = time
  
  private def duration(duration: String): Duration = duration
  
}