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

import org.camunda.feel.impl.spi.SpiServiceLoader
import org.camunda.feel.interpreter.impl.Context
import org.scalatest.Matchers
import org.scalatest.refspec.RefSpec

class ValueMapperOutputTest extends RefSpec with Matchers {

  val engine = new FeelEngine(null, SpiServiceLoader.loadValueMapper)

  def `should return java.lang.String`() {

    engine
      .evalExpression("\"foo\"", context = Context.EmptyContext)
      .getOrElse() shouldBe a[java.lang.String]
  }

  def `should return java.lang.Double`() {

    engine
      .evalExpression("10/3", context = Context.EmptyContext)
      .getOrElse() shouldBe a[java.lang.Double]
  }

  def `should return java.lang.Long`() {

    engine
      .evalExpression("10", context = Context.EmptyContext)
      .getOrElse() shouldBe a[java.lang.Long]
  }

  def `should return java.lang.Boolean`() {

    engine
      .evalExpression("true", context = Context.EmptyContext)
      .getOrElse() shouldBe a[java.lang.Boolean]
  }

  def `should return java.time.LocalDateTime`() {

    engine
      .evalExpression("date and time(\"2019-08-12T22:22:22\")",
                      context = Context.EmptyContext)
      .getOrElse() shouldBe a[java.time.LocalDateTime]
  }

  def `should return java.time.ZonedDateTime`() {

    engine
      .evalExpression("date and time(\"2019-08-12T22:22:22@Europe/Berlin\")",
                      context = Context.EmptyContext)
      .getOrElse() shouldBe a[java.time.ZonedDateTime]
  }

  def `should return null`() {

    val nullValue: java.lang.Integer = null;

    engine
      .evalExpression("null", context = Context.EmptyContext)
      .getOrElse() == nullValue shouldBe true
  }

  def `should return java.util.Map`() {

    val map = engine
      .evalExpression("{\"foo\": 42, \"bar\": 5.5, \"baz\": [1, 2]}",
                      context = Context.EmptyContext)
      .getOrElse()
      .asInstanceOf[java.util.Map[_, _]]

    map shouldBe a[java.util.Map[_, _]]

    map.get("foo") shouldBe a[java.lang.Long]
    map.get("bar") shouldBe a[java.lang.Double]
    map.get("baz") shouldBe a[java.util.List[_]]

    val list = map
      .get("baz")
      .asInstanceOf[java.util.List[_]]

    list.get(0) shouldBe a[java.lang.Long]
  }

  def `should return java.util.List`() {

    val list = engine
      .evalExpression("[6, 5.5, {\"foo\": \"bar\"}]",
                      context = Context.EmptyContext)
      .getOrElse()
      .asInstanceOf[java.util.List[_]]

    list shouldBe a[java.util.List[_]]

    list.get(0) shouldBe a[java.lang.Long]
    list.get(1) shouldBe a[java.lang.Double]
    list.get(2) shouldBe a[java.util.Map[_, _]]

    val map = list
      .get(2)
      .asInstanceOf[java.util.Map[_, _]]

    map.get("foo") shouldBe a[java.lang.String]
  }

}
