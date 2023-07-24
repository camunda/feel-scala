package org.camunda.feel.impl

import org.camunda.feel.api.{EvaluationFailure, EvaluationFailureType, EvaluationResult, FailedEvaluationResult, SuccessfulEvaluationResult}
import org.scalatest.matchers.{MatchResult, Matcher}

trait EvaluationResultMatchers {

  def returnResult(expectedResult: Any) = new EvaluationResultValueMatcher(expectedResult)

  def returnNull() = new EvaluationResultValueMatcher(expectedResult = null)

  def reportFailure(failureType: EvaluationFailureType, failureMessage: String) =
    new SuppressedFailureMatcher(EvaluationFailure(failureType, failureMessage))

  class EvaluationResultValueMatcher(expectedResult: Any) extends Matcher[EvaluationResult] {
    override def apply(evaluationResult: EvaluationResult): MatchResult = {
      evaluationResult match {
        case SuccessfulEvaluationResult(result, _) => MatchResult(
          result == expectedResult,
          s"the evaluation didn't returned '$expectedResult' but '${evaluationResult.result}'",
          s"The evaluation returned '${evaluationResult.result}' as expected",
        )
        case FailedEvaluationResult(failure, _) => MatchResult(
          false,
          s"the evaluation didn't returned '$expectedResult' but failed with '${failure.message}'",
          s"the evaluation didn't returned '$expectedResult' but failed with '${failure.message}'",
        )
      }
    }
  }

  class SuppressedFailureMatcher(expectedFailure: EvaluationFailure) extends Matcher[EvaluationResult] {
    override def apply(evaluationResult: EvaluationResult): MatchResult = {
      val matchResult = (suppressedFailures: List[EvaluationFailure]) => MatchResult(
        suppressedFailures.contains(expectedFailure),
        s"the evaluation didn't report '$expectedFailure' but '$suppressedFailures'",
        s"the evaluation reported '$expectedFailure' as expected",
      )
      evaluationResult match {
        case SuccessfulEvaluationResult(_, suppressedFailures) => matchResult(suppressedFailures)
        case FailedEvaluationResult(_, suppressedFailures) => matchResult(suppressedFailures)
      }
    }
  }

}

object EvaluationResultMatchers extends EvaluationResultMatchers