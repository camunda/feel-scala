package org.camunda.feel.interpreter

import org.camunda.feel._
import org.camunda.feel.spi.FunctionProvider
import org.camunda.feel.spi.DefaultFunctionProviders.EmptyFunctionProvider

/**
 * @author Philipp Ossler
 */
case class Context(variables: Map[String, Any], functionProvider: FunctionProvider = EmptyFunctionProvider) {
	
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
  
  def ++(vars: Map[String, Any]) = Context(variables ++ vars, functionProvider)

  def +(variable : (String, Any)) = Context(variables + variable, functionProvider)
    
  def function(name: String, args: Int): Val = variables.get(name) orElse functionProvider.getFunction(name, args) orElse BuiltinFunctions.getFunction(name, args) match {
	  case Some(f: Val) => f
  	case _ => ValError(s"no function found with name '$name' and $args arguments")
  }
  
}

object Context {

  val defaultInputVariable = "cellInput"
  
  val inputVariableKey = "inputVariableName"
  
  def empty = Context(Map(), EmptyFunctionProvider)
  
}
