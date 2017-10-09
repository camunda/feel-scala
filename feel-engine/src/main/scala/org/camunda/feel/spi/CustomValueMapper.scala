package org.camunda.feel.spi

import org.camunda.feel.interpreter._

/**
  * Override this class if you want to implement a custom ValueMapper.
  * Call the corresponding super method to handle the default/error case
  */
abstract class CustomValueMapper extends DefaultValueMapper {

  override def toVal(x: Any): Val = super.toVal(x)

  override def unpackVal(value: Val): Any = super.unpackVal(value)

}
