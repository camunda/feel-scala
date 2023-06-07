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

import org.camunda.feel.context.Context.{EmptyContext, StaticContext}
import org.camunda.feel.context.FunctionProvider.{CompositeFunctionProvider, EmptyFunctionProvider, StaticFunctionProvider}
import org.camunda.feel.context.VariableProvider.{CompositeVariableProvider, EmptyVariableProvider, StaticVariableProvider}
import org.camunda.feel.context.{Context, FunctionProvider, VariableProvider}
import org.camunda.feel.impl.interpreter.EvalContext.{mergeFunctionProviders, mergeVariableProvider, toSortedVariableProvider, wrap}
import org.camunda.feel.syntaxtree.{Val, ValError, ValFunction}
import org.camunda.feel.valuemapper.ValueMapper

import scala.collection.immutable.SeqMap

class EvalContext(val valueMapper: ValueMapper,
                  val variableProvider: VariableProvider,
                  val functionProvider: FunctionProvider) extends Context {

  def variable(name: String): Val = {
    variableProvider
      .getVariable(name)
      .map(valueMapper.toVal)
      .getOrElse(ValError(s"no variable found for name '$name'"))
  }

  def function(name: String, paramCount: Int): Val = {
    val filter = (f: ValFunction) => f.params.size == paramCount ||
      (f.params.size < paramCount && f.hasVarArgs)

    functionProvider
      .getFunctions(name)
      .find(filter)
      .getOrElse(ValError(
        s"no function found with name '$name' and $paramCount parameters"))
  }

  def function(name: String, parameters: Set[String]): Val = {
    val filter = (f: ValFunction) => f.paramSet == parameters || parameters.subsetOf(f.paramSet)

    functionProvider
      .getFunctions(name)
      .find(filter)
      .getOrElse(ValError(
        s"no function found with name '$name' and parameters: ${
          parameters
            .mkString(",")
        }"))
  }

  def merge(otherContext: EvalContext): EvalContext = new EvalContext(
    valueMapper = valueMapper,
    variableProvider = mergeVariableProvider(variableProvider, otherContext.variableProvider),
    functionProvider = mergeFunctionProviders(functionProvider, otherContext.functionProvider)
  )

  def merge(otherContext: Context): EvalContext = {
    val wrappedContext = wrap(otherContext, valueMapper)
    merge(wrappedContext)
  }

  def add(entry: (String, Val)): EvalContext = entry match {
    case (k: String, f: ValFunction) => addFunction(k, f)
    case (k: String, v) => addVariable(k, v)
  }

  private def addVariable(key: String, variable: Val): EvalContext = new EvalContext(
    valueMapper = valueMapper,
    variableProvider = mergeVariableProvider(
      variableProvider,
      toSortedVariableProvider(key, variable)
    ),
    functionProvider = functionProvider
  )

  private def addFunction(key: String, function: ValFunction): EvalContext = new EvalContext(
    valueMapper = valueMapper,
    variableProvider = variableProvider,
    functionProvider = mergeFunctionProviders(
      functionProvider,
      StaticFunctionProvider(Map(key -> List(function)))
    )
  )

  def addAll(newVariables: Map[String, Val]): EvalContext = new EvalContext(
    valueMapper = valueMapper,
    variableProvider = mergeVariableProvider(
      variableProvider,
      toSortedVariableProvider(newVariables)
    ),
    functionProvider = functionProvider
  )

}

object EvalContext {

  def empty(valueMapper: ValueMapper): EvalContext = create(
    valueMapper = valueMapper,
    functionProvider = EmptyFunctionProvider
  )

  def create(valueMapper: ValueMapper, functionProvider: FunctionProvider): EvalContext = new EvalContext(
    valueMapper = valueMapper,
    variableProvider = EmptyVariableProvider,
    functionProvider = functionProvider
  )

  def wrap(context: Context, valueMapper: ValueMapper): EvalContext =
    context match {
      case evalContext: EvalContext => evalContext
      case EmptyContext => empty(valueMapper)
      case StaticContext(variables, functions) => new EvalContext(
        valueMapper = valueMapper,
        variableProvider = toSortedVariableProvider(variables),
        functionProvider = StaticFunctionProvider(functions)
      )
      case _ => new EvalContext(
        valueMapper = valueMapper,
        variableProvider = context.variableProvider,
        functionProvider = context.functionProvider
      )
    }

  private def mergeVariableProvider(provider: VariableProvider, otherProvider: VariableProvider): VariableProvider = {
    (provider, otherProvider) match {
      case (EmptyVariableProvider, EmptyVariableProvider) => EmptyVariableProvider
      case (EmptyVariableProvider, otherProvider) => otherProvider
      case (thisProvider, EmptyVariableProvider) => thisProvider
      case (StaticVariableProvider(thisVariables), StaticVariableProvider(otherVariables)) =>
        StaticVariableProvider(thisVariables ++ otherVariables)
      case (thisProvider, otherProvider) => CompositeVariableProvider(List(thisProvider, otherProvider))
    }
  }

  private def mergeFunctionProviders(provider: FunctionProvider, otherProvider: FunctionProvider): FunctionProvider = {
    (provider, otherProvider) match {
      case (EmptyFunctionProvider, EmptyFunctionProvider) => EmptyFunctionProvider
      case (EmptyFunctionProvider, otherProvider) => otherProvider
      case (thisProvider, EmptyFunctionProvider) => thisProvider
      case (StaticFunctionProvider(thisFunctions), StaticFunctionProvider(otherFunctions)) => {
        val allKeys = thisFunctions.keys ++ otherFunctions.keys
        val functionsByKey = (key: String) => thisFunctions.getOrElse(key, List.empty) ++ otherFunctions.getOrElse(key, List.empty)
        val allFunctions = allKeys.map(key => key -> functionsByKey(key)).toMap
        StaticFunctionProvider(allFunctions)
      }
      case (thisProvider, otherProvider) => CompositeFunctionProvider(List(thisProvider, otherProvider))
    }
  }

  private def toSortedVariableProvider(variables: Map[String, Any]): VariableProvider =
    StaticVariableProvider(SeqMap[String, Any]() ++ variables)

  private def toSortedVariableProvider(entry: (String, Any)): VariableProvider =
    StaticVariableProvider(SeqMap(entry))

}
