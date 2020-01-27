package org.camunda.feel

import org.camunda.feel.FeelEngine.{Failure, UnaryTests}
import org.camunda.feel.impl.ParsedExpression
import org.camunda.feel.impl.spi.{AnotherFunctionProvider, TestFunctionProvider}
import org.camunda.feel.interpreter.FunctionProvider
import org.scalatest.{FlatSpec, Matchers}

/**
  * @author Philipp Ossler
  */
class FeelEngineTest extends FlatSpec with Matchers {

  val engine = new FeelEngine

  "A FeelEngine" should "evaluate a unaryTests '< 3'" in {

    engine.evalUnaryTests(
      "< 3",
      variables = Map(UnaryTests.defaultInputVariable -> 2)) should be(
      Right(true))
    engine.evalUnaryTests(
      "< 3",
      variables = Map(UnaryTests.defaultInputVariable -> 3)) should be(
      Right(false))
  }

  it should "evaluate a expression '2+4'" in {

    engine.evalExpression("2+4") should be(Right(6))
  }

  it should "evaluate an unaryTest with custom input variable name" in {

    engine.evalUnaryTests("< 3",
                          variables = Map(
                            "myInput" -> 2,
                            UnaryTests.inputVariable -> "myInput")) should be(
      Right(true))
    engine.evalUnaryTests("< 3",
                          variables = Map(
                            "myInput" -> 3,
                            UnaryTests.inputVariable -> "myInput")) should be(
      Right(false))
  }

  it should "fail evaluation because of wrong type" in {

    engine.evalUnaryTests(
      "< 3",
      variables = Map(UnaryTests.defaultInputVariable -> "2")) should be(
      Left(Failure(
        "failed to evaluate expression '< 3': ValString(2) can not be compared to ValNumber(3)")))
  }

  it should "fail evaluation because of missing input" in {

    engine.evalUnaryTests("< 3", variables = Map[String, Any]()) should be(
      Left(Failure(
        "failed to evaluate expression '< 3': no variable found for name 'cellInput'")))
  }

  it should "fail while parsing '<'" in {

    engine.evalUnaryTests("<", variables = Map[String, Any]()) should be(
      Left(Failure(
        "failed to parse expression '<': [1.2] failure: '{' expected but end of source found\n\n<\n ^")))
  }

  it should "parse an expression 'x'" in {
    val expr = engine.parseExpression("x + 1")

    expr shouldBe a[Right[_, ParsedExpression]]
    engine.eval(expr.right.get, Map("x" -> 3)) should be(Right(4))
  }

  it should "fail to parse an expression 'x+'" in {

    engine.parseExpression("x+") should be(Left(Failure(
      "failed to parse expression 'x+': [1.2] failure: end of input expected\n\nx+\n ^")))
  }

  it should "parse an unaryTests '< 3'" in {
    val expr = engine.parseUnaryTests("< 3")

    expr shouldBe a[Right[_, ParsedExpression]]
    engine.eval(expr.right.get, Map(UnaryTests.defaultInputVariable -> 2)) should be(
      Right(true))
  }

  it should "fail to parse an unaryTests '<'" in {

    engine.parseUnaryTests("<") should be(Left(Failure(
      "failed to parse expression '<': [1.2] failure: '{' expected but end of source found\n\n<\n ^")))
  }

  it should "be extendable by a custom function provider" in {

    val engine = new FeelEngine(new TestFunctionProvider)

    engine.evalExpression("foo(2)") should be(Right(3))
  }

  it should "be extendable by multiple custom function providers" in {

    val engine = new FeelEngine(
      FunctionProvider.CompositeFunctionProvider(
        List(new TestFunctionProvider, new AnotherFunctionProvider)
      ))

    engine.evalExpression("foo(2)") should be(Right(3))
    engine.evalExpression("bar(2)") should be(Right(4))
  }

}
