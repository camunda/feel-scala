package org.camunda.feel.spi

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._
import org.camunda.feel.interpreter._
import org.camunda.feel.spi._

class ContextTest extends FlatSpec with Matchers {

	val engine = new FeelEngine

	"A standard context" should "provide its members" in {
		engine.evalExpression("a", context = Map("a" -> 2)) should be(EvalValue(2))
		engine.evalUnaryTests("2", context = Map(Context.defaultInputVariable -> 2)) should be(EvalValue(true))
	}

	it should "crash on access to missing member" in {
		engine.evalExpression("b", context = Map("a" -> 2)) shouldBe a [EvalFailure]
	}

	"A variable context" should "provide its members" in {
		engine.evalExpression("a", (key: String) => if (key == "a") Some(2) else None) should be(EvalValue(2))
		engine.evalUnaryTests("2", (key: String) => if (key == Context.defaultInputVariable) Some(2) else None) should be(EvalValue(true))
	}

	it should "crash on access to missing member" in {
		engine.evalExpression("b", (key: String) => if (key == "a") Some(2) else None) shouldBe a [EvalFailure]
	}

}
