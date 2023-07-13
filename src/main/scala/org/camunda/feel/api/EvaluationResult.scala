package org.camunda.feel.api

import org.camunda.feel.FeelEngine.Failure

/**
  * The result of an expression evaluation.
  */
sealed trait EvaluationResult {
  
  /**
    * The result value of the evaluation.
    */
  val result: Any

  /**
    * The cause if the evaluation failed.
    */
  val failure: Failure

  /**
    * Is true if the evaluation was successful.
    */
  val isSuccess: Boolean

  /**
    * The suppressed failures that occurred during the evaluation. These failures doesn't result in
    * an evaluation failure but may indicate an unintended behavior. Use them for debugging purpose.
    */
  val suppressedFailures: List[EvaluationFailure]

  /**
    * Is true if the evaluation failed.
    */
  def isFailure: Boolean = !isSuccess

  /**
    * Is true if the evaluation has suppressed failures.
    */
  def hasSuppressedFailures: Boolean = suppressedFailures.nonEmpty

  /**
    * Returns the evaluation result as an Either type. If the evaluation was successful, it returns
    * the result as Right. Otherwise, it returns the failure as Left.
    */
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
