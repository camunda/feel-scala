package org.camunda.feel.spi

import org.camunda.feel.interpreter.DefaultValueMapper
import org.camunda.feel.interpreter._

class CustomValueMapper extends DefaultValueMapper {

	override def unpackVal(value: Val): Any = value match {
		case ValNull => "foobar"
		case _ => super.unpackVal(value)
	}
	
}