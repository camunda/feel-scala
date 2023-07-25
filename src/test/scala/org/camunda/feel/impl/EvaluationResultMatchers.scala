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

import org.camunda.feel.api.{EvaluationFailure, EvaluationFailureType, EvaluationResult, FailedEvaluationResult, SuccessfulEvaluationResult}
import org.scalatest.matchers.{MatchResult, Matcher}

trait EvaluationResultMatchers {

  def returnResult(expectedResult: Any) = new EvaluationResultValueMatcher(expectedResult)

  def returnNull() = new EvaluationResultValueMatcher(expectedResult = null)

  def reportFailure(failureType: EvaluationFailureType, failureMessage: String) =
    new SuppressedFailureMatcher(EvaluationFailure(failureType, failureMessage))

  class EvaluationResultValueMatcher(expectedResult: Any) extends Matcher[EvaluationResult] {
    override def apply(evaluationResult: EvaluationResult): MatchResult = {
      evaluationResult match {
        case SuccessfulEvaluationResult(result, _) => MatchResult(
          result == expectedResult,
          s"the evaluation didn't returned '$expectedResult' but '${evaluationResult.result}'",
          s"The evaluation returned '${evaluationResult.result}' as expected",
        )
        case FailedEvaluationResult(failure, _) => MatchResult(
          false,
          s"the evaluation didn't returned '$expectedResult' but failed with '${failure.message}'",
          s"the evaluation didn't returned '$expectedResult' but failed with '${failure.message}'",
        )
      }
    }
  }

  class SuppressedFailureMatcher(expectedFailure: EvaluationFailure) extends Matcher[EvaluationResult] {
    override def apply(evaluationResult: EvaluationResult): MatchResult = {
      val matchResult = (suppressedFailures: List[EvaluationFailure]) => MatchResult(
        suppressedFailures.contains(expectedFailure),
        s"the evaluation didn't report '$expectedFailure' but '$suppressedFailures'",
        s"the evaluation reported '$expectedFailure' as expected",
      )
      evaluationResult match {
        case SuccessfulEvaluationResult(_, suppressedFailures) => matchResult(suppressedFailures)
        case FailedEvaluationResult(_, suppressedFailures) => matchResult(suppressedFailures)
      }
    }
  }

}

object EvaluationResultMatchers extends EvaluationResultMatchers