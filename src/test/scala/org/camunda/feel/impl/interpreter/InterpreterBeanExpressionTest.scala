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

import org.camunda.feel.api.EvaluationFailureType
import org.camunda.feel.impl.{EvaluationResultMatchers, FeelEngineTest}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** @author
  *   Philipp Ossler
  */
class InterpreterBeanExpressionTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

  "A bean" should "access a field" in {
    class A(val b: Int)

    evaluateExpression(
      expression = "a.b",
      variables = Map("a" -> new A(2))
    ) should returnResult(2)
  }

  it should "access a getter method as field" in {
    class A(b: Int) {
      def getFoo() = b + 1
    }

    evaluateExpression(
      expression = "a.foo",
      variables = Map("a" -> new A(2))
    ) should returnResult(3)
  }

  it should "ignore getter method with arguments as field" in {
    class A(x: Int) {
      def getResult(y: Int): Int = x + y
    }

    evaluateExpression(
      expression = "a.result",
      variables = Map("a" -> new A(2))
    ) should (returnNull() and
      reportFailure(
        failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
        failureMessage = "No context entry found with key 'result'. Available keys: "
      ))
  }

  it should "ignore method with arguments as field (builder-style)" in {
    class A(x: Int) {
      def plus(y: Int): A = new A(x + y)
    }

    evaluateExpression(
      expression = "a.plus",
      variables = Map("a" -> new A(2))
    ) should (returnNull() and
      reportFailure(
        failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
        failureMessage = "No context entry found with key 'plus'. Available keys: "
      ))
  }

  it should "not access a private field" in {
    class A(private val x: Int)

    evaluateExpression(
      expression = "a.x",
      variables = Map("a" -> new A(2))
    ) should (returnNull() and
      reportFailure(
        failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
        failureMessage = "No context entry found with key 'x'. Available keys: "
      ))
  }

  it should "not access a private method" in {
    class A(val x: Int) {
      private def getResult(): Int = x
    }

    evaluateExpression(
      expression = "a.result",
      variables = Map("a" -> new A(2))
    ) should (returnNull() and
      reportFailure(
        failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
        failureMessage = "No context entry found with key 'result'. Available keys: 'x'"
      ))
  }

  it should "invoke a method without arguments" in {
    class A {
      def foo() = "foo"
    }

    evaluateExpression(
      expression = "a.foo()",
      variables = Map("a" -> new A())
    ) should returnResult("foo")
  }

  it should "invoke a method with one argument" in {
    class A {
      def incr(x: Int) = x + 1
    }

    evaluateExpression(
      expression = "a.incr(1)",
      variables = Map("a" -> new A())
    ) should returnResult(2)
  }

  it should "access a nullable field" in {
    class A(val a: String, val b: String)

    evaluateExpression(
      expression = "a.a",
      variables = Map("a" -> new A(a = "not null", b = null))
    ) should returnResult("not null")

    evaluateExpression(
      expression = "a.b",
      variables = Map("a" -> new A(a = "not null", b = null))
    ) should returnNull()

    evaluateExpression(
      expression = "a.a = a.b",
      variables = Map("a" -> new A(a = "not null", b = "not null"))
    ) should returnResult(true)

    evaluateExpression(
      expression = "a.a = a.b",
      variables = Map("a" -> new A(a = "not null", b = null))
    ) should returnResult(false)

    evaluateExpression(
      expression = "a.a = a.b",
      variables = Map("a" -> new A(a = null, b = "not null"))
    ) should returnResult(false)

    evaluateExpression(
      expression = "a.a = a.b",
      variables = Map("a" -> new A(a = null, b = null))
    ) should returnResult(true)
  }

}
