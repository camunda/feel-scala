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
package org.camunda.feel.impl

import org.camunda.feel.context.Context
import org.camunda.feel.syntaxtree.{Val, ValContext, ValList, ValNumber}
import org.camunda.feel.valuemapper.CustomValueMapper

import scala.collection.JavaConverters._

/**
  * Transform FEEL types into common Java objects. This includes numbers, lists and contexts.
  */
class JavaValueMapper extends CustomValueMapper {

  override def unpackVal(value: Val,
                         innerValueMapper: Val => Any): Option[Any] =
    value match {

      case ValNumber(number) =>
        Some(
          if (number.isWhole && number.isValidLong) {
            number.longValue: java.lang.Long
          } else {
            number.doubleValue: java.lang.Double
          }
        )

      case ValList(list) =>
        Some(
          (list map innerValueMapper).asJava: java.util.List[Any]
        )

      case ValContext(context: Context) =>
        Some(
          context.variableProvider.getVariables
            .map {
              case (key, value) =>
                value match {
                  case packed: Val => key -> innerValueMapper(packed)
                  case unpacked    => key -> unpacked
                }
            }
            .toMap
            .asJava: java.util.Map[String, Any]
        )

      case _ => None
    }

  override def toVal(x: Any, innerValueMapper: Any => Val): Option[Val] = None

  override val priority: Int = 10
}
