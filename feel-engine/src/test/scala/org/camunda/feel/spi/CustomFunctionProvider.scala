package org.camunda.feel.spi

import org.camunda.feel.interpreter._

class CustomFunctionProvider extends FunctionProvider {
  
   val functions: Map[(String, Int), ValFunction] = Map(
        ("foo", 1) -> ValFunction(List("x"), { case List(ValNumber(x)) => ValNumber(x + 1) } )
      )
      
    def getFunction(functionName: String, argumentCount: Int): Option[ValFunction] = functions.get((functionName, argumentCount))
  
}