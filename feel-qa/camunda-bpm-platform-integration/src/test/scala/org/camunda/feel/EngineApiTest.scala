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
package org.camunda.feel

import org.camunda.feel.FeelEngine.Failure
import org.camunda.feel.FeelEngine.UnaryTests.inputVariable
import org.camunda.feel.helper.SimpleTestContext
import org.camunda.feel.impl.spi.CustomContext
import org.camunda.feel.interpreter.impl.VariableProvider
import org.camunda.feel.interpreter.impl.VariableProvider.StaticVariableProvider
import org.scalatest.Matchers
import org.scalatest.refspec.RefSpec

class EngineApiTest extends RefSpec with Matchers {

  val engine = new org.camunda.feel.FeelEngine()

  val inputVariableContext = StaticVariableProvider(
    Map(
      inputVariable -> "myInputVariable"
    ))

  def `should evaluate expression`() {
    val variables: Map[String, _] = Map("foo" -> 7)

    val context: CustomContext = new CustomContext {
      override val variableProvider = SimpleTestContext(variables)
    }

    engine.evalExpression("foo", context) should be(Right(7))
  }

  def `should fail on expression evaluation`() {
    val variables: Map[String, _] = Map()

    val context: CustomContext = new CustomContext {
      override val variableProvider = SimpleTestContext(variables)
    }

    engine.evalExpression("bar", context) shouldBe Left(Failure(
      "failed to evaluate expression 'bar': no variable found for name 'bar'"))
  }

  def `should evaluate unary-test`() {
    val variables: Map[String, _] = Map("myInputVariable" -> 8, "foo" -> 7)

    val context: CustomContext = new CustomContext {
      override val variableProvider =
        VariableProvider.CompositeVariableProvider(
          List(inputVariableContext, SimpleTestContext(variables)))
    }

    engine.evalUnaryTests("foo", context) should be(Right(false))
  }

  def `should fail on unary-test evaluation`() {
    val variables: Map[String, _] = Map("foo" -> 7)

    val context: CustomContext = new CustomContext {
      override val variableProvider =
        VariableProvider.CompositeVariableProvider(
          List(inputVariableContext, SimpleTestContext(variables)))
    }

    engine.evalUnaryTests("foo", context) shouldBe Left(Failure(
      "failed to evaluate expression 'foo': no variable found for name 'myInputVariable'"))
  }

}
