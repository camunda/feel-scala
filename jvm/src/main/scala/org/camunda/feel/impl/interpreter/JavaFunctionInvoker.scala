package org.camunda.feel.impl.interpreter

import org.camunda.feel.syntaxtree.{Val, ValError}
import org.camunda.feel.valuemapper.ValueMapper

object JavaFunctionInvoker {
  def invokeJavaFunction(
      className: String,
      methodName: String,
      arguments: List[String],
      paramValues: List[Val],
      valueMapper: ValueMapper
  ) =
    try {

      val clazz = JavaClassMapper.loadClass(className)

      val argTypes = arguments map JavaClassMapper.loadClass

      val method = clazz.getDeclaredMethod(methodName, argTypes: _*)

      val argJavaObjects = paramValues zip argTypes map {
        case (obj, clazz) =>
          JavaClassMapper.asJavaObject(obj, clazz)
      }

      val result = method.invoke(null, argJavaObjects: _*)

      valueMapper.toVal(result)

    } catch {
      case e: ClassNotFoundException =>
        ValError(s"fail to load class '$className'")
      case e: NoSuchMethodException =>
        ValError(
          s"fail to get method with name '$methodName' and arguments '$arguments' from class '$className'")
      case _: Throwable =>
        ValError(
          s"fail to invoke method with name '$methodName' and arguments '$arguments' from class '$className'")
    }
}
