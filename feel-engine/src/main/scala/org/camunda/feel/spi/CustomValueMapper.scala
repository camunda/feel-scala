package org.camunda.feel.spi

import org.camunda.feel.interpreter._

/**
  * Transform objects into FEEL types and the other way around.
  * Override [[CustomValueMapper.toVal()]] and/or [[CustomValueMapper.unpackVal()]] to change the default behavior.
  */
trait CustomValueMapper extends DefaultValueMapper {

  /**
    * Transform the given object into a FEEL type - one of [[Val]] (e.g. [[Double]] to [[ValNumber]]).
    * If it can't be transformed then delegate to the fallback via {{{super.toVal()}}}.
    * If something goes wrong then return a [[ValError]] to indicate the failure.
    *
    * @param x the object to transform
    * @return the FEEL representation of the object
    */
  override def toVal(x: Any): Val = super.toVal(x)

  /**
    * Transform the given FEEL type into a base Scala/Java object (e.g. [[ValNumber]] to [[Double]]).
    * If it can't be transformed then delegate to the fallback via {{{super.unpackVal()}}}.
    *
    * @param value the FEEL type to transform
    * @return the base object of the FEEL type
    */
  override def unpackVal(value: Val): Any = super.unpackVal(value)

}
