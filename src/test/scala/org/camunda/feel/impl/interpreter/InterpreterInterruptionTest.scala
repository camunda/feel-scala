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

import org.camunda.feel.impl.{EvaluationResultMatchers, FeelEngineTest}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.lang.management.{ManagementFactory, ThreadInfo}
import java.util.concurrent.{Executors}
import concurrent.{ExecutionContext}

/** @author
  *   Victor Mosin
  */
class InterpreterInterruptionTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

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
    Thread.sleep(1000) // Let evaluation start
    thread.interrupt()
    countDownLatch.await()

    assert(!threadDump.contains("FeelInterpreter"))
  }

  /** Dumps all threads to a string Adapted from
    * https://crunchify.com/how-to-generate-java-thread-dump-programmatically/
    */
  private def threadDump: String = {
    val dump                           = new StringBuilder
    val threadMXBean                   = ManagementFactory.getThreadMXBean
    val threadInfos: Array[ThreadInfo] =
      threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds, 100)
    for (threadInfo <- threadInfos) {
      dump.append('"')
      dump.append(threadInfo.getThreadName)
      dump.append("\" ")
      val state: Thread.State                          = threadInfo.getThreadState
      dump.append("\n   java.lang.Thread.State: ")
      dump.append(state)
      val stackTraceElements: Array[StackTraceElement] = threadInfo.getStackTrace
      for (stackTraceElement <- stackTraceElements) {
        dump.append("\n        at ")
        dump.append(stackTraceElement)
      }
      dump.append("\n\n")
    }
    dump.toString
  }
}
