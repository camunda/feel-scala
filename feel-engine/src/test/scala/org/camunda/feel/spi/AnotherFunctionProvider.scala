package org.camunda.feel.spi

import org.camunda.feel.interpreter._

class AnotherFunctionProvider extends CustomFunctionProvider {

  override def getFunction(name: String): Option[ValFunction] =
    functions.get(name)

  override def functionNames: Iterable[String] = functions.keys

  val functions: Map[String, ValFunction] = Map(
    "bar" ->
      ValFunction(
        params = List("x"),
        invoke = {
          case List(ValNumber(x)) => ValNumber(x + 2)
        }
      )
  )

}
