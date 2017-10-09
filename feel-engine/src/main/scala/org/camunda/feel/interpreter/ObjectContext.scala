package org.camunda.feel.interpreter

import java.lang.reflect._

/**
  * A context that wraps the fields and methods of a given JVM object
  * @param obj the JVM object to be wrapped
  * @param valueMapper the valueMapper to be applied
  */
case class ObjectContext(obj: Any, override val valueMapper: ValueMapper = DefaultValueMapper.instance) extends ContextBase {

  override val variableProvider = new VariableProvider {
    override def getVariable(name: String): Option[Val] = {

        val field = obj.getClass.getFields find(f => f.getName == name)

        val value = field.map(f => f.get(obj)) orElse {
          val methods = obj.getClass.getMethods
          val method = methods find(m => m.getName == name || m.getName == getGetterName(name))

          method.map(m => m.invoke(obj))
        }

        value map valueMapper.toVal
    }
  }

  override val functionProvider = new FunctionProvider {
    override def getFunction(name: String, argumentCount: Int): Option[ValFunction] = {
      obj.getClass.getMethods
        .find(method => { method.getName == name && method.getParameterCount == argumentCount })
        .map(method => {
          val params = method.getParameters.map(param => param.getName).toList
          ValFunction(params, params => {
            val paramValues = params map valueMapper.unpackVal
            val paramJavaObjects = paramValues zip method.getParameterTypes map { case (obj, clazz) => JavaClassMapper.asJavaObject(obj, clazz) }
            val result = method.invoke(obj, paramJavaObjects: _*)
            valueMapper.toVal(result)
          })
        })
    }

  }

  private def getGetterName(fieldName: String) = "get" + fieldName.charAt(0).toUpper + fieldName.substring(1)

}
