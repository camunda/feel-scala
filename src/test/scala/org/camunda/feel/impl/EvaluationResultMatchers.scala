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

import org.camunda.feel.FeelEngine.EvalExpressionResult
import org.scalatest.matchers.{MatchResult, Matcher}

trait EvaluationResultMatchers {

  def returnResult(expectedResult: Any) = new EvaluationResultValueMatcher(expectedResult)

  def returnNull() = new EvaluationResultValueMatcher(expectedResult = null)

  def failWith(expectedFailureMessage: String) =
    new EvaluationResultFailureMatcher(expectedFailureMessage)

  def failToParse() = new EvaluationResultFailureMatcher(
    expectedFailureMessage = "failed to parse"
  )

  class EvaluationResultValueMatcher(expectedResult: Any) extends Matcher[EvalExpressionResult] {
    override def apply(evaluationResult: EvalExpressionResult): MatchResult = {
      evaluationResult match {
        case Right(result) =>
          MatchResult(
            result == expectedResult,
            s"the evaluation didn't returned '$expectedResult' but '$evaluationResult'",
            s"The evaluation returned '$evaluationResult' as expected"
          )
        case Left(failure) =>
          MatchResult(
            false,
            s"the evaluation didn't returned '$expectedResult' but failed with '${failure.message}'",
            s"the evaluation didn't returned '$expectedResult' but failed with '${failure.message}'"
          )
      }
    }
  }

  class EvaluationResultFailureMatcher(expectedFailureMessage: String)
      extends Matcher[EvalExpressionResult] {
    override def apply(evaluationResult: EvalExpressionResult): MatchResult = {
      evaluationResult match {
        case Right(result) =>
          MatchResult(
            false,
            s"the evaluation didn't fail with '$expectedFailureMessage' but returned '$result'",
            s"the evaluation didn't fail with '$expectedFailureMessage' but returned '$result'"
          )
        case Left(failure) =>
          MatchResult(
            failure.message.contains(expectedFailureMessage),
            s"the evaluation failure message didn't contain '$expectedFailureMessage' but was '${failure.message}'",
            s"the evaluation failure message contained '${failure.message}' as expected"
          )
      }
    }
  }

}

object EvaluationResultMatchers extends EvaluationResultMatchers
