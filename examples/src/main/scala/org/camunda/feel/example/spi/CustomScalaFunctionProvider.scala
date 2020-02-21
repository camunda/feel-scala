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
package org.camunda.feel.example.spi

import org.camunda.feel.context.CustomFunctionProvider

import scala.math.BigDecimal.int2bigDecimal
import org.camunda.feel.syntaxtree
import org.camunda.feel.syntaxtree._

class CustomScalaFunctionProvider extends CustomFunctionProvider {

  def getFunction(name: String): Option[syntaxtree.ValFunction] =
    functions.get(name)

  override def functionNames: Iterable[String] = functions.keys

  val functions: Map[String, syntaxtree.ValFunction] = Map(
    "foo" -> ValFunction(
      params = List("x"),
      invoke = { case List(ValNumber(x)) => ValNumber(x + 1) }
    ),
    "isBlack" -> ValFunction(
      params = List("s"),
      invoke = {
        case List(ValString(s)) =>
          if (s == "black") ValBoolean(true) else ValBoolean(false)
      },
    )
  )

}
