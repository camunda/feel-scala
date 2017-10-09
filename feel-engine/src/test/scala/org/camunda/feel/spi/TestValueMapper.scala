package org.camunda.feel.spi

import org.camunda.feel.interpreter.DefaultValueMapper
import org.camunda.feel.interpreter._

// DO NOT DELETE, used in ScriptEngineTest through src/test/resources/META-INF/services
class TestValueMapper extends CustomValueMapper {

  override def unpackVal(value: Val): Any = value match {
    case ValNull => "foobar"
    case _ => super.unpackVal(value)
  }

}
