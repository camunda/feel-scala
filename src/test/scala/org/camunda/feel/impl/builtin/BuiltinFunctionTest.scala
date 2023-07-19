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

import org.camunda.feel.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree._
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

/**
  * @author Philipp
  */
class BuiltinFunctionsTest
    extends AnyFlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A built-in function" should "return null if arguments doesn't match" in {

    eval("date(true)") should be(ValNull)
    eval("number(false)") should be(ValNull)
  }

  "A not() function" should "negate Boolean" in {

    eval(" not(true) ") should be(ValBoolean(false))
    eval(" not(false) ") should be(ValBoolean(true))
  }

  "A is defined() function" should "return true if the value is present" in {

    eval("is defined(null)") should be(ValBoolean(true))

    eval("is defined(1)") should be(ValBoolean(true))
    eval("is defined(true)") should be(ValBoolean(true))
    eval("is defined([])") should be(ValBoolean(true))
    eval("is defined({})") should be(ValBoolean(true))
    eval(""" is defined( {"a":1}.a ) """) should be(ValBoolean(true))
  }

  it should "return false if the value is not present" in {

    eval("is defined(a)") should be(ValBoolean(false))
    eval("is defined(a.b)") should be(ValBoolean(false))

    eval("is defined({}.a)") should be(ValBoolean(false))
    eval("is defined({}.a.b)") should be(ValBoolean(false))
  }

  "A get or else(value: Any, default: Any) function" should "return the value if not null" in {

    eval("get or else(3, 1)") should be(ValNumber(3))
    eval("""get or else("value", "default")""") should be(ValString("value"))
    eval("get or else(value:3, default:1)") should be(ValNumber(3))
  }

  it should "return the default param if value is null" in {

    eval("get or else(null, 1)") should be(ValNumber(1))
    eval("""get or else(null, "default")""") should be(ValString("default"))
    eval("get or else(value:null, default:1)") should be(ValNumber(1))
  }

  it should "return null if both value and default params are null" in {

    eval("get or else(null, null)") should be(ValNull)
    eval("get or else(value:null, default:null)") should be(ValNull)
  }
}
