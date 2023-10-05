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

import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree.{Val, ValBoolean, ValError, ValNull}

object BooleanBuiltinFunctions {

  def functions = Map(
    "not"        -> List(notFunction),
    "is defined" -> List(isDefinedFunction)
  )

  private def notFunction =
    builtinFunction(
      params = List("negand"),
      invoke = {
        case List(ValBoolean(negand)) => ValBoolean(!negand)
        case List(_: Val)             => ValNull
        case _                        => ValNull
      }
    )

  private def isDefinedFunction = builtinFunction(
    params = List("value"),
    invoke = {
      case (value: ValError) :: Nil => ValBoolean(false)
      case _                        => ValBoolean(true)
    }
  )
}
