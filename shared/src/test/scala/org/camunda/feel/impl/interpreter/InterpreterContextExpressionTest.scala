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

/**
  * @author Philipp Ossler
  */
class InterpreterContextExpressionTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A context" should "be accessed" in {

    eval("ctx.a", Map("ctx" -> Map("a" -> 1))) should be(ValNumber(1))

    eval("{ a: 1 }.a") should be(ValNumber(1))
  }

  it should "be accessed in a nested context" in {

    val context = eval("{ a: { b:1 } }.a")
    context shouldBe a[ValContext]
    context
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariables should be(Map("b" -> ValNumber(1)))

    eval("{ a: { b:1 } }.a.b") should be(ValNumber(1))
  }

  it should "be accessed in a list" in {

    eval("[ {a:1, b:2}, {a:3, b:4} ].a") should be(
      ValList(List(ValNumber(1), ValNumber(3))))
  }

  it should "be accessed in same context" in {

    eval("{ a:1, b:(a+1), c:(b+1)}.c") should be(ValNumber(3))

    eval("{ a: { b: 1 }, c: (1 + a.b) }.c") should be(ValNumber(2))
  }

  it should "be filtered in a list" in {

    val list = eval("[ {a:1, b:2}, {a:3, b:4} ][a > 2]")
    list shouldBe a[ValList]

    val items = list.asInstanceOf[ValList].items
    items should have size 1
    val context = items(0)

    context
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariables should be(Map("a" -> ValNumber(3), "b" -> ValNumber(4)))
  }

  it should "be accessed and filtered in a list" in {

    eval("[ {a:1, b:2}, {a:3, b:4} ].a[1]") should be(ValNumber(1))
  }

  it should "access a variable in a nested context" in {

    eval("{ a:1, b:{ c:a+2 } }.b.c") should be(ValNumber(3))
  }

  it should "not override variables of nested context" in {

    eval("{ a:1, b:{ a:2, c:a+3 } }.b.c") should be(ValNumber(5))
  }

  it should "fail if one entry fails" in {

    eval(" { a:1, b: {}.x } ") should be(
      ValError("context contains no entry with key 'x'"))
  }

  it should "be compared with '='" in {

    eval("{} = {}") should be(ValBoolean(true))
    eval("{x:1} = {x:1}") should be(ValBoolean(true))
    eval("{x:{ y:1 }} = {x:{ y:1 }}") should be(ValBoolean(true))

    eval("{} = {x:1}") should be(ValBoolean(false))
    eval("{x:1} = {}") should be(ValBoolean(false))
    eval("{x:1} = {x:2}") should be(ValBoolean(false))
    eval("{x:1} = {y:1}") should be(ValBoolean(false))

    eval("{x:1} = {x:true}") should be(ValBoolean(false))
  }

  it should "be compared with '!='" in {

    eval("{} != {}") should be(ValBoolean(false))
    eval("{x:1} != {x:1}") should be(ValBoolean(false))
    eval("{x:{ y:1 }} != {x:{ y:1 }}") should be(ValBoolean(false))

    eval("{} != {x:1}") should be(ValBoolean(true))
    eval("{x:1} != {}") should be(ValBoolean(true))
    eval("{x:1} != {x:2}") should be(ValBoolean(true))
    eval("{x:1} != {y:1}") should be(ValBoolean(true))

    eval("{x:1} != {x:true}") should be(ValBoolean(true))
  }

  it should "fail to compare if not a context" in {

    eval("{} = 1") should be(
      ValError("expect Context but found 'ValNumber(1)'")
    )
  }

}
