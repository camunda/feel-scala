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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FeelEngineBuilderTest extends AnyFlatSpec with Matchers {

  "The FeelEngineBuilder" should "be usable from Java" in {
    val javaBuilder = new FeelEngineBuilderJava()

    javaBuilder.buildEngine() shouldBe a[FeelEngineApi]
  }

  it should "build a preconfigured Java engine and evaluate expression" in {
    val javaBuilder = new FeelEngineBuilderJava()
    val engine      = javaBuilder.buildEngine()

    val evaluationResult = engine.evaluateExpression("[1,2]")

    evaluationResult.result shouldBe a[java.util.List[Integer]]
  }
}
