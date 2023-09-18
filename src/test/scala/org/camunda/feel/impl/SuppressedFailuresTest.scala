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
package org.camunda.feel.impl

import org.camunda.feel.api.{EvaluationFailureType, EvaluationFailure}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SuppressedFailuresTest extends AnyFlatSpec
  with FeelEngineTest
  with Matchers
  with EvaluationResultMatchers {

  "The engine" should "report a suppressed failure for a non-existing variable" in {
    evaluateExpression("x + 1") should reportFailure(
      failureType = EvaluationFailureType.NO_VARIABLE_FOUND,
      failureMessage = "No variable found with name 'x'"
    )
  }

  it should "report a suppressed failure for a non-existing context entry" in {
    evaluateExpression("{x: 1}.y") should reportFailure(
      failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
      failureMessage = "No context entry found with key 'y'. Available keys: 'x'"
    )
  }

  it should "report a suppressed failure for a non-existing property" in {
    evaluateExpression(""" @"P1Y".days """) should reportFailure(
      failureType = EvaluationFailureType.NO_PROPERTY_FOUND,
      failureMessage = "No property found with name 'days' of value 'P1Y'. Available properties: 'years', 'months'"
    )
  }

  it should "report a suppressed failure if input is not comparable with interval" in {
    evaluateUnaryTests("[2..5]", "NaN") should reportFailure(
      failureType = EvaluationFailureType.NOT_COMPARABLE,
      failureMessage = """Can't compare '"NaN"' with '2' and '5'"""
    )
  }

  it should "report a suppressed failure if values are not comparable" in {
    evaluateExpression("true < 2") should reportFailure(
      failureType = EvaluationFailureType.NOT_COMPARABLE,
      failureMessage = "Can't compare 'true' with '2'"
    )
  }

  it should "report a suppressed failure if an addition has incompatible values" in {
    evaluateExpression("2 + true") should reportFailure(
      failureType = EvaluationFailureType.INVALID_TYPE,
      failureMessage = "Expected number but found 'true'"
    )
  }

  it should "report a suppressed failure if a condition is not a boolean" in {
    evaluateExpression("if 5 then 1 else 2") should reportFailure(
      failureType = EvaluationFailureType.INVALID_TYPE,
      failureMessage = "Expected boolean but found '5'"
    )

    evaluateExpression("true and 2") should reportFailure(
      failureType = EvaluationFailureType.INVALID_TYPE,
      failureMessage = "Expected boolean but found '2'"
    )

    evaluateExpression("false or 3") should reportFailure(
      failureType = EvaluationFailureType.INVALID_TYPE,
      failureMessage = "Expected boolean but found '3'"
    )

    evaluateExpression("some x in [false, 2] satisfies x") should reportFailure(
      failureType = EvaluationFailureType.INVALID_TYPE,
      failureMessage = "Expected boolean but found '2'"
    )

    evaluateExpression("every x in [true, 3] satisfies x") should reportFailure(
      failureType = EvaluationFailureType.INVALID_TYPE,
      failureMessage = "Expected boolean but found '3'"
    )
  }

  it should "report a suppressed failure only once" in {
    val evaluationResult = evaluateExpression("1 + x")

    evaluationResult.hasSuppressedFailures should be (true)
    evaluationResult.suppressedFailures should have size(2)

    evaluationResult.suppressedFailures should contain inOrder(
      EvaluationFailure(
        failureType = EvaluationFailureType.NO_VARIABLE_FOUND,
        failureMessage = "No variable found with name 'x'"
      ),
      EvaluationFailure(
        failureType = EvaluationFailureType.INVALID_TYPE,
        failureMessage = "Expected number but found 'null'"
      )
    )
  }

}
