package org.camunda.feel.impl.interpreter

import org.camunda.feel.context.{Context, FunctionProvider, VariableProvider}
import org.camunda.feel.syntaxtree.ValFunction

/**
  * A context that wraps the fields and methods of a given JVM object
  *
  * @param obj the JVM object to be wrapped
  */
case class ObjectContext(obj: Any) extends Context {

  override val variableProvider = new VariableProvider {
    override def getVariable(name: String): Option[Any] = {

      val field = obj.getClass.getFields find (f => f.getName == name)

      field.map(f => f.get(obj)) orElse {
        val methods = obj.getClass.getMethods
        val method = methods find (m =>
          m.getName == name || m.getName == getGetterName(name))

        method.map(m => m.invoke(obj))
      }
    }

    override def keys: Iterable[String] = obj.getClass.getFields.map(_.getName)
  }

  override val functionProvider = new FunctionProvider {
    override def getFunctions(name: String): List[ValFunction] = {
      obj.getClass.getMethods
        .find(method => {
          method.getName == name
        })
        .map(method => {
          val params = method.getParameters.map(param => param.getName).toList

          ValFunction(
            params,
            params => {

              val paramJavaObjects = params zip method.getParameterTypes map {
                case (obj, clazz) => JavaClassMapper.asJavaObject(obj, clazz)
              }
              val result = method.invoke(obj, paramJavaObjects: _*)
              result
            }
          )
        })
        .toList
    }

    override def functionNames: Iterable[String] =
      obj.getClass.getMethods.map(_.getName)
  }

  private def getGetterName(fieldName: String) =
    "get" + fieldName.charAt(0).toUpper + fieldName.substring(1)

}
