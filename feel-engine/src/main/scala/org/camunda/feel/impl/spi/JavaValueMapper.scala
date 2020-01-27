package org.camunda.feel.impl.spi

import org.camunda.feel.interpreter.impl._
import org.camunda.feel.interpreter.impl.{
  Context,
  Val,
  ValContext,
  ValList,
  ValNumber
}

import scala.collection.JavaConverters._

/**
  * Transform FEEL types into common Java objects. This includes numbers, lists and contexts.
  */
class JavaValueMapper extends CustomValueMapper {

  override def unpackVal(value: Val,
                         innerValueMapper: Val => Any): Option[Any] =
    value match {

      case ValNumber(number) =>
        Some(
          if (number.isWhole) {
            number.longValue: java.lang.Long
          } else {
            number.doubleValue: java.lang.Double
          }
        )

      case ValList(list) =>
        Some(
          (list map innerValueMapper).asJava: java.util.List[Any]
        )

      case ValContext(context: Context) =>
        Some(
          context.variableProvider.getVariables
            .map {
              case (key, value) =>
                value match {
                  case packed: Val => key -> innerValueMapper(packed)
                  case unpacked    => key -> unpacked
                }
            }
            .toMap
            .asJava: java.util.Map[String, Any]
        )

      case _ => None
    }

  override def toVal(x: Any, innerValueMapper: Any => Val): Option[Val] = None

  override val priority: Int = 10
}
