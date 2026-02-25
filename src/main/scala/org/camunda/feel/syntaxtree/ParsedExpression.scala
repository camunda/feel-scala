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
package org.camunda.feel.syntaxtree

import org.camunda.feel.api.VariableReference
import org.camunda.feel.impl.parser.ExpressionVariableExtractor

import scala.jdk.CollectionConverters.SetHasAsJava

case class ParsedExpression(expression: Exp, text: String) {

  /** Returns the variable references from the parsed expression. The references include the
    * variable names and their nested variable properties (e.g., for an expression `a.b + c` the
    * references are `[a.b, c]`).
    */
  lazy val variableReferences: Set[VariableReference] =
    ExpressionVariableExtractor.getVariableReferences(expression)

  /** Returns the names of the referenced variables from the parsed expression. The names do not
    * include nested variable properties, but only the top-level variable names (e.g., for an
    * expression `a.b + c` the variable names are `[a, c]`).
    */
  lazy val variableNames: Set[String] = variableReferences.map(_.variableName)

  /** Returns the variable references from the parsed expression. The references include the
    * variable names and their nested variable properties (e.g., for an expression `a.b + c` the
    * references are `[a.b, c]`).
    */
  def getVariableReferences: java.util.Set[VariableReference] = variableReferences.asJava

  /** Returns the names of the referenced variables from the parsed expression. The names do not
    * include nested variable properties, but only the top-level variable names (e.g., for an
    * expression `a.b + c` the variable names are `[a, c]`).
    */
  def getVariableNames: java.util.Set[String] = variableNames.asJava
}
