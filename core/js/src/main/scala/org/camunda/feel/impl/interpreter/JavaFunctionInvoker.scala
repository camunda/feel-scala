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
  ): Val = throw new UnsupportedOperationException("Cannot invoke java functions from js")

  object JavaFunctionInvoker {
    def invokeJavaFunction(
                            className: String,
                            methodName: String,
                            arguments: List[String],
                            paramValues: List[Val],
                            valueMapper: ValueMapper
                          ): Val = ???

  }
