package org.camunda.feel.spi

import org.camunda.feel.interpreter._

class AnotherFunctionProvider extends FunctionProvider {
  
   val functions: Map[(String, Int), ValFunction] = Map(
        ("bar", 1) -> ValFunction(List("x"), { case List(ValNumber(x)) => ValNumber(x + 2) } )
      )
      
    def getFunction(functionName: String, argumentCount: Int): Option[ValFunction] = functions.get((functionName, argumentCount))
  
}