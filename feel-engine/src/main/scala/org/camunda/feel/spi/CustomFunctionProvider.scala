package org.camunda.feel.spi

import org.camunda.feel.interpreter._

trait CustomFunctionProvider extends FunctionProvider {

  override def getFunction(name: String): List[ValFunction]

}
