package org.camunda.feel.impl.builtin

import org.camunda.feel.context.Context
import org.camunda.feel.context.Context.StaticContext
import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree._

object ContextBuiltinFunctions {

  def functions = Map(
    "get entries" -> List(getEntriesFunction("context"),
                          getEntriesFunction("m")),
    "get value" -> List(getValueFunction),
    "put" -> List(putFunction),
    "put all" -> List(putAllFunction),
    "context" -> List(contextFunction)
  )

  private def getEntriesFunction(paramName: String) = builtinFunction(
    params = List(paramName),
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

  private def putFunction = builtinFunction(
    params = List("context", "key", "value"),
    invoke = {
      case List(ValContext(_), ValString(_), ValError(_)) => ValNull
      case List(ValContext(c), ValString(key), value) =>
        ValContext(
          StaticContext(
            variables = c.variableProvider.getVariables + (key -> value)))
    }
  )

  private def putAllFunction = builtinFunction(
    params = List("contexts"),
    invoke = {
      case List(ValList(contexts)) if isListOfContexts(contexts) =>
        ValContext(
          StaticContext(
            variables = contexts
              .flatMap(_ match {
                case ValContext(c) => c.variableProvider.getVariables
                case _             => Map.empty
              })
              .toMap
          )
        )
      case _ => ValNull
    },
    hasVarArgs = true
  )

  private def isListOfContexts(list: List[Val]): Boolean =
    list.forall(_.isInstanceOf[ValContext])

  private def contextFunction = builtinFunction(
    params = List("entries"),
    invoke = {
      case List(ValList(entries)) if isListOfKeyValuePairs(entries) =>
        ValContext(StaticContext(variables = entries.flatMap {
          case ValContext(context) =>
            val getValue = context.variableProvider.getVariable(_)
            getValue("key")
              .map { case ValString(key) => key }
              .flatMap(key => getValue("value").map(value => key -> value))
        }.toMap))
      case _ => ValNull
    }
  )

  private def isListOfKeyValuePairs(list: List[Val]): Boolean =
    list.forall {
      case ValContext(context) =>
        val keys = context.variableProvider.keys.toList
        keys.contains("value") && context.variableProvider
          .getVariable("key")
          .exists(_.isInstanceOf[ValString])
      case _ => false
    }

}
