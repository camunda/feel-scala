package org.camunda.feel.impl.spi

import org.camunda.feel.context.CustomFunctionProvider
import org.camunda.feel.syntaxtree._

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
