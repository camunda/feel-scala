package org.camunda.feel.interpreter.builtin

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._
import org.camunda.feel.interpreter.FeelIntegrationTest
import org.camunda.feel.interpreter._
import scala.math.BigDecimal.int2bigDecimal

/**
 * @author Philipp
 */
class BuiltinListFunctionsTest extends FlatSpec with Matchers with FeelIntegrationTest {	

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

	"A max() function" should "return the null if empty list" in {

	  eval(" max([]) ") should be(ValNull)
	}

	it should "return the maximum item of numbers" in {

	  eval(" max([1,2,3]) ") should be(ValNumber(3))
		eval(" max(1,2,3) ") should be(ValNumber(3))
	}

	"A sum() function" should "return 0 if empty list" in {

	  eval(" sum([]) ") should be(ValNumber(0))
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

	"A and() function" should "return true if empty list" in {

	  eval(" and([]) ") should be(ValBoolean(true))
	}

	it should "return true if all items are true" in {

	  eval(" and([false,null,true]) ") should be(ValBoolean(false))
		eval(" and(false,null,true) ") should be(ValBoolean(false))

	  eval(" and([true,true]) ") should be(ValBoolean(true))
		eval(" and(true,true) ") should be(ValBoolean(true))
	}

	it should "return null if argument is invalid" in {

		eval("and(0)") should be(ValNull)
	}

	"A or() function" should "return false if empty list" in {

	  eval(" or([]) ") should be(ValBoolean(false))
	}

	it should "return false if all items are false" in {

	  eval(" or([false,null,true]) ") should be(ValBoolean(true))
		eval(" or(false,null,true) ") should be(ValBoolean(true))

	  eval(" or([false,false]) ") should be(ValBoolean(false))
		eval(" or(false,false) ") should be(ValBoolean(false))
	}

	it should "return null if argument is invalid" in {

		eval("or(0)") should be(ValNull)
	}

	"A sublist() function" should "return list starting with _" in {

	  eval(" sublist([1,2,3], 2) ") should be(ValList(List(ValNumber(2), ValNumber(3))))
	}

	it should "return list starting with _ and length _" in {

	  eval(" sublist([1,2,3], 1, 2) ") should be(ValList(List(ValNumber(1), ValNumber(2))))
	}

	"A append() function" should "return list with item appended" in {

	  eval(" append([1,2], 3) ") should be(ValList(List(ValNumber(1), ValNumber(2), ValNumber(3))))
		eval(" append([1], 2, 3) ") should be(ValList(List(ValNumber(1), ValNumber(2), ValNumber(3))))
	}

	"A concatenate() function" should "return list with item appended" in {

	  eval(" concatenate([1,2],[3]) ") should be(ValList(List(ValNumber(1), ValNumber(2), ValNumber(3))))
		eval(" concatenate([1],[2],[3]) ") should be(ValList(List(ValNumber(1), ValNumber(2), ValNumber(3))))
	}

	"A insert before() function" should "return list with new item at _" in {

	  eval(" insert before([1,3],2,2) ") should be(ValList(List(ValNumber(1), ValNumber(2), ValNumber(3))))
	}

	"A remove() function" should "return list with item at _ removed" in {

	  eval(" remove([1,1,3],2) ") should be(ValList(List(ValNumber(1), ValNumber(3))))
	}

	"A reverse() function" should "reverse the list" in {

	  eval(" reverse([1,2,3]) ") should be(ValList(List(ValNumber(3), ValNumber(2), ValNumber(1))))
	}

	"A index of() function" should "return empty list if no match" in {

	  eval(" index of([1,2,3,2], 4) ") should be(ValList(List()))
	}

	it should "return list of positions containing the match" in {

	  eval(" index of([1,2,3,2], 2) ") should be(ValList(List(ValNumber(2), ValNumber(4))))
	}

	"A union() function" should "concatenate with duplicate removal" in {

	  eval(" union([1,2],[2,3]) ") should be(ValList(List(ValNumber(1), ValNumber(2), ValNumber(3))))
		eval(" union([1,2],[2,3], [4]) ") should be(ValList(List(ValNumber(1), ValNumber(2), ValNumber(3), ValNumber(4))))
	}

	"A distinct values() function" should "remove duplicates" in {

	  eval(" distinct values([1,2,3,2,1]) ") should be(ValList(List(ValNumber(1), ValNumber(2), ValNumber(3))))
	}

	"A flatten() function" should "flatten nested lists" in {

	  eval(" flatten([[1,2],[[3]], 4]) ") should be(ValList(List(ValNumber(1), ValNumber(2), ValNumber(3), ValNumber(4))))
	}

	"A sort() function" should "sort list of numbers" in {

	  eval(" sort(list: [3,1,4,5,2], precedes: function(x,y) x < y) ") should be(ValList(List(ValNumber(1), ValNumber(2), ValNumber(3), ValNumber(4), ValNumber(5))))
	}

}
