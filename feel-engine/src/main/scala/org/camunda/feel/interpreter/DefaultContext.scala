package org.camunda.feel.interpreter

import org.camunda.feel.spi._

import scala.collection.mutable

abstract class ContextBase extends Context {

  override def valueMapper: ValueMapper = DefaultValueMapper.instance

  def variables: Map[String, Any] = Map.empty

  private val cachedVariables: mutable.Map[String, Any] = mutable.Map.empty

  def variableProvider: VariableProvider = VariableProvider.EmptyVariableProvider

  def functions: Map[(String, Int), ValFunction] = Map.empty

  private val cachedFunctions: mutable.Map[(String, Int), ValFunction] = mutable.Map.empty

  def functionProvider: FunctionProvider = FunctionProvider.EmptyFunctionProvider

  override def variable(name: String): Val = variables.get(name) orElse cachedVariables.get(name) match {
    case Some(x) => valueMapper.toVal(x)
    case None => variableProvider.getVariable(name) match {
      case Some(x) => val v = valueMapper.toVal(x); cachedVariables.put(name, v); v
      case None => ValError(s"no variable found for name '$name'")
    }
  }

  override def function(name: String, argumentCount: Int): Val = functions.get((name, argumentCount)) orElse cachedFunctions.get((name, argumentCount)) match {
    case Some(f: ValFunction) => f
    case _ => functionProvider.getFunction(name, argumentCount) match {
      case Some(f: ValFunction) => cachedFunctions.put((name, argumentCount), f); f
      case _ => ValError(s"no function found with name '$name' and $argumentCount arguments")
    }
  }

}

case class CompositeContext(contexts: Seq[Context], override val valueMapper: ValueMapper) extends Context {

  override def variable(name: String): Val = {
    for (context <- contexts) {
      context.variable(name) match {
        case _: ValError =>
        case x: Val => return x
      }
    }
    ValError(s"no variable found for name '$name'")
  }

  override def function(name: String, argumentCount: Int): Val = {
    for (context <- contexts) {
      context.function(name, argumentCount) match {
        case _: ValError =>
        case f: ValFunction => return f
        case _: Val =>
      }
    }
    ValError(s"no function found with name '$name' and $argumentCount arguments")
  }

}

case class DefaultContext(override val variables: Map[String, Any] = Map.empty, override val functions: Map[(String, Int), ValFunction] = Map.empty, override val variableProvider: VariableProvider = VariableProvider.EmptyVariableProvider, override val functionProvider: FunctionProvider = FunctionProvider.EmptyFunctionProvider, override val valueMapper: ValueMapper = DefaultValueMapper.instance) extends ContextBase {

}

case class RootContext(override val variables: Map[String, Any] = Map.empty, override val functions: Map[(String, Int), ValFunction] = Map.empty, override val variableProvider: VariableProvider = VariableProvider.EmptyVariableProvider, override val functionProvider: FunctionProvider = FunctionProvider.EmptyFunctionProvider, override val valueMapper: ValueMapper = DefaultValueMapper.instance) extends ContextBase {

  override def function(name: String, argumentCount: Int): Val = super.function(name, argumentCount) match {
    case f: ValFunction => f
    case e: ValError => BuiltinFunctions.getFunction(name, argumentCount) match {
      case Some(f: ValFunction) => f
      case _ => ValError(s"no function found with name '$name' and $argumentCount arguments")
    }
    case _ => ValError(s"no function found with name '$name' and $argumentCount arguments")
  }

}

object RootContext {

  val defaultInputVariable: String = "cellInput"

  val inputVariableKey: String = "inputVariableName"

  def empty: RootContext = RootContext()

}
