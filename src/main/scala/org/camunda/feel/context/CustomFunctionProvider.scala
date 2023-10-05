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

/** Provides one or more functions which can be used in an expression.
  */
trait CustomFunctionProvider extends FunctionProvider {

  /** Returns a list of functions for the given name. There can be multiple functions with different
    * parameters.
    *
    * @param name
    *   the name of the function
    * @return
    *   a list of functions or an empty list, if no function is provided for this name
    */
  override def getFunctions(name: String): List[ValFunction] =
    getFunction(name)
      .map(List(_))
      .getOrElse(List.empty)

  /** Returns the function for the given name.
    *
    * @param name
    *   the name of the function
    * @return
    *   the function or [[None]], if no function is provided for this name
    */
  def getFunction(name: String): Option[ValFunction]

}
