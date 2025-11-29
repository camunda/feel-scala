package org.camunda.feel.cli

import org.camunda.feel.api.{FeelEngineApi, FeelEngineBuilder}

/** Shared FEEL expression evaluation logic */
object FeelEvaluator {

  lazy val engine: FeelEngineApi = FeelEngineBuilder.create().build()

  /** Evaluate an expression and return the result or error message */
  def evaluate(expression: String): Either[String, String] = {
    val result = engine.evaluateExpression(expression)
    if (result.isSuccess) {
      Right(Option(result.result).fold("null")(_.toString))
    } else {
      Left(s"Error: ${result.failure}")
    }
  }
}
