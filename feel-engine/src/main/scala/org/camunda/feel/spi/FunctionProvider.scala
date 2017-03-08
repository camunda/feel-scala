package org.camunda.feel.spi

import org.camunda.feel.interpreter.ValFunction

trait FunctionProvider {
  
  def getFunction(functionName: String, argumentCount: Int): Option[ValFunction]
  
}

object DefaultFunctionProviders {
  
  object EmptyFunctionProvider extends FunctionProvider {
    def getFunction(functionName: String, argumentCount: Int) = None
  }
  
  class CompositeFunctionProvider(providers: List[FunctionProvider]) extends FunctionProvider {
    
    def getFunction(functionName: String, argumentCount: Int): Option[ValFunction] = {
      
      providers map ( p => p.getFunction(functionName, argumentCount)) flatten match {
        case Nil => None
        case f :: Nil => Some(f)
        case f :: fs => {
          System.err.println(s"Found multiple functions for name '$functionName' and argument count $argumentCount. Using the first one.")
          
          Some(f)
        }
      }
    }    
  }
  
}