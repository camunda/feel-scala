package org.camunda.feel.impl

import org.camunda.feel.api._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.{MatchResult, Matcher}

class SuppressedFailuresTest extends AnyFlatSpec
  with Matchers {

  private val engine: FeelEngineApi = FeelEngineBuilder().build()

  "The engine" should "report a suppressed failure for a non-existing variable" in {
    engine.evaluateExpression("x + 1") should reportFailure(
      failureType = EvaluationFailureType.NO_VARIABLE_FOUND,
      failureMessage = "No variable found with name 'x'"
    )
  }

  it should "report a suppressed failure for a non-existing context entry" in {
    engine.evaluateExpression("{x: 1}.y") should reportFailure(
      failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
      failureMessage = "context contains no entry with key 'y'"
    )
  }

  it should "report a suppressed failure for a non-existing property" in {
    engine.evaluateExpression(""" @"P1Y".days """) should reportFailure(
      failureType = EvaluationFailureType.NO_PROPERTY_FOUND,
      failureMessage = "No property found with name 'days' of value 'P1Y'. Available properties: years,months"
    )
  }

  it should "report a suppressed failure for a non-existing function (with position arguments)" in {
    engine.evaluateExpression("f(1, 2)") should reportFailure(
      failureType = EvaluationFailureType.NO_FUNCTION_FOUND,
      failureMessage = "no function found with name 'f' and 2 parameters"
    )
  }

  it should "report a suppressed failure for a non-existing function (with named arguments)" in {
    engine.evaluateExpression("f(x: 1, y: 2)") should reportFailure(
      failureType = EvaluationFailureType.NO_FUNCTION_FOUND,
      failureMessage = "no function found with name 'f' and parameters: x,y"
    )
  }

  it should "report a suppressed failure if a function invocation fails" in {
    engine.evaluateExpression("number(null)") should reportFailure(
      failureType = EvaluationFailureType.FUNCTION_INVOCATION_FAILURE,
      failureMessage = "Failed to invoke function 'number': Illegal arguments: List(ValNull)"
    )
  }

  it should "report a suppressed failure if input is not comparable with interval" in {
    engine.evaluateUnaryTests("[2..5]", "NaN") should reportFailure(
      failureType = EvaluationFailureType.NOT_COMPARABLE,
      failureMessage = "Can't compare ValString(NaN) with ValNumber(2) and ValNumber(5)"
    )
  }

  it should "report a suppressed failure if values are not comparable" in {
    engine.evaluateExpression("true < 2") should reportFailure(
      failureType = EvaluationFailureType.NOT_COMPARABLE,
      failureMessage = "Can't compare ValBoolean(true) with ValNumber(2)"
    )
  }

  it should "report a suppressed failure if an addition has incompatible values" in {
    engine.evaluateExpression("2 + true") should reportFailure(
      failureType = EvaluationFailureType.INVALID_TYPE,
      failureMessage = "Expected Number but found 'ValBoolean(true)'"
    )
  }

  it should "report a suppressed failure if a condition is not a boolean" in {
    engine.evaluateExpression("if 5 then 1 else 2") should reportFailure(
      failureType = EvaluationFailureType.INVALID_TYPE,
      failureMessage = "Expected Boolean but found 'ValNumber(5)'"
    )

    engine.evaluateExpression("true and 2") should reportFailure(
      failureType = EvaluationFailureType.INVALID_TYPE,
      failureMessage = "Expected Boolean but found 'ValNumber(2)'"
    )

    engine.evaluateExpression("false or 3") should reportFailure(
      failureType = EvaluationFailureType.INVALID_TYPE,
      failureMessage = "Expected Boolean but found 'ValNumber(3)'"
    )

    engine.evaluateExpression("some x in [false, 2] satisfies x") should reportFailure(
      failureType = EvaluationFailureType.INVALID_TYPE,
      failureMessage = "Expected Boolean but found 'ValNumber(2)'"
    )

    engine.evaluateExpression("every x in [true, 3] satisfies x") should reportFailure(
      failureType = EvaluationFailureType.INVALID_TYPE,
      failureMessage = "Expected Boolean but found 'ValNumber(3)'"
    )
  }

  it should "report a suppressed failure only once" in {
    val evaluationResult = engine.evaluateExpression("1 + x")

    evaluationResult.hasSuppressedFailures should be (true)
    evaluationResult.suppressedFailures should have size(2)

    evaluationResult.suppressedFailures should contain inOrder(
      EvaluationFailure(
        failureType = EvaluationFailureType.NO_VARIABLE_FOUND,
        failureMessage = "No variable found with name 'x'"
      ),
      EvaluationFailure(
        failureType = EvaluationFailureType.INVALID_TYPE,
        failureMessage = "Expected Number but found 'ValError(No variable found with name 'x')'"
      )
    )
  }

  // ========== test helpers =========

  private def reportFailure(failureType: EvaluationFailureType, failureMessage: String) =
    new SuppressedFailureMatcher(EvaluationFailure(failureType, failureMessage))

  private class SuppressedFailureMatcher(expectedFailure: EvaluationFailure) extends Matcher[EvaluationResult] {
    override def apply(evaluationResult: EvaluationResult): MatchResult = {
      val matchResult = (suppressedFailures: List[EvaluationFailure]) => MatchResult(
        suppressedFailures.contains(expectedFailure),
        s"${evaluationResult.suppressedFailures} doesn't contain '$expectedFailure'",
        s"$evaluationResult contains '$expectedFailure'",
      )
      evaluationResult match {
        case SuccessfulEvaluationResult(_, suppressedFailures) => matchResult(suppressedFailures)
        case FailedEvaluationResult(_, suppressedFailures) => matchResult(suppressedFailures)
      }
    }
  }

}
