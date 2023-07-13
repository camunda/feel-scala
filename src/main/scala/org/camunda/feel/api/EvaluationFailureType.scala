package org.camunda.feel.api

sealed trait EvaluationFailureType

/**
  * Defines the type of an evaluation failure.
  */
object EvaluationFailureType {

  case object UNKNOWN extends EvaluationFailureType

  case object NO_VARIABLE_FOUND extends EvaluationFailureType

  case object NO_CONTEXT_ENTRY_FOUND extends EvaluationFailureType

  case object NO_PROPERTY_FOUND extends EvaluationFailureType

  case object NOT_COMPARABLE extends EvaluationFailureType

  case object INVALID_TYPE extends EvaluationFailureType

  case object NO_FUNCTION_FOUND extends EvaluationFailureType

  case object FUNCTION_INVOCATION_FAILURE extends EvaluationFailureType

}


