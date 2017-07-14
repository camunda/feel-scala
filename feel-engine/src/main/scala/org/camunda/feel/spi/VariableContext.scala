package org.camunda.feel.spi

trait VariableContext {

  def apply(key: String): Option[Any]

  def +(variable : (String, Any)): VariableContext

  def ++(vars: Map[String, Any]): VariableContext

}

object VariableContext {

  type VariableProvider = (String) => Option[Any]

  case class StaticVariableContext(variables: Map[String, Any] = Map()) extends VariableContext {

    def apply(key: String) = variables.get(key)

    def +(variable: (String, Any)) = StaticVariableContext(variables + variable)

    def ++(vars: Map[String, Any]) = StaticVariableContext(variables ++ vars)

  }

  case class DynamicVariableContext(dynamicContext: VariableProvider, staticContext: VariableContext = StaticVariableContext()) extends VariableContext {

    def apply(key: String) = staticContext(key) match {
      case None       => dynamicContext(key)
      case variable   => variable
    }

    def +(variable : (String, Any)) = DynamicVariableContext(dynamicContext, staticContext + variable)

    def ++(vars: Map[String, Any]) = DynamicVariableContext(dynamicContext, staticContext ++ vars)

  }

}
