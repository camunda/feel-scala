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

/** Performance tests to detect O(n²) regressions in list operations.
  *
  * These tests use large lists (100K+ elements) and set time limits to catch performance
  * regressions. If list operations use O(n) append instead of O(1), these tests will timeout.
  */
class ListPerformanceTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

  // Time limit for operations - should complete well under this with O(n) complexity
  // With O(n²) complexity, these would take much longer
  val timeLimitMs = 30000

  "A distinct values function" should "handle large lists efficiently" in {
    // Generate list with duplicates: [1,1,2,2,3,3,...,n,n]
    val listSize           = 10000
    val listWithDuplicates = (1 to listSize).flatMap(i => Seq(i, i)).toList

    val start   = System.currentTimeMillis()
    val result  = evaluateExpression("distinct values(xs)", Map("xs" -> listWithDuplicates))
    val elapsed = System.currentTimeMillis() - start

    result should returnResult((1 to listSize).toList)
    elapsed should be < timeLimitMs.toLong
  }

  "An index of function" should "handle large lists with many matches efficiently" in {
    // List with repeated value to find multiple indices
    val listSize   = 100000
    val listOfOnes = List.fill(listSize)(1)

    val start   = System.currentTimeMillis()
    val result  = evaluateExpression("index of(xs, 1)", Map("xs" -> listOfOnes))
    val elapsed = System.currentTimeMillis() - start

    result should returnResult((1 to listSize).toList)
    elapsed should be < timeLimitMs.toLong
  }

  "A union function" should "handle large lists efficiently" in {
    val listSize = 100000
    val list1    = (1 to listSize).toList
    val list2    = (listSize / 2 to listSize + listSize / 2).toList

    val start   = System.currentTimeMillis()
    val result  = evaluateExpression("union(xs, ys)", Map("xs" -> list1, "ys" -> list2))
    val elapsed = System.currentTimeMillis() - start

    // union should contain distinct values from both lists
    elapsed should be < timeLimitMs.toLong
  }

  "A for loop building a list" should "handle many iterations efficiently" in {
    val iterations = 100000

    val start   = System.currentTimeMillis()
    val result  = evaluateExpression("for i in 1..n return i * 2", Map("n" -> iterations))
    val elapsed = System.currentTimeMillis() - start

    result should returnResult((1 to iterations).map(_ * 2).toList)
    elapsed should be < timeLimitMs.toLong
  }

  "Nested for loops" should "handle moderate sizes efficiently" in {
    // 300 x 300 = 90,000 total iterations
    val outerSize = 300
    val innerSize = 300

    val start   = System.currentTimeMillis()
    val result  = evaluateExpression(
      "for i in 1..outer return for j in 1..inner return i * j",
      Map("outer" -> outerSize, "inner" -> innerSize)
    )
    val elapsed = System.currentTimeMillis() - start

    elapsed should be < timeLimitMs.toLong
  }

  "A flatten function" should "handle deeply nested lists efficiently" in {
    val listSize   = 100000
    // Create [[1],[2],[3],...,[n]]
    val nestedList = (1 to listSize).map(i => List(i)).toList

    val start   = System.currentTimeMillis()
    val result  = evaluateExpression("flatten(xs)", Map("xs" -> nestedList))
    val elapsed = System.currentTimeMillis() - start

    result should returnResult((1 to listSize).toList)
    elapsed should be < timeLimitMs.toLong
  }

  "A filter expression" should "handle large lists efficiently" in {
    val listSize = 100000
    val list     = (1 to listSize).toList

    val start   = System.currentTimeMillis()
    val result  = evaluateExpression("xs[item > n]", Map("xs" -> list, "n" -> (listSize / 2)))
    val elapsed = System.currentTimeMillis() - start

    result should returnResult((listSize / 2 + 1 to listSize).toList)
    elapsed should be < timeLimitMs.toLong
  }

  "Collecting evaluation failures" should "handle many failures efficiently" in {
    // Expression that generates many suppressed failures
    val iterations = 100000
    val start      = System.currentTimeMillis()
    // Each iteration tries to access a non-existing variable, generating a failure
    val result     = evaluateExpression(
      "for i in 1..n return if nonExistingVar = null then i else 0",
      Map("n" -> iterations)
    )
    val elapsed    = System.currentTimeMillis() - start

    elapsed should be < timeLimitMs.toLong
  }

  "Append function" should "handle large lists efficiently" in {
    val listSize = 100000
    val list     = (1 to listSize).toList

    val start   = System.currentTimeMillis()
    val result  = evaluateExpression(
      "append(xs, a, b, c)",
      Map("xs" -> list, "a" -> (listSize + 1), "b" -> (listSize + 2), "c" -> (listSize + 3))
    )
    val elapsed = System.currentTimeMillis() - start

    result should returnResult((1 to (listSize + 3)).toList)
    elapsed should be < timeLimitMs.toLong
  }

  "Concatenate function" should "handle multiple large lists efficiently" in {
    val listSize = 100000
    val list1    = (1 to listSize).toList
    val list2    = (listSize + 1 to listSize * 2).toList
    val list3    = (listSize * 2 + 1 to listSize * 3).toList

    val start   = System.currentTimeMillis()
    val result  = evaluateExpression(
      "concatenate(xs, ys, zs)",
      Map("xs" -> list1, "ys" -> list2, "zs" -> list3)
    )
    val elapsed = System.currentTimeMillis() - start

    result should returnResult((1 to listSize * 3).toList)
    elapsed should be < timeLimitMs.toLong
  }
}
