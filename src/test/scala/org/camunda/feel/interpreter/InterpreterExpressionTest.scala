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
  
  "A function" should "be invoked without parameter" in {

    val variables = Map("f" -> ValFunction(
      params = List(),
      invoke = (params: List[Val]) => {
        ValString("invoked")
      }))

    eval("f()", variables) should be(ValString("invoked"))
  }

  it should "be invoked with one positional parameter" in {

    val variables = Map("f" -> ValFunction(
      params = List(ValParameter("x", classOf[ValNumber])),
      invoke = (params: List[Val]) => {
        params.head match {
          case ValNumber(n) if (n == 1) => ValString("yes")
          case _ => ValString("no")
        }
      }))

    eval("f(1)", variables) should be(ValString("yes"))
    eval("f(2)", variables) should be(ValString("no"))
  }
  
  it should "be invoked with positional parameters" in {

    val variables = Map("add" -> ValFunction(
      params = List(
          ValParameter("x", classOf[ValNumber]),
          ValParameter("y", classOf[ValNumber])),
      invoke = (params: List[Val]) => {
        val x = params(0).asInstanceOf[ValNumber].value
        val y = params(1).asInstanceOf[ValNumber].value
        
        ValNumber(x + y)
      }))

    eval("add(1,2)", variables) should be(ValNumber(3))
    eval("add(2,3)", variables) should be(ValNumber(5))
  }
  
  it should "fail to invoke with wrong number of parameters" in {
    
    val variables = Map("f" -> ValFunction(
      params = List(
          ValParameter("x", classOf[ValNumber]),
          ValParameter("y", classOf[ValNumber])),
      invoke = (params: List[Val]) => {
        ValString("invoked")
      }))

    eval("f()", variables) should be(ValError("expected 2 parameters but found 0"))
    eval("f(1)", variables) should be(ValError("expected 2 parameters but found 1"))
  }
  
  it should "fail to invoke with wrong type of parameters" in {
    
    val variables = Map("f" -> ValFunction(
      params = List(
          ValParameter("x", classOf[ValNumber]),
          ValParameter("y", classOf[ValBoolean])),
      invoke = (params: List[Val]) => {
        ValString("invoked")
      }))

    eval("f(1,2)", variables) should be(ValError("expected parameter 'y' of type ValBoolean but was ValNumber"))
    eval("f(false,true)", variables) should be(ValError("expected parameter 'x' of type ValNumber but was ValBoolean"))
  }
  
  private def eval(expression: String, variables: Map[String, Any] = Map()): Val = {
    val exp = FeelParser.parseExpression(expression)
    interpreter.eval(exp.get)(Context(variables))
  }

  private def date(date: String): Date = date

  private def time(time: String): Time = time
  
  private def duration(duration: String): Duration = duration
  
}