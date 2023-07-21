package org.camunda.feel.impl.interpreter

import org.camunda.feel.api.{EvaluationFailure, EvaluationFailureType}

class EvaluationFailureCollector {

  var failures: List[EvaluationFailure] = List.empty

  def addFailure(failureType: EvaluationFailureType, failureMessage: String): Unit = {
    failures :+= EvaluationFailure(
      failureType = failureType,
      failureMessage = failureMessage
    )
  }

  override def toString: String = failures
    .map { case EvaluationFailure(failureType, message) => s"* [$failureType] $message" }
    .mkString("\n")
}
