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
import org.camunda.feel.FeelEngine.{Failure, UnaryTests}
import org.camunda.feel.context.{
  CustomContext,
  FunctionProvider,
  VariableProvider
}
import org.camunda.feel.context.VariableProvider.StaticVariableProvider
import org.camunda.feel.syntaxtree._
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

class CustomContextTest extends AnyFlatSpec with Matchers {

  val engine = new FeelEngine

  "A default context" should "provide its members" in {
    engine.evalExpression("a", variables = Map("a" -> 2)) should be(Right(2))
    engine.evalUnaryTests(
      "2",
      variables = Map(UnaryTests.defaultInputVariable -> 2)) should be(
      Right(true))
  }

  it should "return null if the variable doesn't exist" in {
    engine.evalExpression("b", variables = Map("a" -> 2)) shouldBe Right(null)
  }

  "A custom context" should "provide its members" in {
    val myCustomContext = new CustomContext {

      override def variableProvider: VariableProvider = new VariableProvider {
        override def getVariable(name: String): Option[Any] = name match {
          case "a"                             => Some(2)
          case UnaryTests.defaultInputVariable => Some(2)
          case _                               => None
        }

        override def keys: Iterable[String] =
          List("a", UnaryTests.defaultInputVariable)
      }

    }
    engine.evalExpression("a", myCustomContext) should be(Right(2))
    engine.evalExpression("floor(3.8)", myCustomContext) should be(Right(3))
    engine.evalUnaryTests("2", myCustomContext) should be(Right(true))
  }

  it should "provide its functions" in {

    var variableCallCount = 0
    var functionCallCount = 0

    val myVariableProvider = new VariableProvider {
      override def getVariable(name: String): Option[Any] = {
        variableCallCount += 1;
        if (name == "a") Some(2) else None
      }

      override def keys: Iterable[String] = List("a")
    }

    val myFunctionProvider = new FunctionProvider {
      val f = ValFunction(List("x"), {
        case List(ValNumber(x)) => ValNumber(x + 2)
      })

      override def getFunctions(name: String): List[ValFunction] = {
        functionCallCount += 1;
        if (name == "f") List(f) else List.empty
      }

      override def functionNames: Iterable[String] = List("f")
    }

    val myCustomContext = new CustomContext {
      override val variableProvider = myVariableProvider
      override val functionProvider = myFunctionProvider
    }

    engine.evalExpression("a + f(2) + a + f(8)", myCustomContext) should be(
      Right(18))
    variableCallCount should be(2)
    functionCallCount should be(2)

  }

  it should "evaluate expression" in {
    val variables: Map[String, _] = Map("foo" -> 7)

    val context: CustomContext = new CustomContext {
      override val variableProvider = SimpleTestContext(variables)
    }

    engine.evalExpression("foo", context) should be(Right(7))
  }

  it should "return null if variable doesn't exist" in {
    val variables: Map[String, _] = Map()

    val context: CustomContext = new CustomContext {
      override val variableProvider = SimpleTestContext(variables)
    }

    engine.evalExpression("bar", context) shouldBe Right(null)
  }

  val inputVariableContext = StaticVariableProvider(
    Map(
      UnaryTests.inputVariable -> "myInputVariable"
    ))

  it should "evaluate unary-test" in {
    val variables: Map[String, _] = Map("myInputVariable" -> 8, "foo" -> 7)

    val context: CustomContext = new CustomContext {
      override val variableProvider =
        VariableProvider.CompositeVariableProvider(
          List(inputVariableContext, SimpleTestContext(variables)))
    }

    engine.evalUnaryTests("foo", context) should be(Right(false))
  }

  it should "return null if input value doesn't exist" in {
    val variables: Map[String, _] = Map("foo" -> 7)

    val context: CustomContext = new CustomContext {
      override val variableProvider =
        VariableProvider.CompositeVariableProvider(
          List(inputVariableContext, SimpleTestContext(variables)))
    }

    engine.evalUnaryTests("foo", context) should be(Right(ValBoolean(false)))
  }

}
