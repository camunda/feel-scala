package org.camunda.feel.interpreter

trait FunctionProvider {
  
  def getFunction(functionName: String, argumentCount: Int): Option[ValFunction]
  
}

object FunctionProvider {
  
  object EmptyFunctionProvider extends FunctionProvider {
    def getFunction(functionName: String, argumentCount: Int) = None
  }
  
}