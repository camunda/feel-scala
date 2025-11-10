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

import scala.jdk.CollectionConverters.SeqHasAsJava

/** The result of an expression evaluation.
  */
sealed trait EvaluationResult {

  /** The result value of the evaluation.
    */
  val result: Any

  /** The cause if the evaluation failed.
    */
  val failure: Failure

  /** Is true if the evaluation was successful.
    */
  val isSuccess: Boolean

  /** The suppressed failures that occurred during the evaluation. These failures doesn't result in
    * an evaluation failure but may indicate an unintended behavior. Use them for debugging purpose.
    */
  val suppressedFailures: List[EvaluationFailure]

  /** Is true if the evaluation failed.
    */
  def isFailure: Boolean = !isSuccess

  /** Is true if the evaluation has suppressed failures.
    */
  def hasSuppressedFailures: Boolean = suppressedFailures.nonEmpty

  /** Returns the evaluation result as an Either type. If the evaluation was successful, it returns
    * the result as Right. Otherwise, it returns the failure as Left.
    */
  def toEither: Either[Failure, Any] =
    if (isSuccess) Right(result)
    else Left(failure)

  /** The suppressed failures that occurred during the evaluation. These failures doesn't result in
    * an evaluation failure but may indicate an unintended behavior. Use them for debugging purpose.
    */
  def getSuppressedFailures: java.util.List[EvaluationFailure] =
    suppressedFailures.asJava

}

case class SuccessfulEvaluationResult(
    result: Any,
    suppressedFailures: List[EvaluationFailure] = List.empty
) extends EvaluationResult {
  override val isSuccess: Boolean = true
  override val failure: Failure   = Failure("<success>")
}

case class FailedEvaluationResult(
    failure: Failure,
    suppressedFailures: List[EvaluationFailure] = List.empty
) extends EvaluationResult {
  override val isSuccess: Boolean = false
  override val result: Any        = failure
}
