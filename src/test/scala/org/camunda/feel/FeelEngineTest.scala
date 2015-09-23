package org.camunda.feel

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel.interpreter.Context

/**
 * @author Philipp Ossler
 */
class FeelEngineTest extends FlatSpec with Matchers {

  val engine = new FeelEngine

  "A FeelEngine" should "evaluate simpleUnaryTest '< 3'" in {

    eval("< 3", context = Map(Context.inputKey -> 2)) should be(EvalValue(true))
    eval("< 3", context = Map(Context.inputKey -> 3)) should be(EvalValue(false))
  }

  it should "failed while evaluation cause of wrong type" in {

    eval("< 3", context = Map(Context.inputKey -> "2")) shouldBe a[EvalFailure]
  }

  it should "failed while evaluation cause by missing input" in {

    eval("< 3", context = Map()) shouldBe a[EvalFailure]
  }

  it should "failed while parsing '<'" in {

    eval("<", context = Map()) shouldBe a[ParseFailure]
  }

  it should "return 'true' for empty simpleUnaryTest" in {

    eval("", context = Map()) should be(EvalValue(true))
  }

  private def eval(expression: String, context: Map[String, Any]): EvalResult = {
    engine.evalSimpleUnaryTest(expression, context)
  }

}