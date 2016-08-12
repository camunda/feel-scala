package org.camunda.feel.interpreter

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._
import org.camunda.feel.parser.FeelParser

/**
 * @author Philipp Ossler
 */
class InterpreterExperimentelTest extends FlatSpec with Matchers {

  val interpreter = new FeelInterpreter

  "A function" should "be invoked without parameter" in {

    val variables = Map("f" -> ValFunction(
      params = List(),
      invoke = (params: List[Val]) => {
        ValString("invoked")
      }))

    eval("f()", variables) should be(ValString("invoked"))
  }

  it should "be invoked with one parameter" in {

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
  
  it should "be invoked with two parameter" in {

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

  private def eval(expression: String, variables: Map[String, Any] = Map()): Val = {
    val exp = FeelParser.parseExpression(expression)
    interpreter.eval(exp.get)(Context(variables))
  }

}