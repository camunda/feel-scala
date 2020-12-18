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
class InterpreterUnaryBeanTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  it should "compare to a field of a bean" in {

    class A(val b: Int)

    evalUnaryTests(3, "a.b", Map("a" -> new A(3))) should be(ValBoolean(true))
    evalUnaryTests(3, "a.b", Map("a" -> new A(4))) should be(ValBoolean(false))

    evalUnaryTests(3, "< a.b", Map("a" -> new A(4))) should be(ValBoolean(true))
    evalUnaryTests(3, "< a.b", Map("a" -> new A(2))) should be(
      ValBoolean(false))
  }

}
