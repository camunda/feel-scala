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
package org.camunda.feel.example

import org.scalatest.{FlatSpec, Matchers}

class BasicFeelEngineFactoryTest
    extends FlatSpec
    with Matchers
    with DmnEvaluationTest {

  val DMN_DT_SIMPLE_UNARY_TEST = "/basic/simpleUnaryTest.dmn"
  val DMN_DT_OUTPUT_EXPRESSION = "/basic/outputExpression.dmn"
  val DMN_LITERAL_EXPRESSION = "/basic/literalExpression.dmn"

  "The DMN engine" should "evaluate a decision table with a simple unary test" in {

    val result =
      evaluateDecision(DMN_DT_SIMPLE_UNARY_TEST, "decision", Map("score" -> 92))

    result.size should be(1)
    result.getSingleResult.getSingleEntry.asInstanceOf[String] should be("cool")
  }

  it should "evaluate a decision table with an output expression" in {

    val result = evaluateDecision(DMN_DT_OUTPUT_EXPRESSION,
                                  "decision",
                                  Map(
                                    "op" -> "add",
                                    "a" -> 2,
                                    "b" -> 3
                                  ))

    result.size should be(1)
    result.getSingleResult.getSingleEntry.asInstanceOf[Int] should be(5)
  }

  it should "evaluate a decision literal expression" in {

    val result = evaluateDecision(DMN_LITERAL_EXPRESSION,
                                  "decision",
                                  Map(
                                    "a" -> 4,
                                    "b" -> 5
                                  ))

    result.size should be(1)
    result.getSingleResult.getSingleEntry.asInstanceOf[Int] should be(9)
  }

}
