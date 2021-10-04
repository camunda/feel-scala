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

import org.camunda.feel.context.Context.StaticContext
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

  it should "return empty list if empty" in {

    eval(""" get entries({}) """) should be(ValList(List()))
  }

  "A get value function" should "return the value" in {
    eval(""" get value({foo: 123}, "foo") """) should be(ValNumber(123))
  }

  it should "return the value when arguments are named 'm' and 'key'" in {
    eval(""" get value(m:{foo: 123}, key:"foo") """) should be(ValNumber(123))
  }

  it should "return the value when arguments are named 'context' and 'key'" in {
    eval(""" get value(context:{foo: 123}, key:"foo") """) should be(
      ValNumber(123))
  }

  it should "return null if not contains" in {
    eval(""" get value({}, "foo") """) should be(ValNull)
    eval(""" get value(m:{}, key:"foo") """) should be(ValNull)
  }

  "A put function" should "add an entry to an empty context" in {

    eval(""" put({}, "x", 1) """) should be(
      ValContext(
        StaticContext(variables = Map("x" -> ValNumber(1)))
      ))
  }

  it should "add an entry to an existing context" in {

    eval(""" put({x:1}, "y", 2) """) should be(
      ValContext(
        StaticContext(variables = Map("x" -> ValNumber(1), "y" -> ValNumber(2)))
      ))
  }

  it should "override an entry of an existing context" in {

    eval(""" put({x:1}, "x", 2) """) should be(
      ValContext(
        StaticContext(variables = Map("x" -> ValNumber(2)))
      ))
  }

  it should "add a context entry to an existing context" in {

    eval(""" put({x:1}, "y", {"z":2}) = {x:1, y:{z:2} } """) should be(
      ValBoolean(true))
  }

  it should "return null if the value is not present" in {

    eval(""" put({}, "x", notExisting) """) should be(ValNull)
  }

  "A put all function" should "return a single empty context" in {

    eval(""" put all({}) """) should be(
      ValContext(
        StaticContext(variables = Map.empty)
      ))
  }

  it should "return a single context" in {

    eval(""" put all({x:1}) """) should be(
      ValContext(
        StaticContext(variables = Map("x" -> ValNumber(1)))
      ))
  }

  it should "combine empty contexts" in {

    eval(""" put all({}, {}) """) should be(
      ValContext(
        StaticContext(variables = Map.empty)
      ))
  }

  it should "add all entries to an empty context" in {

    eval(""" put all({}, {x:1}) """) should be(
      ValContext(
        StaticContext(variables = Map("x" -> ValNumber(1)))
      ))
  }

  it should "add an entry to an context" in {

    eval(""" put all({x:1}, {y:2}) """) should be(
      ValContext(
        StaticContext(variables = Map("x" -> ValNumber(1), "y" -> ValNumber(2)))
      ))
  }

  it should "add all entries to an context" in {

    eval(""" put all({x:1}, {y:2, z:3}) """) should be(
      ValContext(
        StaticContext(variables =
          Map("x" -> ValNumber(1), "y" -> ValNumber(2), "z" -> ValNumber(3)))
      ))
  }

  it should "override an entry of the existing context" in {

    eval(""" put all({x:1}, {x:2}) """) should be(
      ValContext(
        StaticContext(variables = Map("x" -> ValNumber(2)))
      ))
  }

  it should "override entries in order" in {

    eval(""" put all({x:1,y:3,z:1}, {x:2,y:2,z:3}, {x:3,y:1,z:2}) """) should be(
      ValContext(
        StaticContext(variables =
          Map("x" -> ValNumber(3), "y" -> ValNumber(1), "z" -> ValNumber(2)))
      ))
  }

  it should "combine three contexts" in {

    eval(""" put all({x:1}, {y:2}, {z:3}) """) should be(
      ValContext(
        StaticContext(variables =
          Map("x" -> ValNumber(1), "y" -> ValNumber(2), "z" -> ValNumber(3)))
      ))
  }

  it should "add a nested context" in {

    eval(""" put all({x:1}, {y:{z:2}}) = {x:1, y:{z:2} } """) should be(
      ValBoolean(true))
  }

  it should "return null if one entry is not a context" in {

    eval(""" put all({}, 1) """) should be(ValNull)
  }

  "A context function" should "return an empty context" in {

    eval(""" context([]) """) should be(ValContext(StaticContext(Map.empty)))
  }

  it should "return a context with one entry" in {

    eval(""" context([{"key":"a", "value":1}]) """) should be(
      ValContext(
        StaticContext(Map(
          "a" -> ValNumber(1)
        ))))
  }

  it should "return a context with multiple entries" in {

    eval(
      """ context([{"key":"a", "value":1}, {"key":"b", "value":true}, {"key":"c", "value":"ok"}]) """) should be(
      ValContext(
        StaticContext(
          Map(
            "a" -> ValNumber(1),
            "b" -> ValBoolean(true),
            "c" -> ValString("ok")
          ))))
  }

  it should "return a context with a nested list" in {

    eval(""" context([{"key":"a", "value":[1,2,3]}]) """) should be(
      ValContext(
        StaticContext(Map(
          "a" -> ValList(List(ValNumber(1), ValNumber(2), ValNumber(3)))
        ))))
  }

  it should "return a context with a nested context" in {

    eval(""" context([{"key":"a", "value": {x:1} }]) = {a: {x:1}} """) should be(
      ValBoolean(true))
  }

  it should "override entries in order" in {

    eval(
      """ context([{"key":"a", "value":1}, {"key":"a", "value":3}, {"key":"a", "value":2}]) """) should be(
      ValContext(
        StaticContext(Map(
          "a" -> ValNumber(2)
        ))))
  }

  it should "be the reverse operation to `get entries()`" in {

    eval(""" context(get entries({})) = {} """) should be(ValBoolean(true))
    eval(""" context(get entries({a:1})) = {a:1} """) should be(
      ValBoolean(true))
    eval(""" context(get entries({a:1,b:2})) = {a:1, b:2} """) should be(
      ValBoolean(true))
    eval(""" context(get entries({a:1,b:2})[key="a"]) = {a:1} """) should be(
      ValBoolean(true))
  }

  it should "return null if one entry is not a context" in {

    eval(""" context([{"key":"a", "value":1}, "x"]) """) should be(ValNull)
  }

  it should "return null if one entry doesn't contain a key" in {

    eval(""" context([{"key":"a", "value":1}, {"value":2}]) """) should be(
      ValNull)
  }

  it should "return null if one entry doesn't contain a value" in {

    eval(""" context([{"key":"a", "value":1}, {"key":"b"}]) """) should be(
      ValNull)
  }

  it should "return null if the key of one entry is not a string" in {

    eval(""" context([{"key":"a", "value":1}, {"key":2, "value":2}]) """) should be(
      ValNull)
  }

}
