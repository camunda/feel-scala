package org.camunda.feel.impl.interpreter

import org.camunda.feel.syntaxtree.Val
import org.camunda.feel.valuemapper.ValueMapper

object JavaFunctionInvoker {
  def invokeJavaFunction(
      className: String,
      methodName: String,
      arguments: List[String],
      paramValues: List[Val],
      valueMapper: ValueMapper
  ) = throw new NotImplementedError("Cannot invoke Java functions in JS")
}
