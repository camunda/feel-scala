package org.camunda.feel.api

import org.camunda.feel.FeelEngine.Failure
import org.camunda.feel.syntaxtree.{ConstNull, ParsedExpression}

sealed trait ParseResult {
  val parsedExpression: ParsedExpression
  val failure: Failure
  val isSuccess: Boolean

  def isFailure: Boolean = !isSuccess

  def toEither: Either[Failure, ParsedExpression] =
    if (isSuccess) Right(parsedExpression)
    else Left(failure)

}

case class SuccessfulParseResult(parsedExpression: ParsedExpression) extends ParseResult {
  override val isSuccess: Boolean = true
  override val failure: Failure = Failure("<success>")
}

case class FailedParseResult(expression: String, failure: Failure) extends ParseResult {
  override val isSuccess: Boolean = false
  override val parsedExpression: ParsedExpression = ParsedExpression(ConstNull, expression)
}
