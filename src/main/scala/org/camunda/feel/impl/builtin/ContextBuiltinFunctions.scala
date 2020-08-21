package org.camunda.feel.impl.builtin

import org.camunda.feel.context.Context
import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree.{ValContext, ValNull, ValString}

object ContextBuiltinFunctions {

  def functions = Map(
    "get entries" -> List(getEntriesFunction),
    "get value" -> List(getValueFunction)
  )

  private def getEntriesFunction = builtinFunction(
    params = List("context"),
    invoke = {
      case List(ValContext(c: Context)) =>
        c.variableProvider.getVariables.map {
          case (key, value) =>
            Map("key" -> ValString(key), "value" -> value)
        }.toList
    }
  )

  private def getValueFunction = builtinFunction(
    params = List("context", "key"),
    invoke = {
      case List(ValContext(c), ValString(key)) =>
        c.variableProvider
          .getVariable(key)
          .getOrElse(ValNull)
    }
  )
}
