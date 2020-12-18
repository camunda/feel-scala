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
package org.camunda.feel.api.valuemapper

import org.camunda.feel.FeelEngine
import org.camunda.feel.context.Context
import org.camunda.feel.impl.JavaValueMapper
import org.camunda.feel.valuemapper.ValueMapper.CompositeValueMapper
import org.camunda.feel.valuemapper.{SimpleBooleanTestPojo, SimpleTestPojo}
import org.scalatest.{FlatSpec, Matchers}

class BuiltinValueMapperInputReflectionTest extends FlatSpec with Matchers {
  val engine =
    new FeelEngine(null, CompositeValueMapper(List(new JavaValueMapper())))

  it should "read value from object getter" in {

    val variables = Map("foo" -> new SimpleTestPojo("foo"))

    engine
      .evalExpression("foo.getMyString() = \"foo\"",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read value from object getter with attribute notation" in {

    val variables = Map("foo" -> new SimpleTestPojo("foo"))

    engine
      .evalExpression("foo.myString = \"foo\"",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read boolean 'true' from object 'is...' getter" in {
    val pojo = new SimpleBooleanTestPojo()
    pojo.setEnabled(true)

    val variables = Map("foo" -> pojo)

    engine
      .evalExpression("foo.isEnabled() = true",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read boolean 'true' from object getter with attribute notation" in {
    val pojo = new SimpleBooleanTestPojo()
    pojo.setEnabled(true)

    val variables = Map("foo" -> pojo)

    engine
      .evalExpression("foo.enabled = true",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read boolean 'false' from object getter with attribute notation" in {
    val pojo = new SimpleBooleanTestPojo()
    pojo.setEnabled(false)

    val variables = Map("foo" -> pojo)

    engine
      .evalExpression("foo.enabled = false",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read boolean 'true' from object getter" in {
    val pojo = new SimpleBooleanTestPojo()
    pojo.setDisabled(true)

    val variables = Map("foo" -> pojo)

    engine
      .evalExpression("foo.getDisabled() = true",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read boolean 'true' from object getter attribute notation" in {
    val pojo = new SimpleBooleanTestPojo()
    pojo.setDisabled(true)

    val variables = Map("foo" -> pojo)

    engine
      .evalExpression("foo.disabled = true",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read string from object 'is...' getter" in {
    val pojo = new SimpleBooleanTestPojo()
    pojo.setFoo("baz")

    val variables = Map("bar" -> pojo)

    engine
      .evalExpression("bar.isFoo() = \"baz\"",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "not read string from object 'is...' getter with attribute notation" in {
    val pojo = new SimpleBooleanTestPojo()
    pojo.setFoo("baz")

    val variables = Map("bar" -> pojo)

    engine
      .evalExpression("bar.foo = \"baz\"",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe ()
  }

  it should "read scala string from object getter with attribute notation" in {
    val pojo = new MyScalaType()

    val variables = Map("bar" -> pojo)

    engine
      .evalExpression("bar.foo = \"baz\"",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read scala boolean 'true' from object getter" in {
    val pojo = new MyScalaType()

    val variables = Map("bar" -> pojo)

    engine
      .evalExpression("bar.getEnabled() = true",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read scala boolean 'true' from object getter with attribute notation" in {
    val pojo = new MyScalaType()

    val variables = Map("bar" -> pojo)

    engine
      .evalExpression("bar.enabled = true",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read scala boolean 'false' from object IS getter" in {
    val pojo = new MyScalaType()

    val variables = Map("bar" -> pojo)

    engine
      .evalExpression("bar.isDisabled = false",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read scala boolean 'false' from object IS getter with attribute notation" in {
    val pojo = new MyScalaType()

    val variables = Map("bar" -> pojo)

    engine
      .evalExpression("bar.disabled = false",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read scala string from object IS getter with attribute notation" in {
    val pojo = new MyScalaType()

    val variables = Map("bar" -> pojo)

    engine
      .evalExpression("bar.isBaz = \"foo\"",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "not read scala string from object IS getter with attribute notation" in {
    val pojo = new MyScalaType()

    val variables = Map("bar" -> pojo)

    engine
      .evalExpression("bar.baz = \"foo\"",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe ()
  }

  class MyScalaType {
    private val foo: String = "baz"
    private val baz: String = "foo"
    private val enabled: Boolean = true
    private val disabled = false

    def getFoo: String = foo
    def isBaz: String = baz
    def getEnabled: Boolean = enabled
    def isDisabled = disabled
  }
}
