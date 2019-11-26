package org.camunda.feel.spi

import org.camunda.feel.interpreter._

import scala.collection.JavaConverters._

/**
  * Transform FEEL types into common Java objects. This includes numbers, lists and contexts.
  */
class JavaValueMapper extends CustomValueMapper {

  override def unpackVal(value: Val): Any = value match {

    case ValNumber(number) => {
      if (number.isWhole) {
        number.longValue: java.lang.Long
      } else {
        number.doubleValue: java.lang.Double
      }
    }

    case ValList(list) => (list map unpackVal).asJava: java.util.List[Any]

    case ValContext(context: Context) =>
      context.variableProvider.getVariables
        .map { case (key, value) => key -> unpackVal(toVal(value)) }
        .toMap
        .asJava: java.util.Map[String, Any]

    // else
    case _ => super.unpackVal(value)
  }

}
