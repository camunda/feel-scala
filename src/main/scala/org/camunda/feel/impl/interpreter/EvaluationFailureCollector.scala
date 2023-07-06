package org.camunda.feel.impl.interpreter

import org.camunda.feel.impl.interpreter.EvaluationFailure.EvaluationFailureType

class EvaluationFailureCollector {

  var failures: List[EvaluationFailure] = List.empty

  def addFailure(failureType: EvaluationFailureType, failureMessage: String): Unit = {
    failures :+= EvaluationFailure(
      failureType = failureType,
      failureMessage = failureMessage
    )
  }

  def hasFailures: Boolean = failures.nonEmpty

  override def toString: String = failures
    .map { case EvaluationFailure(failureType, message) => s"* [$failureType] $message" }
    .mkString("\n")
}
