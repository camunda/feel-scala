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

class ComparisonTypeTest extends AnyFlatSpec
  with Matchers
  with FeelEngineTest
  with EvaluationResultMatchers {

  "An equal operator" should "compare two values of the same type" in {
    evaluateExpression("1 = 1") should returnResult(true)
    evaluateExpression("1 = 2") should returnResult(false)

    evaluateExpression(""" "a" = "a" """) should returnResult(true)
    evaluateExpression(""" "a" = "b" """) should returnResult(false)

    evaluateExpression(""" @"P1D" = @"P1D" """) should returnResult(true)
    evaluateExpression(""" @"P1D" = @"P2D" """) should returnResult(false)
  }

  it should "compare a value with null" in {
    evaluateExpression("1 = null") should returnResult(false)
    evaluateExpression(""" "a" = null """) should returnResult(false)
    evaluateExpression(""" @"P1D" = null """) should returnResult(false)
  }

  it should "compare two null values" in {
    evaluateExpression("null = null") should returnResult(true)
  }

  it should "return null if the values have a different type" in {
    evaluateExpression("1 = true") should (returnNull() and reportFailure(
      failureType = EvaluationFailureType.NOT_COMPARABLE,
      failureMessage = "Can't compare ValNumber(1) with ValBoolean(true)"
    ))

    evaluateExpression(""" 1 = "a" """) should (returnNull() and reportFailure(
      failureType = EvaluationFailureType.NOT_COMPARABLE,
      failureMessage = "Can't compare ValNumber(1) with ValString(a)"
    ))

    evaluateExpression(""" 1 = @"P1D" """) should (returnNull() and reportFailure(
      failureType = EvaluationFailureType.NOT_COMPARABLE,
      failureMessage = "Can't compare ValNumber(1) with P1D"
    ))
  }

  "A comparison operator" should "compare two values of the same type" in {
    evaluateExpression("1 < 2") should returnResult(true)
    evaluateExpression("1 > 3") should returnResult(false)

    evaluateExpression(""" @"P1D" < @"P2D" """) should returnResult(true)
    evaluateExpression(""" @"P1D" > @"P3D" """) should returnResult(false)
  }

  it should "return null if a value is null" in {
    evaluateExpression("1 < null") should returnNull()
    evaluateExpression(""" @"P1D" > null """) should returnNull()
  }

  it should "return null if the values are null" in {
    evaluateExpression("null < null") should returnNull()
  }

  it should "return null if the values have a different type" in {
    evaluateExpression("1 < true") should (returnNull() and reportFailure(
      failureType = EvaluationFailureType.NOT_COMPARABLE,
      failureMessage = "Can't compare ValNumber(1) with ValBoolean(true)"
    ))

    evaluateExpression(""" 1 > @"P1D" """) should (returnNull() and reportFailure(
      failureType = EvaluationFailureType.NOT_COMPARABLE,
      failureMessage = "Can't compare ValNumber(1) with P1D"
    ))
  }

  "An unary-test equal operator" should "compare two values of the same type" in {
    evaluateUnaryTests(expression = "1", inputValue = 1) should returnResult(true)
    evaluateUnaryTests(expression = "1", inputValue = 2) should returnResult(false)

    evaluateUnaryTests(expression = """ "a" """, inputValue = "a") should returnResult(true)
    evaluateUnaryTests(expression = """ "a" """, inputValue = "b") should returnResult(false)
  }

  it should "compare a value with null" in {
    evaluateUnaryTests(expression = "1", inputValue = null) should returnResult(false)
    evaluateUnaryTests(expression = """ "a" """, inputValue = null) should returnResult(false)
  }

  it should "compare two null values" in {
    evaluateUnaryTests(expression = "null", inputValue = null)should returnResult(true)
  }

  it should "return null if the values have a different type" in {
    evaluateUnaryTests(expression = "1", inputValue = true) should (returnNull() and reportFailure(
      failureType = EvaluationFailureType.NOT_COMPARABLE,
      failureMessage = "Can't compare ValBoolean(true) with ValNumber(1)"
    ))

    evaluateUnaryTests(expression = "1", inputValue = "a") should (returnNull() and reportFailure(
      failureType = EvaluationFailureType.NOT_COMPARABLE,
      failureMessage = "Can't compare ValString(a) with ValNumber(1)"
    ))
  }

  "An unary-test operator" should "compare two values of the same type" in {
    evaluateUnaryTests(expression = "< 2", inputValue = 1) should returnResult(true)
    evaluateUnaryTests(expression = "> 3", inputValue = 1) should returnResult(false)
  }

  it should "return null if the input value is null" in {
    evaluateUnaryTests(expression = "< 2", inputValue = null) should returnNull()
  }

  it should "return null if the values have a different type" in {
    evaluateUnaryTests(expression = "< 1", inputValue = true) should (returnNull() and reportFailure(
      failureType = EvaluationFailureType.NOT_COMPARABLE,
      failureMessage = "Can't compare ValBoolean(true) with ValNumber(1)"
    ))

    evaluateUnaryTests(expression = "> 1", inputValue = "a") should (returnNull() and reportFailure(
      failureType = EvaluationFailureType.NOT_COMPARABLE,
      failureMessage = "Can't compare ValString(a) with ValNumber(1)"
    ))
  }

}
