package org.camunda.feel.impl.spi

import org.camunda.feel.impl.interpreter._

// DO NOT DELETE, used in ScriptEngineTest through src/test/resources/META-INF/services
class TestValueMapper extends CustomValueMapper {

  override def unpackVal(value: Val,
                         innerValueMapper: Val => Any): Option[Any] =
    value match {
      case ValNull => Some("foobar")
      case _       => None
    }

  override def toVal(x: Any, innerValueMapper: Any => Val): Option[Val] = None

}
