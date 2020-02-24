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
package org.camunda.feel.impl.spi

import org.camunda.feel.context.Context
import org.camunda.feel.impl.{DefaultValueMapper, JavaValueMapper}
import org.camunda.feel.syntaxtree._
import org.camunda.feel.valuemapper.ValueMapper
import org.scalatest.{FlatSpec, Matchers}

class JavaValueMapperTest extends FlatSpec with Matchers {

  val valueMapper =
    ValueMapper.CompositeValueMapper(
      List(DefaultValueMapper.instance, new JavaValueMapper()))

  "The JavaValueMapper" should "return number as Java Double" in {

    valueMapper.unpackVal(ValNumber(2.4)) should be(new java.lang.Double(2.4))
  }

  it should "return list as Java List" in {

    val list = new java.util.ArrayList[String]
    list.add("a")
    list.add("b")

    valueMapper.unpackVal(ValList(List(ValString("a"), ValString("b")))) should be(
      list)
  }

  it should "return context as Java Map" in {

    val map = new java.util.HashMap[String, String]
    map.put("x", "1")
    map.put("y", "2")

    valueMapper.unpackVal(
      ValContext(Context.StaticContext(
        variables = Map("x" -> "1", "y" -> "2")))) should be(map)
  }

}
