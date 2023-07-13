package org.camunda.feel.api

/**
  * A failure that occurred during the evaluation of an FEEL expression.
  *
  * @param failureType    The type of failure.
  * @param failureMessage The message that describes the failure.
  */
case class EvaluationFailure(
                              failureType: EvaluationFailureType,
                              failureMessage: String
                            )
