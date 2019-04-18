package org.camunda.feel.spi

import org.camunda.feel.FeelEngine.Failure
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._
import org.camunda.feel.interpreter._

class CustomContextTest extends FlatSpec with Matchers {

  val engine = new FeelEngine

  "A default context" should "provide its members" in {
    engine.evalExpression("a", variables = Map("a" -> 2)) should be(Right(2))
    engine.evalUnaryTests("2", variables = Map(RootContext.defaultInputVariable -> 2)) should be(Right(true))
  }

  it should "fail on access to missing member" in {
    engine.evalExpression("b", variables = Map("a" -> 2)) should be
      Left(Failure("failed to evaluate expression 'b': no variable found for name 'b'"))
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
    engine.evalExpression("a", myCustomContext) should be(Right(2))
    engine.evalExpression("floor(3.8)", myCustomContext) should be(Right(3))
    engine.evalUnaryTests("2", myCustomContext) should be(Right(true))
  }

  it should "fail on access to missing member" in {
    val context = new CustomContext{
      override def variable(name: String): Val = { if (name == "a") ValNumber(2) else super.variable(name) }
    }

    engine.evalExpression("b", context) should be
      Left(Failure("failed to evaluate expression 'b': no variable found for name 'b'"))
  }

  it should "provide its functions" in {

    var variableCallCount = 0
    var functionCallCount = 0

    val myVariableProvider = new VariableProvider {
      override def getVariable(name: String): Option[Val] = { variableCallCount += 1; if (name == "a") Some(ValNumber(2)) else None }
    }

    val myFunctionProvider = new FunctionProvider {
      val f = ValFunction(List("x"), { case List(ValNumber(x)) => ValNumber(x + 2) } )
      override def getFunctions(name: String): List[ValFunction] = { functionCallCount += 1; if (name == "f") List(f) else List.empty }
    }

    val myCustomContext = new CustomContext {
      override val variableProvider = myVariableProvider
      override val functionProvider = myFunctionProvider
    }

    engine.evalExpression("a + f(2) + a + f(8)", myCustomContext) should be(Right(18))
    variableCallCount should be(1)
    functionCallCount should be(1)

  }

}
