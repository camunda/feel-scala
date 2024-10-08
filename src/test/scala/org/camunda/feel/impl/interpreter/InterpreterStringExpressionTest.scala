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

import org.camunda.feel.impl.{EvaluationResultMatchers, FeelEngineTest, FeelIntegrationTest}
import org.camunda.feel.syntaxtree._
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.prop.TableDrivenPropertyChecks

import scala.collection.immutable.Map

/** @author
  *   Philipp Ossler
  */
class InterpreterStringExpressionTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers
    with TableDrivenPropertyChecks {

  "A string" should "concatenates to another String" in {

    evaluateExpression(""" "a" + "b" """) should returnResult("ab")
  }

  it should "compare with '='" in {

    evaluateExpression(""" "a" = "a" """) should returnResult(true)
    evaluateExpression(""" "a" = "b" """) should returnResult(false)
  }

  it should "compare with '!='" in {

    evaluateExpression(""" "a" != "a" """) should returnResult(false)
    evaluateExpression(""" "a" != "b" """) should returnResult(true)
  }

  it should "compare with '<'" in {

    evaluateExpression(""" "a" < "b" """) should returnResult(true)
    evaluateExpression(""" "b" < "a" """) should returnResult(false)
  }

  it should "compare with '<='" in {

    evaluateExpression(""" "a" <= "a" """) should returnResult(true)
    evaluateExpression(""" "b" <= "a" """) should returnResult(false)
  }

  it should "compare with '>'" in {

    evaluateExpression(""" "b" > "a" """) should returnResult(true)
    evaluateExpression(""" "a" > "b" """) should returnResult(false)
  }

  it should "compare with '>='" in {

    evaluateExpression(""" "b" >= "b" """) should returnResult(true)
    evaluateExpression(""" "a" >= "b" """) should returnResult(false)
  }

  it should "compare with null" in {

    evaluateExpression(""" "a" = null """) should returnResult(false)
    evaluateExpression(""" null = "a" """) should returnResult(false)
    evaluateExpression(""" "a" != null """) should returnResult(true)
  }

  private val escapeSequences = Table(
    ("Character", "Expected", "Display name"),
    ('\n', '\n', "new line"),
    ('\r', '\r', "carriage return"),
    ('\t', '\t', "tab"),
    ('\b', '\b', "backspace"),
    ('\f', '\f', "form feed"),
    ('\'', '\'', "single quote"),
    ("\\\"", '"', "double quote"),
    ("\\\\", '\\', "backslash")
  )

  it should "contains an escape sequence" in {
    forEvery(escapeSequences) { (character, expected, _) =>
      val expectedString = s"a $expected b"

      evaluateExpression(s" \"a $character b\" ") should returnResult(expectedString)
      evaluateExpression("char", Map("char" -> expectedString)) should returnResult(expectedString)
    }
  }

  private val unicodeCharacters = Table(
    ("Character", "Display name"),
    ('\u269D', "\\u269D"),
    ("\\U101EF", "\\U101EF")
  )

  it should "contains unicode characters" in {
    forEvery(unicodeCharacters) { (character, _) =>
      evaluateExpression(s" \"a $character b\" ") should returnResult(s"a $character b")
    }
  }

  private val regexCharacters = Table(
    ("Character", "Display name"),
    ("\\s", "\\s"),
    ("\\S", "\\S"),
    ("\\d", "\\d"),
    ("\\w", "\\w"),
    ("\\R", "\\R"),
    ("\\h", "\\h"),
    ("\\v", "\\v"),
    ("\\\n", "\\n"),
    ("\\\r", "\\r")
  )

  it should "contains a regex character" in {
    forEvery(regexCharacters) { (character, _) =>
      val expectedString = s"a $character b"

      evaluateExpression(s" \"a $character b\" ") should returnResult(expectedString)
      evaluateExpression("char", Map("char" -> expectedString)) should returnResult(expectedString)
    }
  }

}
