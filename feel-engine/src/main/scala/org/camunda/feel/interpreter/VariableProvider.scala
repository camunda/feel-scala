package org.camunda.feel.interpreter

trait VariableProvider {

  def getVariable(name: String): Option[Val]

}

object VariableProvider {

  object EmptyVariableProvider extends VariableProvider {
    override def getVariable(name: String): Option[Val] = None
  }

  class CompositeVariableProvider(providers: List[VariableProvider])
      extends VariableProvider {
    override def getVariable(name: String): Option[Val] = {
      for (provider <- providers) {
        provider.getVariable(name) match {
          case Some(v) => return Some(v)
          case _       =>
        }
      }
      None
    }
  }

}
