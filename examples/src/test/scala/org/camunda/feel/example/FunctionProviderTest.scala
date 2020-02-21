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

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class FunctionProviderTest
    extends FlatSpec
    with Matchers
    with DmnEvaluationTest {

  val DMN_FILE = "/function/outputEntryWithFunction.dmn"

  "The decision table" should "invoke a custom scala function" in {

    val result =
      evaluateDecision(DMN_FILE, "decision", Map("status" -> "green"))

    result.size should be(1)
    result.getSingleEntry.asInstanceOf[Int] should be(3)
  }

  it should "invoke a custom java function" in {

    val result =
      evaluateDecision(DMN_FILE, "decision", Map("status" -> "yellow"))

    result.size should be(1)
    result.getSingleEntry.asInstanceOf[Int] should be(2)
  }

  it should "invoke a custom scala function with input variable" in {

    val result =
      evaluateDecision(DMN_FILE, "decision", Map("status" -> "black"))

    result.size should be(1)
    result.getSingleEntry.asInstanceOf[Int] should be(14)
  }

}
