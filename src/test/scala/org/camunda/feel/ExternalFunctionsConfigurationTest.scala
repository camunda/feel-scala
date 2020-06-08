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
package org.camunda.feel

import org.camunda.feel.FeelEngine.{Configuration, Failure}
import org.camunda.feel.syntaxtree.ParsedExpression
import org.scalatest.{FlatSpec, Matchers}

class ExternalFunctionsConfigurationTest extends FlatSpec with Matchers {

  val defaultEngine = new FeelEngine()

  val engineWithEnabledFunctions = new FeelEngine(
    configuration = Configuration(externalFunctionsEnabled = true))

  val externalFunctionInvocation =
    """{
        f: function(x) external { java: { class: "java.lang.Math", method signature: "abs(long)" } },
        call: f(-1)
        }.call"""

  val parsedExternalFunctionInvocation = engineWithEnabledFunctions
    .parseExpression(externalFunctionInvocation)
    .getOrElse(???)

  val disabledExternalFunctionFailure = Failure(
    s"validation of expression '$externalFunctionInvocation' failed: " +
      "External functions are disabled. Use the FunctionProvider SPI (recommended) or enable external function in the configuration.")

  val invocationResult = 1

  "A (default) FeelEngine" should "fail to parse an external function" in {

    defaultEngine.parseExpression(externalFunctionInvocation) should be(
      Left(disabledExternalFunctionFailure))
  }

  it should "fail to evaluate an external function" in {

    defaultEngine.evalExpression(externalFunctionInvocation) should be(
      Left(disabledExternalFunctionFailure))
  }

  it should "fail to evaluate a parsed external function" in {

    defaultEngine.eval(parsedExternalFunctionInvocation) should be(
      Left(disabledExternalFunctionFailure))
  }

  "A FEEL engine with enabled external functions" should "parse an external function" in {

    engineWithEnabledFunctions.parseExpression(externalFunctionInvocation) shouldBe a[
      Right[_, ParsedExpression]]
  }

  it should "evaluate an external function" in {

    engineWithEnabledFunctions.evalExpression(externalFunctionInvocation) should be(
      Right(invocationResult))
  }

  it should "evaluate a parsed external function" in {

    engineWithEnabledFunctions.eval(parsedExternalFunctionInvocation) should be(
      Right(invocationResult))
  }

}
