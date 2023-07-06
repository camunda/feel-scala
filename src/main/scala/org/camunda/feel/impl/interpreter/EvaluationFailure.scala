package org.camunda.feel.impl.interpreter

import org.camunda.feel.impl.interpreter.EvaluationFailure.EvaluationFailureType

object EvaluationFailure {
  sealed trait EvaluationFailureType
  case object UNKOWN extends EvaluationFailureType
  case object NO_VARIABLE_FOUND extends EvaluationFailureType
  case object NO_CONTEXT_ENTRY_FOUND extends EvaluationFailureType
  case object ILLEGAL_ARGUMENTS extends EvaluationFailureType
  case object NO_PROPERTY_FOUND extends EvaluationFailureType

}

case class EvaluationFailure(
  failureType: EvaluationFailureType,
  failureMessage: String
  )
