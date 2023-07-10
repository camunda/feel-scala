package org.camunda.feel.api

import org.camunda.feel.FeelEngine.Failure

sealed trait EvaluationResult {
  val result: Any
  val failure: Failure
  val isSuccess: Boolean
  val suppressedFailures: List[EvaluationFailure]

  def isFailure: Boolean = !isSuccess
  def hasSuppressedFailures: Boolean = suppressedFailures.nonEmpty

  def toEither: Either[Failure, Any] =
    if (isSuccess) Right(result)
    else Left(failure)

}

case class SuccessfulEvaluationResult(
                                       result: Any,
                                       suppressedFailures: List[EvaluationFailure] = List.empty) extends EvaluationResult {
  override val isSuccess: Boolean = true
  override val failure: Failure = Failure("<success>")
}

case class FailedEvaluationResult(
                                   failure: Failure,
                                   suppressedFailures: List[EvaluationFailure] = List.empty
                                 ) extends EvaluationResult {
  override val isSuccess: Boolean = false
  override val result: Any = failure
}
