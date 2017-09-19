package org.camunda.feel

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel.interpreter._
import org.camunda.feel.spi._

/**
 * @author Philipp Ossler
 */
class FeelEngineTest extends FlatSpec with Matchers {

  val engine = new FeelEngine

  "A FeelEngine" should "evaluate a simpleUnaryTest '< 3'" in {

    evalUnaryTest("< 3", context = Map(RootContext.defaultInputVariable -> 2)) should be(EvalValue(true))
    evalUnaryTest("< 3", context = Map(RootContext.defaultInputVariable -> 3)) should be(EvalValue(false))
  }

  it should "evaluate a expression '2+4'" in {

    engine.evalExpression("2+4") should be(EvalValue(6))
  }

  it should "evaluate an unaryTest with custom input variable name" in {

    evalUnaryTest("< 3", context = Map("myInput" -> 2, RootContext.inputVariableKey -> "myInput")) should be(EvalValue(true))
    evalUnaryTest("< 3", context = Map("myInput" -> 3, RootContext.inputVariableKey -> "myInput")) should be(EvalValue(false))
  }

  it should "fail evaluation because of wrong type" in {

    evalUnaryTest("< 3", context = Map(RootContext.defaultInputVariable -> "2")) shouldBe a[EvalFailure]
  }

  it should "fail evaluation because of missing input" in {

    evalUnaryTest("< 3", context = Map()) shouldBe a[EvalFailure]
  }

  it should "fail while parsing '<'" in {

    evalUnaryTest("<", context = Map()) shouldBe a[ParseFailure]
  }

  it should "be extendable by a custom function provider" in {

    val engine = new FeelEngine(new TestFunctionProvider)

    engine.evalExpression("foo(2)") should be(EvalValue(3))
  }

  it should "be extendable by multiple custom function providers" in {

    val engine = new FeelEngine(new FunctionProvider.CompositeFunctionProvider(
      List(new TestFunctionProvider, new AnotherFunctionProvider)
    ))

    engine.evalExpression("foo(2)") should be(EvalValue(3))
    engine.evalExpression("bar(2)") should be(EvalValue(4))
  }

  private def evalUnaryTest(expression: String, context: Map[String, Any]): EvalResult = {
    engine.evalUnaryTests(expression, context)
  }

}
