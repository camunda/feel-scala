package org.camunda.feel.spi

import org.camunda.feel.interpreter._

trait ValueMapper {
	
  def toVal(x: Any): Val

  def unpackVal(value: Val): Any

}
