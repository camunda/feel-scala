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
  * These tests use moderately large lists and set time limits to catch performance regressions. If
  * list operations use O(n) append instead of O(1), these tests will timeout.
  */
class ListPerformanceTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

  // Time limit for operations - should complete well under this with O(n) complexity
  // With O(n²) complexity, these would take much longer
  val timeLimitMs = 5000

  "A distinct values function" should "handle large lists efficiently" in {
    // Generate list with duplicates: [1,1,2,2,3,3,...,500,500]
    val listSize = 500
    val listExpr = (1 to listSize).flatMap(i => Seq(i, i)).mkString("[", ",", "]")

    val start   = System.currentTimeMillis()
    val result  = evaluateExpression(s"distinct values($listExpr)")
    val elapsed = System.currentTimeMillis() - start

    result should returnResult((1 to listSize).toList)
    elapsed should be < timeLimitMs.toLong
  }

  "An index of function" should "handle large lists with many matches efficiently" in {
    // List with repeated value to find multiple indices
    val listSize = 1000
    val listExpr = (1 to listSize).map(_ => 1).mkString("[", ",", "]")

    val start   = System.currentTimeMillis()
    val result  = evaluateExpression(s"index of($listExpr, 1)")
    val elapsed = System.currentTimeMillis() - start

    result should returnResult((1 to listSize).toList)
    elapsed should be < timeLimitMs.toLong
  }

  "A union function" should "handle large lists efficiently" in {
    val listSize = 300
    val list1    = (1 to listSize).mkString("[", ",", "]")
    val list2    = (listSize / 2 to listSize + listSize / 2).mkString("[", ",", "]")

    val start   = System.currentTimeMillis()
    val result  = evaluateExpression(s"union($list1, $list2)")
    val elapsed = System.currentTimeMillis() - start

    // union should contain distinct values from both lists
    elapsed should be < timeLimitMs.toLong
  }

  "A for loop building a list" should "handle many iterations efficiently" in {
    val iterations = 1000

    val start   = System.currentTimeMillis()
    val result  = evaluateExpression(s"for i in 1..$iterations return i * 2")
    val elapsed = System.currentTimeMillis() - start

    result should returnResult((1 to iterations).map(_ * 2).toList)
    elapsed should be < timeLimitMs.toLong
  }

  "Nested for loops" should "handle moderate sizes efficiently" in {
    val outerSize = 50
    val innerSize = 50

    val start   = System.currentTimeMillis()
    val result  =
      evaluateExpression(s"for i in 1..$outerSize return for j in 1..$innerSize return i * j")
    val elapsed = System.currentTimeMillis() - start

    elapsed should be < timeLimitMs.toLong
  }

  "A flatten function" should "handle deeply nested lists efficiently" in {
    val listSize   = 100
    // Create [[1],[2],[3],...,[100]]
    val nestedList = (1 to listSize).map(i => s"[$i]").mkString("[", ",", "]")

    val start   = System.currentTimeMillis()
    val result  = evaluateExpression(s"flatten($nestedList)")
    val elapsed = System.currentTimeMillis() - start

    result should returnResult((1 to listSize).toList)
    elapsed should be < timeLimitMs.toLong
  }

  "A filter expression" should "handle large lists efficiently" in {
    val listSize = 1000
    val listExpr = (1 to listSize).mkString("[", ",", "]")

    val start   = System.currentTimeMillis()
    val result  = evaluateExpression(s"$listExpr[item > 500]")
    val elapsed = System.currentTimeMillis() - start

    result should returnResult((501 to listSize).toList)
    elapsed should be < timeLimitMs.toLong
  }

  "Collecting evaluation failures" should "handle many failures efficiently" in {
    // Expression that generates many suppressed failures
    val iterations = 500
    val start      = System.currentTimeMillis()
    // Each iteration tries to access a non-existing variable, generating a failure
    val result     = evaluateExpression(
      s"for i in 1..$iterations return if nonExistingVar = null then i else 0"
    )
    val elapsed    = System.currentTimeMillis() - start

    elapsed should be < timeLimitMs.toLong
  }

  "Append function" should "handle large lists efficiently" in {
    val listSize = 500
    val listExpr = (1 to listSize).mkString("[", ",", "]")

    val start   = System.currentTimeMillis()
    val result  = evaluateExpression(s"append($listExpr, 501, 502, 503)")
    val elapsed = System.currentTimeMillis() - start

    result should returnResult((1 to 503).toList)
    elapsed should be < timeLimitMs.toLong
  }

  "Concatenate function" should "handle multiple large lists efficiently" in {
    val listSize = 300
    val list1    = (1 to listSize).mkString("[", ",", "]")
    val list2    = (listSize + 1 to listSize * 2).mkString("[", ",", "]")
    val list3    = (listSize * 2 + 1 to listSize * 3).mkString("[", ",", "]")

    val start   = System.currentTimeMillis()
    val result  = evaluateExpression(s"concatenate($list1, $list2, $list3)")
    val elapsed = System.currentTimeMillis() - start

    result should returnResult((1 to listSize * 3).toList)
    elapsed should be < timeLimitMs.toLong
  }
}
