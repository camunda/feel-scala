package org.camunda.feel.spi

import org.camunda.feel.interpreter._

class AnotherFunctionProvider extends CustomFunctionProvider {

  def getFunction(name: String): List[ValFunction] = functions.getOrElse(name, List.empty)

   val functions: Map[String, List[ValFunction]] = Map(
      "bar" -> List(
        ValFunction(
          params = List("x"),
          invoke = { case List(ValNumber(x)) => ValNumber(x + 2) }
        )
      )
    )

}
