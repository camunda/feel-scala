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

import org.camunda.feel.syntaxtree.{Val, ValError}
import org.scalatest.matchers.{BeMatcher, MatchResult}

trait EvaluationErrorMatcher {
  class EvaluationErrorMatcher(expectedMessage: String) extends BeMatcher[Val] {
    override def apply(result: Val): MatchResult =
      result match {
        case ValError(failure) => MatchResult(
          failure.startsWith(expectedMessage),
          s"$result doesn't start with '$expectedMessage'",
          s"$result starts with '$expectedMessage'",
        )
        case _ => MatchResult(
          false,
          s"$result is not an error",
          s"$result is an error"
        )
      }
  }

  def anError(expectedMessage: String) = new EvaluationErrorMatcher(expectedMessage)

  def aParseError: EvaluationErrorMatcher = anError(expectedMessage = "failed to parse expression")
}

object EvaluationErrorMatcher extends EvaluationErrorMatcher