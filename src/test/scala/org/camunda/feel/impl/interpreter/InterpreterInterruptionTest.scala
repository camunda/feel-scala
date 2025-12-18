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

import org.camunda.feel.impl.FeelEngineTest
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.concurrent.{Executors, TimeUnit}
import concurrent.ExecutionContext

/** @author
  *   Victor Mosin
  */
class InterpreterInterruptionTest extends AnyFlatSpec with Matchers with FeelEngineTest {

  protected implicit val context =
    ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())

  "A long-running evaluation invocation" should "be interrupted after X time" in {
    val countDownLatch = new java.util.concurrent.CountDownLatch(1)
    val thread         = new Thread {
      override def run {
        try {
          evaluateExpression(
            expression = "count(for x in 1..(2 ** 16) return {\"power\": 2 ** x}) > 0"
          )
        } catch {
          case _: InterruptedException =>
            countDownLatch.countDown()
        }
      }
    }

    thread.start()
    Thread.sleep(100) // Let evaluation start
    thread.interrupt()
    val wasInterrupted = countDownLatch.await(1, TimeUnit.SECONDS)

    wasInterrupted should be(true)
  }

  it should "be interrupted while materializing a large iteration range" in {
    val countDownLatch = new java.util.concurrent.CountDownLatch(1)

    val thread = new Thread {
      override def run {
        try {
          // This interrupts inside IterationContext (1..N) materialization.
          // Without cooperative interrupt checks, this can run for a long time even after interrupt.
          // Use a range large enough to be "hot" but avoid risking OOM.
          evaluateExpression(expression = "for x in 1..(2 ** 24) return x")
        } catch {
          case _: InterruptedException =>
            countDownLatch.countDown()
        }
      }
    }

    thread.start()
    Thread.sleep(50) // Let evaluation start
    thread.interrupt()
    val wasInterrupted = countDownLatch.await(1, TimeUnit.SECONDS)

    wasInterrupted should be(true)
  }

  it should "be interrupted while expanding a large cartesian product" in {
    val countDownLatch = new java.util.concurrent.CountDownLatch(1)

    val thread = new Thread {
      override def run {
        try {
          // This interrupts inside cartesian product expansion (flattenAndZipLists).
          // This size intentionally creates a large eager expansion; the goal is to ensure
          // interruption is checked inside the nested expansion loops.
          evaluateExpression(expression = "for x in 1..1500, y in 1..1500 return x + y")
        } catch {
          case _: InterruptedException =>
            countDownLatch.countDown()
        }
      }
    }

    thread.start()
    Thread.sleep(50) // Let evaluation start
    thread.interrupt()
    val wasInterrupted = countDownLatch.await(1, TimeUnit.SECONDS)

    wasInterrupted should be(true)
  }
}
