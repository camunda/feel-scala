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
import org.camunda.feel.context.{CustomContext, VariableProvider}
import org.camunda.feel.impl.FeelIntegrationTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import org.camunda.feel.syntaxtree._

import scala.math.BigDecimal.int2bigDecimal

/**
  * @author Philipp
  */
class BuiltinContextFunctionsTest
    extends AnyFlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A get entries function" should "return all entries (when invoked with 'context' argument)" in {

    val list = eval(""" get entries(context:{foo: 123}) """)
    list match {
      case ValList(List(ValContext(context))) =>
        context.variableProvider.getVariables should be(
          Map("key" -> ValString("foo"), "value" -> ValNumber(123)))
      case other => fail(s"Expected list with one context but found '$other'")
    }
  }

  it should "return all entries (when invoked with 'm' argument)" in {

    val list = eval(""" get entries(m:{foo: 123}) """)
    list match {
      case ValList(List(ValContext(context))) =>
        context.variableProvider.getVariables should be(
          Map("key" -> ValString("foo"), "value" -> ValNumber(123)))
      case other => fail(s"Expected list with one context but found '$other'")
    }
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
  }

  "A get value with path function" should "return the value when a path is provided" in {
    eval("""get value({x: {y: {z:1}}}, ["x", "y", "z"])""") should be(ValNumber(1))
  }

  it should "return a context when a path is provided" in {
    eval("""get value({x: {y: {z:1}}}, ["x", "y"]) = {z:1}""") should be(ValBoolean(true))
  }

  it should "return null if non-existing path is provided" in {
    eval("""get value({x: {y: {z:1}}}, ["z"])""") should be(ValNull)
  }

  it should "return null if non-existing nested path is provided" in {
    eval("""get value({x: {y: {z:1}}}, ["x", "z"])""") should be(ValNull)
  }

  it should "return null if non-String list of keys is provided" in {
    eval("""get value({x: {y: {z:1}}}, ["1", 2])""") should be(ValNull)
  }

  it should "return null if an empty context is provided" in {
    eval("""get value({}, ["z"])""") should be(ValNull)
  }

  it should "return null if an empty list is provided as a path" in {
    eval("""get value({x: {y: {z:1}}}, [])""") should be(ValNull)
  }

  it should "return a value if named arguments are used" in {
    eval("""get value(context: {x: {y: {z:1}}}, keys: ["x"]) = {y: {z:1}}""") should be(ValBoolean(true))
  }

  it should "return a value from a custom context" in {

    class MyCustomContext extends CustomContext {
      class MyVariableProvider extends VariableProvider {
        private val entries = Map(
          "x" -> Map("y" -> 1)
        )

        override def getVariable(name: String): Option[Any] = entries.get(name)

        override def keys: Iterable[String] = entries.keys
      }

      override def variableProvider: VariableProvider = new MyVariableProvider
    }

    eval("""get value(context, ["x", "y"])""", Map("context" -> ValContext(new MyCustomContext))) should be(ValNumber(1))
  }

  "A context put function" should "add an entry to an empty context" in {

    eval(""" context put({}, "x", 1) """) should be(
      ValContext(
        StaticContext(variables = Map("x" -> ValNumber(1)))
      ))
  }

  it should "add an entry to an existing context" in {

    eval(""" context put({x:1}, "y", 2) """) should be(
      ValContext(
        StaticContext(variables = Map("x" -> ValNumber(1), "y" -> ValNumber(2)))
      ))
  }

  it should "override an entry of an existing context" in {

    eval(""" context put({x:1}, "x", 2) """) should be(
      ValContext(
        StaticContext(variables = Map("x" -> ValNumber(2)))
      ))
  }

  it should "override an entry and keep the original order" in {
    eval(""" context put({x:1, y:0, z:0}, "y", 2) = {x:1, y:2, z:0} """) should be (
      ValBoolean(true)
    )
  }

  it should "add a context entry to an existing context" in {

    eval(""" context put({x:1}, "y", {"z":2}) = {x:1, y:{z:2} } """) should be(
      ValBoolean(true))
  }

  it should "return null if the value is not present" in {

    eval(""" context put({}, "x", notExisting) """) should be(ValNull)
  }

  it should "be invoked with named parameters (key)" in {
    eval(""" context put(context: {x:1}, key: "y", value: 2) = {x:1, y:2} """) should be (
      ValBoolean(true)
    )
  }

  it should "add an a context entry with list argument" in {
    eval(""" context put({x:1}, ["y"], 2) = {x:1, y:2} """) should be (
      ValBoolean(true)
    )
  }

  it should "add nested context entry" in {
    eval(""" context put({x:1, y:{a:1}}, ["y", "b"], 2) = {x:1, y:{a:1, b:2}} """) should be (
      ValBoolean(true)
    )
  }

  it should "override nested context entry" in {
    eval(""" context put({x:1, y:{a:1}}, ["y", "a"], 2) = {x:1, y:{a:2}} """) should be (
      ValBoolean(true)
    )
  }

  it should "add nested context entry if key doesn't exist" in {
    eval(""" context put({x:1}, ["y", "z"], 2) = {x:1, y:{z:2}} """) should be (
      ValBoolean(true)
    )
  }

  it should "override nested context entry if existing value is not a context" in {
    eval(""" context put({x:1, y:2}, ["y", "z"], 2) = {x:1, y:{z:2}} """) should be (
      ValBoolean(true)
    )
  }

  it should "be invoked with named parameters (keys)" in {
    eval(""" context put(context: {x:{y:1}}, keys: ["x","y"], value: 2) = {x:{y:2}} """) should be(
      ValBoolean(true)
    )
  }

  it should "return null if keys are empty" in {
    eval(""" context put({x:1}, [], 2) """) should be (ValNull)
  }

  it should "return null if keys are null" in {
    eval(""" context put({x:1}, null, 2) """) should be (ValNull)
  }

  it should "return null if keys are not a list of strings" in {
    eval(""" context put({x:1}, [1,2,3], 2) """) should be(ValNull)
  }

  "A put function (deprecated)" should "behave as the context put function" in {
    eval(""" put({}, "x", 1) = context put({}, "x", 1) """) should be (
      ValBoolean(true)
    )

    eval(""" put({x:1}, "y", 2) = context put({x:1}, "y", 2) """) should be (
      ValBoolean(true)
    )

    eval(""" put({x:1}, "x", 2) = context put({x:1}, "x", 2) """) should be (
      ValBoolean(true)
    )
  }

  "A context merge function" should "return a single empty context" in {

    eval(""" context merge({}) """) should be(
      ValContext(
        StaticContext(variables = Map.empty)
      ))
  }

  it should "return a single context" in {

    eval(""" context merge({x:1}) """) should be(
      ValContext(
        StaticContext(variables = Map("x" -> ValNumber(1)))
      ))
  }

  it should "combine empty contexts" in {

    eval(""" context merge({}, {}) """) should be(
      ValContext(
        StaticContext(variables = Map.empty)
      ))
  }

  it should "add all entries to an empty context" in {

    eval(""" context merge({}, {x:1}) """) should be(
      ValContext(
        StaticContext(variables = Map("x" -> ValNumber(1)))
      ))
  }

  it should "add an entry to an context" in {

    eval(""" context merge({x:1}, {y:2}) """) should be(
      ValContext(
        StaticContext(variables = Map("x" -> ValNumber(1), "y" -> ValNumber(2)))
      ))
  }

  it should "add all entries to an context" in {

    eval(""" context merge({x:1}, {y:2, z:3}) """) should be(
      ValContext(
        StaticContext(variables =
          Map("x" -> ValNumber(1), "y" -> ValNumber(2), "z" -> ValNumber(3)))
      ))
  }

  it should "override an entry of the existing context" in {

    eval(""" context merge({x:1}, {x:2}) """) should be(
      ValContext(
        StaticContext(variables = Map("x" -> ValNumber(2)))
      ))
  }

  it should "override entries in order" in {

    eval(""" context merge({x:1,y:3,z:1}, {x:2,y:2,z:3}, {x:3,y:1,z:2}) """) should be(
      ValContext(
        StaticContext(variables =
          Map("x" -> ValNumber(3), "y" -> ValNumber(1), "z" -> ValNumber(2)))
      ))
  }

  it should "combine three contexts" in {

    eval(""" context merge({x:1}, {y:2}, {z:3}) """) should be(
      ValContext(
        StaticContext(variables =
          Map("x" -> ValNumber(1), "y" -> ValNumber(2), "z" -> ValNumber(3)))
      ))
  }

  it should "add a nested context" in {

    eval(""" context merge({x:1}, {y:{z:2}}) = {x:1, y:{z:2} } """) should be(
      ValBoolean(true))
  }

  it should "return null if one entry is not a context" in {

    eval(""" context merge({}, 1) """) should be(ValNull)
  }

  it should "be invoked with a list of contexts" in {
    eval(""" context merge([{x:1}, {y:2}]) = {x:1, y: 2} """) should be(
      ValBoolean(true)
    )
  }

  it should "be invoked with named parameters" in {
    eval(""" context merge(contexts: [{x:1}, {y:2}]) = {x:1, y:2} """) should be(
      ValBoolean(true)
    )
  }

  "A put all function (deprecated)" should "behave as the context merge function" in {
    eval(""" put all({}) = context merge({}) """) should be(ValBoolean(true))

    eval(""" put all({x:1}) = context merge({x:1}) """) should be(ValBoolean(true))

    eval(""" put all({x:1}, {y:2}) = context merge({x:1}, {y:2}) """) should be(ValBoolean(true))

    eval(
      """ put all({x:1,y:3,z:1}, {x:2,y:2,z:3}, {x:3,y:1,z:2}) = context merge({x:1,y:3,z:1}, {x:2,y:2,z:3}, {x:3,y:1,z:2}) """) should be(
      ValBoolean(true)
    )

    eval(""" put all({x:1}, {y:{z:2}}) = context merge({x:1}, {y:{z:2}}) """) should be(
      ValBoolean(true)
    )
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
