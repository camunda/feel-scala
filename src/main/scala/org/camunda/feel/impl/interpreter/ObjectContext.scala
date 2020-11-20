/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

      val objClass = obj.getClass
      val fields = objClass.getFields find (field => field.getName == name)

      fields.map(_.get(obj)) orElse {
        val methods = objClass.getMethods find (method => {
          val methodName = method.getName
          val returnType = method.getReturnType
          methodName == name ||
          methodName == getGetterName(name) ||
          ((returnType == java.lang.Boolean.TYPE ||
          returnType == classOf[java.lang.Boolean]) &&
          methodName == getBooleanGetterName(name))
        })

        methods.map(_.invoke(obj))
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
    "get" + fieldName.capitalize

  private def getBooleanGetterName(fieldName: String) =
    "is" + fieldName.capitalize

}
