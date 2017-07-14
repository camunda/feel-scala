package org.camunda.feel.interpreter

import org.camunda.feel._
import org.camunda.feel.spi._
import org.camunda.feel.spi.DefaultFunctionProviders.EmptyFunctionProvider
import org.camunda.feel.spi.VariableContext.StaticVariableContext

/**
 * @author Philipp Ossler
 */
case class Context(variables: VariableContext, functionProvider: FunctionProvider = EmptyFunctionProvider, valueMapper: ValueMapper = new DefaultValueMapper) {

	def inputKey: String = variables(Context.inputVariableKey) match {
   	case Some(inputVariableName: String) => inputVariableName
    case _ => Context.defaultInputVariable
	}

  def input: Val = apply(inputKey)

  def apply(key: String): Val = variables(key) match {
    case None => ValError(s"no variable found for key '$key'")
		case Some(x : Val) => x
    case Some(x) => valueMapper.toVal(x)
  }

  def ++(vars: Map[String, Any]) = Context(variables ++ vars, functionProvider, valueMapper)

  def +(variable : (String, Any)) = Context(variables + variable, functionProvider, valueMapper)

  def function(name: String, args: Int): Val = variables(name) orElse functionProvider.getFunction(name, args) orElse BuiltinFunctions.getFunction(name, args) match {
	  case Some(f: Val) => f
  	case _ => ValError(s"no function found with name '$name' and $args arguments")
  }

}

object Context {

  val defaultInputVariable = "cellInput"

  val inputVariableKey = "inputVariableName"

  def empty = Context(StaticVariableContext(), EmptyFunctionProvider, new DefaultValueMapper)

}
