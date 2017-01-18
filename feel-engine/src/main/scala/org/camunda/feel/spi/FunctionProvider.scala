package org.camunda.feel.spi

import org.camunda.feel.interpreter.ValFunction

trait FunctionProvider {
  
  def getFunction(functionName: String, argumentCount: Int): Option[ValFunction]
  
}

object FunctionProvider {
  
  object EmptyFunctionProvider extends FunctionProvider {
    def getFunction(functionName: String, argumentCount: Int) = None
  }
  
}