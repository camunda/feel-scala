package org.camunda.feel.api

case class EvaluationFailure(
                              failureType: EvaluationFailureType,
                              failureMessage: String
                            )
