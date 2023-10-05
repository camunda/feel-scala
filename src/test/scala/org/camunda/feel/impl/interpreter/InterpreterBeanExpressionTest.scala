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

import org.camunda.feel.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** @author
  *   Philipp Ossler
  */
class InterpreterBeanExpressionTest extends AnyFlatSpec with Matchers with FeelIntegrationTest {

  "A bean" should "access a field" in {

    class A(val b: Int)

    eval("a.b", Map("a" -> new A(2))) should be(ValNumber(2))

  }

  it should "access a getter method as field" in {

    class A(b: Int) {
      def getFoo() = b + 1
    }

    eval("a.foo", Map("a" -> new A(2))) should be(ValNumber(3))

  }

  it should "ignore getter method with arguments as field" in {

    class A(x: Int) {
      def getResult(y: Int): Int = x + y
    }

    eval("a.result", Map("a" -> new A(2))) should be(
      ValError("context contains no entry with key 'result'")
    )
  }

  it should "ignore method with arguments as field (builder-style)" in {

    class A(x: Int) {
      def plus(y: Int): A = new A(x + y)
    }

    eval("a.plus", Map("a" -> new A(2))) should be(
      ValError("context contains no entry with key 'plus'")
    )
  }

  it should "not access a private field" in {
    class A(private val x: Int)

    eval("a.x", Map("a" -> new A(2))) should be(
      ValError("context contains no entry with key 'x'")
    )
  }

  it should "not access a private method" in {
    class A(val x: Int) {
      private def getResult(): Int = x
    }

    eval("a.result", Map("a" -> new A(2))) should be(
      ValError("context contains no entry with key 'result'")
    )
  }

  it should "invoke a method without arguments" in {

    class A {
      def foo() = "foo"
    }

    eval("a.foo()", Map("a" -> new A())) should be(ValString("foo"))

  }

  it should "invoke a method with one argument" in {

    class A {
      def incr(x: Int) = x + 1
    }

    eval("a.incr(1)", Map("a" -> new A())) should be(ValNumber(2))

  }

  it should "access a nullable field" in {

    class A(val a: String, val b: String)

    eval(""" a.a = null """, Map("a" -> new A("not null", null))) should be(ValBoolean(false))
    eval(""" a.b = null """, Map("a" -> new A("not null", null))) should be(ValBoolean(true))
    eval(""" null = a.a """, Map("a" -> new A("not null", null))) should be(ValBoolean(false))
    eval(""" null = a.b""", Map("a" -> new A("not null", null))) should be(ValBoolean(true))
    eval(""" a.a = a.b """, Map("a" -> new A("not null", "not null"))) should be(ValBoolean(true))
    eval(""" a.a = a.b """, Map("a" -> new A("not null", null))) should be(ValBoolean(false))
    eval(""" a.a = a.b """, Map("a" -> new A(null, "not null"))) should be(ValBoolean(false))
    eval(""" a.a = a.b """, Map("a" -> new A(null, null))) should be(ValBoolean(true))
  }

}
