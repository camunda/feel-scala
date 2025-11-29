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
package org.camunda.feel.impl.script

import fastparse.Parsed
import org.camunda.feel.FeelEngine
import org.camunda.feel.FeelEngine.EvalExpressionResult
import org.camunda.feel.syntaxtree.Exp

import javax.script.{ScriptContext, ScriptEngineFactory, ScriptException}
import scala.jdk.CollectionConverters._

trait AbstractFeelScriptEngine {
  def eval: (String, Map[String, Any]) => EvalExpressionResult

  def parse: String => Parsed[Exp]

  def factory: ScriptEngineFactory

  def engine: FeelEngine

  def eval(script: String, context: ScriptContext): Object = {
    val engineContext = getEngineContext(context)
    val result        = eval(script, engineContext)

    handleEvaluationResult(result)
  }

  protected def handleEvaluationResult(result: EvalExpressionResult): Object =
    result match {
      case Right(value)  => value.asInstanceOf[AnyRef]
      case Left(failure) => throw new ScriptException(failure.message)
    }

  protected def getEngineContext(context: ScriptContext): Map[String, Any] = {
    List(ScriptContext.GLOBAL_SCOPE, ScriptContext.ENGINE_SCOPE)
      .flatMap(scope => Option(context.getBindings(scope)))
      .flatMap(_.asScala)
      .toMap
  }

}
