package org.camunda.feel.impl.interpreter

import org.camunda.feel.impl.{EvaluationResultMatchers, FeelEngineTest}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class InterpreterFunctionTest extends AnyFlatSpec
  with Matchers
  with FeelEngineTest
  with EvaluationResultMatchers {
  "An external Java function invocation" should "invoke a function with a double parameter" in {
    val functions = Map(
      "cos" -> evaluateFunction(
        """ function(angle) external { java: { class: "java.lang.Math", method signature: "cos(double)" } } """
      )
    )

    evaluateExpression(
      expression = "cos(0)",
      functions = functions
    ) should returnResult(1)

    evaluateExpression(
      expression = "cos(1)",
      functions = functions
    ) should returnResult(Math.cos(1))
  }

  it should "invoke a function with two int parameters" in {
    evaluateExpression(
      expression = "max(1,2)",
      functions = Map("max" -> evaluateFunction(
        """ function(x,y) external { java: {
          class: "java.lang.Math", method signature: "max(int, int)" } } """))
    ) should returnResult(2)
  }

  it should "invoke a function with a long parameters" in {
    evaluateExpression(
      expression = "abs(-1)",
      functions = Map("abs" -> evaluateFunction(
        """ function(a) external { java: {
          class: "java.lang.Math", method signature: "abs(long)" } } """))
    ) should returnResult(1)
  }

  it should "invoke a function with a float parameters" in {
    evaluateExpression(
      expression = "round(3.2)",
      functions = Map("round" -> evaluateFunction(
        """ function(a) external { java: {
          class: "java.lang.Math", method signature: "round(float)" } } """))
    ) should returnResult(3)
  }

}
