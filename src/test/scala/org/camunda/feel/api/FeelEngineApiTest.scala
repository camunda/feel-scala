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

import org.camunda.feel.FeelEngine.Failure
import org.camunda.feel.syntaxtree.ParsedExpression
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.jdk.CollectionConverters._

class FeelEngineApiTest extends AnyFlatSpec with Matchers with EitherValues {

  private val engine: FeelEngineApi = FeelEngineBuilder().build()

  "The FEEL engine API" should "parse an expression" in {
    val parseResult = engine.parseExpression("2 + x")

    parseResult.isSuccess should be(true)
    parseResult.parsedExpression shouldBe a[ParsedExpression]

    parseResult.isFailure should be(false)
    parseResult.failure should be(Failure("<success>"))

    parseResult.toEither.isRight should be(true)
    parseResult.toEither should be(Right(parseResult.parsedExpression))
  }

  it should "parse a unary-tests expression" in {
    val parseResult = engine.parseUnaryTests("< 4")

    parseResult.isSuccess should be(true)
    parseResult.parsedExpression shouldBe a[ParsedExpression]

    parseResult.isFailure should be(false)
    parseResult.failure should be(Failure("<success>"))

    parseResult.toEither.isRight should be(true)
    parseResult.toEither should be(Right(parseResult.parsedExpression))
  }

  it should "fail to parse an expression" in {
    val parseResult = engine.parseExpression("x +")

    parseResult.isSuccess should be(false)
    parseResult.parsedExpression shouldBe a[ParsedExpression]

    parseResult.isFailure should be(true)
    parseResult.failure shouldBe a[Failure]
    parseResult.failure.message should startWith("failed to parse expression")

    parseResult.toEither.isLeft should be(true)
    parseResult.toEither should be(Left(parseResult.failure))
  }

  it should "fail to parse a unary-tests expression" in {
    val parseResult = engine.parseUnaryTests("<")

    parseResult.isSuccess should be(false)
    parseResult.parsedExpression shouldBe a[ParsedExpression]

    parseResult.isFailure should be(true)
    parseResult.failure shouldBe a[Failure]
    parseResult.failure.message should startWith("failed to parse expression")

    parseResult.toEither.isLeft should be(true)
    parseResult.toEither should be(Left(parseResult.failure))
  }

  it should "evaluate a parsed expression'" in {
    val parseResult = engine.parseExpression("2 + x")
    parseResult.isSuccess should be(true)

    val evaluationResult = engine.evaluate(
      expression = parseResult.parsedExpression,
      variables = Map("x" -> 3)
    )

    evaluationResult.isSuccess should be(true)
    evaluationResult.result should be(5)

    evaluationResult.isFailure should be(false)
    evaluationResult.failure should be(Failure("<success>"))

    evaluationResult.hasSuppressedFailures should be(false)
    evaluationResult.suppressedFailures shouldBe empty

    evaluationResult.toEither.isRight should be(true)
    evaluationResult.toEither should be(Right(evaluationResult.result))
  }

  it should "evaluate a parsed unary-tests expression'" in {
    val parseResult = engine.parseUnaryTests("< 3")
    parseResult.isSuccess should be(true)

    val evaluationResult = engine.evaluateWithInput(
      expression = parseResult.parsedExpression,
      inputValue = 2
    )

    evaluationResult.isSuccess should be(true)
    evaluationResult.result shouldBe true

    evaluationResult.isFailure should be(false)
    evaluationResult.failure should be(Failure("<success>"))

    evaluationResult.hasSuppressedFailures should be(false)
    evaluationResult.suppressedFailures shouldBe empty

    evaluationResult.toEither.isRight should be(true)
    evaluationResult.toEither should be(Right(evaluationResult.result))
  }

  it should "evaluate an expression'" in {
    val evaluationResult = engine.evaluateExpression(
      expression = "2 + x",
      variables = Map("x" -> 3)
    )

    evaluationResult.isSuccess should be(true)
    evaluationResult.result should be(5)

    evaluationResult.isFailure should be(false)
    evaluationResult.failure should be(Failure("<success>"))

    evaluationResult.hasSuppressedFailures should be(false)
    evaluationResult.suppressedFailures shouldBe empty

    evaluationResult.toEither.isRight should be(true)
    evaluationResult.toEither should be(Right(evaluationResult.result))
  }

  it should "evaluate a unary-tests expression" in {
    val evaluationResult = engine.evaluateUnaryTests(
      expression = "< 3",
      inputValue = 2
    )

    evaluationResult.isSuccess should be(true)
    evaluationResult.result shouldBe true

    evaluationResult.isFailure should be(false)
    evaluationResult.failure should be(Failure("<success>"))

    evaluationResult.hasSuppressedFailures should be(false)
    evaluationResult.suppressedFailures shouldBe empty

    evaluationResult.toEither.isRight should be(true)
    evaluationResult.toEither should be(Right(evaluationResult.result))
  }

  it should "evaluate an expression and return suppressed failures" in {
    val evaluationResult = engine.evaluateExpression(
      expression = "1 + x"
    )

    evaluationResult.isSuccess should be(true)
    evaluationResult.result should matchPattern { case null => }

    evaluationResult.isFailure should be(false)
    evaluationResult.failure should be(Failure("<success>"))

    evaluationResult.hasSuppressedFailures should be(true)
    evaluationResult.suppressedFailures.exists(f =>
      f.failureType == EvaluationFailureType.NO_VARIABLE_FOUND &&
        f.failureMessage == "No variable found with name 'x'"
    ) should be(true)

    evaluationResult.toEither.isRight should be(true)
    evaluationResult.toEither should be(Right(evaluationResult.result))
  }

  it should "evaluate a unary-tests expression and return suppressed failures" in {
    val evaluationResult = engine.evaluateUnaryTests(
      expression = "> x, < 10",
      inputValue = 2
    )

    evaluationResult.isSuccess should be(true)
    evaluationResult.result shouldBe true

    evaluationResult.isFailure should be(false)
    evaluationResult.failure should be(Failure("<success>"))

    evaluationResult.hasSuppressedFailures should be(true)
    evaluationResult.suppressedFailures.exists(f =>
      f.failureType == EvaluationFailureType.NO_VARIABLE_FOUND &&
        f.failureMessage == "No variable found with name 'x'"
    ) should be(true)

    evaluationResult.toEither.isRight should be(true)
    evaluationResult.toEither should be(Right(evaluationResult.result))
  }

  "A Java consumer of the FEEL engine API" should "get the result" in {
    val javaEngine       = new FeelEngineApiJava()
    val evaluationResult =
      javaEngine.evaluateExpression("1 + x", java.util.Collections.singletonMap("x", 2))

    evaluationResult.getResult shouldBe 3

    evaluationResult.getSuppressedFailures shouldBe empty
  }

  it should "get suppressed failures" in {
    val javaEngine       = new FeelEngineApiJava()
    val evaluationResult = javaEngine.evaluateExpression("1 + x", java.util.Collections.emptyMap())

    evaluationResult.getResult shouldBe null

    evaluationResult.getSuppressedFailures.asScala.exists(f =>
      f.failureType == EvaluationFailureType.NO_VARIABLE_FOUND &&
        f.failureMessage == "No variable found with name 'x'"
    ) should be(true)
  }

  it should "get a failure message" in {
    val javaEngine = new FeelEngineApiJava()

    val exception = intercept[RuntimeException](
      javaEngine.evaluateExpression("x +", java.util.Collections.emptyMap())
    )

    exception.getMessage should startWith("failed to parse expression")
  }

}
