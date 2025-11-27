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
package org.camunda.feel.impl.valuemapper

import org.camunda.feel.impl._
import org.camunda.feel.syntaxtree._
import org.camunda.feel.valuemapper.ValueMapper
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

/** JVM-specific tests for DefaultValueMapper that rely on JVM reflection semantics.
  * These tests verify behavior that differs between JVM and JS platforms.
  */
class DefaultValueMapperJvmTest extends AnyFlatSpec with Matchers {

  implicit val valueMapper: ValueMapper =
    ValueMapper.CompositeValueMapper(List(DefaultValueMapper.instance))

  "The DefaultValueMapper" should "convert from object ignore private fields and methods" in {
    // This test uses a regular class (not case class) to test JVM reflection behavior
    // where private fields should not be accessible.
    class Obj(val a: Int, private val b: String) {
      def getC(): String = "c"
    }

    valueMapper.toVal(new Obj(a = 2, b = "foo")) match {
      case ValContext(context) =>
        val variables = context.variableProvider.getVariables
        variables should be(Map("a" -> 2))
    }
  }
}
