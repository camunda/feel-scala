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

import org.camunda.feel.impl.{EvaluationResultMatchers, FeelEngineTest}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class InterpreterJavaFunctionTest
    extends AnyFlatSpec
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
      functions = Map("max" -> evaluateFunction(""" function(x,y) external { java: {
          class: "java.lang.Math", method signature: "max(int, int)" } } """))
    ) should returnResult(2)
  }

  it should "invoke a function with a long parameters" in {
    evaluateExpression(
      expression = "abs(-1)",
      functions = Map("abs" -> evaluateFunction(""" function(a) external { java: {
          class: "java.lang.Math", method signature: "abs(long)" } } """))
    ) should returnResult(1)
  }

  it should "invoke a function with a float parameters" in {
    evaluateExpression(
      expression = "round(3.2)",
      functions = Map("round" -> evaluateFunction(""" function(a) external { java: {
          class: "java.lang.Math", method signature: "round(float)" } } """))
    ) should returnResult(3)
  }

}
