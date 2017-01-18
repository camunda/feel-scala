package org.camunda.feel

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel.interpreter.Context
import org.camunda.feel.interpreter.FunctionProvider
import org.camunda.feel.interpreter.ValFunction
import org.camunda.feel.interpreter.ValNumber
import org.camunda.feel.interpreter.ValString
import org.camunda.feel.interpreter.ValBoolean

/**
 * @author Philipp Ossler
 */
class FeelEngineTest extends FlatSpec with Matchers {

  val engine = new FeelEngine

  "A FeelEngine" should "evaluate a simpleUnaryTest '< 3'" in {

    evalUnaryTest("< 3", context = Map(Context.defaultInputVariable -> 2)) should be(EvalValue(true))
    evalUnaryTest("< 3", context = Map(Context.defaultInputVariable -> 3)) should be(EvalValue(false))
  }
  
  it should "evaluate a simpleExpression '2+4'" in {
     
    engine.evalExpression("2+4", context = Map()) should be(EvalValue(6))
  }
  
  it should "evaluate an simpleUnaryTest with custom input variable name" in {
     
    evalUnaryTest("< 3", context = Map("myInput" -> 2, Context.inputVariableKey -> "myInput")) should be(EvalValue(true))
    evalUnaryTest("< 3", context = Map("myInput" -> 3, Context.inputVariableKey -> "myInput")) should be(EvalValue(false))
  }

  it should "failed while evaluation cause of wrong type" in {

    evalUnaryTest("< 3", context = Map(Context.defaultInputVariable -> "2")) shouldBe a[EvalFailure]
  }

  it should "failed while evaluation cause by missing input" in {

    evalUnaryTest("< 3", context = Map()) shouldBe a[EvalFailure]
  }

  it should "failed while parsing '<'" in {

    evalUnaryTest("<", context = Map()) shouldBe a[ParseFailure]
  }
  
  it should "be extend by a custom function provider" in {
    
    val customFunctionProvider = new FunctionProvider {
      
      val functions: Map[(String, Int), ValFunction] = Map(
        ("foo", 1) -> ValFunction(List("x"), { case List(ValNumber(x)) => ValNumber(x + 1) } )
      )
      
      def getFunction(functionName: String, argumentCount: Int): Option[ValFunction] = functions.get((functionName, argumentCount))
    }
    
    val engine = new FeelEngine(customFunctionProvider)
    
    engine.evalExpression("foo(2)", Map()) should be(EvalValue(3))
  }

  private def evalUnaryTest(expression: String, context: Map[String, Any]): EvalResult = {
    engine.evalSimpleUnaryTests(expression, context)
  }

}