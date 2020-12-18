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

import org.camunda.feel.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree._
import org.scalatest.{FlatSpec, Matchers}

/** @author Philipp Ossler
  */
class InterpreterJavaFunctionTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  "An external java function definition" should "be invoked with one double parameter" in {

    val functions = Map(
      "cos" -> eval(
        """ function(angle) external { java: { class: "java.lang.Math", method signature: "cos(double)" } } """
      ).asInstanceOf[ValFunction]
    )

    eval("cos(0)", functions = functions) should be(ValNumber(1))
    eval("cos(1)", functions = functions) should be(ValNumber(Math.cos(1)))
  }

  it should "be invoked with two int parameters" in {

    val functions = Map(
      "max" -> eval(
        """ function(x,y) external { java: { class: "java.lang.Math", method signature: "max(int, int)" } } """
      ).asInstanceOf[ValFunction]
    )

    eval("max(1,2)", functions = functions) should be(ValNumber(2))
  }

  it should "be invoked with one long parameters" in {

    val functions = Map(
      "abs" -> eval(
        """ function(a) external { java: { class: "java.lang.Math", method signature: "abs(long)" } } """)
        .asInstanceOf[ValFunction]
    )

    eval("abs(-1)", functions = functions) should be(ValNumber(1))
  }

  it should "be invoked with one float parameters" in {

    val functions = Map(
      "round" -> eval(
        """ function(a) external { java: { class: "java.lang.Math", method signature: "round(float)" } } """
      ).asInstanceOf[ValFunction]
    )

    eval("round(3.2)", functions = functions) should be(ValNumber(3))
  }

}
