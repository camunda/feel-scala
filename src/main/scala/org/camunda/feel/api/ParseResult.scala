package org.camunda.feel.api

import org.camunda.feel.FeelEngine.Failure
import org.camunda.feel.syntaxtree.{ConstNull, ParsedExpression}

/**
  * The result of an expression parsing.
  */
sealed trait ParseResult {

  /**
    * The parsed expression if the parsing was successful.
    */
  val parsedExpression: ParsedExpression

  /**
    * The cause if the parsing failed.
    */
  val failure: Failure

  /**
    * Is true if the parsing was successful.
    */
  val isSuccess: Boolean

  /**
    * Is true if the parsing failed.
    */
  def isFailure: Boolean = !isSuccess

  /**
    * Returns the parsing result as an Either type. If the parsing was successful, it returns
    * the result as Right. Otherwise, it returns the failure as Left.
    */
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
