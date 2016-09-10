package org.camunda.feel.interpreter

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._
import org.camunda.feel.parser.FeelParser


/**
 * @author Philipp Ossler
 */
class InterpreterExpressionTest extends FlatSpec with Matchers {
  
  val interpreter = new FeelInterpreter
  
  "An interpreter for expression" should "interpret a number" in {
    
    eval("2") should be(ValNumber(2))
  }
  
  it should "interpret a string" in {
    
    eval(""" "a" """) should be(ValString("a"))
  }
  
  it should "interpret a boolean" in {
    
    eval("true") should be(ValBoolean(true))
  }
  
  it should "interpret null" in {
    
    eval("null") should be(ValNull)
  }
  
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
  
  // TODO add test case for add. and sub. of durations
  // TODO add tests cases for compare op's with date, time and duration
  
  "A function definition" should "be interpeted" in {
    
    val function = eval("function(x) x + 1")
    
    function shouldBe a [ValFunction]
    function.asInstanceOf[ValFunction].params should be(List("x")) 
  }
  
  it should "be invoked without parameter" in {

    val variables = Map("f" -> eval("""function() "invoked" """))

    eval("f()", variables) should be(ValString("invoked"))
  }

  it should "be invoked with one positional parameter" in {

    val variables = Map("f" -> eval("function(x) x + 1"))

    eval("f(1)", variables) should be(ValNumber(2))
    eval("f(2)", variables) should be(ValNumber(3))
  }
  
  it should "be invoked with positional parameters" in {

    val variables = Map("add" -> eval("function(x,y) x + y"))

    eval("add(1,2)", variables) should be(ValNumber(3))
    eval("add(2,3)", variables) should be(ValNumber(5))
  }
  
  it should "be invoked with one named parameter" in {

    val variables = Map("f" -> eval("function(x) x + 1"))

    eval("f(x:1)", variables) should be(ValNumber(2))
    eval("f(x:2)", variables) should be(ValNumber(3))
  }
  
  it should "be invoked with named parameters" in {

    val variables = Map("sub" -> eval("function(x,y) x - y"))

    eval("sub(x:4,y:2)", variables) should be(ValNumber(2))
    eval("sub(y:2,x:4)", variables) should be(ValNumber(2))    
  }
  
  it should "fail to invoke with wrong number of parameters" in {
    
    val variables = Map("f" -> eval("function(x,y) true"))

    eval("f()", variables) should be(ValError("expected 2 parameters but found 0"))
    eval("f(1)", variables) should be(ValError("expected 2 parameters but found 1"))
    
    eval("f(x:1)", variables) should be(ValError("expected parameter 'y' but not found"))
    eval("f(y:1)", variables) should be(ValError("expected parameter 'x' but not found"))
    eval("f(x:1,y:2,z:3)", variables) should be(ValError("unexpected parameter 'z'"))
  }
  
  private def eval(expression: String, variables: Map[String, Any] = Map()): Val = {
    val exp = FeelParser.parseExpression(expression)
    interpreter.eval(exp.get)(Context(variables))
  }

  private def date(date: String): Date = date

  private def time(time: String): Time = time
  
  private def duration(duration: String): Duration = duration
  
}