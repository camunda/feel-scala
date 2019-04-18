package org.camunda.feel.spi

import org.camunda.feel.interpreter._

class AnotherFunctionProvider extends CustomFunctionProvider {

  def getFunction(name: String): Option[ValFunction] = functions.get(name)

  val functions: Map[String, ValFunction] = Map(
    "bar" ->
      ValFunction(
        params = List("x"),
        invoke = { case List(ValNumber(x)) => ValNumber(x + 2) }
      )
  )

}
