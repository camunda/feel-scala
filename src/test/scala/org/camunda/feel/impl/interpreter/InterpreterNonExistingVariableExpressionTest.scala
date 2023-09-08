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
package org.camunda.feel.impl.interpreter;

import org.camunda.feel.api.EvaluationFailureType
import org.camunda.feel.context.Context
import org.camunda.feel.impl.{EvaluationResultMatchers, FeelEngineTest}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class InterpreterNonExistingVariableExpressionTest
  extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

  "A non-existing variable" should "compare with '='" in {
    evaluateExpression("x = 1") should returnResult(false)
    evaluateExpression("1 = x") should returnResult(false)
    evaluateExpression("x = true") should returnResult(false)
    evaluateExpression("true = x") should returnResult(false)
    evaluateExpression(""" x = "string" """) should returnResult(false)
    evaluateExpression(""" "string" = x """) should returnResult(false)

    evaluateExpression("x = null") should returnResult(true)
    evaluateExpression("null = x") should returnResult(true)
    evaluateExpression("x = y") should returnResult(true)
  }

  it should "compare with `<`" in {
    evaluateExpression("x < 1") should returnNull()
    evaluateExpression("1 < x") should returnNull()
    evaluateExpression("x < true") should returnNull()
    evaluateExpression("true < x") should returnNull()
    evaluateExpression(""" x < "string" """) should returnNull()
    evaluateExpression(""" "string" < x """) should returnNull()
    evaluateExpression("x < null") should returnNull()
    evaluateExpression("null < x") should returnNull()
    evaluateExpression("x < y") should returnNull()
  }

  it should "compare with `>`" in {
    evaluateExpression("x > 1") should returnNull()
    evaluateExpression("1 > x") should returnNull()
    evaluateExpression("x > true") should returnNull()
    evaluateExpression("true > x") should returnNull()
    evaluateExpression(""" x > "string" """) should returnNull()
    evaluateExpression(""" "string" > x """) should returnNull()
    evaluateExpression("x > null") should returnNull()
    evaluateExpression("null > x") should returnNull()
    evaluateExpression("x > y") should returnNull()
  }

  it should "compare with `<=`" in {
    evaluateExpression("x <= 1") should returnNull()
    evaluateExpression("1 <= x") should returnNull()
    evaluateExpression("x <= true") should returnNull()
    evaluateExpression("true <= x") should returnNull()
    evaluateExpression(""" x <= "string" """) should returnNull()
    evaluateExpression(""" "string" <= x """) should returnNull()
    evaluateExpression("x <= null") should returnNull()
    evaluateExpression("null <= x") should returnNull()
    evaluateExpression("x <= y") should returnNull()
  }

  it should "compare with `>=`" in {
    evaluateExpression("x >= 1") should returnNull()
    evaluateExpression("1 >= x") should returnNull()
    evaluateExpression("x >= true") should returnNull()
    evaluateExpression("true >= x") should returnNull()
    evaluateExpression(""" x >= "string" """) should returnNull()
    evaluateExpression(""" "string" >= x """) should returnNull()
    evaluateExpression("x >= null") should returnNull()
    evaluateExpression("null >= x") should returnNull()
    evaluateExpression("x >= y") should returnNull()
  }

  it should "compare with `between _ and _`" in {
    evaluateExpression("x between 1 and 3") should returnNull()
    evaluateExpression("1 between x and 3") should returnNull()
    evaluateExpression("3 between 1 and x") should returnNull()
    evaluateExpression("x between y and 3") should returnNull()
    evaluateExpression("x between 1 and y") should returnNull()
    evaluateExpression("x between y and z") should returnNull()
  }

  it should "return null" in {
    evaluateExpression("non_existing") should (
      returnNull() and reportFailure(
        failureType = EvaluationFailureType.NO_VARIABLE_FOUND,
        failureMessage = "No variable found with name 'non_existing'")
      )
  }

  "A non-existing input value" should "be equal to null" in {
    evaluateUnaryTests(expression = "null", context = Context.EmptyContext) should (
      returnResult(true) and reportFailure(
        failureType = EvaluationFailureType.NO_VARIABLE_FOUND,
        failureMessage = "No input value found.")
      )
  }

  it should "not be equal to a non-null value" in {
    evaluateUnaryTests("2", context = Context.EmptyContext) should (
      returnResult(false) and reportFailure(
        failureType = EvaluationFailureType.NO_VARIABLE_FOUND,
        failureMessage = "No input value found.")
      )
  }

  it should "not compare to a non-null value" in {
    evaluateUnaryTests("< 2", context = Context.EmptyContext) should (
      returnNull() and reportFailure(
        failureType = EvaluationFailureType.NO_VARIABLE_FOUND,
        failureMessage = "No input value found.")
      )
  }

}
