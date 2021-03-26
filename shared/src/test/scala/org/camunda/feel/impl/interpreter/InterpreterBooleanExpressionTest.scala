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

import org.camunda.feel.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree._
import org.scalatest.{FlatSpec, Matchers}

/**
  * @author Philipp Ossler
  */
class InterpreterBooleanExpressionTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A boolean" should "compare with '='" in {

    eval("true = true") should be(ValBoolean(true))
    eval("true = false") should be(ValBoolean(false))
  }

  it should "compare with null" in {

    eval(""" true = null """) should be(ValBoolean(false))
    eval(""" null = false """) should be(ValBoolean(false))
    eval(""" true != null """) should be(ValBoolean(true))
    eval(""" null != false """) should be(ValBoolean(true))
  }

  it should "be in conjunction" in {

    eval("true and true") should be(ValBoolean(true))
    eval("true and false") should be(ValBoolean(false))

    eval("true and true and false") should be(ValBoolean(false))

    eval("true and 2") should be(ValNull)
    eval("false and 2") should be(ValBoolean(false))

    eval("2 and true") should be(ValNull)
    eval("2 and false") should be(ValBoolean(false))

    eval("2 and 4") should be(ValNull)
  }

  it should "be in disjunction" in {

    eval("false or true") should be(ValBoolean(true))
    eval("false or false") should be(ValBoolean(false))

    eval("false or false or true") should be(ValBoolean(true))

    eval("true or 2") should be(ValBoolean(true))
    eval("false or 2") should be(ValNull)

    eval("2 or true") should be(ValBoolean(true))
    eval("2 or false") should be(ValNull)

    eval("2 or 4") should be(ValNull)
  }

  it should "negate" in {

    eval("not(true)") should be(ValBoolean(false))
    eval("not(false)") should be(ValBoolean(true))

    eval("not(2)") should be(ValNull)
  }

}
