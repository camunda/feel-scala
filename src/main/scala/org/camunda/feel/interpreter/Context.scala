package org.camunda.feel.interpreter

import org.camunda.feel._
import org.joda.time.LocalDate

/**
 * @author Philipp Ossler
 */
case class Context(variables: Map[String, Any]) {
	
	val builtinFunctions: Map[(String, Int), ValFunction] = BuiltinFunctions.builtinFunctions
		.map { case (name, f) => (name, f.params.size) -> f }
	  .toMap
	
  def input: Val = apply(Context.inputKey)

  def apply(key: String): Val = variables.get(key) match {
    case None => ValError(s"no variable found for key '$key'")
    case Some(x : Val) => x
    case Some(x) => ValueMapper.toVal(x)
  }
  
  def ++(vars: Map[String, Any]) = Context(variables ++ vars)

  def +(variable : (String, Any)) = Context(variables + variable)
    
  def function(name: String, args: Int): Val = variables.get(name) orElse builtinFunctions.get((name, args)) match {
	  case Some(f: Val) => f
  	case _ => ValError(s"no function found with name '$name' and $args arguments")
  }
  
}

object Context {

  val inputKey = "cellInput"
  
  def empty = Context(Map())

}
