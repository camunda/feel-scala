package org.camunda.feel.spi

import org.camunda.feel.interpreter._

// DO NOT DELETE, used in ScriptEngineTest through src/test/resources/META-INF/services
class TestFunctionProvider extends CustomFunctionProvider {

  override def getFunction(name: String): Option[ValFunction] =
    functions.get(name)

  override def functionNames: Iterable[String] = functions.keys

  val functions: Map[String, ValFunction] = Map(
    "foo" ->
      ValFunction(
        params = List("x"),
        invoke = { case List(ValNumber(x)) => ValNumber(x + 1) }
      )
  )

}
