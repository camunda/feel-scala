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
class InterpreterListExpressionTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A list" should "be checked with 'some'" in {

    eval("some x in [1,2,3] satisfies x > 2") should be(ValBoolean(true))
    eval("some x in [1,2,3] satisfies x > 3") should be(ValBoolean(false))

    eval("some x in xs satisfies x > 2", Map("xs" -> List(1, 2, 3))) should be(
      ValBoolean(true))
    eval("some x in xs satisfies x > 2", Map("xs" -> List(1, 2))) should be(
      ValBoolean(false))

    eval("some x in [1,2], y in [2,3] satisfies x < y") should be(
      ValBoolean(true))
    eval("some x in [1,2], y in [1,1] satisfies x < y") should be(
      ValBoolean(false))
  }

  it should "be checked with 'every'" in {

    eval("every x in [1,2,3] satisfies x >= 1") should be(ValBoolean(true))
    eval("every x in [1,2,3] satisfies x >= 2") should be(ValBoolean(false))

    eval("every x in xs satisfies x >= 1", Map("xs" -> List(1, 2, 3))) should be(
      ValBoolean(true))
    eval("every x in xs satisfies x >= 1", Map("xs" -> List(0, 1, 2, 3))) should be(
      ValBoolean(false))

    eval("every x in [1,2], y in [3,4] satisfies x < y") should be(
      ValBoolean(true))
    eval("every x in [1,2], y in [2,3] satisfies x < y") should be(
      ValBoolean(false))
  }

  it should "be processed in a for-expression" in {

    eval("for x in [1,2] return x * 2") should be(
      ValList(List(ValNumber(2), ValNumber(4))))

    eval("for x in [1,2], y in [3,4] return x * y") should be(
      ValList(List(ValNumber(3), ValNumber(4), ValNumber(6), ValNumber(8))))

    eval("for x in xs return x * 2", Map("xs" -> List(1, 2))) should be(
      ValList(List(ValNumber(2), ValNumber(4))))
  }

  it should "be filtered via boolean expression" in {

    eval("[1,2,3,4][item > 2]") should be(
      ValList(List(ValNumber(3), ValNumber(4))))

    eval("xs [item > 2]", Map("xs" -> List(1, 2, 3, 4))) should be(
      ValList(List(ValNumber(3), ValNumber(4))))
  }

  it should "be filtered via index" in {

    eval("[1,2,3,4][1]") should be(ValNumber(1))
    eval("[1,2,3,4][2]") should be(ValNumber(2))

    eval("[1,2,3,4][-1]") should be(ValNumber(4))
    eval("[1,2,3,4][-2]") should be(ValNumber(3))

    eval("[1,2,3,4][5]") should be(ValNull)
    eval("[1,2,3,4][-5]") should be(ValNull)

    eval("[1,2,3,4][i]", Map("i" -> 2)) should be(ValNumber(2))
    eval("[1,2,3,4][i]", Map("i" -> -2)) should be(ValNumber(3))
  }

  it should "fail if one element fails" in {

    eval("[1, {}.x]") should be(
      ValError("context contains no entry with key 'x'"))
  }

  it should "be compared with '='" in {

    eval("[] = []") should be(ValBoolean(true))
    eval("[1] = [1]") should be(ValBoolean(true))
    eval("[[1]] = [[1]]") should be(ValBoolean(true))
    eval("[{x:1}] = [{x:1}]") should be(ValBoolean(true))

    eval("[] = [1]") should be(ValBoolean(false))
    eval("[1] = []") should be(ValBoolean(false))
    eval("[1] = [2]") should be(ValBoolean(false))
    eval("[[1]] = [[2]]") should be(ValBoolean(false))
    eval("[{x:1}] = [{x:2}]") should be(ValBoolean(false))

    eval("[1] = [true]") should be(ValBoolean(false))
  }

  it should "be compared with '!='" in {

    eval("[] != []") should be(ValBoolean(false))
    eval("[1] != [1]") should be(ValBoolean(false))
    eval("[[1]] != [[1]]") should be(ValBoolean(false))
    eval("[{x:1}] != [{x:1}]") should be(ValBoolean(false))

    eval("[] != [1]") should be(ValBoolean(true))
    eval("[1] != []") should be(ValBoolean(true))
    eval("[1] != [2]") should be(ValBoolean(true))
    eval("[[1]] != [[2]]") should be(ValBoolean(true))
    eval("[{x:1}] != [{x:2}]") should be(ValBoolean(true))

    eval("[1] != [true]") should be(ValBoolean(true))
  }

  it should "fail to compare if not a list" in {

    eval("[] = 1") should be(
      ValError("expect List but found 'ValNumber(1)'")
    )
  }

  "A for-expression" should "iterate over a range" in {

    eval("for x in 1..3 return x * 2") should be(
      ValList(List(ValNumber(2), ValNumber(4), ValNumber(6))))

    eval("for x in 1..n return x * 2", Map("n" -> 3)) should be(
      ValList(List(ValNumber(2), ValNumber(4), ValNumber(6))))
  }

  it should "iterate over a range in descending order" in {

    eval("for x in 3..1 return x * 2") should be(
      ValList(List(ValNumber(6), ValNumber(4), ValNumber(2))))

    eval("for x in n..1 return x * 2", Map("n" -> 3)) should be(
      ValList(List(ValNumber(6), ValNumber(4), ValNumber(2))))
  }

  it should "access the partial result" in {

    eval("for x in 1..5 return if (x = 1) then 1 else x + sum(partial)") should be(
      ValList(
        List(ValNumber(1),
             ValNumber(3),
             ValNumber(7),
             ValNumber(15),
             ValNumber(31))))

    eval(
      "for i in 1..8 return if (i <= 2) then 1 else partial[-1] + partial[-2]") should be(
      ValList(
        List(ValNumber(1),
             ValNumber(1),
             ValNumber(2),
             ValNumber(3),
             ValNumber(5),
             ValNumber(8),
             ValNumber(13),
             ValNumber(21))))
  }

}
