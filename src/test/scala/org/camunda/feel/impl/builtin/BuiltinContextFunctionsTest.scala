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

import org.camunda.feel.api.FeelEngineBuilder
import org.camunda.feel.context.Context
import org.camunda.feel.impl.interpreter.MyCustomContext
import org.camunda.feel.impl.{EvaluationResultMatchers, FeelEngineTest}
import org.camunda.feel.syntaxtree._
import org.camunda.feel.valuemapper.CustomValueMapper
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** @author
  *   Philipp
  */
class BuiltinContextFunctionsTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

  "A get entries function" should "return all entries (when invoked with 'context' argument)" in {
    evaluateExpression("get entries(context:{foo: 123})") should returnResult(
      List(Map("key" -> "foo", "value" -> 123))
    )
  }

  it should "return all entries (when invoked with 'm' argument)" in {
    evaluateExpression(""" get entries(m:{foo: 123}) """) should returnResult(
      List(Map("key" -> "foo", "value" -> 123))
    )
  }

  it should "return empty list if empty" in {
    evaluateExpression(""" get entries({}) """) should returnResult(List.empty)
  }

  it should "return all entries in the same order as in the context" in {
    evaluateExpression("get entries({a: 1, b: 2, c: 3}).key") should returnResult(
      List("a", "b", "c")
    )

    evaluateExpression("""get entries({a: "foo", b: "bar"}).key""") should returnResult(
      List("a", "b")
    )

    evaluateExpression("get entries({c: 1, b: 2, a: 3}).key") should returnResult(
      List("c", "b", "a")
    )
  }

  "A get value function" should "return the value" in {
    evaluateExpression(""" get value({foo: 123}, "foo") """) should returnResult(123)
  }

  it should "return the value when arguments are named 'm' and 'key'" in {
    evaluateExpression(""" get value(m:{foo: 123}, key:"foo") """) should returnResult(123)
  }

  it should "return the value when arguments are named 'context' and 'key'" in {
    evaluateExpression(
      """ get value(context:{foo: 123}, key:"foo") """
    ) should returnResult(123)
  }

  it should "return null if not contains" in {
    evaluateExpression(""" get value({}, "foo") """) should returnNull()
  }

  "A get value with path function" should "return the value when a path is provided" in {
    evaluateExpression("""get value({x: {y: {z:1}}}, ["x", "y", "z"])""") should returnResult(1)
  }

  it should "return a context when a path is provided" in {
    evaluateExpression("""get value({x: {y: {z:1}}}, ["x", "y"]) = {z:1}""") should returnResult(
      true
    )
  }

  it should "return null if non-existing path is provided" in {
    evaluateExpression("""get value({x: {y: {z:1}}}, ["z"])""") should returnNull()
  }

  it should "return null if non-existing nested path is provided" in {
    evaluateExpression("""get value({x: {y: {z:1}}}, ["x", "z"])""") should returnNull()
  }

  it should "return null if non-String list of keys is provided" in {
    evaluateExpression("""get value({x: {y: {z:1}}}, ["1", 2])""") should returnNull()
  }

  it should "return null if an empty context is provided" in {
    evaluateExpression("""get value({}, ["z"])""") should returnNull()
  }

  it should "return null if an empty list is provided as a path" in {
    evaluateExpression("""get value({x: {y: {z:1}}}, [])""") should returnNull()
  }

  it should "return a value if named arguments are used" in {
    evaluateExpression(
      """get value(context: {x: {y: {z:1}}}, keys: ["x"]) = {y: {z:1}}"""
    ) should returnResult(true)
  }

  it should "return a value from a custom context" in {

    evaluateExpression(
      expression = """get value(context, ["x", "y"])""",
      variables = Map(
        "context" -> ValContext(
          new MyCustomContext(
            Map(
              "x" -> Map("y" -> 1)
            )
          )
        )
      )
    ) should returnResult(1)
  }

  "A context put function" should "add an entry to an empty context" in {
    evaluateExpression(""" context put({}, "x", 1) """) should returnResult(
      Map("x" -> 1)
    )
  }

  it should "add an entry to an existing context" in {
    evaluateExpression(""" context put({x:1}, "y", 2) """) should returnResult(
      Map("x" -> 1, "y" -> 2)
    )
  }

  it should "add a new entry at the end of the context" in {
    evaluateExpression(
      """ get entries(context put({a: 1, b: 2, c: 3}, "d", 4)).key """
    ) should returnResult(List("a", "b", "c", "d"))

    evaluateExpression(
      """ get entries(context put({c: 1, b: 2, a: 3}, "d", 4)).key """
    ) should returnResult(List("c", "b", "a", "d"))
  }

  it should "override an entry of an existing context" in {
    evaluateExpression(""" context put({x:1}, "x", 2) """) should returnResult(
      Map("x" -> 2)
    )
  }

  it should "override an entry and keep the original order" in {
    evaluateExpression(
      """ get entries(context put({a: 1, b: 2, c: 3}, "b", 20)).key """
    ) should returnResult(List("a", "b", "c"))

    evaluateExpression(
      """ get entries(context put({c: 1, b: 2, a: 3}, "c", 10)).key """
    ) should returnResult(List("c", "b", "a"))

    evaluateExpression(
      """ get entries(context put({c: 1, b: 2, a: 3}, "a", 30)).key """
    ) should returnResult(List("c", "b", "a"))
  }

  it should "add a context entry to an existing context" in {
    evaluateExpression(""" context put({x:1}, "y", {"z":2}) """) should returnResult(
      Map("x" -> 1, "y" -> Map("z" -> 2))
    )
  }

  it should "add a context entry with null if the value is not present" in {
    evaluateExpression(""" context put({}, "x", notExisting) """) should returnResult(
      Map("x" -> null)
    )
  }

  it should "be invoked with named parameters (key)" in {
    evaluateExpression(""" context put(context: {x:1}, key: "y", value: 2) """) should returnResult(
      Map("x" -> 1, "y" -> 2)
    )
  }

  it should "add a context entry with list argument" in {
    evaluateExpression(""" context put({x:1}, ["y"], 2) """) should returnResult(
      Map("x" -> 1, "y" -> 2)
    )
  }

  it should "add nested context entry" in {
    evaluateExpression(""" context put({x:1, y:{a:1}}, ["y", "b"], 2) """) should returnResult(
      Map("x" -> 1, "y" -> Map("a" -> 1, "b" -> 2))
    )

    evaluateExpression(
      """ context put({x:1, a:{b:{c:1}}}, ["a", "b", "d"], 2) """
    ) should returnResult(
      Map("x" -> 1, "a" -> Map("b" -> Map("c" -> 1, "d" -> 2)))
    )

    evaluateExpression(
      """ context put({x:1, a:{b:{c:{d:1}}}}, ["a", "b", "c", "e"], 2) """
    ) should returnResult(
      Map("x" -> 1, "a" -> Map("b" -> Map("c" -> Map("d" -> 1, "e" -> 2))))
    )
  }

  it should "override nested context entry" in {
    evaluateExpression(""" context put({x:1, y:{a:1}}, ["y", "a"], 2) """) should returnResult(
      Map("x" -> 1, "y" -> Map("a" -> 2))
    )

    evaluateExpression(
      """ context put({x:1, a:{b:{c:1}}}, ["a", "b", "c"], 2) """
    ) should returnResult(
      Map("x" -> 1, "a" -> Map("b" -> Map("c" -> 2)))
    )

    evaluateExpression(
      """ context put({x:1, a:{b:{c:{d:1}}}}, ["a", "b", "c", "d"], 2) """
    ) should returnResult(
      Map("x" -> 1, "a" -> Map("b" -> Map("c" -> Map("d" -> 2))))
    )
  }

  it should "handle a lazy value mapper" in {
    val lazyEngine = FeelEngineBuilder()
      .withCustomValueMapper(new CustomValueMapper {
        override def toVal(x: Any, innerValueMapper: Any => Val): Option[Val] = x match {
          case x: Map[String, Any] =>
            Some {
              ValContext(
                Context.StaticContext(
                  variables = x, // don't eagerly map inner values
                )
              )
            }
          case  _ => None // fallback to default
        }

        override def unpackVal(value: Val, innerValueMapper: Val => Any): Option[Any] = {
          None // fallback to default
        }
      })
      .build()

    lazyEngine.evaluateExpression(
      """ context put(vars, ["a", "c"], 3) """,
      Map("vars" -> Map("a" -> Map("b" -> 1, "c" -> 2)))
    ) should returnResult(
      Map("a" -> Map("b" -> 1, "c" -> 3))
    )
  }

  it should "add nested context entry if key doesn't exist" in {
    evaluateExpression(""" context put({x:1}, ["y", "z"], 2) """) should returnResult(
      Map("x" -> 1, "y" -> Map("z" -> 2))
    )

    evaluateExpression(""" context put({x:1}, ["a", "b", "c"], 2) """) should returnResult(
      Map("x" -> 1, "a" -> Map("b" -> Map("c" -> 2)))
    )

    evaluateExpression(""" context put({x:1}, ["a", "b", "c", "d"], 2) """) should returnResult(
      Map("x" -> 1, "a" -> Map("b" -> Map("c" -> Map("d" -> 2))))
    )
  }

  it should "override nested context entry if existing value is not a context" in {
    evaluateExpression(""" context put({x:1, y:2}, ["y", "z"], 2) """) should returnResult(
      Map("x" -> 1, "y" -> Map("z" -> 2))
    )
  }

  it should "be invoked with named parameters (keys)" in {
    evaluateExpression(
      """ context put(context: {x:{y:1}}, keys: ["x","y"], value: 2) """
    ) should returnResult(
      Map("x" -> Map("y" -> 2))
    )
  }

  it should "return null if keys are empty" in {
    evaluateExpression(""" context put({x:1}, [], 2) """) should returnNull()
  }

  it should "return null if keys are null" in {
    evaluateExpression(""" context put({x:1}, null, 2) """) should returnNull()
  }

  it should "return null if keys are not a list of strings" in {
    evaluateExpression(""" context put({x:1}, [1,2,3], 2) """) should returnNull()
  }

  "A put function (deprecated)" should "behave as the context put function" in {
    evaluateExpression(""" put({}, "x", 1) = context put({}, "x", 1) """) should returnResult(true)

    evaluateExpression(""" put({x:1}, "y", 2) = context put({x:1}, "y", 2) """) should returnResult(
      true
    )

    evaluateExpression(""" put({x:1}, "x", 2) = context put({x:1}, "x", 2) """) should returnResult(
      true
    )
  }

  "A context merge function" should "return a single empty context" in {
    evaluateExpression(""" context merge({}) """) should returnResult(Map.empty)
  }

  it should "return a single context" in {
    evaluateExpression(""" context merge({x:1}) """) should returnResult(Map("x" -> 1))
  }

  it should "combine empty contexts" in {
    evaluateExpression(""" context merge({}, {}) """) should returnResult(Map.empty)
  }

  it should "add all entries to an empty context" in {
    evaluateExpression(""" context merge({}, {x:1}) """) should returnResult(Map("x" -> 1))
  }

  it should "add an entry to an context" in {
    evaluateExpression(""" context merge({x:1}, {y:2}) """) should returnResult(
      Map("x" -> 1, "y" -> 2)
    )
  }

  it should "add all entries to an context" in {
    evaluateExpression(""" context merge({x:1}, {y:2, z:3}) """) should returnResult(
      Map("x" -> 1, "y" -> 2, "z" -> 3)
    )
  }

  it should "add all entries at the end of the context" in {
    evaluateExpression(
      " get entries(context merge({a: 1, b: 2}, {c: 3, d: 4})).key "
    ) should returnResult(
      List("a", "b", "c", "d")
    )

    evaluateExpression(
      " get entries(context merge({d: 1, c: 2}, {b: 3, a: 4})).key "
    ) should returnResult(
      List("d", "c", "b", "a")
    )
  }

  it should "override an entry of the existing context" in {
    evaluateExpression(""" context merge({x:1}, {x:2}) """) should returnResult(
      Map("x" -> 2)
    )
  }

  it should "override entries in order" in {
    evaluateExpression(
      """ context merge({x:1,y:3,z:1}, {x:2,y:2,z:3}, {x:3,y:1,z:2}) """
    ) should returnResult(
      Map("x" -> 3, "y" -> 1, "z" -> 2)
    )
  }

  it should "override entries and keep the original order" in {
    evaluateExpression(
      " get entries(context merge({a: 1, b: 2, c: 3}, {b: 20, d: 4})).key "
    ) should returnResult(
      List("a", "b", "c", "d")
    )

    evaluateExpression(
      " get entries(context merge({c: 1, b: 2, a: 3}, {b: 20, d: 4})).key "
    ) should returnResult(
      List("c", "b", "a", "d")
    )
  }

  it should "combine three contexts" in {
    evaluateExpression(""" context merge({x:1}, {y:2}, {z:3}) """) should returnResult(
      Map("x" -> 1, "y" -> 2, "z" -> 3)
    )
  }

  it should "add a nested context" in {
    evaluateExpression(""" context merge({x:1}, {y:{z:2}}) """) should returnResult(
      Map("x" -> 1, "y" -> Map("z" -> 2))
    )
  }

  it should "return null if one entry is not a context" in {
    evaluateExpression(""" context merge({}, 1) """) should returnNull()
  }

  it should "be invoked with a list of contexts" in {
    evaluateExpression(""" context merge([{x:1}, {y:2}]) """) should returnResult(
      Map("x" -> 1, "y" -> 2)
    )
  }

  it should "be invoked with named parameters" in {
    evaluateExpression(""" context merge(contexts: [{x:1}, {y:2}]) """) should returnResult(
      Map("x" -> 1, "y" -> 2)
    )
  }

  "A put all function (deprecated)" should "behave as the context merge function" in {
    evaluateExpression(
      """ put all({}) = context merge({}) """
    ) should returnResult(true)

    evaluateExpression(
      """ put all({x:1}) = context merge({x:1}) """
    ) should returnResult(true)

    evaluateExpression(
      """ put all({x:1}, {y:2}) = context merge({x:1}, {y:2}) """
    ) should returnResult(true)

    evaluateExpression(
      """ put all({x:1,y:3,z:1}, {x:2,y:2,z:3}, {x:3,y:1,z:2}) = context merge({x:1,y:3,z:1}, {x:2,y:2,z:3}, {x:3,y:1,z:2}) """
    ) should returnResult(true)

    evaluateExpression(
      """ put all({x:1}, {y:{z:2}}) = context merge({x:1}, {y:{z:2}}) """
    ) should returnResult(true)
  }

  "A context function" should "return an empty context" in {
    evaluateExpression(""" context([]) """) should returnResult(Map.empty)
  }

  it should "return a context with one entry" in {
    evaluateExpression(""" context([{"key":"a", "value":1}]) """) should returnResult(
      Map("a" -> 1)
    )
  }

  it should "return a context with multiple entries" in {
    evaluateExpression(
      """ context([{"key":"a", "value":1}, {"key":"b", "value":true}, {"key":"c", "value":"ok"}]) """
    ) should returnResult(
      Map("a" -> 1, "b" -> true, "c" -> "ok")
    )
  }

  it should "return a context with a nested list" in {
    evaluateExpression(""" context([{"key":"a", "value":[1,2,3]}]) """) should returnResult(
      Map("a" -> List(1, 2, 3))
    )
  }

  it should "return a context with a nested context" in {
    evaluateExpression(""" context([{"key":"a", "value": {x:1} }]) """) should returnResult(
      Map("a" -> Map("x" -> 1))
    )
  }

  it should "return a context with the same order as the given entries" in {
    evaluateExpression("""get entries(context([
         {"key":"a","value":1},
         {"key":"b","value":2},
         {"key":"c","value":3}
         ])).key""") should returnResult(List("a", "b", "c"))

    evaluateExpression("""get entries(context([
         {"key":"c","value":1},
         {"key":"b","value":2},
         {"key":"a","value":3}
         ])).key""") should returnResult(List("c", "b", "a"))
  }

  it should "override entries in order" in {
    evaluateExpression(
      """ context([{"key":"a", "value":1}, {"key":"a", "value":3}, {"key":"a", "value":2}]) """
    ) should returnResult(
      Map("a" -> 2)
    )
  }

  it should "be the reverse operation to `get entries()`" in {
    evaluateExpression(""" context(get entries({})) = {} """) should returnResult(true)
    evaluateExpression(""" context(get entries({a:1})) = {a:1} """) should returnResult(true)
    evaluateExpression(""" context(get entries({a:1,b:2})) = {a:1, b:2} """) should returnResult(
      true
    )
    evaluateExpression(
      """ context(get entries({a:1,b:2})[key="a"]) = {a:1} """
    ) should returnResult(true)
  }

  it should "return null if one entry is not a context" in {
    evaluateExpression(""" context([{"key":"a", "value":1}, "x"]) """) should returnNull()
  }

  it should "return null if one entry doesn't contain a key" in {
    evaluateExpression(""" context([{"key":"a", "value":1}, {"value":2}]) """) should returnNull()
  }

  it should "return null if one entry doesn't contain a value" in {
    evaluateExpression(""" context([{"key":"a", "value":1}, {"key":"b"}]) """) should returnNull()
  }

  it should "return null if the key of one entry is not a string" in {
    evaluateExpression(
      """ context([{"key":"a", "value":1}, {"key":2, "value":2}]) """
    ) should returnNull()
  }

  it should "return a context with entries from a custom context" in {

    evaluateExpression(
      expression = """context(list)""",
      variables = Map(
        "list" -> List(
          ValContext(
            new MyCustomContext(
              Map(
                "key"   -> "a",
                "value" -> 1
              )
            )
          )
        )
      )
    ) should returnResult(Map("a" -> 1))
  }

}
