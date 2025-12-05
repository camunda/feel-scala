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

import scala.collection.mutable

trait FunctionProvider {

  def getFunctions(name: String): List[ValFunction]

  def functionNames: Iterable[String]

  def getFunctions(): Map[String, List[ValFunction]] =
    functionNames
      .map(name => name -> getFunctions(name))
      .toMap

}

object FunctionProvider {

  object EmptyFunctionProvider extends FunctionProvider {

    override def getFunctions(name: String): List[ValFunction] = List.empty

    override def functionNames: Iterable[String] = List.empty
  }

  case class StaticFunctionProvider(functions: Map[String, List[ValFunction]])
      extends FunctionProvider {

    override def getFunctions(name: String): List[ValFunction] =
      functions.getOrElse(name, List.empty)

    override def functionNames: Iterable[String] = functions.keys
  }

  case class CacheFunctionProvider(provider: FunctionProvider) extends FunctionProvider {

    private val cache: mutable.Map[String, List[ValFunction]] =
      mutable.Map.empty

    override def getFunctions(name: String): List[ValFunction] =
      cache.getOrElseUpdate(name, provider.getFunctions(name))

    override def functionNames: Iterable[String] =
      cache.keys ++ provider.functionNames
  }

  case class CompositeFunctionProvider(providers: List[FunctionProvider]) extends FunctionProvider {

    override def getFunctions(name: String): List[ValFunction] =
      providers.flatMap(_.getFunctions(name))

    override def functionNames: Iterable[String] =
      providers.flatMap(_.functionNames)
  }

}
