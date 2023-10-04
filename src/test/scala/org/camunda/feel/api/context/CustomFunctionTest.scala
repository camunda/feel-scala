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
package org.camunda.feel.api.context

import org.camunda.feel.FeelEngine
import org.camunda.feel.context.{CustomFunctionProvider, FunctionProvider}
import org.camunda.feel.syntaxtree.{ValFunction, ValNumber}
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

class CustomFunctionTest extends AnyFlatSpec with Matchers {

  val functionProviderFoo = new TestFunctionProvider(
    functions = Map(
      "foo" ->
        ValFunction(
          params = List("x"),
          invoke = { case List(ValNumber(x)) =>
            ValNumber(x + 1)
          }
        )
    )
  )

  val functionProviderBar = new TestFunctionProvider(
    functions = Map(
      "bar" ->
        ValFunction(
          params = List("x"),
          invoke = { case List(ValNumber(x)) =>
            ValNumber(x + 2)
          }
        )
    )
  )

  "A FeelEngine" should "be extendable by a custom function provider" in {

    val engine = new FeelEngine(functionProviderFoo)

    engine.evalExpression("foo(2)") should be(Right(3))
  }

  it should "be extendable by multiple custom function providers" in {

    val engine = new FeelEngine(
      FunctionProvider.CompositeFunctionProvider(
        List(functionProviderFoo, functionProviderBar)
      )
    )

    engine.evalExpression("foo(2)") should be(Right(3))
    engine.evalExpression("bar(2)") should be(Right(4))
  }

  class TestFunctionProvider(functions: Map[String, ValFunction]) extends CustomFunctionProvider {

    override def getFunction(name: String): Option[ValFunction] =
      functions.get(name)

    override def functionNames: Iterable[String] = functions.keys

  }

}
