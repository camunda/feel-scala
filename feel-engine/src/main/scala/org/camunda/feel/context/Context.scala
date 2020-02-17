package org.camunda.feel.context

import org.camunda.feel.syntaxtree.ValFunction

/**
  * A Context provides access to the variables/fields and functions/methods in the scope represented by this Context.
  */
trait Context {

  def variableProvider: VariableProvider

  def functionProvider: FunctionProvider

}

object Context {

  object EmptyContext extends Context {

    override def variableProvider: VariableProvider =
      VariableProvider.EmptyVariableProvider

    override def functionProvider: FunctionProvider =
      FunctionProvider.EmptyFunctionProvider
  }

  case class StaticContext(
      variables: Map[String, Any],
      functions: Map[String, List[ValFunction]] = Map.empty
  ) extends Context {

    override def variableProvider: VariableProvider =
      VariableProvider.StaticVariableProvider(variables)

    override def functionProvider: FunctionProvider =
      FunctionProvider.StaticFunctionProvider(functions)
  }

  case class CacheContext(context: Context) extends Context {

    override def variableProvider: VariableProvider =
      VariableProvider.CacheVariableProvider(context.variableProvider)

    override def functionProvider: FunctionProvider =
      FunctionProvider.CacheFunctionProvider(context.functionProvider)
  }

}
