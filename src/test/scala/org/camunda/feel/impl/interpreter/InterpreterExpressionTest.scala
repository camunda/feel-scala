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

import org.camunda.feel.FeelEngine.UnaryTests
import org.camunda.feel.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree._
import org.scalatest.{FlatSpec, Matchers}

/**
  * @author Philipp Ossler
  */
class InterpreterExpressionTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  "An expression" should "be an if-then-else" in {

    val exp = """ if (x < 5) then "low" else "high" """

    eval(exp, Map("x" -> 2)) should be(ValString("low"))
    eval(exp, Map("x" -> 7)) should be(ValString("high"))

    eval(exp, Map("x" -> "foo")) should be(ValString("high"))
  }

  it should "be a simple positive unary test" in {

    eval("< 3", Map(UnaryTests.defaultInputVariable -> 2)) should be(
      ValBoolean(true))

    eval("(2 .. 4)", Map(UnaryTests.defaultInputVariable -> 5)) should be(
      ValBoolean(false))
  }

  it should "be an instance of" in {

    eval("x instance of number", Map("x" -> 1)) should be(ValBoolean(true))
    eval("x instance of number", Map("x" -> "NaN")) should be(ValBoolean(false))

    eval("x instance of boolean", Map("x" -> true)) should be(ValBoolean(true))
    eval("x instance of boolean", Map("x" -> 0)) should be(ValBoolean(false))

    eval("x instance of string", Map("x" -> "yes")) should be(ValBoolean(true))
    eval("x instance of string", Map("x" -> 0)) should be(ValBoolean(false))
  }

  it should "be an escaped identifier" in {
    // regular identifier
    eval(" `x` ", Map("x" -> "foo")) should be(ValString("foo"))
    // with whitespace
    eval(" `a b` ", Map("a b" -> "foo")) should be(ValString("foo"))
    // with operator
    eval(" `a-b` ", Map("a-b" -> 3)) should be(ValNumber(3))
  }

  "Null" should "compare to null" in {

    eval("null = null") should be(ValBoolean(true))
    eval("null != null") should be(ValBoolean(false))
  }

}
