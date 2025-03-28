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

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import org.camunda.feel.api.EvaluationFailureType.FUNCTION_INVOCATION_FAILURE
import org.camunda.feel.impl.{EvaluationResultMatchers, FeelEngineTest}

import java.time.LocalDate

/** @author
  *   Philipp
  */
class BuiltinListFunctionsTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

  "A list contains() function" should "return if the list contains Number" in {

    evaluateExpression(" list contains([1,2,3], 2) ") should returnResult(true)

    evaluateExpression(" list contains([1,2,3], 4) ") should returnResult(false)
  }

  it should "return if the list contains String" in {

    evaluateExpression(""" list contains(["a","b"], "a") """) should returnResult(true)

    evaluateExpression(""" list contains(["a","b"], "c") """) should returnResult(false)
  }

  "A count() function" should "return the size of a list" in {

    evaluateExpression(" count([1,2,3]) ") should returnResult(3)
  }

  "A min() function" should "return the null if empty list" in {

    evaluateExpression(" min([]) ") should returnNull()
  }

  it should "return the minimum item of numbers" in {

    evaluateExpression(" min([1,2,3]) ") should returnResult(1)
    evaluateExpression(" min(1,2,3) ") should returnResult(1)
  }

  it should "return the minimum item of date" in {

    evaluateExpression(
      """ min([date("2017-01-01"), date("2018-01-01"), date("2019-01-01")]) """
    ) should returnResult(LocalDate.parse("2017-01-01"))
  }

  it should "return null if value is not comparable" in {

    evaluateExpression(""" min([true, false]) """) should returnNull()
  }

  "A max() function" should "return the null if empty list" in {

    evaluateExpression(" max([]) ") should returnNull()
  }

  it should "return the maximum item of numbers" in {

    evaluateExpression(" max([1,2,3]) ") should returnResult(3)
    evaluateExpression(" max(1,2,3) ") should returnResult(3)
  }

  it should "return the maximum item of date" in {

    evaluateExpression(
      """ max([date("2017-01-01"), date("2018-01-01"), date("2019-01-01")]) """
    ) should returnResult(
      LocalDate.parse("2019-01-01")
    )
  }

  it should "return null if value is not comparable" in {

    evaluateExpression(""" max([true, false]) """) should returnNull()
  }

  "A sum() function" should "return null if empty list" in {

    evaluateExpression(" sum([]) ") should returnNull()
  }

  it should "return sum of numbers" in {

    evaluateExpression(" sum([1,2,3]) ") should returnResult(6)
    evaluateExpression(" sum(1,2,3) ") should returnResult(6)
  }

  "A mean() function" should "return null if empty list" in {

    evaluateExpression(" mean([]) ") should returnNull()
  }

  it should "return mean of numbers" in {

    evaluateExpression(" mean([1,2,3]) ") should returnResult(2)
    evaluateExpression(" mean(1,2,3) ") should returnResult(2)
  }

  "A median() function" should "return null if empty list" in {

    evaluateExpression(" median([]) ") should returnNull()
  }

  it should "return the median of numbers" in {

    evaluateExpression(" median(8, 2, 5, 3, 4) ") should returnResult(4)
    evaluateExpression(" median([6, 1, 2, 3]) ") should returnResult(2.5)
  }

  "A stddev() function" should "return null if empty list" in {

    evaluateExpression(" stddev([]) ") should returnNull()
  }

  it should "return the standard deviation" in {

    evaluateExpression(" stddev(2, 4, 7, 5) ") should returnResult(2.0816659994661326)
    evaluateExpression(" stddev([2, 4, 7, 5]) ") should returnResult(2.0816659994661326)
  }

  "A mode() function" should "return empty list if empty list" in {

    evaluateExpression(" mode([]) ") should returnResult(List.empty)
  }

  it should "return the mode of the list" in {

    evaluateExpression(" mode(6, 3, 9, 6, 6) ") should returnResult(List(6))
    evaluateExpression(" mode([6, 1, 9, 6, 1]) ") should returnResult(List(1, 6))
  }

  "A and() / all() function" should "return true if empty list" in {

    evaluateExpression(" and([]) ") should returnResult(true)
    evaluateExpression(" all([]) ") should returnResult(true)
  }

  it should "return true if all items are true" in {

    evaluateExpression(" and([false,null,true]) ") should returnResult(false)
    evaluateExpression(" all([false,null,true]) ") should returnResult(false)

    evaluateExpression(" and(false,null,true) ") should returnResult(false)
    evaluateExpression(" all(false,null,true) ") should returnResult(false)

    evaluateExpression(" and([true,true]) ") should returnResult(true)
    evaluateExpression(" all([true,true]) ") should returnResult(true)

    evaluateExpression(" and(true,true) ") should returnResult(true)
    evaluateExpression(" all(true,true) ") should returnResult(true)
  }

  it should "return null if argument is invalid" in {

    evaluateExpression("and(0)") should returnNull()
    evaluateExpression("all(0)") should returnNull()
  }

  it should "return null if one item is not a boolean value" in {

    evaluateExpression("and(true, null, true)") should returnNull()
    evaluateExpression("all(true, null, true)") should returnNull()
  }

  it should "return true if all items are true (huge list)" in {
    val hugeList = (1 to 10_000).map(_ => true).toList

    evaluateExpression("all(xs)", Map("xs" -> hugeList)) should returnResult(true)
  }

  it should "return null if items are not boolean values (huge list)" in {
    val hugeList = (1 to 10_000).toList

    evaluateExpression("all(xs)", Map("xs" -> hugeList)) should returnNull()
  }

  "A or() / any() function" should "return false if empty list" in {

    evaluateExpression(" or([]) ") should returnResult(false)
    evaluateExpression(" any([]) ") should returnResult(false)
  }

  it should "return false if all items are false" in {

    evaluateExpression(" or([false,null,true]) ") should returnResult(true)
    evaluateExpression(" any([false,null,true]) ") should returnResult(true)

    evaluateExpression(" or(false,null,true) ") should returnResult(true)
    evaluateExpression(" any(false,null,true) ") should returnResult(true)

    evaluateExpression(" or([false,false]) ") should returnResult(false)
    evaluateExpression(" any([false,false]) ") should returnResult(false)

    evaluateExpression(" or(false,false) ") should returnResult(false)
    evaluateExpression(" any(false,false) ") should returnResult(false)
  }

  it should "return null if argument is invalid" in {

    evaluateExpression("or(0)") should returnNull()
    evaluateExpression("any(0)") should returnNull()
  }

  it should "return null if one item is not a boolean value" in {

    evaluateExpression("or(false, null, false)") should returnNull()
    evaluateExpression("any(false, null, false)") should returnNull()
  }

  it should "return false if all items are false (huge list)" in {
    val hugeList = (1 to 10_000).map(_ => false).toList

    evaluateExpression("any(xs)", Map("xs" -> hugeList)) should returnResult(false)
  }

  it should "return null if items are not boolean values (huge list)" in {
    val hugeList = (1 to 10_000).toList

    evaluateExpression("any(xs)", Map("xs" -> hugeList)) should returnNull()
  }

  "A sublist() function" should "return list starting with _" in {

    evaluateExpression(" sublist([1,2,3], 2) ") should returnResult(List(2, 3))
  }

  it should "return list starting with _ and length _" in {

    evaluateExpression(" sublist([1,2,3], 1, 2) ") should returnResult(List(1, 2))
  }

  it should "return null if the start position is 0" in {

    evaluateExpression(" sublist([1,2,3], 0) ") should (returnNull() and reportFailure(
      failureType = FUNCTION_INVOCATION_FAILURE,
      failureMessage =
        "Failed to invoke function 'sublist': start position must be a non-zero number"
    ))

    evaluateExpression(" sublist([1,2,3], 0, 2) ") should (returnNull() and reportFailure(
      failureType = FUNCTION_INVOCATION_FAILURE,
      failureMessage =
        "Failed to invoke function 'sublist': start position must be a non-zero number"
    ))
  }

  "A append() function" should "return list with item appended" in {

    evaluateExpression(" append([1,2], 3) ") should returnResult(List(1, 2, 3))
    evaluateExpression(" append([1], 2, 3) ") should returnResult(List(1, 2, 3))
  }

  "A concatenate() function" should "return list with item appended" in {

    evaluateExpression(" concatenate([1,2],[3]) ") should returnResult(List(1, 2, 3))
    evaluateExpression(" concatenate([1],[2],[3]) ") should returnResult(List(1, 2, 3))
  }

  "A insert before() function" should "return list with new item at _" in {

    evaluateExpression(" insert before([1,3],2,2) ") should returnResult(List(1, 2, 3))
  }

  it should "return null if the position is 0" in {

    evaluateExpression(" insert before([1,3],0,2) ") should (returnNull() and reportFailure(
      failureType = FUNCTION_INVOCATION_FAILURE,
      failureMessage =
        "Failed to invoke function 'insert before': position must be a non-zero number"
    ))
  }

  "A remove() function" should "return list with item at _ removed" in {

    evaluateExpression(" remove([1,1,3],2) ") should returnResult(List(1, 3))
  }

  it should "return null if the position is 0" in {

    evaluateExpression(" remove([1,2,3], 0) ") should (returnNull() and reportFailure(
      failureType = FUNCTION_INVOCATION_FAILURE,
      failureMessage = "Failed to invoke function 'remove': position must be a non-zero number"
    ))
  }

  "A reverse() function" should "reverse the list" in {

    evaluateExpression(" reverse([1,2,3]) ") should returnResult(List(3, 2, 1))
  }

  "A index of() function" should "return empty list if no match" in {

    evaluateExpression(" index of([1,2,3,2], 4) ") should returnResult(List())
  }

  it should "return list of positions containing the match" in {

    evaluateExpression(" index of([1,2,3,2], 1) ") should returnResult(List(1))
    evaluateExpression(" index of([1,2,3,2], 2) ") should returnResult(List(2, 4))
    evaluateExpression(" index of([1,2,3,2], 3) ") should returnResult(List(3))
  }

  "A union() function" should "concatenate with duplicate removal" in {

    evaluateExpression(" union([1,2],[2,3]) ") should returnResult(List(1, 2, 3))
    evaluateExpression(" union([1,2],[2,3], [4]) ") should returnResult(List(1, 2, 3, 4))
  }

  it should "invoked with named parameter" in {

    evaluateExpression(" union(lists: [[1,2],[2,3]]) ") should returnResult(List(1, 2, 3))
  }

  it should "remove duplicated context values" in {

    evaluateExpression("union([{a:1},{a:2}],[{a:1},{b:3}])") should returnResult(
      List(Map("a" -> 1), Map("a" -> 2), Map("b" -> 3))
    )

    evaluateExpression("union([{a:1},{a:null}],[{a:null},{b:2}])") should returnResult(
      List(Map("a" -> 1), Map("a" -> null), Map("b" -> 2))
    )

    evaluateExpression("union([{a:1},{}],[{},{b:2}])") should returnResult(
      List(Map("a" -> 1), Map(), Map("b" -> 2))
    )

    evaluateExpression(
      "union([{a:1,b:{c:2}}, {a:1,b:{c:3}}], [{a:1,b:{c:2}}, {a:1,b:{c:3},d:4}])"
    ) should returnResult(
      List(
        Map("a" -> 1, "b" -> Map("c" -> 2)),
        Map("a" -> 1, "b" -> Map("c" -> 3)),
        Map("a" -> 1, "b" -> Map("c" -> 3), "d" -> 4)
      )
    )
  }

  it should "remove duplicated list values" in {
    evaluateExpression(" union([[1],[2]],[[3],[2]]) ") should returnResult(
      List(List(1), List(2), List(3))
    )

    evaluateExpression(" union([[1],[null]],[[1],[null]]) ") should returnResult(
      List(List(1), List(null))
    )

    evaluateExpression(" union([[1],[]],[[],[2]]) ") should returnResult(
      List(List(1), List.empty, List(2))
    )

    evaluateExpression(" union([[1,2],[4,5]],[[1,2],[4]]) ") should returnResult(
      List(List(1, 2), List(4, 5), List(4))
    )
  }

  it should "remove duplicated null values" in {
    evaluateExpression(" union([1,null],[2,null]) ") should returnResult(List(1, null, 2))
  }

  "A distinct values() function" should "remove duplicates" in {

    evaluateExpression(" distinct values([1,2,3,2,1]) ") should returnResult(List(1, 2, 3))
  }

  it should "invoked with named parameter" in {

    evaluateExpression(" distinct values(list: [1,2,3,2,1]) ") should returnResult(List(1, 2, 3))
  }

  it should "remove duplicated context values" in {

    evaluateExpression("distinct values([{a:1},{a:2},{a:1},{b:3}])") should returnResult(
      List(Map("a" -> 1), Map("a" -> 2), Map("b" -> 3))
    )

    evaluateExpression("distinct values([{a:1},{a:null},{a:null}])") should returnResult(
      List(Map("a" -> 1), Map("a" -> null))
    )

    evaluateExpression("distinct values([{a:1},{},{}])") should returnResult(
      List(Map("a" -> 1), Map())
    )

    evaluateExpression(
      "distinct values([{a:1,b:{c:2}}, {a:1,b:{c:3}}, {a:1,b:{c:2}}, {a:1,b:{c:3},d:4}])"
    ) should returnResult(
      List(
        Map("a" -> 1, "b" -> Map("c" -> 2)),
        Map("a" -> 1, "b" -> Map("c" -> 3)),
        Map("a" -> 1, "b" -> Map("c" -> 3), "d" -> 4)
      )
    )
  }

  it should "remove duplicated list values" in {
    evaluateExpression(" distinct values([[1],[2],[3],[2]]) ") should returnResult(
      List(List(1), List(2), List(3))
    )

    evaluateExpression(" distinct values([[1],[null],[1],[null]]) ") should returnResult(
      List(List(1), List(null))
    )

    evaluateExpression(" distinct values([[1],[],[]]) ") should returnResult(
      List(List(1), List.empty)
    )

    evaluateExpression(" distinct values([[1,2],[4,5],[1,2],[4]]) ") should returnResult(
      List(List(1, 2), List(4, 5), List(4))
    )
  }

  it should "remove duplicated null values" in {
    evaluateExpression(" distinct values([1,null,2,null]) ") should returnResult(List(1, null, 2))
  }

  it should "preserve the order" in {
    evaluateExpression(" distinct values([1,2,3,4,2,3,1]) ") should returnResult(List(1, 2, 3, 4))
  }

  "A duplicate values() function" should "return duplicate values" in {

    evaluateExpression(" duplicate values([1,2,3,2,1]) ") should returnResult(List(1, 2))
  }

  it should "invoked with named parameter" in {

    evaluateExpression(" duplicate values(list: [1,2,3,2,1]) ") should returnResult(List(1, 2))
  }

  it should "return an empty list if there are no duplicates" in {

    evaluateExpression(" duplicate values(list: [1,2,3,4,5]) ") should returnResult(List.empty)
  }

  it should "return duplicated context values" in {

    evaluateExpression("duplicate values([{a:1},{a:2},{a:1},{a:2},{b:3}])") should returnResult(
      List(Map("a" -> 1), Map("a" -> 2))
    )

    evaluateExpression(
      "duplicate values([{a:1},{a:null},{a:null},{b:2},{a:1}])"
    ) should returnResult(
      List(Map("a" -> 1), Map("a" -> null))
    )

    evaluateExpression("duplicate values([{a:1},{},{},{b:2},{a:1}])") should returnResult(
      List(Map("a" -> 1), Map.empty)
    )

    evaluateExpression(
      "duplicate values([{a:1,b:{c:2}}, {a:1,b:{c:3}}, {a:1,b:{c:2}}, {a:1,b:{c:3},d:4}, {a:1}])"
    ) should returnResult(
      List(
        Map("a" -> 1, "b" -> Map("c" -> 2))
      )
    )
  }

  it should "return duplicated list values" in {
    evaluateExpression(" duplicate values([[1],[2],[3],[2],[3]]) ") should returnResult(
      List(List(2), List(3))
    )

    evaluateExpression(" duplicate values([[1],[null],[1],[null],[2]]) ") should returnResult(
      List(List(1), List(null))
    )

    evaluateExpression(" duplicate values([[1],[],[],[2],[1]]) ") should returnResult(
      List(List(1), List.empty)
    )

    evaluateExpression(" duplicate values([[1,2],[4,5],[1,2],[4]]) ") should returnResult(
      List(List(1, 2))
    )
  }

  it should "return duplicated null values" in {
    evaluateExpression(" duplicate values([1,null,2,null,2]) ") should returnResult(List(null, 2))
  }

  it should "preserve the order" in {
    evaluateExpression(" duplicate values([1,2,3,4,2,3,1]) ") should returnResult(List(1, 2, 3))

    evaluateExpression(" duplicate values([1,2,3,4,3,1,2]) ") should returnResult(List(1, 2, 3))
  }

  "A flatten() function" should "flatten nested lists" in {

    evaluateExpression(" flatten([[1,2],[[3]], 4]) ") should returnResult(List(1, 2, 3, 4))
  }

  it should "flatten a huge list of lists" in {
    val hugeList = (1 to 10_000).map(List(_)).toList

    evaluateExpression("flatten(xs)", Map("xs" -> hugeList)) should returnResult(hugeList.flatten)
  }

  "A sort() function" should "sort list of numbers" in {

    evaluateExpression(
      " sort(list: [3,1,4,5,2], precedes: function(x,y) x < y) "
    ) should returnResult(List(1, 2, 3, 4, 5))
  }

  "A product() function" should "return null if empty list" in {

    evaluateExpression(" product([]) ") should returnNull()
  }

  it should "return product of numbers" in {

    evaluateExpression(" product([2,3,4]) ") should returnResult(24)
    evaluateExpression(" product(2,3,4) ") should returnResult(24)
  }

  "A join function" should "return an empty string if the input list is empty" in {
    evaluateExpression(" string join([]) ") should returnResult("")
  }

  it should "return an empty string if the input list is empty and a delimiter is defined" in {
    evaluateExpression(""" string join([], "X") """) should returnResult("")
  }

  it should "return joined strings" in {
    evaluateExpression(""" string join(["foo","bar","baz"]) """) should returnResult("foobarbaz")
  }

  it should "return joined strings when delimiter is null" in {
    evaluateExpression(""" string join(["foo","bar","baz"], null) """) should returnResult(
      "foobarbaz"
    )
  }

  it should "return original string when list contains a single entry" in {
    evaluateExpression(""" string join(["a"], "X") """) should returnResult("a")
  }

  it should "ignore null strings" in {
    evaluateExpression(""" string join(["foo", null, "baz"], null) """) should returnResult(
      "foobaz"
    )
  }

  it should "ignore null strings with delimiter" in {
    evaluateExpression(""" string join(["foo", null, "baz"], "X") """) should returnResult(
      "fooXbaz"
    )
  }

  it should "return joined strings with custom separator" in {
    evaluateExpression(""" string join(["foo","bar","baz"], "::") """) should returnResult(
      "foo::bar::baz"
    )
  }

  it should "return joined strings with custom separator, a prefix and a suffix" in {
    evaluateExpression(
      """ string join(["foo","bar","baz"], "::", "hello-", "-goodbye")  """
    ) should returnResult(
      "hello-foo::bar::baz-goodbye"
    )
  }

  it should "return null if the list contains other values than strings" in {
    evaluateExpression(""" string join(["foo", 123, "bar"]) """) should returnNull()
  }

  "A list is empty() function" should "return if the list is empty" in {

    evaluateExpression(" is empty([]) ") should returnResult(true)
    evaluateExpression(" is empty([1]) ") should returnResult(false)
    evaluateExpression(" is empty([1,2,3]) ") should returnResult(false)
    evaluateExpression(" is empty(list: [1]) ") should returnResult(false)
  }

  "A partition() function" should "return list partitioned by _" in {
    evaluateExpression(" partition([1,2,3,4,5], 2) ") should returnResult(
      List(List(1, 2), List(3, 4), List(5))
    )
    evaluateExpression(" partition(list: [1,2,3,4,5], size: 2) ") should returnResult(
      List(List(1, 2), List(3, 4), List(5))
    )

    evaluateExpression(" partition([], 2) ") should returnResult(List())
    evaluateExpression(" partition([1], 2) ") should returnResult(List(List(1)))
  }

  it should "return null if the size is invalid" in {
    evaluateExpression(" partition([1,2], 0) ") should (
      returnNull() and reportFailure(
        FUNCTION_INVOCATION_FAILURE,
        "Failed to invoke function 'partition': 'size' should be greater than zero but was '0'"
      )
    )

    evaluateExpression(" partition([1,2], -1) ") should (
      returnNull() and reportFailure(
        FUNCTION_INVOCATION_FAILURE,
        "Failed to invoke function 'partition': 'size' should be greater than zero but was '-1'"
      )
    )
  }
}
