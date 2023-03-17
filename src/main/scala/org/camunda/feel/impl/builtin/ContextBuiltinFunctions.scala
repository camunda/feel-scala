package org.camunda.feel.impl.builtin

import org.camunda.feel.context.Context
import org.camunda.feel.context.Context.{EmptyContext, StaticContext}
import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree.{Val, ValContext, ValError, ValList, ValNull, ValString}
import org.camunda.feel.valuemapper.ValueMapper

import scala.annotation.tailrec

class ContextBuiltinFunctions(valueMapper: ValueMapper) {

  def functions = Map(
    "get entries" -> List(getEntriesFunction("context"),
      getEntriesFunction("m")),
    "get value" -> List(getValueFunction(List("m", "key")),
      getValueFunction(List("context", "key"))),
    "context put" -> List(contextPutFunction, contextPutFunction2),
    "put" -> List(contextPutFunction), // deprecated function name
    "context merge" -> List(contextMergeFunction),
    "put all" -> List(contextMergeFunction), // deprecated function name
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

  private def getValueFunction(parameters: List[String]) = builtinFunction(
    params = parameters,
    invoke = {
      case List(ValContext(c), ValString(key)) =>
        c.variableProvider
          .getVariable(key)
          .getOrElse(ValNull)
    }
  )

  private def contextPutFunction = builtinFunction(
    params = List("context", "key", "value"),
    invoke = {
      case List(ValContext(_), ValString(_), ValError(_)) => ValNull
      case List(context: ValContext, ValString(key), value) => contextPut(
        contextValue = context, key = key, value = value
      )
      // delegate to other context put function with keys
      case List(ValContext(_), ValList(_), ValError(_)) => ValNull
      case List(ValContext(_), ValList(keys), _) if keys.isEmpty => ValNull
      case List(context: ValContext, keys: ValList, value) if isListOfStrings(keys.items) =>
        contextPutFunction2.invoke(List(context, keys, value))
    }
  )

  private def contextPut(contextValue: ValContext, key: String, value: Val): ValContext = {
    ValContext(
      StaticContext(
        variables = contextValue.context.variableProvider.getVariables + (key -> value)))
  }

  private def contextPutFunction2 = builtinFunction(
    params = List("context", "keys", "value"),
    invoke = {
      case List(ValContext(_), ValList(_), ValError(_)) => ValNull
      case List(ValContext(_), ValList(keys), _) if keys.isEmpty => ValNull
      case List(context: ValContext, ValList(keys), value) if isListOfStrings(keys) =>
        val listOfKeys = keys.asInstanceOf[List[ValString]].map(_.value)

        contextPutWithKeys(context, listOfKeys, value)
    }
  )

  @tailrec
  private def contextPutWithKeys(
                                  contextValue: ValContext,
                                  keys: List[String], value: Val,
                                  parentContextUpdater: ValContext => ValContext = identity): ValContext = {
    keys match {
      case Nil => contextValue
      case key :: Nil =>
        val modifiedContext = contextPut(contextValue = contextValue, key = key, value = value)
        parentContextUpdater(modifiedContext)
      case key :: tail =>
        val contextOfKey = contextValue.context.variableProvider.getVariable(key).map {
          case contextOfKey: ValContext => contextOfKey
          case _ => ValContext(EmptyContext)
        }.getOrElse(ValContext(EmptyContext))

        // recursive call for the next key with its nested context
        contextPutWithKeys(
          contextValue = contextOfKey,
          keys = tail,
          value = value,
          parentContextUpdater =
            // pass a lambda to update this context with the modified nested context
            nestedContext => contextPut(
              contextValue = contextValue,
              key = key,
              value = nestedContext
            )
        )
    }
  }

  private def isListOfStrings(list: List[Val]): Boolean =
    list.forall(_.isInstanceOf[ValString])

  private def contextMergeFunction = builtinFunction(
    params = List("contexts"),
    invoke = {
      case List(ValList(contexts)) if isListOfContexts(contexts) =>
        ValContext(
          StaticContext(
            variables = contexts
              .flatMap(_ match {
                case ValContext(c) => c.variableProvider.getVariables
                case _ => Map.empty
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
