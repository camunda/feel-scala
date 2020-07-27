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
package org.camunda.feel.impl.builtin

import org.camunda.feel.impl.FeelIntegrationTest
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel.syntaxtree._

import scala.math.BigDecimal.int2bigDecimal

/**
  * @author Philipp
  */
class BuiltinContextFunctionsTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A get entries function" should "return all entries" in {

    val list = eval(""" get entries({foo: 123}) """)
    list shouldBe a[ValList]

    val items = list.asInstanceOf[ValList].items
    items should have size 1
    val context = items(0)
    context
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariables should be(
      Map("key" -> ValString("foo"), "value" -> ValNumber(123)))
  }

  it should "return empty list if emtpy" in {

    eval(""" get entries({}) """) should be(ValList(List()))
  }

  "A get value function" should "return the value" in {

    eval(""" get value({foo: 123}, "foo") """) should be(ValNumber(123))
  }

  it should "return null if not contains" in {

    eval(""" get value({}, "foo") """) should be(ValNull)
  }

}
