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

import org.camunda.feel.syntaxtree._

/**
  * @author Philipp
  */
object JavaClassMapper {

  val classLoader = getClass.getClassLoader

  val stringClass = classOf[java.lang.String]

  def loadClass(className: String): Class[_] = className match {
    case "int"     => java.lang.Integer.TYPE
    case "long"    => java.lang.Long.TYPE
    case "float"   => java.lang.Float.TYPE
    case "double"  => java.lang.Double.TYPE
    case "boolean" => java.lang.Boolean.TYPE
    case "String"  => stringClass
    case _         => classLoader.loadClass(className)
  }

  def asJavaObject(value: Val, clazz: Class[_]): java.lang.Object =
    (value, clazz) match {
      case (ValNull, _) => null
      case (ValBoolean(b), java.lang.Boolean.TYPE) =>
        java.lang.Boolean.valueOf(b)
      case (ValString(s), stringClass) => java.lang.String.valueOf(s)
      case (ValNumber(n), java.lang.Integer.TYPE) =>
        java.lang.Integer.valueOf(n.intValue)
      case (ValNumber(n), java.lang.Long.TYPE) =>
        java.lang.Long.valueOf(n.longValue)
      case (ValNumber(n), java.lang.Float.TYPE) =>
        java.lang.Float.valueOf(n.floatValue)
      case (ValNumber(n), java.lang.Double.TYPE) =>
        java.lang.Double.valueOf(n.doubleValue)
      case _ =>
        throw new IllegalArgumentException(
          s"can not cast value '$value' to class '$clazz'")
    }

}
