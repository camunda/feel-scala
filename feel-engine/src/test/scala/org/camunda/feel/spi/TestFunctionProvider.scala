package org.camunda.feel.spi

import org.camunda.feel.interpreter._

// DO NOT DELETE, used in ScriptEngineTest through src/test/resources/META-INF/services
class TestFunctionProvider extends CustomFunctionProvider {

  def getFunction(name: String): List[ValFunction] = functions.getOrElse(name, List.empty)

  val functions: Map[String, List[ValFunction]] = Map(
    "foo" -> List(
      ValFunction(
        params = List("x"),
        invoke = { case List(ValNumber(x)) => ValNumber(x + 1) }
      )
    )
  )

}
