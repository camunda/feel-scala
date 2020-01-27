package org.camunda.feel.impl.spi

import org.camunda.feel.interpreter.impl.Val

abstract class JavaCustomValueMapper extends CustomValueMapper {

  /**
    * Transform the given object into a FEEL type - one of [[Val]] (e.g. [[Double]] to [[ValNumber]]).
    * If it can't be transformed then it returns [[None]] instead and the object is passed to the next mapper in the chain.
    *
    * @param x                the object to transform
    * @param innerValueMapper the mapper function to transform inner values of a collection type
    * @return the FEEL representation of the object
    */
  def toValue(x: Any, innerValueMapper: java.util.function.Function[Any, Val])
    : java.util.Optional[Val]

  override def toVal(x: Any, innerValueMapper: Any => Val): Option[Val] = {
    toValue(x, innerValue => innerValueMapper.apply(innerValue)) match {
      case v if (v.isPresent) => Some(v.get)
      case _                  => None
    }
  }

  /**
    * Transform the given FEEL type into a base Scala/Java object (e.g. [[ValNumber]] to [[Double]]).
    * If it can't be transformed then it returns [[None]] instead and the object is passed to the next mapper in the chain.
    *
    * @param value            the FEEL type to transform
    * @param innerValueMapper the mapper function to transform inner values of a collection type
    * @return the base object of the FEEL type
    */
  def unpackValue(value: Val,
                  innerValueMapper: java.util.function.Function[Val, Any])
    : java.util.Optional[Any]

  override def unpackVal(value: Val,
                         innerValueMapper: Val => Any): Option[Any] = {
    unpackValue(value, innerValue => innerValueMapper.apply(innerValue)) match {
      case x if (x.isPresent) => Some(x.get)
      case _                  => None
    }
  }

  /**
    * The priority of this mapper in the chain. The mappers are invoked in order of their priority,
    * starting with the highest priority.
    */
  override val priority: Int = 1
}
