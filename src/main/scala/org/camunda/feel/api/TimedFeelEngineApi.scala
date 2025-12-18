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
package org.camunda.feel.api

import org.camunda.feel.FeelEngine
import org.camunda.feel.context.Context
import org.camunda.feel.syntaxtree.ParsedExpression

import scala.concurrent.duration.FiniteDuration

/** A FeelEngineApi that applies a default timeout to evaluation entry points.
  *
  * This wrapper preserves backward compatibility: existing call-sites can keep using the
  * non-timeout `evaluate*` methods, while a builder can still provide a default timeout for the API
  * instance.
  */
private[api] final class TimedFeelEngineApi(engine: FeelEngine, timeout: FiniteDuration)
    extends FeelEngineApi(engine) {

  override def evaluate(expression: ParsedExpression, context: Context): EvaluationResult =
    super.evaluate(expression, context, timeout)

  override def evaluateWithInput(
      expression: ParsedExpression,
      inputValue: Any,
      context: Context
  ): EvaluationResult =
    super.evaluateWithInput(expression, inputValue, context, timeout)

  override def evaluateExpression(expression: String, context: Context): EvaluationResult =
    super.evaluateExpression(expression, context, timeout)

  override def evaluateUnaryTests(
      expression: String,
      inputValue: Any,
      context: Context
  ): EvaluationResult =
    super.evaluateUnaryTests(expression, inputValue, context, timeout)
}
