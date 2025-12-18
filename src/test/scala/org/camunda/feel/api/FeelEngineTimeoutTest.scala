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

import org.camunda.feel.context.{Context, FunctionProvider}
import org.camunda.feel.syntaxtree.ValFunction
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.concurrent.atomic.AtomicBoolean

import scala.concurrent.duration._

class FeelEngineTimeoutTest extends AnyFlatSpec with Matchers {

  private def sleepFunction(interrupted: AtomicBoolean, sleepMillis: Long): ValFunction =
    ValFunction(
      params = List("ms"),
      invoke = _ => {
        try {
          Thread.sleep(sleepMillis)
          123
        } catch {
          case _: InterruptedException =>
            interrupted.set(true)
            throw new InterruptedException()
        }
      }
    )

  "The FEEL engine API" should "interrupt evaluation and return a timeout failure" in {
    val interrupted = new AtomicBoolean(false)

    val functionProvider = FunctionProvider.StaticFunctionProvider(
      functions = Map("sleep" -> List(sleepFunction(interrupted, sleepMillis = 5_000)))
    )

    val api = FeelEngineBuilder()
      .withFunctionProvider(functionProvider)
      .buildWithEvaluationTimeout(50.millis)

    val result = api.evaluateExpression("sleep(5000)", Context.EmptyContext)

    result.isFailure shouldBe true
    result.failure.message should include("timed out")

    val deadline = System.currentTimeMillis() + 1000
    while (!interrupted.get() && System.currentTimeMillis() < deadline) {
      Thread.sleep(10)
    }

    interrupted.get() shouldBe true
  }

  it should "support a per-call timeout override" in {
    val interrupted = new AtomicBoolean(false)

    val functionProvider = FunctionProvider.StaticFunctionProvider(
      functions = Map("sleep" -> List(sleepFunction(interrupted, sleepMillis = 5_000)))
    )

    val api = FeelEngineBuilder()
      .withFunctionProvider(functionProvider)
      .build()

    val result = api.evaluateExpression("sleep(5000)", Context.EmptyContext, 50.millis)

    result.isFailure shouldBe true
    result.failure.message should include("timed out")

    val deadline = System.currentTimeMillis() + 1000
    while (!interrupted.get() && System.currentTimeMillis() < deadline) {
      Thread.sleep(10)
    }

    interrupted.get() shouldBe true
  }

  it should "support a java.time.Duration timeout override" in {
    val interrupted = new AtomicBoolean(false)

    val functionProvider = FunctionProvider.StaticFunctionProvider(
      functions = Map("sleep" -> List(sleepFunction(interrupted, sleepMillis = 5_000)))
    )

    val api = FeelEngineBuilder()
      .withFunctionProvider(functionProvider)
      .build()

    val result = api.evaluateExpression(
      "sleep(5000)",
      Context.EmptyContext,
      java.time.Duration.ofMillis(50)
    )

    result.isFailure shouldBe true
    result.failure.message should include("timed out")

    val deadline = System.currentTimeMillis() + 1000
    while (!interrupted.get() && System.currentTimeMillis() < deadline) {
      Thread.sleep(10)
    }

    interrupted.get() shouldBe true
  }

  it should "not apply a timeout when not configured" in {
    val interrupted = new AtomicBoolean(false)

    val functionProvider = FunctionProvider.StaticFunctionProvider(
      functions = Map("sleep" -> List(sleepFunction(interrupted, sleepMillis = 20)))
    )

    val api = FeelEngineBuilder()
      .withFunctionProvider(functionProvider)
      .build()

    val result = api.evaluateExpression("sleep(20)", Context.EmptyContext)

    result.isSuccess shouldBe true
    result.result shouldBe 123
    interrupted.get() shouldBe false
  }

  it should "apply a timeout to evaluateWithInput" in {
    val interrupted = new AtomicBoolean(false)

    val functionProvider = FunctionProvider.StaticFunctionProvider(
      functions = Map("sleep" -> List(sleepFunction(interrupted, sleepMillis = 5_000)))
    )

    val api = FeelEngineBuilder()
      .withFunctionProvider(functionProvider)
      .buildWithEvaluationTimeout(50.millis)

    val parseResult = api.parseExpression("sleep(5000)")
    parseResult.isSuccess shouldBe true

    val result = api.evaluateWithInput(
      expression = parseResult.parsedExpression,
      inputValue = 1,
      context = Context.EmptyContext
    )

    result.isFailure shouldBe true
    result.failure.message should include("timed out")

    val deadline = System.currentTimeMillis() + 1000
    while (!interrupted.get() && System.currentTimeMillis() < deadline) {
      Thread.sleep(10)
    }

    interrupted.get() shouldBe true
  }

  it should "time out an evaluation that materializes a huge iteration range" in {
    val api = FeelEngineBuilder().build()

    // Materializing 1..(2 ** 20) creates a large list and should reliably exceed a small timeout.
    // This exercises the cooperative interrupt checks added to IterationContext range building.
    val result = api.evaluateExpression(
      "for x in 1..(2 ** 20) return x",
      Context.EmptyContext,
      20.millis
    )

    result.isFailure shouldBe true
    result.failure.message should include("timed out")

    // Sanity: the API should still be usable after a timeout.
    api.evaluateExpression("1+1", Context.EmptyContext).result shouldBe 2
  }

  it should "time out an evaluation that expands a large cartesian product" in {
    val api = FeelEngineBuilder().build()

    // The cartesian product is expanded eagerly before iterating, so keep the size large enough
    // to exceed the timeout but not so large that it risks OOM in case of regressions.
    // This exercises the cooperative interrupt checks added to cartesian product expansion.
    val result = api.evaluateExpression(
      "for x in 1..400, y in 1..400 return x + y",
      Context.EmptyContext,
      20.millis
    )

    result.isFailure shouldBe true
    result.failure.message should include("timed out")

    api.evaluateExpression("2+3", Context.EmptyContext).result shouldBe 5
  }
}
