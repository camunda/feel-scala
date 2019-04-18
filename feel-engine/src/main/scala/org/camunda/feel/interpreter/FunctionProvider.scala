package org.camunda.feel.interpreter

trait FunctionProvider {

  def getFunctions(name: String): List[ValFunction]

}

object FunctionProvider {

  object EmptyFunctionProvider extends FunctionProvider {
    override def getFunctions(name: String): List[ValFunction] = List.empty
  }

  case class CompositeFunctionProvider(providers: List[FunctionProvider])
    extends FunctionProvider {
    override def getFunctions(name: String): List[ValFunction] =
      (List[ValFunction]() /: providers)((functions, provider) =>
        functions ++ provider.getFunctions(name))
  }

}
