package org.camunda.feel.interpreter

import org.camunda.feel._

/**
 * @author Philipp Ossler
 */
case class Context(variables: Map[String, Any]) {
	
	def inputKey: String = variables.get(Context.inputVariableKey) match {
   	case Some(inputVariableName: String) => inputVariableName
    case _ => Context.defaultInputVariable
	}  
	  
  def input: Val = apply(inputKey)

  def apply(key: String): Val = variables.get(key) match {
    case None => ValError(s"no variable found for key '$key'")
    case Some(x : Val) => x
    case Some(x) => ValueMapper.toVal(x)
  }
  
  def ++(vars: Map[String, Any]) = Context(variables ++ vars)

  def +(variable : (String, Any)) = Context(variables + variable)
    
  def function(name: String, args: Int): Val = variables.get(name) orElse BuiltinFunctions.getFunction(name, args) match {
	  case Some(f: Val) => f
  	case _ => ValError(s"no function found with name '$name' and $args arguments")
  }
  
}

object Context {

  val defaultInputVariable = "cellInput"
  
  val inputVariableKey = "inputVariableName"
  
  def empty = Context(Map())

}
