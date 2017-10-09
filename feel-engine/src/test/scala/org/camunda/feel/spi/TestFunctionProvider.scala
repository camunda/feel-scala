package org.camunda.feel.spi

import org.camunda.feel.interpreter._

// DO NOT DELETE, used in ScriptEngineTest through src/test/resources/META-INF/services
class TestFunctionProvider extends CustomFunctionProvider {

  val functions: Map[(String, Int), ValFunction] = Map(
    ("foo", 1) -> ValFunction(List("x"), { case List(ValNumber(x)) => ValNumber(x + 1) } )
  )

  override def getFunction(functionName: String, argumentCount: Int): Option[ValFunction] = functions.get((functionName, argumentCount))

}
