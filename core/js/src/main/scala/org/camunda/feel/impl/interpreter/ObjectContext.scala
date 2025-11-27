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

import scala.scalajs.js

/** A context that wraps the fields and methods of a given object for Scala.js
  *
  * In Scala.js, Scala classes are compiled to JavaScript objects where:
  * - Public val fields become properties
  * - Methods become functions on the prototype
  * - Getter methods (getX, isX) can be used as property accessors
  *
  * @param obj
  *   the object to be wrapped
  */
case class ObjectContext(obj: Any) extends Context {

  private lazy val jsObj: js.Dynamic = obj.asInstanceOf[js.Dynamic]

  // Get all property names from the object and its prototype chain (recursively)
  private lazy val allKeys: Seq[String] = {
    obj match {
      case product: Product =>
        product.productElementNames.toSeq
      case _ =>
        def getProtoKeys(proto: js.Object): Seq[String] = {
          if (proto == null || js.isUndefined(proto)) {
            Seq.empty
          } else {
            val keys = js.Object.keys(proto).toSeq
            val parentProto = js.Object.getPrototypeOf(proto)
            // Stop at Object.prototype
            if (parentProto == null || js.isUndefined(parentProto) || 
                js.Object.keys(parentProto).isEmpty) {
              keys
            } else {
              keys ++ getProtoKeys(parentProto)
            }
          }
        }
        
        val objKeys = js.Object.keys(obj.asInstanceOf[js.Object]).toSeq
        val proto = js.Object.getPrototypeOf(obj.asInstanceOf[js.Object])
        val protoKeys = getProtoKeys(proto)
        
        (objKeys ++ protoKeys).distinct.filterNot(k => k == "constructor" || k.startsWith("$"))
    }
  }

  // Get all zero-argument methods from the prototype
  private lazy val zeroArgMethods: Map[String, js.Dynamic] = {
    obj match {
      case _: Product => Map.empty
      case _ =>
        allKeys.flatMap { key =>
          val prop = jsObj.selectDynamic(key)
          if (js.typeOf(prop) == "function") {
            Some(key -> prop)
          } else {
            None
          }
        }.toMap
    }
  }

  private def getGetterName(fieldName: String): String =
    "get" + fieldName.capitalize

  private def getBooleanGetterName(fieldName: String): String =
    "is" + fieldName.capitalize

  private def invokeIfZeroArgFunction(prop: js.Dynamic): Option[Any] = {
    if (js.typeOf(prop) == "function") {
      try {
        val result = prop.apply(jsObj).asInstanceOf[Any]
        Some(result)
      } catch {
        case _: Throwable => None
      }
    } else {
      None
    }
  }

  override val variableProvider: VariableProvider = new VariableProvider {
    override def getVariable(name: String): Option[Any] = {
      obj match {
        case product: Product =>
          product.productElementNames.zip(product.productIterator)
            .find(_._1 == name)
            .map(_._2)

        case _ =>
          // First, try direct property access (for public val fields)
          val directProp = jsObj.selectDynamic(name)
          if (!js.isUndefined(directProp) && js.typeOf(directProp) != "function") {
            Some(directProp.asInstanceOf[Any])
          } else if (!js.isUndefined(directProp) && js.typeOf(directProp) == "function") {
            // It's a zero-arg method with the same name (like `def foo()`)
            invokeIfZeroArgFunction(directProp)
          } else {
            // Try getter method: getFoo -> foo
            val getterName = getGetterName(name)
            zeroArgMethods.get(getterName).flatMap(invokeIfZeroArgFunction).orElse {
              // Try boolean getter: isEnabled -> enabled (only if returns boolean)
              val boolGetterName = getBooleanGetterName(name)
              zeroArgMethods.get(boolGetterName).flatMap { method =>
                invokeIfZeroArgFunction(method).filter {
                  case _: Boolean => true
                  case _ => false
                }
              }
            }
          }
      }
    }

    override def keys: Iterable[String] = {
      obj match {
        case product: Product =>
          product.productElementNames.toSeq

        case _ =>
          // Return direct properties (non-functions) 
          val directProps = allKeys.filter { key =>
            val prop = jsObj.selectDynamic(key)
            !js.isUndefined(prop) && js.typeOf(prop) != "function"
          }

          // Also include fields that have getters
          val getterFields = zeroArgMethods.keys.flatMap { methodName =>
            if (methodName.startsWith("get") && methodName.length > 3) {
              Some(methodName.drop(3).head.toLower + methodName.drop(4))
            } else if (methodName.startsWith("is") && methodName.length > 2) {
              // Only include boolean getters
              invokeIfZeroArgFunction(zeroArgMethods(methodName)) match {
                case Some(_: Boolean) =>
                  Some(methodName.drop(2).head.toLower + methodName.drop(3))
                case _ => None
              }
            } else {
              None
            }
          }

          (directProps ++ getterFields).distinct
      }
    }
  }

  override val functionProvider: FunctionProvider = new FunctionProvider {
    override def getFunctions(name: String): List[ValFunction] = {
      obj match {
        case _: Product => List.empty

        case _ =>
          zeroArgMethods.get(name).map { method =>
            // For now, we only support zero-argument methods
            // Getting parameter info at runtime in JS is complex
            ValFunction(
              params = List.empty,
              invoke = _ => {
                try {
                  method.apply(jsObj).asInstanceOf[Any]
                } catch {
                  case e: Throwable => null
                }
              }
            )
          }.toList
      }
    }

    override def functionNames: Iterable[String] = {
      obj match {
        case _: Product => Iterable.empty
        case _ => zeroArgMethods.keys
      }
    }
  }
}
