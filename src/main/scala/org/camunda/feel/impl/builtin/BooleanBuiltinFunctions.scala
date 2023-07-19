package org.camunda.feel.impl.builtin

import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree.{Val, ValBoolean, ValError, ValNull}

object BooleanBuiltinFunctions {

  def functions = Map(
    "not" -> List(notFunction),
    "is defined" -> List(isDefinedFunction),
    "get or else" -> List(getOrElse)
  )

  private def notFunction =
    builtinFunction(params = List("negand"), invoke = {
      case List(ValBoolean(negand)) => ValBoolean(!negand)
      case List(_: Val)             => ValNull
      case _                        => ValNull
    })

  private def isDefinedFunction = builtinFunction(
    params = List("value"),
    invoke = {
      case (value: ValError) :: Nil => ValBoolean(false)
      case _                        => ValBoolean(true)
    }
  )

  private def getOrElse = builtinFunction(
    params = List("value", "default"),
    invoke = {
      case List(ValNull, default) => default
      case List(value, _)         => value
    }
  )
}
