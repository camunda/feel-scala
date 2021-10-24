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
import org.scalatest.{FlatSpec, Matchers}

/**
  * @author Philipp Ossler
  */
class InterpreterLiteralExpressionTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A literal" should "be a number" in {

    eval("2") should be(ValNumber(2))
    eval("2.4") should be(ValNumber(2.4))
    eval("-3") should be(ValNumber(-3))
  }

  it should "be a string" in {

    eval(""" "a" """) should be(ValString("a"))
  }

  it should "be a boolean" in {

    eval("true") should be(ValBoolean(true))
  }

  it should "be null" in {

    eval("null") should be(ValNull)
  }

  it should "be a context (identifier as key)" in {

    eval("{ a : 1 }")
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariables should be(Map("a" -> ValNumber(1)))

    eval("""{ a:1, b:"foo" }""")
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariables should be(Map("a" -> ValNumber(1), "b" -> ValString("foo")))

    // nested
    val nestedContext = eval("{ a : { b : 1 } }")
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariable("a")
      .get

    nestedContext shouldBe a[ValContext]
    nestedContext
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariables should be(Map("b" -> ValNumber(1)))
  }

  it should "be a context (string as key)" in {
    val result = eval(""" {"a":1} """)

    result shouldBe a[ValContext]
    result match {
      case ValContext(context) =>
        context.variableProvider.getVariables should be(
          Map("a" -> ValNumber(1)))
    }
  }

  it should "be a list" in {

    eval("[1]") should be(ValList(List(ValNumber(1))))

    eval("[1,2]") should be(ValList(List(ValNumber(1), ValNumber(2))))

    // nested
    eval("[ [1], [2] ]") should be(
      ValList(List(ValList(List(ValNumber(1))), ValList(List(ValNumber(2))))))
  }

  it should "be a range with boundary" in {
    eval("[1..3]") should be(ValRange(
      RangeWithBoundaries(ValNumber(1).value, ValNumber(3).value, true, true)))

    eval("(1..3]") should be(ValRange(
      RangeWithBoundaries(ValNumber(1).value, ValNumber(3).value, false, true)))

    eval("(1..3)") should be(
      ValRange(
        RangeWithBoundaries(ValNumber(1).value,
                            ValNumber(3).value,
                            false,
                            false)))

    eval("[1..3)") should be(ValRange(
      RangeWithBoundaries(ValNumber(1).value, ValNumber(3).value, true, false)))
  }

}
