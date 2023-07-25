/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.feel.impl.interpreter

import org.camunda.feel.api.EvaluationFailureType
import org.camunda.feel.impl.{EvaluationResultMatchers, FeelEngineTest}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
  * @author Philipp Ossler
  */
class InterpreterFunctionTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

  "A function definition" should "be returned as a function" in {
    val function = evaluateFunction("function(x) x + 1")

    function.params should be(List("x"))
  }

  "A function invocation" should "invoke a function without parameter" in {
    evaluateExpression(
      expression  = "f()",
      functions = Map("f" -> evaluateFunction("""function() "invoked" """))
    ) should returnResult("invoked")
  }

  it should "invoke a function with a positional parameter" in {
    val functions = Map("f" -> evaluateFunction("function(x) x + 1"))

    evaluateExpression(
      expression = "f(1)",
      functions = functions
    ) should returnResult(2)

    evaluateExpression(
      expression = "f(2)",
      functions = functions
    ) should returnResult(3)
  }

  it should "invoke a function with positional parameters" in {
    val functions =
      Map("add" -> evaluateFunction("function(x,y) x + y"))

    evaluateExpression(
      expression = "add(1,2)",
      functions = functions
    ) should returnResult(3)

    evaluateExpression(
      expression = "add(2,3)",
      functions = functions
    ) should returnResult(5)
  }

  it should "invoke a function a named parameter" in {
    val functions =
      Map("f" -> evaluateFunction("function(x) x + 1"))

    evaluateExpression(
      expression = "f(x:1)",
      functions = functions
    ) should returnResult(2)

    evaluateExpression(
      expression = "f(x:2)",
      functions = functions
    ) should returnResult(3)
  }

  it should "invoke a function with named parameters" in {
    val functions =
      Map("sub" -> evaluateFunction("function(x,y) x - y"))

    evaluateExpression(
      expression = "sub(x:4,y:2)",
      functions = functions
    ) should returnResult(2)

    evaluateExpression(
      expression = "sub(y:2,x:4)",
      functions = functions
    ) should returnResult(2)
  }

  it should "take an expression as parameter" in {
    evaluateExpression(
      expression = "f(2 + 3)",
      functions = Map("f" -> evaluateFunction("function(x) x + 1"))
    ) should returnResult(6)
  }

  it should "take another function as parameter" in {
    evaluateExpression(
      expression = "a(b(1))",
      functions = Map(
        "a" -> evaluateFunction("function(x) x + 1"),
        "b" -> evaluateFunction("function(x) x + 2")
      )
    ) should returnResult(4)
  }

  it should "return null if invoked with wrong parameters" in {
    val functions =
      Map("f" -> evaluateFunction("function(x,y) true"))

    evaluateExpression(expression = "f()", functions = functions) should (
      returnNull() and
        reportFailure(EvaluationFailureType.NO_FUNCTION_FOUND, "No function found with name 'f' and 0 parameters")
      )

    evaluateExpression(expression = "f(1)", functions = functions) should (
      returnNull() and
        reportFailure(EvaluationFailureType.NO_FUNCTION_FOUND, "No function found with name 'f' and 1 parameters")
      )

    evaluateExpression(expression = "f(x:1,z:3)", functions = functions) should (
      returnNull() and
        reportFailure(EvaluationFailureType.NO_FUNCTION_FOUND, "No function found with name 'f' and parameters: x,z")
      )

    evaluateExpression(expression = "f(x:1,y:2,z:3)", functions = functions) should (
      returnNull() and
        reportFailure(EvaluationFailureType.NO_FUNCTION_FOUND, "No function found with name 'f' and parameters: x,y,z")
      )
  }

  it should "return null if no function exists with the name" in {
    evaluateExpression("f()") should (
      returnNull() and
        reportFailure(EvaluationFailureType.NO_FUNCTION_FOUND, "No function found with name 'f' and 0 parameters")
      )
  }

  it should "return null if the name doesn't resolve to a function" in {
    evaluateExpression(expression = "f()", variables = Map("x" -> "a variable")) should (
      returnNull() and
        reportFailure(EvaluationFailureType.NO_FUNCTION_FOUND, "No function found with name 'f' and 0 parameters")
      )
  }

  it should "return null for a built-in function if invoked with wrong arguments" in {
    evaluateExpression("number(null)") should (
      returnNull() and
        reportFailure(
          failureType = EvaluationFailureType.FUNCTION_INVOCATION_FAILURE,
          failureMessage = "Failed to invoke function 'number': Illegal arguments: List(ValNull)")
      )
  }

  it should "replace not set parameters with null" in {
    val functions = Map("f" -> evaluateFunction("""
      function(x,y)
        if x = null
        then "x"
        else if y = null
        then "y"
        else "ok"
        """))

    evaluateExpression(
      expression = "f(x:1)",
      functions = functions
    ) should returnResult("y")

    evaluateExpression(
      expression = "f(y:1)",
      functions = functions
    ) should returnResult("x")

    evaluateExpression(
      expression = "f(x:1,y:1)",
      functions = functions
    ) should returnResult("ok")
  }

  it should "be followed by a path" in {
    evaluateExpression(""" date(2019,09,17).year """) should returnResult(2019)
  }

  it should "be followed by a filter" in {
    evaluateExpression(""" index of([1,2,3,2],2)[1]  """) should returnResult(2)
  }

  it should "invoke a function with parameters contain whitespaces" in {
    evaluateExpression(
      expression = """number(from: "1.000.000,01", decimal separator:",", grouping separator:".")"""
    ) should returnResult(1_000_000.01)
  }

  it should "invoke a function with a named parameter containing whitespaces" in {
    val functions =
      Map("f" -> evaluateFunction("""function(test name) `test name` + 1"""))

    evaluateExpression(
      expression = "f(test name:1)",
      functions = functions
    ) should returnResult(2)

    evaluateExpression(
      expression = "f(test name:2)",
      functions = functions
    ) should returnResult(3)
  }

  it should "invoke a function with a named parameter containing more than one whitespace" in {
    val functions =
      Map("f" -> evaluateFunction("""function(test   name yada) `test   name yada` + 1"""))

    evaluateExpression(
      expression = "f(test   name yada:1)",
      functions = functions
    ) should returnResult(2)

    evaluateExpression(
      expression = "f(test   name yada:2)",
      functions = functions
    ) should returnResult(3)
  }

  "An external Java function invocation" should "invoke a function with a double parameter" in {
    val functions = Map(
      "cos" -> evaluateFunction(
        """ function(angle) external { java: { class: "java.lang.Math", method signature: "cos(double)" } } """))

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
