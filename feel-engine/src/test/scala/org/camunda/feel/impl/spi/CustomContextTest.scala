package org.camunda.feel.impl.spi

import org.camunda.feel.FeelEngine
import org.camunda.feel.FeelEngine.{Failure, UnaryTests}
import org.camunda.feel.context.{
  CustomContext,
  FunctionProvider,
  VariableProvider
}
import org.camunda.feel.syntaxtree._
import org.scalatest.{FlatSpec, Matchers}

class CustomContextTest extends FlatSpec with Matchers {

  val engine = new FeelEngine

  "A default context" should "provide its members" in {
    engine.evalExpression("a", variables = Map("a" -> 2)) should be(Right(2))
    engine.evalUnaryTests(
      "2",
      variables = Map(UnaryTests.defaultInputVariable -> 2)) should be(
      Right(true))
  }

  it should "fail on access to missing member" in {
    engine.evalExpression("b", variables = Map("a" -> 2)) should be
    Left(
      Failure(
        "failed to evaluate expression 'b': no variable found for name 'b'"))
  }

  "A custom context" should "provide its members" in {
    val myCustomContext = new CustomContext {

      override def variableProvider: VariableProvider = new VariableProvider {
        override def getVariable(name: String): Option[Any] = name match {
          case "a"                             => Some(2)
          case UnaryTests.defaultInputVariable => Some(2)
          case _                               => None
        }

        override def keys: Iterable[String] =
          List("a", UnaryTests.defaultInputVariable)
      }

    }
    engine.evalExpression("a", myCustomContext) should be(Right(2))
    engine.evalExpression("floor(3.8)", myCustomContext) should be(Right(3))
    engine.evalUnaryTests("2", myCustomContext) should be(Right(true))
  }

  it should "fail on access to missing member" in {
    val context = new CustomContext {
      override def variableProvider: VariableProvider =
        VariableProvider.StaticVariableProvider(Map.empty)
    }

    engine.evalExpression("b", context) should be
    Left(
      Failure(
        "failed to evaluate expression 'b': no variable found for name 'b'"))
  }

  it should "provide its functions" in {

    var variableCallCount = 0
    var functionCallCount = 0

    val myVariableProvider = new VariableProvider {
      override def getVariable(name: String): Option[Any] = {
        variableCallCount += 1;
        if (name == "a") Some(2) else None
      }

      override def keys: Iterable[String] = List("a")
    }

    val myFunctionProvider = new FunctionProvider {
      val f = ValFunction(List("x"), {
        case List(ValNumber(x)) => ValNumber(x + 2)
      })

      override def getFunctions(name: String): List[ValFunction] = {
        functionCallCount += 1;
        if (name == "f") List(f) else List.empty
      }

      override def functionNames: Iterable[String] = List("f")
    }

    val myCustomContext = new CustomContext {
      override val variableProvider = myVariableProvider
      override val functionProvider = myFunctionProvider
    }

    engine.evalExpression("a + f(2) + a + f(8)", myCustomContext) should be(
      Right(18))
    variableCallCount should be(2)
    functionCallCount should be(2)

  }

}
