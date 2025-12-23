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
import org.scalatest.concurrent.{Signaler, ThreadSignaler, TimeLimitedTests}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Seconds, Span}
import org.scalatest.TryValues

import scala.util.Try

// Wrap interpreter executions in Try so the ThreadSignaler can interrupt cleanly on timeouts
// (otherwise an exception during evaluation can leave the time-limited test process hanging).

/** Performance tests to detect O(n²) regressions in list operations.
  *
  * These tests use large lists (100K+ elements) and set time limits to catch performance
  * regressions. If list operations use O(n) append instead of O(1), these tests will timeout.
  */
class ListPerformanceTest
    extends AnyFlatSpec
    with Matchers
    with TryValues
    with FeelEngineTest
    with EvaluationResultMatchers
    with TimeLimitedTests {

  // Each test must complete within 10 seconds
  override val timeLimit                     = Span(10, Seconds)
  // Interrupt the test thread on timeout
  override val defaultTestSignaler: Signaler = ThreadSignaler

  val listSize = 1_000_000

  "A distinct values function" should "handle large lists efficiently" in {
    // Generate list with duplicates: [1,1,2,2,3,3,...,n,n]
    val listWithDuplicates = (1 to listSize).flatMap(i => List(i, i)).toVector

    val result = Try(evaluateExpression("distinct values(xs)", Map("xs" -> listWithDuplicates)))

    result.success.value should returnResult((1 to listSize).toVector)
  }

  "An index of function" should "handle large lists with many matches efficiently" in {
    // List with repeated value to find multiple indices
    val listOfOnes = List.fill(listSize)(1)

    val result = Try(evaluateExpression("index of(xs, 1)", Map("xs" -> listOfOnes)))

    result.success.value should returnResult((1 to listSize).toList)
  }

  "A union function" should "handle large lists efficiently" in {
    val list1 = (1 to listSize).toList
    val list2 = (listSize / 2 to listSize + listSize / 2).toList

    val result = Try(evaluateExpression("union(xs, ys)", Map("xs" -> list1, "ys" -> list2)))

    // union should contain distinct values from both lists
    result.success.value should returnResult((list1 ++ list2).distinct)
  }

  "A for loop building a list" should "handle many iterations efficiently" in {
    val result = Try(evaluateExpression("for i in 1..n return i * 2", Map("n" -> listSize)))

    result.success.value should returnResult((1 to listSize).map(_ * 2).toList)
  }

  "Nested for loops" should "handle moderate sizes efficiently" in {
    // 300 x 300 = 90,000 total iterations
    val outerSize = 300
    val innerSize = 300

    val result = Try(
      evaluateExpression(
        "for i in 1..outer return for j in 1..inner return i * j",
        Map("outer" -> outerSize, "inner" -> innerSize)
      )
    )

    val expected =
      (1 to outerSize).map(i => (1 to innerSize).map(j => i * j))

    result.success.value should returnResult(expected)
  }

  "A flatten function" should "handle nested lists efficiently" in {
    // Create [[1],[2],[3],...,[n]]
    val nestedList = (1 to listSize).map(i => List(i)).toList

    val result = Try(evaluateExpression("flatten(xs)", Map("xs" -> nestedList)))

    result.success.value should returnResult((1 to listSize).toList)
  }

  "A filter expression" should "handle large lists efficiently" ignore {
    val list = (1 to listSize).toList

    val result = Try(evaluateExpression("xs[item > n]", Map("xs" -> list, "n" -> (listSize / 2))))

    result.success.value should returnResult((listSize / 2 + 1 to listSize).toList)
  }

  "Collecting evaluation failures" should "handle many failures efficiently" in {
    // Expression that generates many suppressed failures
    val iterations = 100000
    // Each iteration tries to access a non-existing variable, generating a failure
    val result     = Try(
      evaluateExpression(
        "for i in 1..n return if nonExistingVar = null then i else 0",
        Map("n" -> iterations)
      )
    )

    // ensure failures are surfaced as test failures
    result.success.value
  }

  "Append function" should "handle large lists efficiently" in {
    val list = (1 to listSize).toList

    val result = Try(
      evaluateExpression(
        "append(xs, a, b, c)",
        Map("xs" -> list, "a" -> (listSize + 1), "b" -> (listSize + 2), "c" -> (listSize + 3))
      )
    )

    result.success.value should returnResult((1 to (listSize + 3)).toList)
  }

  "Concatenate function" should "handle multiple large lists efficiently" in {
    val list1 = (1 to listSize).toList
    val list2 = (listSize + 1 to listSize * 2).toList
    val list3 = (listSize * 2 + 1 to listSize * 3).toList

    val result = Try(
      evaluateExpression(
        "concatenate(xs, ys, zs)",
        Map("xs" -> list1, "ys" -> list2, "zs" -> list3)
      )
    )

    result.success.value should returnResult((1 to listSize * 3).toList)
  }
}
