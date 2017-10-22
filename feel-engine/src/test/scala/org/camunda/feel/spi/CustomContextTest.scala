package org.camunda.feel.spi

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._
import org.camunda.feel.interpreter._

class CustomContextTest extends FlatSpec with Matchers {

  val engine = new FeelEngine

  "A default context" should "provide its members" in {
    engine.evalExpression("a", context = Map("a" -> 2)) should be(EvalValue(2))
    engine.evalUnaryTests("2", context = Map(RootContext.defaultInputVariable -> 2)) should be(EvalValue(true))
  }

  it should "crash on access to missing member" in {
    engine.evalExpression("b", context = Map("a" -> 2)) shouldBe a [EvalFailure]
  }

  "A custom context" should "provide its members" in {
    val myCustomContext = new CustomContext {

      override def variable(name: String): Val = name match {
        case "a" => ValNumber(2)
        case RootContext.defaultInputVariable => ValNumber(2)
        case _ => super.variable(name)
      }

      override def functions = BuiltinFunctions.functions

    }
    engine.evalExpression("a", myCustomContext) should be(EvalValue(2))
    engine.evalExpression("floor(3.8)", myCustomContext) should be(EvalValue(3))
    engine.evalUnaryTests("2", myCustomContext) should be(EvalValue(true))
  }

  it should "crash on access to missing member" in {
    engine.evalExpression("b", new CustomContext{
      override def variable(name: String): Val = { if (name == "a") ValNumber(2) else super.variable(name) }
    }) shouldBe a [EvalFailure]
  }

  it should "cache its feature access" in {

    val myVariableProvider = new VariableProvider {
      var callCount = 0
      override def getVariable(name: String): Option[Val] = { callCount += 1; if (name == "a") Some(ValNumber(2)) else None }
    }

    val myFunctionProvider = new FunctionProvider {
      var callCount = 0
      val f = ValFunction(List("x"), { case List(ValNumber(x)) => ValNumber(x + 2) } )
      override def getFunction(name: String): List[ValFunction] = { callCount += 1; if (name == "f") List(f) else List.empty }
    }

    val myCustomContext = new CustomContext {
      override val variableProvider = myVariableProvider
      override val functionProvider = myFunctionProvider
    }

    engine.evalExpression("a + f(2) + a + f(8)", myCustomContext) should be(EvalValue(18))
    myVariableProvider.callCount should be(1)
    myFunctionProvider.callCount should be(1)

  }

}
