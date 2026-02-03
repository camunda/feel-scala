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

import org.camunda.feel.api.{EvaluationFailure, EvaluationFailureType, Position}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SuppressedFailuresWithPositionTest
    extends AnyFlatSpec
    with FeelEngineTest
    with Matchers
    with EvaluationResultMatchers {

  "The engine" should "report position for a non-existing variable" in {
    val result = evaluateExpression("x + 1")

    result.hasSuppressedFailures should be(true)
    result.suppressedFailures should have size 2

    val failure = result.suppressedFailures.head
    failure.failureType should be(EvaluationFailureType.NO_VARIABLE_FOUND)
    failure.failureMessage should be("No variable found with name 'x'")
    failure.position should be(Some(Position(0, 2)))
  }

  it should "report position for a non-existing context entry" in {
    val result = evaluateExpression("{x: 1}.y")

    result.hasSuppressedFailures should be(true)
    val failure = result.suppressedFailures.head

    failure.failureType should be(EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND)
    failure.failureMessage should be("No context entry found with key 'y'. Available keys: 'x'")
    failure.position should be(Some(Position(6, 8)))
  }

  it should "report position for a non-existing property" in {
    val result = evaluateExpression(""" @"P1Y".days """)

    result.hasSuppressedFailures should be(true)
    val failure = result.suppressedFailures.head

    failure.failureType should be(EvaluationFailureType.NO_PROPERTY_FOUND)
    failure.position should be(Some(Position(7, 13)))
  }

  it should "report position for nested path expression" in {
    val result = evaluateExpression("{a: {b: 1}}.a.c")

    result.hasSuppressedFailures should be(true)
    val failure = result.suppressedFailures.head

    failure.failureType should be(EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND)
    failure.position should be(Some(Position(13, 15)))
  }

  it should "report position in longer expressions" in {
    val result = evaluateExpression("1 + unknown_var * 2")

    result.hasSuppressedFailures should be(true)

    // Find the NO_VARIABLE_FOUND failure
    val variableFailure = result.suppressedFailures.find(
      _.failureType == EvaluationFailureType.NO_VARIABLE_FOUND
    )

    variableFailure should be(defined)
    variableFailure.get.failureMessage should be("No variable found with name 'unknown_var'")
    variableFailure.get.position should be(Some(Position(4, 16)))
  }

  it should "maintain backward compatibility when position is not available" in {
    // This test ensures that failures without positions still work
    val result = evaluateExpression("true < 2")

    result.hasSuppressedFailures should be(true)
    val failure = result.suppressedFailures.head

    failure.failureType should be(EvaluationFailureType.NOT_COMPARABLE)
    // Position might be None for comparison failures that don't have position tracking yet
  }
}
