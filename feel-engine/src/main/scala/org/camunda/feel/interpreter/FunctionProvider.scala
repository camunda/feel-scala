package org.camunda.feel.interpreter

trait FunctionProvider {

  def getFunction(name: String): List[ValFunction]

}

object FunctionProvider {

  object EmptyFunctionProvider extends FunctionProvider {
    override def getFunction(name: String): List[ValFunction] = List.empty
  }

  class CompositeFunctionProvider(providers: List[FunctionProvider])
      extends FunctionProvider {
    override def getFunction(name: String): List[ValFunction] =
      (List[ValFunction]() /: providers)((functions, provider) =>
        functions ++ provider.getFunction(name))
  }

}
