package org.camunda.feel

/**
  * @author Philipp Ossler
  */
sealed trait EvalResult

case class EvalValue(value: Any) extends EvalResult

case class ParseFailure(error: String) extends EvalResult

case class EvalFailure(error: String) extends EvalResult
