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
import org.camunda.feel._
import org.camunda.feel.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree._

import scala.math.BigDecimal.int2bigDecimal

/**
  * @author Philipp
  */
class BuiltinListFunctionsTest
  extends AnyFlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A list contains() function" should "return if the list contains Number" in {

    eval(" list contains([1,2,3], 2) ") should be(ValBoolean(true))

    eval(" list contains([1,2,3], 4) ") should be(ValBoolean(false))
  }

  it should "return if the list contains String" in {

    eval(""" list contains(["a","b"], "a") """) should be(ValBoolean(true))

    eval(""" list contains(["a","b"], "c") """) should be(ValBoolean(false))
  }

  "A count() function" should "return the size of a list" in {

    eval(" count([1,2,3]) ") should be(ValNumber(3))
  }

  "A min() function" should "return the null if empty list" in {

    eval(" min([]) ") should be(ValNull)
  }

  it should "return the minimum item of numbers" in {

    eval(" min([1,2,3]) ") should be(ValNumber(1))
    eval(" min(1,2,3) ") should be(ValNumber(1))
  }

  it should "return the minimum item of date" in {

    eval(
      """ min([date("2017-01-01"), date("2018-01-01"), date("2019-01-01")]) """) should be(
      ValDate("2017-01-01"))
  }

  it should "return null if value is not comparable" in {

    eval(""" min([true, false]) """) should be(ValNull)
  }

  "A max() function" should "return the null if empty list" in {

    eval(" max([]) ") should be(ValNull)
  }

  it should "return the maximum item of numbers" in {

    eval(" max([1,2,3]) ") should be(ValNumber(3))
    eval(" max(1,2,3) ") should be(ValNumber(3))
  }

  it should "return the maximum item of date" in {

    eval(
      """ max([date("2017-01-01"), date("2018-01-01"), date("2019-01-01")]) """) should be(
      ValDate("2019-01-01"))
  }

  it should "return null if value is not comparable" in {

    eval(""" max([true, false]) """) should be(ValNull)
  }

  "A sum() function" should "return null if empty list" in {

    eval(" sum([]) ") should be(ValNull)
  }

  it should "return sum of numbers" in {

    eval(" sum([1,2,3]) ") should be(ValNumber(6))
    eval(" sum(1,2,3) ") should be(ValNumber(6))
  }

  "A mean() function" should "return null if empty list" in {

    eval(" mean([]) ") should be(ValNull)
  }

  it should "return mean of numbers" in {

    eval(" mean([1,2,3]) ") should be(ValNumber(2))
    eval(" mean(1,2,3) ") should be(ValNumber(2))
  }

  "A median() function" should "return null if empty list" in {

    eval(" median([]) ") should be(ValNull)
  }

  it should "return the median of numbers" in {

    eval(" median(8, 2, 5, 3, 4) ") should be(ValNumber(4))
    eval(" median([6, 1, 2, 3]) ") should be(ValNumber(2.5))
  }

  "A stddev() function" should "return null if empty list" in {

    eval(" stddev([]) ") should be(ValNull)
  }

  it should "return the standard deviation" in {

    eval(" stddev(2, 4, 7, 5) ") should be(ValNumber(2.0816659994661326))
    eval(" stddev([2, 4, 7, 5]) ") should be(ValNumber(2.0816659994661326))
  }

  "A mode() function" should "return empty list if empty list" in {

    eval(" mode([]) ") should be(ValList(List.empty))
  }

  it should "return the mode of the list" in {

    eval(" mode(6, 3, 9, 6, 6) ") should be(ValList(List(ValNumber(6))))
    eval(" mode([6, 1, 9, 6, 1]) ") should be(
      ValList(List(ValNumber(1), ValNumber(6))))
  }

  "A and() / all() function" should "return true if empty list" in {

    eval(" and([]) ") should be(ValBoolean(true))
    eval(" all([]) ") should be(ValBoolean(true))
  }

  it should "return true if all items are true" in {

    eval(" and([false,null,true]) ") should be(ValBoolean(false))
    eval(" all([false,null,true]) ") should be(ValBoolean(false))

    eval(" and(false,null,true) ") should be(ValBoolean(false))
    eval(" all(false,null,true) ") should be(ValBoolean(false))

    eval(" and([true,true]) ") should be(ValBoolean(true))
    eval(" all([true,true]) ") should be(ValBoolean(true))

    eval(" and(true,true) ") should be(ValBoolean(true))
    eval(" all(true,true) ") should be(ValBoolean(true))
  }

  it should "return null if argument is invalid" in {

    eval("and(0)") should be(ValNull)
    eval("all(0)") should be(ValNull)
  }

  it should "return null if one item is not a boolean value" in {

    eval("and(true, null, true)") should be(ValNull)
    eval("all(true, null, true)") should be(ValNull)
  }

  it should "return true if all items are true (huge list)" in {
    val hugeList = (1 to 10_000).map(_ => true).toList

    eval("all(xs)", Map("xs" -> hugeList)) should be(ValBoolean(true))
  }

  it should "return null if items are not boolean values (huge list)" in {
    val hugeList = (1 to 10_000).toList

    eval("all(xs)", Map("xs" -> hugeList)) should be(ValNull)
  }

  "A or() / any() function" should "return false if empty list" in {

    eval(" or([]) ") should be(ValBoolean(false))
    eval(" any([]) ") should be(ValBoolean(false))
  }

  it should "return false if all items are false" in {

    eval(" or([false,null,true]) ") should be(ValBoolean(true))
    eval(" any([false,null,true]) ") should be(ValBoolean(true))

    eval(" or(false,null,true) ") should be(ValBoolean(true))
    eval(" any(false,null,true) ") should be(ValBoolean(true))

    eval(" or([false,false]) ") should be(ValBoolean(false))
    eval(" any([false,false]) ") should be(ValBoolean(false))

    eval(" or(false,false) ") should be(ValBoolean(false))
    eval(" any(false,false) ") should be(ValBoolean(false))
  }

  it should "return null if argument is invalid" in {

    eval("or(0)") should be(ValNull)
    eval("any(0)") should be(ValNull)
  }

  it should "return null if one item is not a boolean value" in {

    eval("or(false, null, false)") should be(ValNull)
    eval("any(false, null, false)") should be(ValNull)
  }

  it should "return false if all items are false (huge list)" in {
    val hugeList = (1 to 10_000).map(_ => false).toList

    eval("any(xs)", Map("xs" -> hugeList)) should be(ValBoolean(false))
  }

  it should "return null if items are not boolean values (huge list)" in {
    val hugeList = (1 to 10_000).toList

    eval("any(xs)", Map("xs" -> hugeList)) should be(ValNull)
  }

  "A sublist() function" should "return list starting with _" in {

    eval(" sublist([1,2,3], 2) ") should be(
      ValList(List(ValNumber(2), ValNumber(3))))
  }

  it should "return list starting with _ and length _" in {

    eval(" sublist([1,2,3], 1, 2) ") should be(
      ValList(List(ValNumber(1), ValNumber(2))))
  }

  "A append() function" should "return list with item appended" in {

    eval(" append([1,2], 3) ") should be(
      ValList(List(ValNumber(1), ValNumber(2), ValNumber(3))))
    eval(" append([1], 2, 3) ") should be(
      ValList(List(ValNumber(1), ValNumber(2), ValNumber(3))))
  }

  "A concatenate() function" should "return list with item appended" in {

    eval(" concatenate([1,2],[3]) ") should be(
      ValList(List(ValNumber(1), ValNumber(2), ValNumber(3))))
    eval(" concatenate([1],[2],[3]) ") should be(
      ValList(List(ValNumber(1), ValNumber(2), ValNumber(3))))
  }

  "A insert before() function" should "return list with new item at _" in {

    eval(" insert before([1,3],2,2) ") should be(
      ValList(List(ValNumber(1), ValNumber(2), ValNumber(3))))
  }

  "A remove() function" should "return list with item at _ removed" in {

    eval(" remove([1,1,3],2) ") should be(
      ValList(List(ValNumber(1), ValNumber(3))))
  }

  "A reverse() function" should "reverse the list" in {

    eval(" reverse([1,2,3]) ") should be(
      ValList(List(ValNumber(3), ValNumber(2), ValNumber(1))))
  }

  "A index of() function" should "return empty list if no match" in {

    eval(" index of([1,2,3,2], 4) ") should be(ValList(List()))
  }

  it should "return list of positions containing the match" in {

    eval(" index of([1,2,3,2], 1) ") should be(ValList(List(ValNumber(1))))
    eval(" index of([1,2,3,2], 2) ") should be(
      ValList(List(ValNumber(2), ValNumber(4))))
    eval(" index of([1,2,3,2], 3) ") should be(ValList(List(ValNumber(3))))
  }

  "A union() function" should "concatenate with duplicate removal" in {

    eval(" union([1,2],[2,3]) ") should be(
      ValList(List(ValNumber(1), ValNumber(2), ValNumber(3))))
    eval(" union([1,2],[2,3], [4]) ") should be(
      ValList(List(ValNumber(1), ValNumber(2), ValNumber(3), ValNumber(4))))
  }

  "A distinct values() function" should "remove duplicates" in {

    eval(" distinct values([1,2,3,2,1]) ") should be(
      ValList(List(ValNumber(1), ValNumber(2), ValNumber(3))))
  }

  "A flatten() function" should "flatten nested lists" in {

    eval(" flatten([[1,2],[[3]], 4]) ") should be(
      ValList(List(ValNumber(1), ValNumber(2), ValNumber(3), ValNumber(4))))
  }

  it should "flatten a huge list of lists" in {
    val hugeList = (1 to 10_000).map(List(_)).toList

    eval("flatten(xs)", Map("xs" -> hugeList)) should be(
      ValList(
        hugeList.flatten.map(ValNumber(_))
      )
    )
  }

  "A sort() function" should "sort list of numbers" in {

    eval(" sort(list: [3,1,4,5,2], precedes: function(x,y) x < y) ") should be(
      ValList(
        List(ValNumber(1),
          ValNumber(2),
          ValNumber(3),
          ValNumber(4),
          ValNumber(5))))
  }

  "A product() function" should "return null if empty list" in {

    eval(" product([]) ") should be(ValNull)
  }

  it should "return product of numbers" in {

    eval(" product([2,3,4]) ") should be(ValNumber(24))
    eval(" product(2,3,4) ") should be(ValNumber(24))
  }

  "A join function" should "return an empty string if the input list is empty" in {
    eval(" string join([]) ") should be(ValString(""))
  }

  it should "return an empty string if the input list is empty and a delimiter is defined" in {
    eval(""" string join([], "X") """) should be(ValString(""))
  }

  it should "return joined strings" in {
    eval(""" string join(["foo","bar","baz"]) """) should be(ValString("foobarbaz"))
  }

  it should "return joined strings when delimiter is null" in {
    eval(""" string join(["foo","bar","baz"], null) """) should be(ValString("foobarbaz"))
  }

  it should "return original string when list contains a single entry" in {
    eval(""" string join(["a"], "X") """) should be(ValString("a"))
  }

  it should "ignore null strings" in {
    eval(""" string join(["foo", null, "baz"], null) """) should be(ValString("foobaz"))
  }

  it should "ignore null strings with delimiter" in {
    eval(""" string join(["foo", null, "baz"], "X") """) should be(ValString("fooXbaz"))
  }

  it should "return joined strings with custom separator" in {
    eval(""" string join(["foo","bar","baz"], "::") """) should be(
      ValString("foo::bar::baz"))
  }

  it should "return joined strings with custom separator, a prefix and a suffix" in {
    eval(""" string join(["foo","bar","baz"], "::", "hello-", "-goodbye")  """) should be(
      ValString("hello-foo::bar::baz-goodbye"))
  }

  it should "return null if the list contains other values than strings" in {
    eval(""" string join(["foo", 123, "bar"]) """) should be(ValNull)
  }

}
