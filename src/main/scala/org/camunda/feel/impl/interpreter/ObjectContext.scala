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

import java.lang.reflect.Method

/** A context that wraps the fields and methods of a given JVM object
  *
  * @param obj
  *   the JVM object to be wrapped
  */
case class ObjectContext(obj: Any) extends Context {

  private lazy val publicFields = obj.getClass.getFields
  private lazy val allFields    = obj.getClass.getDeclaredFields

  private lazy val publicMethodsWithoutArguments = obj.getClass.getMethods
    .filter(method => method.getParameterCount == 0)

  override val variableProvider = new VariableProvider {
    override def getVariable(name: String): Option[Any] = {

      val fieldForName = publicFields find (field => field.getName == name)

      fieldForName.map(_.get(obj)) orElse {
        val methods = publicMethodsWithoutArguments find (method =>
          isGetterOf(method, name) || isBooleanGetterOf(method, name)
        )

        methods.map(_.invoke(obj))
      }
    }

    override def keys: Iterable[String] = {
      val fieldsWithPublicGetter = allFields.filter(field =>
        publicMethodsWithoutArguments.exists(method =>
          isGetterOf(method, field.getName) || isBooleanGetterOf(method, field.getName)
        )
      )

      publicFields.map(_.getName) ++ fieldsWithPublicGetter.map(_.getName)
    }
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

              val paramJavaObjects = params zip method.getParameterTypes map { case (obj, clazz) =>
                JavaClassMapper.asJavaObject(obj, clazz)
              }
              val result           = method.invoke(obj, paramJavaObjects: _*)
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

  private def isGetterOf(method: Method, fieldName: String): Boolean = {
    val methodName = method.getName

    methodName == fieldName || methodName == getGetterName(fieldName)
  }

  private def isBooleanGetterOf(method: Method, fieldName: String): Boolean = {
    val returnType = method.getReturnType

    method.getName == getBooleanGetterName(
      fieldName
    ) && (returnType == java.lang.Boolean.TYPE || returnType == classOf[java.lang.Boolean])
  }

}
