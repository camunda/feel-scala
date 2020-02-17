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
package org.camunda.feel

import java.util
import java.util.Collections

import org.camunda.feel.helper.TestPojo
import org.camunda.feel.impl.spi.SpiServiceLoader
import org.camunda.feel.interpreter.impl.Context
import org.scalatest.refspec.RefSpec
import org.scalatest.{Ignore, Matchers}

class ValueMapperInputTest extends RefSpec with Matchers {

  val engine = new FeelEngine(null, SpiServiceLoader.loadValueMapper)

  def `should read java.lang.String`() {
    val variables = Map("foo" -> java.lang.String.valueOf("3.4"))

    engine
      .evalExpression("foo + \"hello\"",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe a[java.lang.String]
  }

  def `should read java.lang.Float`() {
    val variables = Map("foo" -> java.lang.Float.valueOf("3.4"))

    engine
      .evalExpression("foo + 1",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe a[java.lang.Double]
  }

  def `should read java.lang.Double`() {
    val variables = Map("foo" -> java.lang.Double.valueOf("3.4"))

    engine
      .evalExpression("foo + 1",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe a[java.lang.Double]
  }

  def `should read java.lang.Integer`() {
    val variables = Map("foo" -> java.lang.Integer.valueOf("3"))

    engine
      .evalExpression("foo + 1",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe a[java.lang.Long]
  }

  def `should read java.lang.Long`() {
    val variables = Map("foo" -> java.lang.Long.valueOf("3"))

    engine
      .evalExpression("foo + 1",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe a[java.lang.Long]
  }

  @Ignore
  def `should read java.lang.Short`() {
    val variables = Map("foo" -> java.lang.Short.valueOf("3"))

    engine
      .evalExpression("foo + 1",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe a[java.lang.Long]
  }

  def `should read java.lang.Boolean`() {
    val variables = Map("foo" -> java.lang.Boolean.valueOf("true"))

    engine
      .evalExpression("foo or false",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe a[java.lang.Boolean]
  }

  def `should read java.util.Date`() {
    val variables = Map("foo" -> new java.util.Date())

    engine
      .evalExpression("foo", context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe a[java.time.LocalDateTime]
  }

  def `should read null value`() {
    val nullValue: java.lang.Integer = null

    val variables = Map("foo" -> nullValue)

    engine
      .evalExpression("foo = null",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  def `should read value from object getter`() {

    val variables = Map("foo" -> new TestPojo("foo"))

    engine
      .evalExpression("foo.getMyString() = \"foo\"",
                      context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  def `should read java.util.Map`() {
    val map: java.util.Map[java.lang.String, java.lang.Object] =
      new util.HashMap[java.lang.String, java.lang.Object]()
    map.put("foo", "myString")
    map.put("bar", java.lang.Integer.valueOf(2))
    map.put("baz", Collections.singletonMap("foo", "bar"))

    val variables = Map("map" -> map)

    engine
      .evalExpression(
        "map.foo = \"myString\" and map.bar = 2 and map.baz.foo = \"bar\"",
        context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  def `should read java.util.List`() {
    val list: java.util.List[java.lang.Object] =
      new util.ArrayList[java.lang.Object]()
    list.add("myString")
    list.add(java.lang.Integer.valueOf(2))
    list.add(Collections.singletonMap("foo", "bar"))

    val variables = Map("list" -> list)

    engine
      .evalExpression(
        "list[1] = \"myString\" and list[2] = 2 and list[3].foo = \"bar\"",
        context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

}
