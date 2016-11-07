package org.camunda.feel.interpreter

import org.camunda.feel._
import org.joda.time.LocalDate

/**
 * @author Philipp Ossler
 */
case class Context(variables: Map[String, Any] = Map()) {

  def input: Val = apply(Context.inputKey)

  def apply(key: String): Val = variables.get(key) match {
    case None => ValError(s"no variable found for key '$key'")
    case Some(x : Val) => x
    case Some(x) => ValueMapper.toVal(x)
  }
  
  def ++(vars: Map[String, Any]) = Context(variables ++ vars)

  def +(variable : (String, Any)) = Context(variables + variable)
  
}

object Context {

  val inputKey = "cellInput"

}
