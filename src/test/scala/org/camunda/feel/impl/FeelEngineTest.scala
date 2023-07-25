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

import org.camunda.feel.api.{EvaluationResult, FeelEngineApi, FeelEngineBuilder, SuccessfulEvaluationResult, FailedEvaluationResult}
import org.camunda.feel.context.Context
import org.camunda.feel.syntaxtree.ValFunction

trait FeelEngineTest {

  val engine: FeelEngineApi = FeelEngineBuilder()
    .withEnabledExternalFunctions(true)
    .build()

  def evaluateExpression(
                          expression: String,
                          variables: Map[String, Any] = Map(),
                          functions: Map[String, ValFunction] = Map()
                        ): EvaluationResult = {
    val context =
      Context.StaticContext(
        variables = variables,
        functions = functions.map { case (n, f) => n -> List(f) })

    engine.evaluateExpression(expression, context)
  }

  def evaluateFunction(function: String): ValFunction = {
    engine.evaluateExpression(function) match {
      case SuccessfulEvaluationResult(result: ValFunction, _) => result
      case SuccessfulEvaluationResult(result, _) =>
        throw new AssertionError(s"Expected to return a function but was '$result'")
      case FailedEvaluationResult(failure, _) =>
        throw new AssertionError(s"Expected to return a function but failed with '${failure.message}'")
    }
  }

}
