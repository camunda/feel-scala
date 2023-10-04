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

import org.camunda.feel.context.Context
import org.camunda.feel.context.Context.StaticContext
import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree.{Val, ValContext, ValError, ValList, ValNull, ValString}

object ContextBuiltinFunctions {

  def functions = Map(
    "get entries" -> List(getEntriesFunction("context"), getEntriesFunction("m")),
    "get value"   -> List(
      getValueFunction(List("m", "key")),
      getValueFunction(List("context", "key"))
    ),
    "put"         -> List(putFunction),
    "put all"     -> List(putAllFunction),
    "context"     -> List(contextFunction)
  )

  private def getEntriesFunction(paramName: String) = builtinFunction(
    params = List(paramName),
    invoke = { case List(ValContext(c: Context)) =>
      c.variableProvider.getVariables.map { case (key, value) =>
        Map("key" -> ValString(key), "value" -> value)
      }.toList
    }
  )

  private def getValueFunction(parameters: List[String]) = builtinFunction(
    params = parameters,
    invoke = { case List(ValContext(c), ValString(key)) =>
      c.variableProvider
        .getVariable(key)
        .getOrElse(ValNull)
    }
  )

  private def putFunction = builtinFunction(
    params = List("context", "key", "value"),
    invoke = {
      case List(ValContext(_), ValString(_), ValError(_)) => ValNull
      case List(ValContext(c), ValString(key), value)     =>
        ValContext(StaticContext(variables = c.variableProvider.getVariables + (key -> value)))
    }
  )

  private def putAllFunction = builtinFunction(
    params = List("contexts"),
    invoke = {
      case List(ValList(contexts)) if isListOfContexts(contexts) =>
        ValContext(
          StaticContext(
            variables = contexts
              .flatMap(_ match {
                case ValContext(c) => c.variableProvider.getVariables
                case _             => Map.empty
              })
              .toMap
          )
        )
      case _                                                     => ValNull
    },
    hasVarArgs = true
  )

  private def isListOfContexts(list: List[Val]): Boolean =
    list.forall(_.isInstanceOf[ValContext])

  private def contextFunction = builtinFunction(
    params = List("entries"),
    invoke = {
      case List(ValList(entries)) if isListOfKeyValuePairs(entries) =>
        ValContext(StaticContext(variables = entries.flatMap { case ValContext(context) =>
          val getValue = context.variableProvider.getVariable(_)
          getValue("key")
            .map { case ValString(key) => key }
            .flatMap(key => getValue("value").map(value => key -> value))
        }.toMap))
      case _                                                        => ValNull
    }
  )

  private def isListOfKeyValuePairs(list: List[Val]): Boolean =
    list.forall {
      case ValContext(context) =>
        val keys = context.variableProvider.keys.toList
        keys.contains("value") && context.variableProvider
          .getVariable("key")
          .exists(_.isInstanceOf[ValString])
      case _                   => false
    }

}
