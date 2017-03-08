package org.camunda.feel.example.spi

import org.camunda.feel.interpreter._
import org.camunda.feel.spi.FunctionProvider

class CustomScalaFunctionProvider extends FunctionProvider {
  
   val functions: Map[(String, Int), ValFunction] = Map(
        ("foo", 1) -> ValFunction(List("x"), { case List(ValNumber(x)) => ValNumber(x + 1) } )
      )
      
    def getFunction(functionName: String, argumentCount: Int): Option[ValFunction] = functions.get((functionName, argumentCount))
  
}