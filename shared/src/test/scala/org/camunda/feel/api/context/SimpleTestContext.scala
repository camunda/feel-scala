package org.camunda.feel.api.context

import org.camunda.feel.context.VariableProvider

case class SimpleTestContext(context: Map[String, _]) extends VariableProvider {

  override def getVariable(name: String): Option[Any] = {
    if (context.contains(name)) {
      Some(context.get(name))

    } else {
      None
    }
  }

  override def keys: Iterable[String] = context.keys
}
