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

import org.camunda.feel.FeelEngine
import org.camunda.feel.FeelEngine.{Failure, UnaryTests}
import org.camunda.feel.syntaxtree.ParsedExpression
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.{EitherValues}

/**
  * @author Philipp Ossler
  */
class FeelEngineTest extends AnyFlatSpec with Matchers with EitherValues {

  val engine = new FeelEngine

  "A FeelEngine" should "evaluate a unaryTests '< 3'" in {

    engine.evalUnaryTests(
      "< 3",
      variables = Map(UnaryTests.defaultInputVariable -> 2)) should be(
      Right(true))
    engine.evalUnaryTests(
      "< 3",
      variables = Map(UnaryTests.defaultInputVariable -> 3)) should be(
      Right(false))
  }

  it should "evaluate a expression '2+4'" in {

    engine.evalExpression("2+4") should be(Right(6))
  }

  it should "evaluate an unaryTest with custom input variable name" in {

    engine.evalUnaryTests("< 3",
                          variables = Map(
                            "myInput" -> 2,
                            UnaryTests.inputVariable -> "myInput")) should be(
      Right(true)
    )
    engine.evalUnaryTests("< 3",
                          variables = Map(
                            "myInput" -> 3,
                            UnaryTests.inputVariable -> "myInput")) should be(
      Right(false)
    )
  }

  it should "fail evaluation because of wrong type" in {

    engine.evalUnaryTests(
      "< 3",
      variables = Map(UnaryTests.defaultInputVariable -> "2")) should be(
      Left(Failure(
        "failed to evaluate expression '< 3': ValString(2) can not be compared to ValNumber(3)"))
    )
  }

  it should "fail evaluation because of missing input" in {

    engine.evalUnaryTests("< 3", variables = Map[String, Any]()) should be(
      Left(Failure(
        "failed to evaluate expression '< 3': no variable found for name 'cellInput'"))
    )
  }

  it should "fail while parsing '<'" in {
    engine
      .evalUnaryTests("<", variables = Map[String, Any]())
      .left
      .value
      .message should startWith(
      "failed to parse expression '<'"
    )
  }

  it should "parse an expression 'x'" in {
    val expr = engine.parseExpression("x + 1")

    expr shouldBe a[Right[_, ParsedExpression]]
    engine.eval(expr.value, Map("x" -> 3)) should be(Right(4))
  }

  it should "fail to parse an expression 'x+'" in {
    engine.parseExpression("x+").left.value.message should startWith(
      "failed to parse expression 'x+'")
  }

  it should "parse an unaryTests '< 3'" in {
    val expr = engine.parseUnaryTests("< 3")

    expr shouldBe a[Right[_, ParsedExpression]]
    engine.eval(expr.value, Map(UnaryTests.defaultInputVariable -> 2)) should be(
      Right(true))
  }

  it should "fail to parse an unaryTests '<'" in {
    engine.parseUnaryTests("<").left.value.message should startWith(
      "failed to parse expression '<'")
  }

}
