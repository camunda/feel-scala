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
package org.camunda.feel.context

import org.camunda.feel.syntaxtree.ValFunction

/**
  * A Context provides access to the variables/fields and functions/methods in the scope represented by this Context.
  */
trait Context {

  def variableProvider: VariableProvider

  def functionProvider: FunctionProvider

}

object Context {

  object EmptyContext extends Context {

    override def variableProvider: VariableProvider =
      VariableProvider.EmptyVariableProvider

    override def functionProvider: FunctionProvider =
      FunctionProvider.EmptyFunctionProvider
  }

  case class StaticContext(
      variables: Map[String, Any],
      functions: Map[String, List[ValFunction]] = Map.empty
  ) extends Context {

    override def variableProvider: VariableProvider =
      VariableProvider.StaticVariableProvider(variables)

    override def functionProvider: FunctionProvider =
      FunctionProvider.StaticFunctionProvider(functions)
  }

  case class CacheContext(context: Context) extends Context {

    override def variableProvider: VariableProvider =
      VariableProvider.CacheVariableProvider(context.variableProvider)

    override def functionProvider: FunctionProvider =
      FunctionProvider.CacheFunctionProvider(context.functionProvider)
  }

}
