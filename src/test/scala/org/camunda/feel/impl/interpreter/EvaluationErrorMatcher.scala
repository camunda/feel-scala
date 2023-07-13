package org.camunda.feel.impl.interpreter

import org.camunda.feel.syntaxtree.{Val, ValError}
import org.scalatest.matchers.{BeMatcher, MatchResult}

trait EvaluationErrorMatcher {
  class EvaluationErrorMatcher(expectedMessage: String) extends BeMatcher[Val] {
    override def apply(result: Val): MatchResult =
      result match {
        case ValError(failure) => MatchResult(
          failure.startsWith(expectedMessage),
          s"$result doesn't start with '$expectedMessage'",
          s"$result starts with '$expectedMessage'",
        )
        case _ => MatchResult(
          false,
          s"$result is not an error",
          s"$result is an error"
        )
      }
  }

  def anError(expectedMessage: String) = new EvaluationErrorMatcher(expectedMessage)

  def aParseError: EvaluationErrorMatcher = anError(expectedMessage = "failed to parse expression")
}

object EvaluationErrorMatcher extends EvaluationErrorMatcher