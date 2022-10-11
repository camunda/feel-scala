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
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

/**
  * @author Philipp Ossler
  */
class InterpreterExpressionTest
    extends AnyFlatSpec
    with Matchers
    with FeelIntegrationTest {

  "An expression" should "be an if-then-else (with parentheses)" in {
    val exp = """ if (x < 5) then "low" else "high" """

    eval(exp, Map("x" -> 2)) should be(ValString("low"))
    eval(exp, Map("x" -> 7)) should be(ValString("high"))

    eval(exp, Map("x" -> "foo")) should be(ValString("high"))
  }

  it should "be an if-then-else (without parentheses)" in {
    eval("if x < 5 then 1 else 2", Map("x" -> 2)) should be(ValNumber(1))
  }

  it should "be an if-then-else (with literal)" in {
    eval("if true then 1 else 2") should be(ValNumber(1))
  }

  it should "be an if-then-else (with path)" in {
    eval("if {a: true}.a then 1 else 2") should be(ValNumber(1))
  }

  it should "be an if-then-else (with filter)" in {
    eval("if [true][1] then 1 else 2") should be(ValNumber(1))
  }

  it should "be an if-then-else (with conjunction)" in {
    eval("if true and true then 1 else 2") should be(ValNumber(1))
  }

  it should "be an if-then-else (with disjunction)" in {
    eval("if false or true then 1 else 2") should be(ValNumber(1))
  }

  it should "be an if-then-else (with in-test)" in {
    eval("if 1 in < 5 then 1 else 2") should be(ValNumber(1))
  }

  it should "be an if-then-else (with instance of)" in {
    eval("if 1 instance of number then 1 else 2") should be(ValNumber(1))
  }

  it should "be an if-then-else (with variable and function call -> then)" in {
    eval("if 7 > var then flatten(xs) else []",
         Map("xs" -> List(1, 2), "var" -> 3)) should be(
      ValList(List(ValNumber(1), ValNumber(2)))
    )
  }

  it should "be an if-then-else (with variable and function call -> else)" in {
    eval("if false then var else flatten(xs)",
         Map("xs" -> List(1, 2), "var" -> 3)) should be(
      ValList(List(ValNumber(1), ValNumber(2)))
    )
  }

  it should "be a simple positive unary test" in {

    eval("< 3", Map(UnaryTests.defaultInputVariable -> 2)) should be(
      ValBoolean(true))

    eval("(2 .. 4)", Map(UnaryTests.defaultInputVariable -> 5)) should be(
      ValBoolean(false))
  }

  it should "be an instance of (literal)" in {

    eval("x instance of number", Map("x" -> 1)) should be(ValBoolean(true))
    eval("x instance of number", Map("x" -> "NaN")) should be(ValBoolean(false))

    eval("x instance of boolean", Map("x" -> true)) should be(ValBoolean(true))
    eval("x instance of boolean", Map("x" -> 0)) should be(ValBoolean(false))

    eval("x instance of string", Map("x" -> "yes")) should be(ValBoolean(true))
    eval("x instance of string", Map("x" -> 0)) should be(ValBoolean(false))
  }

  it should "be an instance of (duration)" in {
    eval("duration(P3M) instance of years and months duration") should be(ValBoolean(true))
    eval("duration(PT4H) instance of days and time duration") should be(ValBoolean(true))
    eval("null instance of ears and months duration") should be(ValBoolean(false))
    eval("null instance of days and time duration") should be(ValBoolean(false))
  }

  it should "be an instance of (multiplication)" in {
    eval("2 * 3 instance of number") should be(ValBoolean(true))
  }

  it should "be an instance of (function definition)" in {
    eval(""" (function() "foo") instance of Any """) should be(ValBoolean(true))
  }

  it should "be a instance of Any should always pass" in {
    eval("x instance of Any", Map("x" -> "yes")) should be(ValBoolean(true))
    eval("x instance of Any", Map("x" -> 1)) should be(ValBoolean(true))
    eval("x instance of Any", Map("x" -> true)) should be(ValBoolean(true))
    eval("x instance of Any", Map("x" -> null)) should be(ValBoolean(false))
  }

  it should "be an escaped identifier" in {
    // regular identifier
    eval(" `x` ", Map("x" -> "foo")) should be(ValString("foo"))
    // with whitespace
    eval(" `a b` ", Map("a b" -> "foo")) should be(ValString("foo"))
    // with operator
    eval(" `a-b` ", Map("a-b" -> 3)) should be(ValNumber(3))
  }

  it should "contains parentheses" in {
    eval("(1 + 2)") should be(ValNumber(3))
    eval("(1 + 2) + 3") should be(ValNumber(6))
    eval("1 + (2 + 3)") should be(ValNumber(6))

    eval("([1,2,3])[1]") should be(ValNumber(1))
    eval("({x:1}).x") should be(ValNumber(1))
    eval("{x:(1)}.x") should be(ValNumber(1))

    eval("[1,2,3,4][(1)]") should be(ValNumber(1))
  }

  it should "contain parentheses in a context literal" in {
    val context = Map("xs" -> List(1, 2, 3))

    eval("{x:(xs[1])}.x", context) should be(ValNumber(1))
    eval("{x:(xs)[1]}.x", context) should be(ValNumber(1))
    eval("{x:(xs)}.x", context) should be(
      ValList(List(ValNumber(1), ValNumber(2), ValNumber(3))))
  }

  it should "contains nested filter expressions" in {
    eval("[1,2,3,4][item > 2][1]") should be(ValNumber(3))
    eval("([1,2,3,4])[item > 2][1]") should be(ValNumber(3))
    eval("([1,2,3,4][item > 2])[1]") should be(ValNumber(3))
  }

  it should "contains nested path expressions" in {
    eval("{x:{y:1}}.x.y") should be(ValNumber(1))
    eval("{x:{y:{z:1}}}.x.y.z") should be(ValNumber(1))

    eval("({x:{y:{z:1}}}).x.y.z") should be(ValNumber(1))
    eval("({x:{y:{z:1}}}.x).y.z") should be(ValNumber(1))
    eval("({x:{y:{z:1}}}.x.y).z") should be(ValNumber(1))
  }

  it should "contains nested filter and path expressions" in {
    eval("[{x:{y:1}},{x:{y:2}},{x:{y:3}}].x.y[2]") should be(ValNumber(2))
    eval("([{x:{y:1}},{x:{y:2}},{x:{y:3}}]).x.y[2]") should be(ValNumber(2))
    eval("([{x:{y:1}},{x:{y:2}},{x:{y:3}}].x).y[2]") should be(ValNumber(2))
    eval("([{x:{y:1}},{x:{y:2}},{x:{y:3}}].x.y)[2]") should be(ValNumber(2))

    eval("([{x:{y:1}},{x:{y:2}},{x:{y:3}}]).x[2].y") should be(ValNumber(2))
    eval("([{x:{y:1}},{x:{y:2}},{x:{y:3}}])[2].x.y") should be(ValNumber(2))

    eval("[{x:[1,2]},{x:[3,4]},{x:[5,6]}][2].x[1]") should be(ValNumber(3))

    eval("([{x:[1,2]},{x:[3,4]},{x:[5,6]}]).x[2][1]") should be(ValNumber(3))
    eval("([{x:[1,2]},{x:[3,4]},{x:[5,6]}].x)[2][1]") should be(ValNumber(3))
    eval("([{x:[1,2]},{x:[3,4]},{x:[5,6]}].x[2])[1]") should be(ValNumber(3))
  }

  "Null" should "compare to null" in {

    eval("null = null") should be(ValBoolean(true))
    eval("null != null") should be(ValBoolean(false))
  }

  it should "compare to nullable variable" in {

    eval("null = x", Map("x" -> ValNull)) should be(ValBoolean(true))
    eval("null = x", Map("x" -> 1)) should be(ValBoolean(false))

    eval("null != x", Map("x" -> ValNull)) should be(ValBoolean(false))
    eval("null != x", Map("x" -> 1)) should be(ValBoolean(true))
  }

  it should "compare to nullable context entry" in {

    eval("null = {x: null}.x") should be(ValBoolean(true))
    eval("null = {x: 1}.x") should be(ValBoolean(false))

    eval("null != {x: null}.x") should be(ValBoolean(false))
    eval("null != {x: 1}.x") should be(ValBoolean(true))
  }

  it should "compare to not existing variable" in {

    eval("null = x") should be(ValBoolean(true))
    eval("null = x.y") should be(ValBoolean(true))

    eval("x = null") should be(ValBoolean(true))
    eval("x.y = null") should be(ValBoolean(true))
  }

  it should "compare to not existing context entry" in {

    eval("null = {}.x") should be(ValBoolean(true))
    eval("null = {x: null}.x.y") should be(ValBoolean(true))

    eval("{}.x = null") should be(ValBoolean(true))
    eval("{x: null}.x.y = null") should be(ValBoolean(true))
  }

  "A variable name" should "not be a key-word" in {

    eval("some = true") shouldBe a[ValError]
    eval("every = true") shouldBe a[ValError]
    eval("if = true") shouldBe a[ValError]
    eval("then = true") shouldBe a[ValError]
    eval("else = true") shouldBe a[ValError]
    eval("function = true") shouldBe a[ValError]
    eval("for = true") shouldBe a[ValError]
    eval("between = true") shouldBe a[ValError]
    eval("instance = true") shouldBe a[ValError]
    eval("of = true") shouldBe a[ValError]
    eval("not = true") shouldBe a[ValError]
    eval("in = true") shouldBe a[ValError]
  }

  List(
    "something",
    "everything",
    "often",
    "orY",
    "andX",
    "trueX",
    "falseY",
    "nullOrString",
    "functionX",
    "instances",
    "forDev",
    "ifImportant",
    "thenX",
    "elseY",
    "betweenXandY",
    "notThis",
    "inside",
    "durationX",
    "dateX",
    "timeX"
  ).foreach { variableName =>
    it should s"contain a key-word ($variableName)" in {

      eval(s"$variableName = true", Map(variableName -> true)) should be(
        ValBoolean(true))
    }
  }

  "A comment" should "be written as end of line comments //" in {
    eval(""" [1,2,3][1] // the first item """) should be(ValNumber(1))
  }

  it should "be written as trailing comments /* .. */" in {
    eval(""" [1,2,3][1] /* the first item */ """) should be(ValNumber(1))
  }

  it should "be written as single line comments /* .. */" in {
    eval("""
        /* the first item */
        [1,2,3][1]
        """) should be(ValNumber(1))
  }

  it should "be written as block comments /* .. */" in {
    eval("""
        /*
         * the first item
         */
        [1,2,3][1]
        """) should be(ValNumber(1))
  }

}
