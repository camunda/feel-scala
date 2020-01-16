package org.camunda.feel.impl.interpreter

import org.camunda.feel.impl.spi.CustomValueMapper

trait ValueMapper {

  def toVal(x: Any): Val

  def unpackVal(value: Val): Any

}

object ValueMapper {

  case class CompositeValueMapper(customMappers: List[CustomValueMapper])
      extends ValueMapper {

    val customMappersByPriority =
      (DefaultValueMapper.instance :: customMappers).distinct
        .sortBy(_.priority)(Ordering[Int].reverse)

    override def toVal(x: Any): Val = {
      for (customMapper <- customMappersByPriority) {
        customMapper.toVal(x, this.toVal) match {
          case Some(value) => return value
          case _           =>
        }
      }
      throw new IllegalArgumentException(
        s"no value mapper found for '$x' ('${x.getClass}')")
    }

    override def unpackVal(value: Val): Any = {
      for (customMapper <- customMappersByPriority) {
        customMapper.unpackVal(value, this.unpackVal) match {
          case Some(x) => return x
          case _       =>
        }
      }
      throw new IllegalArgumentException(s"no value mapper found for '$value'")
    }
  }

  val defaultValueMapper = CompositeValueMapper(
    List(DefaultValueMapper.instance)
  )

}
