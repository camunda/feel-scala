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
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.mutable.ListBuffer

/**
  * @author Philipp Ossler
  */
class InterpreterListExpressionTest
    extends AnyFlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A list" should "be checked with 'some'" in {

    eval("some x in [1,2,3] satisfies x > 2") should be(ValBoolean(true))
    eval("some x in [1,2,3] satisfies x > 3") should be(ValBoolean(false))

    eval("some x in xs satisfies x > 2", Map("xs" -> List(1, 2, 3))) should be(
      ValBoolean(true))
    eval("some x in xs satisfies x > 2", Map("xs" -> List(1, 2))) should be(
      ValBoolean(false))
    eval("some x in xs satisfies count(xs) > 2", Map("xs" -> List(1, 2))) should be(
      ValBoolean(false))

    eval("some x in [1,2], y in [2,3] satisfies x < y") should be(
      ValBoolean(true))
    eval("some x in [1,2], y in [1,1] satisfies x < y") should be(
      ValBoolean(false))
  }

  it should "be checked with 'some' (range)" in {
    eval("some x in 1..5 satisfies x > 3") should be(ValBoolean(true))
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

  it should "be checked with 'every' (range)" in {
    eval("every x in 1..5 satisfies x < 10") should be(ValBoolean(true))
  }

  it should "be processed in a for-expression" in {

    eval("for x in [1,2] return x * 2") should be(
      ValList(List(ValNumber(2), ValNumber(4))))

    eval("for x in [1,2], y in [3,4] return x * y") should be(
      ValList(List(ValNumber(3), ValNumber(4), ValNumber(6), ValNumber(8))))

    eval("for x in xs return x * 2", Map("xs" -> List(1, 2))) should be(
      ValList(List(ValNumber(2), ValNumber(4))))

    eval("for y in xs return index of([2, 3], y)",  Map("xs" -> List(1, 2))) should be(
      ValList(List(ValList(List()), ValList(List(ValNumber(1)))))
    )
  }

  it should "be processed in a for-expression (range)" in {
    eval("for x in 1..3 return x") should be(
      ValList(
        List(
          ValNumber(1),
          ValNumber(2),
          ValNumber(3)
        )))
  }

  it should "be filtered via comparison" in {

    eval("[1,2,3,4][item > 2]") should be(
      ValList(List(ValNumber(3), ValNumber(4))))

    eval("xs [item > 2]", Map("xs" -> List(1, 2, 3, 4))) should be(
      ValList(List(ValNumber(3), ValNumber(4))))

    // items that are not comparable to null are ignored
    eval("[1,2,3,4][item > null]") should be(
      ValList(List()))

    // items that are not comparable to null are ignored
    eval("[1,2,3,4][item < null]") should be(
      ValList(List()))

    // null is not comparable to 2, so it's ignored
    eval("[1,2,null,4][item > 2]") should be(
      ValList(List(ValNumber(4))))

    // null is the only item for which the comparison returns true
    eval("[1,2,null,4][item = null]") should be(
      ValList(List(ValNull)))
  }

  ignore should "be filtered via comparison with missing variable" in {
    // null is the only item for which the comparison returns true
    eval("[1,2,x,4][item = null]") should be(
      ValList(List(ValNull)))

    // missing variable becomes null, so same as direct null item
    eval("[1,2,x,4][item > 2]") should be(
      ValList(List(ValNumber(4))))
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

  it should "be filtered via boolean expression" in {
    eval("[1,2,3,4][odd(item)]") should be(
      ValList(List(ValNumber(1), ValNumber(3))))

    eval("[1,2,3,4][even(item)]") should be(
      ValList(List(ValNumber(2), ValNumber(4))))
  }

  it should "be filtered via numeric function" in {
    eval("[1,2,3,4][abs(1)]") should be(ValNumber(1))

    eval("[1,2,3,4][modulo(2,4)]") should be(ValNumber(2))
  }

  it should "be filtered via custom boolean function" in {
    val functionInvocations: ListBuffer[Val] = ListBuffer.empty

    val result = eval(
      expression = "[1,2,3,4][f(item)]",
      variables = Map(),
      functions = Map("f" -> ValFunction(
        params = List("x"),
        invoke = {
          case List(x) =>
            functionInvocations += x
            ValBoolean(x == ValNumber(3))
        }
      )))

    result should be(ValList(List(ValNumber(3))))

    functionInvocations should be(List(
      ValNumber(1),
      ValNumber(2),
      ValNumber(3),
      ValNumber(4))
    )
  }

  it should "be filtered via custom numeric function" in {
    val functionInvocations: ListBuffer[Val] = ListBuffer.empty

    val result = eval(
      expression = "[1,2,3,4][f(item)]",
      variables = Map(),
      functions = Map("f" -> ValFunction(
        params = List("x"),
        invoke = {
          case List(x) =>
            functionInvocations += x
            ValNumber(3)
        }
      )))

    result should be(ValNumber(3))

    functionInvocations should be(List(
      ValNumber(1))
    )
  }

  it should "be filtered multiple times (from literal)" in {
    eval("[[1]][1][1]") should be(ValNumber(1))
    eval("[[[1]]][1][1][1]") should be(ValNumber(1))
    eval("[[[[1]]]][1][1][1][1]") should be(ValNumber(1))
  }

  it should "be filtered multiple times (from variable)" in {
    val listOfLists = List(List(1))

    eval("xs[1][1]", Map("xs" -> listOfLists)) should be(ValNumber(1))
    eval("xs[1][1][1]", Map("xs" -> List(listOfLists))) should be(ValNumber(1))
    eval("xs[1][1][1][1]", Map("xs" -> List(List(listOfLists)))) should be(
      ValNumber(1))
  }

  it should "be filtered multiple times (from function invocation)" in {
    eval("append([], [1])[1][1]") should be(ValNumber(1))
    eval("append([], [[1]])[1][1][1]") should be(ValNumber(1))
    eval("append([], [[[1]]])[1][1][1][1]") should be(ValNumber(1))
  }

  it should "be filtered multiple times (from path)" in {
    val listOfLists = List(List(1))

    eval("x.y[1][1]", Map("x" -> Map("y" -> listOfLists))) should be(
      ValNumber(1))
    eval("x.y[1][1][1]", Map("x" -> Map("y" -> List(listOfLists)))) should be(
      ValNumber(1))
    eval("x.y[1][1][1][1]", Map("x" -> Map("y" -> List(List(listOfLists))))) should be(
      ValNumber(1))
  }

  it should "be filtered multiple times (from context projection)" in {
    eval("{x:[[1]]}.x[1][1]") should be(ValNumber(1))
    eval("{x:[[[1]]]}.x[1][1][1]") should be(ValNumber(1))
    eval("{x:[[[[1]]]]}.x[1][1][1][1]") should be(ValNumber(1))
  }

  it should "be filtered multiple times (in a context)" in {
    val listOfLists = List(List(1))

    eval("{z: x.y[1][1]}.z", Map("x" -> Map("y" -> listOfLists))) should be(
      ValNumber(1))
    eval("{z: x.y[1][1][1]}.z", Map("x" -> Map("y" -> List(listOfLists)))) should be(
      ValNumber(1))
    eval("{z: x.y[1][1][1][1]}.z",
         Map("x" -> Map("y" -> List(List(listOfLists))))) should be(
      ValNumber(1))
  }

  it should "fail if the filter doesn't return a boolean or a number" in {
    eval(""" [1,2,3,4]["not a valid filter"] """) should be (
      ValError("Expected boolean filter or number but found 'ValString(not a valid filter)'")
    )
  }

  it should "fail if the filter doesn't return always a boolean" in {
    eval("[1,2,3,4][if item < 3 then true else null]") should be (
      ValError("Expected Boolean but found 'ValNull'")
    )
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

  it should "be accessed and compared" in {
    eval("[1][1] = 1") should be(ValBoolean(true))
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

  private val hugeList: List[Int] = (1 to 10000).toList

  "A huge list" should "be defined as range" in {
    eval("for x in 1..10000 return x") should be(
      ValList(
        hugeList.map(ValNumber(_))
      ))
  }

  it should "be checked with 'some'" in {
    eval("some x in xs satisfies x >= 10000", Map("xs" -> hugeList)) should be(
      ValBoolean(true))

    eval("some x in xs satisfies x > 10000", Map("xs" -> hugeList)) should be(
      ValBoolean(false))
  }

  it should "be checked with 'some' (invalid condition)" in {
    eval("some x in xs satisfies null", Map("xs" -> hugeList)) should be(
      ValNull)
  }

  it should "be checked with 'every'" in {
    eval("every x in xs satisfies x > 0", Map("xs" -> hugeList)) should be(
      ValBoolean(true))
  }

  it should "be checked with 'every' (invalid condition)" in {
    eval("every x in xs satisfies null", Map("xs" -> hugeList)) should be(
      ValNull)
  }

  it should "be iterated with `for`" in {
    eval("for x in xs return x", Map("xs" -> hugeList)) should be(
      ValList(
        hugeList.map(ValNumber(_))
      ))
  }

  it should "be filtered" in {
    eval("xs[item <= 5000]", Map("xs" -> hugeList)) should be(
      ValList(
        hugeList.take(5000).map(ValNumber(_))
      ))
  }

  it should "be accessed by index" in {
    eval("xs[-1]", Map("xs" -> hugeList)) should be(
      ValNumber(hugeList.last)
    )
  }

}
