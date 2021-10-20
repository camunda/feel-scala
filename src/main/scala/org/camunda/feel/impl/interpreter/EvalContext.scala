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

import org.camunda.feel.context.FunctionProvider.StaticFunctionProvider
import org.camunda.feel.context.VariableProvider.StaticVariableProvider
import org.camunda.feel.context.{Context, FunctionProvider, VariableProvider}
import org.camunda.feel.syntaxtree.{Val, ValError, ValFunction}
import org.camunda.feel.valuemapper.ValueMapper

class EvalContext(val valueMapper: ValueMapper,
                  val variableProvider: VariableProvider,
                  val functionProvider: FunctionProvider)
    extends Context {

  def variable(name: String): Val = {
    variableProvider
      .getVariable(name)
      .map(valueMapper.toVal)
      .getOrElse(ValError(s"no variable found for name '$name'"))
  }

  def function(name: String, paramCount: Int): Val = {
    functionProvider
      .getFunctions(name)
      .find(f =>
        f.params.size == paramCount || (f.params.size < paramCount && f.hasVarArgs))
      .getOrElse(ValError(
        s"no function found with name '$name' and $paramCount parameters"))
  }

  def function(name: String, parameters: Set[String]): Val = {
    functionProvider
      .getFunctions(name)
      .find(f => f.paramSet == parameters || parameters.subsetOf(f.paramSet))
      .getOrElse(ValError(
        s"no function found with name '$name' and parameters: ${parameters
          .mkString(",")}"))
  }

  def +(context: EvalContext): EvalContext = new EvalContext(
    valueMapper = valueMapper,
    variableProvider = VariableProvider.CompositeVariableProvider(
      List(context.variableProvider, variableProvider)),
    functionProvider = FunctionProvider.CompositeFunctionProvider(
      List(context.functionProvider, functionProvider))
  )

  def +(context: Context): EvalContext = new EvalContext(
    valueMapper = valueMapper,
    variableProvider = VariableProvider.CompositeVariableProvider(
      List(context.variableProvider, variableProvider)),
    functionProvider = FunctionProvider.CompositeFunctionProvider(
      List(context.functionProvider, functionProvider))
  )

  def +(entry: (String, Any)): EvalContext = entry match {
    case (k: String, f: ValFunction) => addFunction(k, f)
    case (k: String, v)              => addVariable(k, v)
  }

  def addVariable(key: String, variable: Any): EvalContext = new EvalContext(
    valueMapper = valueMapper,
    variableProvider = VariableProvider.CompositeVariableProvider(
      List(variableProvider, StaticVariableProvider(Map(key -> variable)))),
    functionProvider = functionProvider
  )

  def addFunction(key: String, function: ValFunction): EvalContext =
    new EvalContext(
      valueMapper = valueMapper,
      variableProvider = variableProvider,
      functionProvider = FunctionProvider.CompositeFunctionProvider(
        List(StaticFunctionProvider(Map(key -> List(function))),
             functionProvider)
      )
    )

  def ++(variables: Map[String, Any]): EvalContext = new EvalContext(
    valueMapper = valueMapper,
    variableProvider = VariableProvider.CompositeVariableProvider(
      List(StaticVariableProvider(variables), variableProvider)),
    functionProvider = functionProvider
  )

}

object EvalContext {

  def wrap(context: Context)(implicit valueMapper: ValueMapper): EvalContext =
    new EvalContext(
      valueMapper = valueMapper,
      variableProvider = context.variableProvider,
      functionProvider = context.functionProvider
    )

}
