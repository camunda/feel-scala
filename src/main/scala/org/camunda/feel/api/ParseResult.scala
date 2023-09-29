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
package org.camunda.feel.api

import org.camunda.feel.FeelEngine.Failure
import org.camunda.feel.syntaxtree.{ConstNull, ParsedExpression}

/** The result of an expression parsing.
  */
sealed trait ParseResult {

  /** The parsed expression if the parsing was successful.
    */
  val parsedExpression: ParsedExpression

  /** The cause if the parsing failed.
    */
  val failure: Failure

  /** Is true if the parsing was successful.
    */
  val isSuccess: Boolean

  /** Is true if the parsing failed.
    */
  def isFailure: Boolean = !isSuccess

  /** Returns the parsing result as an Either type. If the parsing was successful, it returns the
    * result as Right. Otherwise, it returns the failure as Left.
    */
  def toEither: Either[Failure, ParsedExpression] =
    if (isSuccess) Right(parsedExpression)
    else Left(failure)

}

case class SuccessfulParseResult(parsedExpression: ParsedExpression) extends ParseResult {
  override val isSuccess: Boolean = true
  override val failure: Failure   = Failure("<success>")
}

case class FailedParseResult(expression: String, failure: Failure) extends ParseResult {
  override val isSuccess: Boolean                 = false
  override val parsedExpression: ParsedExpression = ParsedExpression(ConstNull, expression)
}
