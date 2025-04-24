package org.camunda.feel.impl.interpreter

import org.camunda.feel.context.FunctionProvider.{StaticFunctionProvider}
import org.camunda.feel.context.VariableProvider.{StaticVariableProvider}
import org.camunda.feel.context.{Context, FunctionProvider, VariableProvider}
import org.camunda.feel.syntaxtree.ValFunction

import scala.scalajs.js
import scala.scalajs.js.Object.keys

case class ObjectContext(obj: Any) extends Context{
  private val variables = obj match{
    case product: Product =>
      product.productElementNames.zip(product.productIterator).toMap

    case jsObj: js.Object=>
      println(s"jsObj.keys: ${keys(jsObj)}")
      keys(jsObj).map(key => (key, jsObj.asInstanceOf[js.Dynamic].selectDynamic(key))).toMap
  }

  private val functions = obj match{
    case jsObj: js.Object=>
      keys(jsObj).map(key => (key, jsObj.asInstanceOf[js.Dynamic].selectDynamic(key)))
        .collect{case (k, dyn) =>
          k -> List(ValFunction(List(), (params) => dyn(params.map(_.asInstanceOf[js.Dynamic].selectDynamic("value")): _*)))
        }
        .toMap

  }

  override val variableProvider: VariableProvider = StaticVariableProvider(variables)

  override def functionProvider: FunctionProvider = StaticFunctionProvider(functions)
}
