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

import org.camunda.feel.syntaxtree.{Val, ValFunction}

import java.util.{Collections, Optional}
import scala.jdk.CollectionConverters.{CollectionHasAsScala, ListHasAsScala, SeqHasAsJava}

/** Provides one or more functions which can be used in an expression.
  */
abstract class JavaFunctionProvider extends CustomFunctionProvider {

  /** Returns the function for the given name.
    *
    * @param functionName
    *   the name of the function
    * @return
    *   the function or [[Optional.empty()]], if no function is provided for this name
    */
  def resolveFunction(functionName: String): Optional[JavaFunction]

  /** Returns the names of all functions.
    *
    * @return
    *   the names of all functions
    */
  def getFunctionNames(): java.util.Collection[String]

  /** Returns a list of functions for the given name. There can be multiple functions with different
    * parameters.
    *
    * @param functionName
    *   the name of the function
    * @return
    *   a list of functions or an empty list, if no function is provided for this name
    */
  def resolveFunctions(functionName: String): java.util.List[JavaFunction] = {
    val function = resolveFunction(functionName)

    if (function.isPresent) {
      Collections.singletonList(function.get)
    } else {
      Collections.emptyList()
    }
  }

  override def getFunctions(name: String): List[ValFunction] = {
    resolveFunctions(name).asScala.map(f => asFunction(f)).toList
  }

  override def getFunction(name: String): Option[ValFunction] = {
    getFunctions(name).headOption
  }

  private def asFunction(function: JavaFunction): ValFunction = {

    val paramList: List[String] = function.getParams().asScala.toList

    val f: (List[Val] => Val) = (args: List[Val]) => {

      val argList: java.util.List[Val] = args.asJava

      function.getFunction().apply(argList)
    }

    ValFunction(paramList, f, function.hasVarArgs)
  }

  override def functionNames: Iterable[String] = getFunctionNames().asScala
}
