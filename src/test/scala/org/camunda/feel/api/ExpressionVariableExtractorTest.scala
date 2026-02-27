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
package org.camunda.feel.api

import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ExpressionVariableExtractorTest extends AnyFlatSpec with Matchers with EitherValues {

  private val engine: FeelEngineApi = FeelEngineBuilder().build()

  "The variable names of a parsed expression" should "be empty if no variable is referenced" in {
    val parseResult = engine.parseExpression("1 + 2")

    parseResult.isSuccess should be(true)
    parseResult.variableNames shouldBe empty
  }

  it should "contain the name of a single variable" in {
    val parseResult = engine.parseExpression("a")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a"))
  }

  it should "contain the names of all variables" in {
    val parseResult = engine.parseExpression("a < b")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the names of the variables only once" in {
    val parseResult = engine.parseExpression("a + a + b")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the top-level names of nested variables" in {
    val parseResult = engine.parseExpression("a.b < c.d")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "c"))
  }

  it should "contain the names of variables in a list" in {
    val parseResult = engine.parseExpression("[a, b]")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the names of variables in a context" in {
    val parseResult = engine.parseExpression("{a: c, b: d}")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("c", "d"))
  }

  it should "not contain the names of nested context entries" in {
    val parseResult = engine.parseExpression("{a: c, b: a + d}")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("c", "d"))
  }

  it should "not contain the names of nested context entries (level 2)" in {
    val parseResult = engine.parseExpression("{a: c, b: {d: a + e}}")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("c", "e"))
  }

  it should "contain the names of variables in a range" in {
    val parseResult = engine.parseExpression("[a..b)")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the names of variables in a comparison" in {
    val parseResult = engine.parseExpression("a < b")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the names of variables in an addition" in {
    val parseResult = engine.parseExpression("a + b")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the names of variables in an subtraction" in {
    val parseResult = engine.parseExpression("a - b")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the names of variables in an multiplication" in {
    val parseResult = engine.parseExpression("a * b")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the names of variables in an division" in {
    val parseResult = engine.parseExpression("a / b")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the names of variables in an exponentiation" in {
    val parseResult = engine.parseExpression("a ** b")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the names of variables in an arithmetic negation" in {
    val parseResult = engine.parseExpression("- a")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a"))
  }

  it should "contain the names of variables in a disjunction" in {
    val parseResult = engine.parseExpression("a or b")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the names of variables in a conjunction" in {
    val parseResult = engine.parseExpression("a and b")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the names of variables in 'if _ then _ else _'" in {
    val parseResult = engine.parseExpression("if a < 5 then b else c")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b", "c"))
  }

  it should "contain the names of variables in '_ in _'" in {
    val parseResult = engine.parseExpression("a in (0..b)")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the names of variables in 'some _ in _ satisfies _'" in {
    val parseResult = engine.parseExpression("some x in [a] satisfies x < b")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the names of variables in 'every _ in _ satisfies _'" in {
    val parseResult = engine.parseExpression("every x in [a] satisfies x < b")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the names of variables in 'for _ in _ return _'" in {
    val parseResult = engine.parseExpression("for x in [a] return x + b")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the names of variables in a list filter" in {
    val parseResult = engine.parseExpression("[1, 2][item < a]")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a"))
  }

  it should "not contain the names of context entries in a context list filter" in {
    val parseResult = engine.parseExpression("[{a: b}][a < c]")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("b", "c"))
  }

  it should "contain the names of variables in path expression" in {
    val parseResult = engine.parseExpression("{a: b}.a")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("b"))
  }

  it should "contain the names of variables in '_ instance of _'" in {
    val parseResult = engine.parseExpression("a instance of string")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a"))
  }

  it should "contain the names of variables in function invocation with positional arguments" in {
    val parseResult = engine.parseExpression("ceiling(a)")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a"))
  }

  it should "contain the names of variables in function invocation with named arguments" in {
    val parseResult = engine.parseExpression("ceiling(n: a)")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a"))
  }

  it should "contain the names of variables in function definition" in {
    val parseResult = engine.parseExpression("function(a) a + b")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("b"))
  }

  "The variable names of a parsed unary-test expression" should "contain the names of variables in comparison" in {
    val parseResult = engine.parseUnaryTests("< a")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a"))
  }

  it should "contain the names of variables in a disjunction" in {
    val parseResult = engine.parseUnaryTests("a, b")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the names of variables in a negation" in {
    val parseResult = engine.parseUnaryTests("not(a, b)")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  it should "contain the names of variables in a boolean expression" in {
    val parseResult = engine.parseUnaryTests("contains(?, a), ends with(?, b)")

    parseResult.isSuccess should be(true)
    parseResult.variableNames should be(Set("a", "b"))
  }

  "The variable references of a parsed expression" should "be empty if no variable is referenced" in {
    val parseResult = engine.parseExpression("1 + 2")

    parseResult.isSuccess should be(true)
    parseResult.variableReferences shouldBe empty
  }

  it should "contain all top-level variables" in {
    val parseResult = engine.parseExpression("a + b + c")

    parseResult.isSuccess should be(true)
    parseResult.variableReferences should be(
      Set(
        VariableReference(Seq("a")),
        VariableReference(Seq("b")),
        VariableReference(Seq("c"))
      )
    )
  }

  it should "contain all nested variables" in {
    val parseResult = engine.parseExpression("a.b + a.c + d.e")

    parseResult.isSuccess should be(true)
    parseResult.variableReferences should be(
      Set(
        VariableReference(Seq("a", "b")),
        VariableReference(Seq("a", "c")),
        VariableReference(Seq("d", "e"))
      )
    )
  }

  it should "contain top-level and nested variables" in {
    val parseResult = engine.parseExpression("a + b.c + b.d")

    parseResult.isSuccess should be(true)
    parseResult.variableReferences should be(
      Set(
        VariableReference(Seq("a")),
        VariableReference(Seq("b", "c")),
        VariableReference(Seq("b", "d"))
      )
    )
  }

  it should "contain all variables only once" in {
    val parseResult = engine.parseExpression("a + a + b.c + b.c")

    parseResult.isSuccess should be(true)
    parseResult.variableReferences should be(
      Set(
        VariableReference(Seq("a")),
        VariableReference(Seq("b", "c"))
      )
    )
  }

  it should "contain all variables from context" in {
    val parseResult = engine.parseExpression("{a: b, c: d.e, f: {g: h.i}}")

    parseResult.isSuccess should be(true)
    parseResult.variableReferences should be(
      Set(
        VariableReference(Seq("b")),
        VariableReference(Seq("d", "e")),
        VariableReference(Seq("h", "i"))
      )
    )
  }

}
