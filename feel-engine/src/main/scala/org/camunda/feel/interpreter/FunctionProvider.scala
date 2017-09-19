package org.camunda.feel.interpreter

trait FunctionProvider {

  def getFunction(name: String, argumentCount: Int): Option[ValFunction]

}

object FunctionProvider {

  object EmptyFunctionProvider extends FunctionProvider {
    override def getFunction(name: String, argumentCount: Int): Option[ValFunction] = None
  }

  class CompositeFunctionProvider(providers: List[FunctionProvider]) extends FunctionProvider {
    override def getFunction(name: String, argumentCount: Int): Option[ValFunction] = {
      for (provider <- providers) {
        provider.getFunction(name, argumentCount) match {
          case Some(f) => return Some(f)
          case _ =>
        }
      }
      None
    }
  }

}
