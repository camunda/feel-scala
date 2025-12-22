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
package org.camunda.feel.performance

import org.camunda.feel.api.EvaluationFailureType
import org.camunda.feel.impl.interpreter.EvaluationFailureCollector
import org.scalatest.concurrent.{Signaler, ThreadSignaler, TimeLimitedTests}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Seconds, Span}

import scala.util.Try

class SuppressedFailuresPerformanceTest extends AnyFlatSpec with Matchers with TimeLimitedTests {

  // Each test must complete within 5 seconds
  override val timeLimit                     = Span(5, Seconds)
  // Interrupt the test thread on timeout
  override val defaultTestSignaler: Signaler = ThreadSignaler

  "The failure collector" should "accumulate a large number of errors in linear time" in {

    val errorCount = 1_000_000
    val collector  = new EvaluationFailureCollector()

    Try {
      for (i <- 1L to errorCount) {
        collector.addFailure(EvaluationFailureType.NO_VARIABLE_FOUND, s"x$i")

        if (Thread.interrupted()) throw new InterruptedException()
      }

      collector.failures should have size errorCount
    }
  }
}
