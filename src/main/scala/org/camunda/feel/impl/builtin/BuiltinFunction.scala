package org.camunda.feel.impl.builtin

import org.camunda.feel.logger
import org.camunda.feel.syntaxtree.{Val, ValError, ValFunction, ValNull}

object BuiltinFunction {

  def builtinFunction(params: List[String],
                      invoke: PartialFunction[List[Val], Any],
                      hasVarArgs: Boolean = false): ValFunction = {
    ValFunction(
      params = params,
      invoke = invoke.orElse(error),
      hasVarArgs = hasVarArgs
    )
  }

  private def error: PartialFunction[List[Val], Any] = {
    case vars if (vars.exists(_.isInstanceOf[ValError])) =>
      vars.filter(_.isInstanceOf[ValError]).head.asInstanceOf[ValError]
    case e => ValError(s"Illegal arguments: $e")
  }

}
