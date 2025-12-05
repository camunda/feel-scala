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

import org.camunda.feel.api.EvaluationFailureType
import org.camunda.feel.api.EvaluationFailureType.INVALID_TYPE
import org.camunda.feel.context.{CustomContext, VariableProvider}
import org.camunda.feel.impl.EvaluationResultMatchers.returnResult
import org.camunda.feel.impl.{EvaluationResultMatchers, FeelEngineTest}
import org.camunda.feel.syntaxtree._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable.ListBuffer

/** @author
  *   Philipp Ossler
  */
class InterpreterListExpressionTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

  "A list" should "contain null if a variable doesn't exist" in {
    evaluateExpression("[1, x]") should returnResult(List(1, null))
  }

  it should "be compared with '='" in {
    evaluateExpression("[] = []") should returnResult(true)
    evaluateExpression("[1] = [1]") should returnResult(true)
    evaluateExpression("[[1]] = [[1]]") should returnResult(true)
    evaluateExpression("[{x:1}] = [{x:1}]") should returnResult(true)

    evaluateExpression("[] = [1]") should returnResult(false)
    evaluateExpression("[1] = []") should returnResult(false)
    evaluateExpression("[1] = [2]") should returnResult(false)
    evaluateExpression("[[1]] = [[2]]") should returnResult(false)
    evaluateExpression("[{x:1}] = [{x:2}]") should returnResult(false)

    evaluateExpression("[1] = [true]") should returnResult(false)
  }

  it should "be compared with '!='" in {
    evaluateExpression("[] != []") should returnResult(false)
    evaluateExpression("[1] != [1]") should returnResult(false)
    evaluateExpression("[[1]] != [[1]]") should returnResult(false)
    evaluateExpression("[{x:1}] != [{x:1}]") should returnResult(false)

    evaluateExpression("[] != [1]") should returnResult(true)
    evaluateExpression("[1] != []") should returnResult(true)
    evaluateExpression("[1] != [2]") should returnResult(true)
    evaluateExpression("[[1]] != [[2]]") should returnResult(true)
    evaluateExpression("[{x:1}] != [{x:2}]") should returnResult(true)

    evaluateExpression("[1] != [true]") should returnResult(true)
  }

  it should "be accessed and compared" in {
    evaluateExpression("[1][1] = 1") should returnResult(true)
  }

  it should "return null if compare to not a list" in {
    evaluateExpression("[] = 1") should (returnNull() and reportFailure(
      failureType = EvaluationFailureType.NOT_COMPARABLE,
      failureMessage = "Can't compare '[]' with '1'"
    ))
  }

  "A some-expression" should "return true if one item satisfies the condition" in {

    evaluateExpression("some x in [1,2,3] satisfies x > 2") should returnResult(true)

    evaluateExpression(
      expression = "some x in xs satisfies x > 1",
      variables = Map("xs" -> List(1, 2, 3))
    ) should returnResult(true)

    evaluateExpression(
      expression = "some x in xs satisfies count(xs) > 2",
      variables = Map("xs" -> List(1, 2, 3))
    ) should returnResult(true)

    evaluateExpression("some x in [1,2], y in [2,3] satisfies x < y") should returnResult(true)
  }

  it should "return false if no item satisfies the condition" in {

    evaluateExpression("some x in [1,2,3] satisfies x > 3") should returnResult(false)

    evaluateExpression(
      expression = "some x in xs satisfies x > 2",
      variables = Map("xs" -> List(1, 2))
    ) should returnResult(false)

    evaluateExpression("some x in [1,2], y in [1,1] satisfies x < y") should returnResult(false)
  }

  it should "return true if the range satisfies the condition" in {
    evaluateExpression("some x in 1..5 satisfies x > 3") should returnResult(true)
  }

  it should "return false if the range doesn't satisfy the condition" in {
    evaluateExpression("some x in 1..5 satisfies x > 10") should returnResult(false)
  }

  it should "return null if the value is not a list" in {
    evaluateExpression(
      expression = "some item in x satisfies x > 10"
    ) should (returnNull() and reportFailure(
      INVALID_TYPE,
      "Expected list but found 'null'"
    ))

    evaluateExpression(
      expression = "some item in x satisfies x > 10",
      variables = Map("x" -> 2)
    ) should (returnNull() and reportFailure(
      INVALID_TYPE,
      "Expected list but found '2'"
    ))
  }

  "An every-expression" should "return true if all items satisfy the condition" in {

    evaluateExpression("every x in [1,2,3] satisfies x >= 1") should returnResult(true)

    evaluateExpression(
      expression = "every x in xs satisfies x >= 1",
      variables = Map("xs" -> List(1, 2, 3))
    ) should returnResult(true)

    evaluateExpression("every x in [1,2], y in [3,4] satisfies x < y") should returnResult(true)
  }

  it should "return false if one item doesn't satisfy the condition" in {

    evaluateExpression("every x in [1,2,3] satisfies x >= 2") should returnResult(false)

    evaluateExpression(
      expression = "every x in xs satisfies x >= 1",
      variables = Map("xs" -> List(0, 1, 2, 3))
    ) should returnResult(false)

    evaluateExpression("every x in [1,2], y in [2,3] satisfies x < y") should returnResult(false)
  }

  it should "return true if the range satisfies the condition" in {
    evaluateExpression("every x in 1..5 satisfies x < 10") should returnResult(true)
  }

  it should "return false if the range doesn't satisfy the condition" in {
    evaluateExpression("every x in 1..10 satisfies x < 5") should returnResult(false)
  }

  it should "return null if the value is not a list" in {
    evaluateExpression(
      expression = "every item in x satisfies x > 10"
    ) should (returnNull() and reportFailure(
      INVALID_TYPE,
      "Expected list but found 'null'"
    ))

    evaluateExpression(
      expression = "every item in x satisfies x > 10",
      variables = Map("x" -> 2)
    ) should (returnNull() and reportFailure(
      INVALID_TYPE,
      "Expected list but found '2'"
    ))
  }

  "A for-expression" should "iterate over a list" in {
    evaluateExpression("for x in [1,2] return x * 2") should returnResult(
      List(2, 4)
    )

    evaluateExpression("for x in [1,2], y in [3,4] return x * y") should returnResult(
      List(3, 4, 6, 8)
    )

    evaluateExpression(
      expression = "for x in xs return x * 2",
      variables = Map("xs" -> List(1, 2))
    ) should returnResult(List(2, 4))

    evaluateExpression(
      expression = "for y in xs return index of([2, 3], y)",
      variables = Map("xs" -> List(1, 2))
    ) should returnResult(List(List.empty, List(1)))
  }

  it should "iterate over a range" in {
    evaluateExpression("for x in 1..3 return x * 2") should returnResult(List(2, 4, 6))

    evaluateExpression("for x in 1..n return x * 2", Map("n" -> 3)) should returnResult(
      List(2, 4, 6)
    )
  }

  it should "iterate over a range in descending order" in {
    evaluateExpression("for x in 3..1 return x * 2") should returnResult(List(6, 4, 2))

    evaluateExpression("for x in n..1 return x * 2", Map("n" -> 3)) should returnResult(
      List(6, 4, 2)
    )
  }

  it should "access the partial result" in {
    evaluateExpression(
      "for x in 1..5 return if (x = 1) then 1 else x + sum(partial)"
    ) should returnResult(
      List(1, 3, 7, 15, 31)
    )

    evaluateExpression(
      "for i in 1..8 return if (i <= 2) then 1 else partial[-1] + partial[-2]"
    ) should returnResult(List(1, 1, 2, 3, 5, 8, 13, 21))
  }

  it should "return null if the value is not a list" in {
    evaluateExpression(
      expression = "for item in x return item * 2"
    ) should (returnNull() and reportFailure(
      INVALID_TYPE,
      "Expected list but found 'null'"
    ))

    evaluateExpression(
      expression = "for item in x return item * 2",
      variables = Map("x" -> 2)
    ) should (returnNull() and reportFailure(
      INVALID_TYPE,
      "Expected list but found '2'"
    ))
  }

  private val hugeList: List[Int] = (1 to 10000).toList

  "A huge list" should "be defined as range" in {
    evaluateExpression("for x in 1..10000 return x") should returnResult(hugeList)
  }

  it should "be checked with 'some'" in {
    evaluateExpression(
      "some x in xs satisfies x >= 10000",
      Map("xs" -> hugeList)
    ) should returnResult(true)

    evaluateExpression(
      "some x in xs satisfies x > 10000",
      Map("xs" -> hugeList)
    ) should returnResult(false)
  }

  it should "be checked with 'some' (invalid condition)" in {
    evaluateExpression("some x in xs satisfies null", Map("xs" -> hugeList)) should returnNull()
  }

  it should "be checked with 'every'" in {
    evaluateExpression("every x in xs satisfies x > 0", Map("xs" -> hugeList)) should returnResult(
      true
    )
  }

  it should "be checked with 'every' (invalid condition)" in {
    evaluateExpression("every x in xs satisfies null", Map("xs" -> hugeList)) should returnNull()
  }

  it should "be iterated with `for`" in {
    evaluateExpression("for x in xs return x", Map("xs" -> hugeList)) should returnResult(hugeList)
  }

  it should "be filtered" in {
    evaluateExpression("xs[item <= 5000]", Map("xs" -> hugeList)) should returnResult(
      hugeList.take(5000)
    )
  }

  it should "be accessed by index" in {
    evaluateExpression("xs[-1]", Map("xs" -> hugeList)) should returnResult(
      hugeList.last
    )
  }

  "A filter expression" should "access the item" in {
    evaluateExpression("[1,2,3,4][item > 2]") should returnResult(
      List(3, 4)
    )

    evaluateExpression(
      expression = "xs [item > 2]",
      variables = Map("xs" -> List(1, 2, 3, 4))
    ) should returnResult(List(3, 4))
  }

  it should "compare the item with null" in {
    // items that are not comparable to null are ignored
    evaluateExpression("[1,2,3,4][item > null]") should returnResult(List.empty)

    // items that are not comparable to null are ignored
    evaluateExpression("[1,2,3,4][item < null]") should returnResult(List.empty)
  }

  it should "compare the item if the item is null" in {
    // null is not comparable to 2, so it's ignored
    evaluateExpression("[1,2,null,4][item > 2]") should returnResult(List(4))

    // null is the only item for which the comparison returns true
    evaluateExpression("[1,2,null,4][item = null]") should returnResult(List(null))
  }

  it should "compare the item if the item is a missing variable" in {
    // null is the only item for which the comparison returns true
    evaluateExpression("[1,2,x,4][item = null]") should returnResult(List(null))

    // missing variable becomes null, so same as direct null item
    evaluateExpression("[1,2,x,4][item > 2]") should returnResult(List(4))
  }

  it should "access an item by index" in {
    evaluateExpression("[1,2,3,4][1]") should returnResult(1)
    evaluateExpression("[1,2,3,4][2]") should returnResult(2)

    evaluateExpression("[1,2,3,4][-1]") should returnResult(4)
    evaluateExpression("[1,2,3,4][-2]") should returnResult(3)

    evaluateExpression("[1,2,3,4][5]") should returnNull()
    evaluateExpression("[1,2,3,4][-5]") should returnNull()

    evaluateExpression("[1,2,3,4][i]", Map("i" -> 2)) should returnResult(2)
    evaluateExpression("[1,2,3,4][i]", Map("i" -> -2)) should returnResult(3)
  }

  it should "compare the item with a boolean expression" in {
    evaluateExpression("[1,2,3,4][odd(item)]") should returnResult(List(1, 3))

    evaluateExpression("[1,2,3,4][even(item)]") should returnResult(List(2, 4))
  }

  it should "access an item by a numeric function" in {
    evaluateExpression("[1,2,3,4][abs(1)]") should returnResult(1)

    evaluateExpression("[1,2,3,4][modulo(2,4)]") should returnResult(2)
  }

  it should "compare the item with a custom boolean function" in {
    val functionInvocations: ListBuffer[Val] = ListBuffer.empty

    val result = evaluateExpression(
      expression = "[1,2,3,4][f(item)]",
      variables = Map(),
      functions = Map(
        "f" -> ValFunction(
          params = List("x"),
          invoke = { case List(x) =>
            functionInvocations += x
            ValBoolean(x == ValNumber(3))
          }
        )
      )
    )

    result should returnResult(List(3))

    functionInvocations should be(List(ValNumber(1), ValNumber(2), ValNumber(3), ValNumber(4)))
  }

  it should "access the item with a custom numeric function" in {
    val functionInvocations: ListBuffer[Val] = ListBuffer.empty

    val result = evaluateExpression(
      expression = "[1,2,3,4][f(item)]",
      variables = Map(),
      functions = Map(
        "f" -> ValFunction(
          params = List("x"),
          invoke = { case List(x) =>
            functionInvocations += x
            ValNumber(3)
          }
        )
      )
    )

    result should returnResult(3)

    functionInvocations should be(List(ValNumber(1)))
  }

  it should "access a nested item by index (from literal)" in {
    evaluateExpression("[[1]][1][1]") should returnResult(1)
    evaluateExpression("[[[1]]][1][1][1]") should returnResult(1)
    evaluateExpression("[[[[1]]]][1][1][1][1]") should returnResult(1)
  }

  it should "access a nested item by index (from variable)" in {
    val listOfLists = List(List(1))

    evaluateExpression("xs[1][1]", Map("xs" -> listOfLists)) should returnResult(1)
    evaluateExpression("xs[1][1][1]", Map("xs" -> List(listOfLists))) should returnResult(1)
    evaluateExpression("xs[1][1][1][1]", Map("xs" -> List(List(listOfLists)))) should returnResult(
      1
    )
  }

  it should "access a nested item by index (from function invocation)" in {
    evaluateExpression("append([], [1])[1][1]") should returnResult(1)
    evaluateExpression("append([], [[1]])[1][1][1]") should returnResult(1)
    evaluateExpression("append([], [[[1]]])[1][1][1][1]") should returnResult(1)
  }

  it should "access a nested item by index (from path)" in {
    val listOfLists = List(List(1))

    evaluateExpression("x.y[1][1]", Map("x" -> Map("y" -> listOfLists))) should returnResult(1)
    evaluateExpression(
      "x.y[1][1][1]",
      Map("x" -> Map("y" -> List(listOfLists)))
    ) should returnResult(1)
    evaluateExpression(
      "x.y[1][1][1][1]",
      Map("x" -> Map("y" -> List(List(listOfLists))))
    ) should returnResult(1)
  }

  it should "access a nested item by index (from context projection)" in {
    evaluateExpression("{x:[[1]]}.x[1][1]") should returnResult(1)
    evaluateExpression("{x:[[[1]]]}.x[1][1][1]") should returnResult(1)
    evaluateExpression("{x:[[[[1]]]]}.x[1][1][1][1]") should returnResult(1)
  }

  it should "access a nested item by index (in a context)" in {
    val listOfLists = List(List(1))

    evaluateExpression("{z: x.y[1][1]}.z", Map("x" -> Map("y" -> listOfLists))) should returnResult(
      1
    )
    evaluateExpression(
      "{z: x.y[1][1][1]}.z",
      Map("x" -> Map("y" -> List(listOfLists)))
    ) should returnResult(1)
    evaluateExpression(
      "{z: x.y[1][1][1][1]}.z",
      Map("x" -> Map("y" -> List(List(listOfLists))))
    ) should returnResult(1)
  }

  it should "ignore items if the filter doesn't return a boolean or a number" in {
    evaluateExpression(""" [1,2,3,4]["not a valid filter"] """) should returnResult(List.empty)
    evaluateExpression("[1,2,3,4][if item < 3 then true else null]") should returnResult(List(1, 2))
  }

  it should "access an item property if the context contains a variable with the same name" in {
    evaluateExpression(
      expression = """sum({"loans" : [
                           {"loanId" : "AAA001", "amount" : 10},
                           {"loanId" : "AAA002", "amount" : 20},
                           {"loanId" : "AAA001", "amount" : 50}
                         ]}.loans[loanId = id].amount)""",
      variables = Map("id" -> "AAA002", "loanId" -> "AAA002")
    ) should returnResult(20)
  }

  it should "access an item property if the custom context contains a variable with the same name" in {
    evaluateExpression(
      expression = """sum({"loans" : [
                           {"loanId" : "AAA001", "amount" : 10},
                           {"loanId" : "AAA002", "amount" : 20},
                           {"loanId" : "AAA001", "amount" : 50}
                         ]}.loans[loanId = id].amount)""",
      context = new MyCustomContext(Map("id" -> "AAA002", "loanId" -> "AAA002"))
    ) should returnResult(20)
  }

  it should "return null if the value is not a list" in {
    evaluateExpression(
      expression = "x[even(item)]"
    ) should (returnNull() and reportFailure(
      INVALID_TYPE,
      "Expected list but found 'null'"
    ))

    evaluateExpression(
      expression = "x[even(item)]",
      variables = Map("x" -> 2)
    ) should (returnNull() and reportFailure(
      INVALID_TYPE,
      "Expected list but found '2'"
    ))

    evaluateExpression(
      expression = "x[1]"
    ) should (returnNull() and reportFailure(
      INVALID_TYPE,
      "Expected list but found 'null'"
    ))
  }

  it should "compute a long list" in {
    evaluateExpression(
      """count(for x in 0..1000000 return "Hi there")"""
    ) should returnResult(1000001)
  }
}
