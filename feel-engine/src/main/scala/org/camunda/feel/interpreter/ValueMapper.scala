package org.camunda.feel.interpreter

trait ValueMapper {

  def toVal(x: Any): Val

  def unpackVal(value: Val): Any

}
