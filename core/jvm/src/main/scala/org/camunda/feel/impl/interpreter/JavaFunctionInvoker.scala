package org.camunda.feel.impl.interpreter

import org.camunda.feel.api.EvaluationFailureType
import org.camunda.feel.syntaxtree.Val
import org.camunda.feel.valuemapper.ValueMapper

object JavaFunctionInvoker {
  def invokeJavaFunction(
                          className: String,
                          methodName: String,
                          arguments: List[String],
                          paramValues: List[Val],
                          valueMapper: ValueMapper
                        ): Val = {
    val clazz = JavaClassMapper.loadClass(className)

    val argTypes = arguments map JavaClassMapper.loadClass

    val method = clazz.getDeclaredMethod(methodName, argTypes: _*)

    val argJavaObjects = paramValues zip argTypes map { case (obj, clazz) =>
      JavaClassMapper.asJavaObject(obj, clazz)
    }

    val result = method.invoke(null, argJavaObjects: _*)

    valueMapper.toVal(result)
  }

}
