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

import fastparse.Parsed
import org.camunda.feel.FeelEngine.Failure
import org.camunda.feel.api.EvaluationFailureType.FUNCTION_INVOCATION_FAILURE
import org.camunda.feel.impl.interpreter.{EvalContext, FeelInterpreter}
import org.camunda.feel.impl.parser.FeelParser
import org.camunda.feel.syntaxtree._
import org.camunda.feel.valuemapper.ValueMapper
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ExternalFunctionsConfigurationTest extends AnyFlatSpec with Matchers {

  private val defaultEngine: FeelEngineApi = FeelEngineBuilder().build()

  private val engineWithEnabledFunctions =
    FeelEngineBuilder().withEnabledExternalFunctions(true).build()

  val externalFunctionInvocation =
    """{
        f: function(x) external { java: { class: "java.lang.Math", method signature: "abs(long)" } },
        call: f(-1)
        }.call"""

  val parsedExternalFunction: ParsedExpression =
    FeelParser.parseExpression(externalFunctionInvocation) match {
      case success: Parsed.Success[Exp] =>
        ParsedExpression(success.value, externalFunctionInvocation)
      case failure                      => fail(s"Failed to parse expression: $failure")
    }

  val validationFailure = Failure(
    s"validation of expression '$externalFunctionInvocation' failed: External Java functions are not supported."
  )

  val invocationResult = 1

  "A (default) FeelEngine" should "fail to parse an external function" in {

    val result = defaultEngine.parseExpression(externalFunctionInvocation)

    result.isFailure should be(true)
    result.failure should be(validationFailure)
  }

  it should "fail to evaluate an external function" in {

    val result = defaultEngine.evaluateExpression(externalFunctionInvocation)

    result.isFailure should be(true)
    result.failure should be(validationFailure)
  }

  it should "fail to evaluate a parsed external function" in {

    val result = defaultEngine.evaluate(parsedExternalFunction)

    result.isFailure should be(true)
    result.failure should be(validationFailure)
  }

  "A FEEL engine with enabled external functions" should "fail to parse an external function (for security reasons)" in {

    val result = engineWithEnabledFunctions.parseExpression(externalFunctionInvocation)

    result.isFailure should be(true)
    result.failure should be(validationFailure)
  }

  it should "fail to evaluate an external function (for security reasons)" in {

    val result = engineWithEnabledFunctions.evaluateExpression(externalFunctionInvocation)

    result.isFailure should be(true)
    result.failure should be(validationFailure)
  }

  it should "fail to evaluate a parsed external function (for security reasons)" in {

    val result = engineWithEnabledFunctions.evaluate(parsedExternalFunction)

    result.isFailure should be(true)
    result.failure should be(validationFailure)
  }

  "The FEEL engine interpreter" should "evaluate an external function to null (for security reasons)" in {

    // Bypass the engine to verify the security gate inside the interpreter
    val interpreter = new FeelInterpreter(ValueMapper.defaultValueMapper)
    val evalContext = EvalContext.empty(ValueMapper.defaultValueMapper)

    val result = interpreter.eval(parsedExternalFunction.expression)(evalContext)

    result should matchPattern { case ValNull => }

    evalContext.failureCollector.failures contains EvaluationFailure(
      failureType = FUNCTION_INVOCATION_FAILURE,
      failureMessage = "External Java functions are not supported."
    )
  }

}
