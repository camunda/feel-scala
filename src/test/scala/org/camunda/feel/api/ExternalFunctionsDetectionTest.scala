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

import org.camunda.feel.FeelEngine
import org.camunda.feel.FeelEngine.Failure
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ExternalFunctionsDetectionTest extends AnyFlatSpec with Matchers {

  private val externalFunctionDefinition =
    """function() external { java: { class: "com.example.Hello", method signature: "greeting(string)" } }"""

  private val engine = new FeelEngine()

  "A (default) FeelEngine" should "fail to parse an external function in context" in {

    val r = engine.parseExpression(s"""{f: $externalFunctionDefinition}""")

    r.isLeft should be(true)
    r.left.get.message should include("External functions are disabled")
  }

  it should "fail to parse an external function inside a function with positional arguments" in {

    val r = engine.parseExpression(s"""sort([], $externalFunctionDefinition)""")

    r.isLeft should be(true)
    r.left.get.message should include("External functions are disabled")
  }

  it should "fail to parse an external function inside a function with named arguments" in {

    val r = engine.parseExpression(s"""sort(list: [], precedes: $externalFunctionDefinition)""")

    r.isLeft should be(true)
    r.left.get.message should include("External functions are disabled")
  }

  it should "fail to parse an external function inside list" in {

    val r = engine.parseExpression(s"""[$externalFunctionDefinition]""")

    r.isLeft should be(true)
    r.left.get.message should include("External functions are disabled")
  }

  it should "fail to parse an external function inside range start" in {

    val r = engine.parseExpression(s"""[1..$externalFunctionDefinition]""")

    r.isLeft should be(true)
    r.left.get.message should include("External functions are disabled")
  }

  it should "fail to parse an external function inside range end" in {

    val r = engine.parseExpression(s"""[$externalFunctionDefinition..10]""")

    r.isLeft should be(true)
    r.left.get.message should include("External functions are disabled")
  }

  it should "fail to parse an external function inside comparison" in {

    val r = engine.parseExpression(s"""$externalFunctionDefinition < 10""")

    r.isLeft should be(true)
    r.left.get.message should include("External functions are disabled")
  }

  it should "fail to parse an external function inside addition" in {

    val r = engine.parseExpression(s"""10 + $externalFunctionDefinition""")

    r.isLeft should be(true)
    r.left.get.message should include("External functions are disabled")
  }

  it should "fail to parse an external function inside condition" in {

    val r = engine.parseExpression(s"""if(true) then $externalFunctionDefinition else 10""")

    r.isLeft should be(true)
    r.left.get.message should include("External functions are disabled")
  }

  it should "fail to parse an external function inside list filter" in {

    val r = engine.parseExpression(s"""[][$externalFunctionDefinition]""")

    r.isLeft should be(true)
    r.left.get.message should include("External functions are disabled")
  }

  it should "fail to parse an external function inside instance-of" in {

    val r = engine.parseExpression(s"""$externalFunctionDefinition instance of function""")

    r.isLeft should be(true)
    r.left.get.message should include("External functions are disabled")
  }

  it should "fail to parse an external function inside unary test" in {

    val r = engine.parseUnaryTests(s"""< $externalFunctionDefinition""")

    r.isLeft should be(true)
    r.left.get.message should include("External functions are disabled")
  }

}
