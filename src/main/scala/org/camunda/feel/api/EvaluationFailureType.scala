package org.camunda.feel.api

sealed trait EvaluationFailureType

object EvaluationFailureType {
  case object UNKNOWN extends EvaluationFailureType

  case object NO_VARIABLE_FOUND extends EvaluationFailureType

  case object NO_CONTEXT_ENTRY_FOUND extends EvaluationFailureType

  case object ILLEGAL_ARGUMENTS extends EvaluationFailureType

  case object NO_PROPERTY_FOUND extends EvaluationFailureType
}


