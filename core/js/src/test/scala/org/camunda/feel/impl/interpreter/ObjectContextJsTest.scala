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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.scalajs.js

/** Scala.js-specific tests for ObjectContext.
  *
  * These tests verify that ObjectContext correctly handles:
  *   - Scala case classes (Product types)
  *   - JavaScript objects (js.Object / js.Dynamic)
  *   - Property access patterns specific to the JS runtime
  */
class ObjectContextJsTest extends AnyFlatSpec with Matchers {

  // ==========================================================================
  // Case Class (Product) Tests
  // ==========================================================================

  "ObjectContext with case class" should "access fields by name" in {
    case class Person(name: String, age: Int)
    val ctx = ObjectContext(Person("Alice", 30))

    ctx.variableProvider.getVariable("name") shouldBe Some("Alice")
    ctx.variableProvider.getVariable("age") shouldBe Some(30)
  }

  it should "return None for non-existent fields" in {
    case class Person(name: String)
    val ctx = ObjectContext(Person("Alice"))

    ctx.variableProvider.getVariable("nonExistent") shouldBe None
  }

  it should "return all field names via keys" in {
    case class Person(name: String, age: Int, active: Boolean)
    val ctx = ObjectContext(Person("Alice", 30, true))

    ctx.variableProvider.keys.toSet shouldBe Set("name", "age", "active")
  }

  it should "handle case class with various types" in {
    case class Data(
        stringVal: String,
        intVal: Int,
        longVal: Long,
        doubleVal: Double,
        boolVal: Boolean
    )
    val ctx = ObjectContext(Data("hello", 42, 100L, 3.14, true))

    ctx.variableProvider.getVariable("stringVal") shouldBe Some("hello")
    ctx.variableProvider.getVariable("intVal") shouldBe Some(42)
    ctx.variableProvider.getVariable("longVal") shouldBe Some(100L)
    ctx.variableProvider.getVariable("doubleVal") shouldBe Some(3.14)
    ctx.variableProvider.getVariable("boolVal") shouldBe Some(true)
  }

  it should "handle case class with null values" in {
    case class Person(name: String, nickname: String)
    val ctx = ObjectContext(Person("Alice", null))

    ctx.variableProvider.getVariable("name") shouldBe Some("Alice")
    ctx.variableProvider.getVariable("nickname") shouldBe Some(null)
  }

  it should "handle empty case class" in {
    case class Empty()
    val ctx = ObjectContext(Empty())

    ctx.variableProvider.keys shouldBe empty
    ctx.variableProvider.getVariable("anything") shouldBe None
  }

  it should "handle nested case classes" in {
    case class Address(city: String, zip: String)
    case class Person(name: String, address: Address)

    val address = Address("Berlin", "10115")
    val person  = Person("Alice", address)
    val ctx     = ObjectContext(person)

    ctx.variableProvider.getVariable("name") shouldBe Some("Alice")
    ctx.variableProvider.getVariable("address") shouldBe Some(address)
  }

  it should "handle case class with List field" in {
    case class Container(items: List[Int])
    val ctx = ObjectContext(Container(List(1, 2, 3)))

    ctx.variableProvider.getVariable("items") shouldBe Some(List(1, 2, 3))
  }

  it should "handle case class with Map field" in {
    case class Container(data: Map[String, Int])
    val ctx = ObjectContext(Container(Map("a" -> 1, "b" -> 2)))

    ctx.variableProvider.getVariable("data") shouldBe Some(Map("a" -> 1, "b" -> 2))
  }

  it should "handle case class with Option field" in {
    case class Container(maybeValue: Option[String])

    val ctxSome = ObjectContext(Container(Some("value")))
    ctxSome.variableProvider.getVariable("maybeValue") shouldBe Some(Some("value"))

    val ctxNone = ObjectContext(Container(None))
    ctxNone.variableProvider.getVariable("maybeValue") shouldBe Some(None)
  }

  it should "not provide functions for case classes" in {
    case class Person(name: String)
    val ctx = ObjectContext(Person("Alice"))

    ctx.functionProvider.getFunctions("name") shouldBe empty
    ctx.functionProvider.functionNames shouldBe empty
  }

  // ==========================================================================
  // Tuple (Product) Tests
  // ==========================================================================

  "ObjectContext with tuple" should "access elements by name" in {
    val tuple = ("Alice", 30)
    val ctx   = ObjectContext(tuple)

    ctx.variableProvider.getVariable("_1") shouldBe Some("Alice")
    ctx.variableProvider.getVariable("_2") shouldBe Some(30)
  }

  it should "return tuple element names via keys" in {
    val tuple = ("Alice", 30, true)
    val ctx   = ObjectContext(tuple)

    ctx.variableProvider.keys.toSet shouldBe Set("_1", "_2", "_3")
  }

  // ==========================================================================
  // JavaScript Object Tests
  // ==========================================================================

  "ObjectContext with js.Dynamic.literal" should "access properties" in {
    val jsObj = js.Dynamic.literal(name = "Bob", age = 25)
    val ctx   = ObjectContext(jsObj)

    ctx.variableProvider.getVariable("name") shouldBe Some("Bob")
    ctx.variableProvider.getVariable("age") shouldBe Some(25)
  }

  it should "return None for non-existent properties" in {
    val jsObj = js.Dynamic.literal(name = "Bob")
    val ctx   = ObjectContext(jsObj)

    ctx.variableProvider.getVariable("nonExistent") shouldBe None
  }

  it should "return property names via keys" in {
    val jsObj = js.Dynamic.literal(name = "Bob", age = 25, active = true)
    val ctx   = ObjectContext(jsObj)

    ctx.variableProvider.keys.toSet should contain allOf ("name", "age", "active")
  }

  it should "handle nested js objects" in {
    val address = js.Dynamic.literal(city = "Berlin", zip = "10115")
    val person  = js.Dynamic.literal(name = "Bob", address = address)
    val ctx     = ObjectContext(person)

    ctx.variableProvider.getVariable("name") shouldBe Some("Bob")
    val retrievedAddress = ctx.variableProvider.getVariable("address")
    retrievedAddress shouldBe defined
  }

  it should "handle js object with null values" in {
    val jsObj = js.Dynamic.literal(name = "Bob", nickname = null)
    val ctx   = ObjectContext(jsObj)

    ctx.variableProvider.getVariable("name") shouldBe Some("Bob")
    ctx.variableProvider.getVariable("nickname") shouldBe Some(null)
  }

  it should "handle empty js object" in {
    val jsObj = js.Dynamic.literal()
    val ctx   = ObjectContext(jsObj)

    ctx.variableProvider.keys shouldBe empty
  }

  it should "handle js object with array values" in {
    val jsObj = js.Dynamic.literal(items = js.Array(1, 2, 3))
    val ctx   = ObjectContext(jsObj)

    val items = ctx.variableProvider.getVariable("items")
    items shouldBe defined
  }

  // ==========================================================================
  // Function Provider Tests for js.Object
  // ==========================================================================

  "ObjectContext functionProvider with js.Object" should "invoke zero-argument functions" in {
    val jsObj = js.Dynamic.literal(
      getValue = js.defined { () => 42 }
    )
    val ctx   = ObjectContext(jsObj)

    val functions = ctx.functionProvider.getFunctions("getValue")
    functions should have length 1

    val result = functions.head.invoke(List.empty)
    result shouldBe 42
  }

  it should "list available function names" in {
    val jsObj = js.Dynamic.literal(
      foo = js.defined { () => "foo" },
      bar = js.defined { () => "bar" },
      data = "not a function"
    )
    val ctx   = ObjectContext(jsObj)

    val funcNames = ctx.functionProvider.functionNames.toSet
    funcNames should contain("foo")
    funcNames should contain("bar")
  }

  it should "return empty list for non-existent function" in {
    val jsObj = js.Dynamic.literal(name = "Bob")
    val ctx   = ObjectContext(jsObj)

    ctx.functionProvider.getFunctions("nonExistent") shouldBe empty
  }

  // ==========================================================================
  // Edge Cases
  // ==========================================================================

  "ObjectContext" should "filter out properties starting with $" in {
    // In Scala.js compiled code, internal properties often start with $
    val jsObj = js.Dynamic.literal(
      name = "Bob",
      `$internal` = "hidden"
    )
    val ctx   = ObjectContext(jsObj)

    val keys = ctx.variableProvider.keys.toSeq
    keys should contain("name")
    keys should not contain "$internal"
  }

  it should "filter out constructor from keys" in {
    val jsObj = js.Dynamic.literal(name = "Bob")
    val ctx   = ObjectContext(jsObj)

    // constructor should not appear in the keys list
    ctx.variableProvider.keys.toSeq should not contain "constructor"
  }

  // ==========================================================================
  // Integration with FEEL expressions (using case classes)
  // ==========================================================================

  "ObjectContext in FEEL context" should "work with simple case class for variable access" in {
    case class Order(orderId: String, amount: Double, isPaid: Boolean)
    val order = Order("ORD-123", 99.99, true)
    val ctx   = ObjectContext(order)

    // Simulate what FEEL interpreter does
    ctx.variableProvider.getVariable("orderId") shouldBe Some("ORD-123")
    ctx.variableProvider.getVariable("amount") shouldBe Some(99.99)
    ctx.variableProvider.getVariable("isPaid") shouldBe Some(true)
  }

  it should "work with deeply nested case classes" in {
    case class Item(name: String, price: Double)
    case class Customer(name: String, email: String)
    case class Order(customer: Customer, items: List[Item], total: Double)

    val order = Order(
      customer = Customer("Alice", "alice@example.com"),
      items = List(Item("Widget", 10.0), Item("Gadget", 20.0)),
      total = 30.0
    )
    val ctx   = ObjectContext(order)

    ctx.variableProvider.getVariable("customer") shouldBe Some(order.customer)
    ctx.variableProvider.getVariable("items") shouldBe Some(order.items)
    ctx.variableProvider.getVariable("total") shouldBe Some(30.0)

    // Access nested context
    val customerCtx = ObjectContext(order.customer)
    customerCtx.variableProvider.getVariable("name") shouldBe Some("Alice")
    customerCtx.variableProvider.getVariable("email") shouldBe Some("alice@example.com")
  }
}
