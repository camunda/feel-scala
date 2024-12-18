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
package org.camunda.feel.impl.builtin

import org.camunda.feel.syntaxtree.{Val, ValError, ValFatalError, ValFunction}

object BuiltinFunction {

  def builtinFunction(
      params: List[String],
      invoke: PartialFunction[List[Val], Any],
      hasVarArgs: Boolean = false
  ): ValFunction = {
    ValFunction(
      params = params,
      invoke = validateArgs.orElse(invoke).orElse(error),
      hasVarArgs = hasVarArgs
    )
  }

  private def validateArgs: PartialFunction[List[Val], Any] = {
    case args if args.exists(_.isInstanceOf[ValFatalError]) => args.find(_.isInstanceOf[ValFatalError]).get
    case args if args.exists(_.isInstanceOf[ValError]) => args.find(_.isInstanceOf[ValError]).get
  }

  private def error: PartialFunction[List[Val], Any] = {
    case args =>
      val argumentList = args.map("'" + _ + "'").mkString(", ")
      ValError(s"Illegal arguments: $argumentList")
  }

}
