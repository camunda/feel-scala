package org.camunda.feel.impl.spi

import org.camunda.feel.interpreter.impl._
import org.camunda.feel.interpreter.impl.{Val, ValNumber}

/**
  * Transform objects into FEEL types and the other way around.
  *
  * Multiple mappers are chained and invoked in order of their [[CustomValueMapper.priority]]. If one
  * mapper can't transform the object then the next handler of the chain is invoked.
  */
trait CustomValueMapper {

  /**
    * Transform the given object into a FEEL type - one of [[Val]] (e.g. [[Double]] to [[ValNumber]]).
    * If it can't be transformed then it returns [[None]] instead and the object is passed to the next mapper in the chain.
    *
    * @param x                the object to transform
    * @param innerValueMapper the mapper function to transform inner values of a collection type
    * @return the FEEL representation of the object
    */
  def toVal(x: Any, innerValueMapper: Any => Val): Option[Val]

  /**
    * Transform the given FEEL type into a base Scala/Java object (e.g. [[ValNumber]] to [[Double]]).
    * If it can't be transformed then it returns [[None]] instead and the object is passed to the next mapper in the chain.
    *
    * @param value            the FEEL type to transform
    * @param innerValueMapper the mapper function to transform inner values of a collection type
    * @return the base object of the FEEL type
    */
  def unpackVal(value: Val, innerValueMapper: Val => Any): Option[Any]

  /**
    * The priority of this mapper in the chain. The mappers are invoked in order of their priority,
    * starting with the highest priority.
    */
  val priority: Int = 1

}
