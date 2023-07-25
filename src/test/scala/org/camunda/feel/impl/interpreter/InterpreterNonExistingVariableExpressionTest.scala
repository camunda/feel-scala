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
package org.camunda.feel.impl.interpreter;

import org.camunda.feel.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree._
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

class InterpreterNonExistingVariableExpressionTest
        extends AnyFlatSpec
        with Matchers
        with FeelIntegrationTest {

  "a non existing variable" should "compare with '='" in {
    eval("x = 1") should be(ValBoolean(false))
    eval("1 = x") should be(ValBoolean(false))
    eval("x = true") should be(ValBoolean(false))
    eval("true = x") should be(ValBoolean(false))
    eval(""" x = "string" """) should be(ValBoolean(false))
    eval(""" "string" = x """) should be(ValBoolean(false))
    eval("x = null") should be(ValBoolean(true))
    eval("null = x") should be(ValBoolean(true))
    eval("x = y") should be(ValBoolean(true))
  }
  
  it should "compare with `<`" in {
    eval("x < 1") should be(ValNull)
    eval("1 < x") should be(ValNull)
    eval("x < true") should be(ValNull)
    eval("true < x") should be(ValNull)
    eval(""" x < "string" """) should be(ValNull)
    eval(""" "string" < x """) should be(ValNull)
    eval("x < null") should be(ValNull)
    eval("null < x") should be(ValNull)
    eval("x < y") should be(ValNull)
  }

  it should "compare with `>`" in {
    eval("x > 1") should be(ValNull)
    eval("1 > x") should be(ValNull)
    eval("x > true") should be(ValNull)
    eval("true > x") should be(ValNull)
    eval(""" x > "string" """) should be(ValNull)
    eval(""" "string" > x """) should be(ValNull)
    eval("x > null") should be(ValNull)
    eval("null > x") should be(ValNull)
    eval("x > y") should be(ValNull)
  }

  it should "compare with `<=`" in {
    eval("x <= 1") should be(ValNull)
    eval("1 <= x") should be(ValNull)
    eval("x <= true") should be(ValNull)
    eval("true <= x") should be(ValNull)
    eval(""" x <= "string" """) should be(ValNull)
    eval(""" "string" <= x """) should be(ValNull)
    eval("x <= null") should be(ValNull)
    eval("null <= x") should be(ValNull)
    eval("x <= y") should be(ValNull)
  }

  it should "compare with `>=`" in {
    eval("x >= 1") should be(ValNull)
    eval("1 >= x") should be(ValNull)
    eval("x >= true") should be(ValNull)
    eval("true >= x") should be(ValNull)
    eval(""" x >= "string" """) should be(ValNull)
    eval(""" "string" >= x """) should be(ValNull)
    eval("x >= null") should be(ValNull)
    eval("null >= x") should be(ValNull)
    eval("x >= y") should be(ValNull)
  }

  it should "compare with `between _ and _`" in {
    eval("x between 1 and 3") should be(ValNull)
    eval("1 between x and 3") should be(ValNull)
    eval("3 between 1 and x") should be(ValNull)
    eval("x between y and 3") should be(ValNull)
    eval("x between 1 and y") should be(ValNull)
    eval("x between y and z") should be(ValNull)
  }
}
