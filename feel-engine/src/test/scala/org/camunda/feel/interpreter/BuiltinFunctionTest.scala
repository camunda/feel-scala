package org.camunda.feel.interpreter

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._
import org.camunda.feel.parser.FeelParser

/**
 * @author Philipp
 */
class BuiltinFunctionTest extends FlatSpec with Matchers with FeelIntegrationTest {

	"A built-in function" should "return null if arguments doesn't match" in {

		eval("date(true)") should be(ValNull)
		eval("number(false)") should be(ValNull)
	}

	"A date() function" should "convert String" in {

		eval(""" date(x) """, Map("x" -> "2012-12-25")) should be(ValDate("2012-12-25"))
	}

	it should "convert Date-Time" in {

		eval(""" date( date and time("2012-12-25T11:00:00") ) """) should be(ValDate("2012-12-25"))

		eval(""" date( date and time("2012-12-25T11:00:00+01:00") ) """) should be(ValDate("2012-12-25"))
	}

	it should "convert (year,month,day)" in {

		eval(""" date(2012, 12, 25) """) should be(ValDate("2012-12-25"))
	}

	"A date and time() function" should "convert String" in {

		eval(""" date and time(x) """, Map("x" -> "2012-12-24T23:59:00")) should be(ValLocalDateTime("2012-12-24T23:59:00"))

		eval(""" date and time(x) """, Map("x" -> "2012-12-24T23:59:00+01:00")) should be(ValDateTime("2012-12-24T23:59:00+01:00"))
	}

	it should "convert (Date,Time)" in {

		eval(""" date and time(date("2012-12-24"),time("T23:59:00")) """) should be(ValLocalDateTime("2012-12-24T23:59:00"))

		eval(""" date and time(date("2012-12-24"),time("T23:59:00+01:00")) """) should be(ValDateTime("2012-12-24T23:59:00+01:00"))
	}

	it should "convert (DateTime,Time)" in {

		eval(""" date and time(date and time("2012-12-24T10:24:00"),time("T23:59:00")) """) should be(ValLocalDateTime("2012-12-24T23:59:00"))
		eval(""" date and time(date and time("2012-12-24T10:24:00"),time("T23:59:00+01:00")) """) should be(ValDateTime("2012-12-24T23:59:00+01:00"))
		eval(""" date and time(date and time("2012-12-24T10:24:00+01:00"),time("T23:59:00")) """) should be(ValLocalDateTime("2012-12-24T23:59:00"))
		eval(""" date and time(date and time("2012-12-24T10:24:00+01:00"),time("T23:59:00+01:00")) """) should be(ValDateTime("2012-12-24T23:59:00+01:00"))
	}

	"A time() function" should "convert String" in {

		eval(""" time(x) """, Map("x" -> "23:59:00")) should be(ValLocalTime("23:59:00"))

		eval(""" time(x) """, Map("x" -> "23:59:00+01:00")) should be(ValTime("23:59:00+01:00"))
	}

	it should "convert Date-Time" in {

		eval(""" time( date and time("2012-12-25T11:00:00") ) """) should be(ValLocalTime("11:00:00"))

		eval(""" time( date and time("2012-12-25T11:00:00+01:00") ) """) should be(ValTime("11:00:00+01:00"))
	}

	it should "convert (hour,minute,second)" in {

		eval(""" time(23, 59, 0) """) should be(ValLocalTime("23:59:00"))
	}

	it should "convert (hour,minute,second, offset)" in {

		eval(""" time(14, 30, 0, duration("PT1H")) """) should be(ValTime("14:30:00+01:00"))
	}

	"A number() function" should "convert String" in {

		eval(""" number("1500.5") """) should be(ValNumber(1500.5))
	}

	it should "convert String with Grouping Separator ' '" in {

		eval(""" number("1 500.5", " ") """) should be(ValNumber(1500.5))
	}

	it should "convert String with Grouping Separator ','" in {

		eval(""" number("1,500", ",") """) should be(ValNumber(1500))
	}

	it should "convert String with Grouping Separator '.'" in {

		eval(""" number("1.500", ".") """) should be(ValNumber(1500))
	}

	it should "convert String with Grouping ' ' and Decimal Separator '.'" in {

		eval(""" number("1 500.5", " ", ".") """) should be(ValNumber(1500.5))
	}

	it should "convert String with Grouping ' ' and Decimal Separator ','" in {

		eval(""" number("1 500,5", " ", ",") """) should be(ValNumber(1500.5))
	}

	"A string() function" should "convert Number" in {

		eval(""" string(1.1) """) should be(ValString("1.1"))
	}

	it should "convert Boolean" in {

		eval(""" string(true) """) should be(ValString("true"))
	}

	it should "convert Date" in {

		eval(""" string(date("2012-12-25")) """) should be(ValString("2012-12-25"))
	}

	it should "convert Time" in {

		eval(""" string(time("23:59:00")) """) should be(ValString("23:59:00"))
		eval(""" string(time("23:59:00+01:00")) """) should be(ValString("23:59:00+01:00"))
	}

	it should "convert Date-Time" in {

		eval(""" string(date and time("2012-12-25T11:00:00")) """) should be(ValString("2012-12-25T11:00:00"))
		eval(""" string(date and time("2012-12-25T11:00:00+02:00")) """) should be(ValString("2012-12-25T11:00:00+02:00"))
	}

	it should "convert Duration" in {

		eval(""" string(duration("PT1H")) """) should be(ValString("PT1H"))
	}

	"A duration() function" should "convert day-time-String" in {

		eval(""" duration(x) """, Map("x" -> "P2DT20H14M")) should be(ValDayTimeDuration("P2DT20H14M"))
	}

	it should "convert year-month-String" in {

		eval(""" duration(x) """, Map("x" -> "P2Y4M")) should be(ValYearMonthDuration("P2Y4M"))
	}

	"A years and months duration(from,to) function" should "convert (Date,Date)" in {

		eval(""" years and months duration( date("2011-12-22"), date("2013-08-24") ) """) should be(ValYearMonthDuration("P1Y8M"))
		eval(""" years and months duration( date and time("2011-12-22T10:00:00"), date and time("2013-08-24T10:00:00") ) """) should be(ValYearMonthDuration("P1Y8M"))
		eval(""" years and months duration( date and time("2011-12-22T10:00:00+01:00"), date and time("2013-08-24T10:00:00+01:00") ) """) should be(ValYearMonthDuration("P1Y8M"))
	}

	"A not() function" should "negate Boolean" in {

	  eval(" not(true) ") should be(ValBoolean(false))
	  eval(" not(false) ") should be(ValBoolean(true))
	}

	"A substring() function" should "return string with _ characters" in {

	  eval(""" substring("foobar",3) """) should be(ValString("obar"))
	}

	it should "return string with _ characters starting at _" in {

	  eval(""" substring("foobar",3,3) """) should be(ValString("oba"))
	}

	it should "return string with _ characters starting at negative _" in {

	  eval(""" substring("foobar",-2,1) """) should be(ValString("a"))
	}

	"A string length() function" should "return the length of a String" in {

	  eval(""" string length("foo") """) should be(ValNumber(3))
	}

	"A upper case() function" should "return uppercased String" in {

	  eval(""" upper case("aBc4") """) should be(ValString("ABC4"))
	}

	"A lower case() function" should "return lowercased String" in {

	  eval(""" lower case("aBc4") """) should be(ValString("abc4"))
	}

	"A substring before() function" should "return substring before match" in {

	  eval(""" substring before("foobar", "bar") """) should be(ValString("foo"))

	  eval(""" substring before("foobar", "xyz") """) should be(ValString(""))
	}

	"A substring after() function" should "return substring after match" in {

	  eval(""" substring after("foobar", "ob") """) should be(ValString("ar"))

	  eval(""" substring after("", "a") """) should be(ValString(""))

		eval(""" substring after("foo", "") """) should be(ValString("foo"))
	}

	"A replace() function" should "replace a String" in {

	  eval(""" replace("abcd", "(ab)|(a)", "[1=$1][2=$2]") """) should be(ValString("[1=ab][2=]cd"))
	}

	"A contains() function" should "return if contains the match" in {

	  eval(""" contains("foobar", "ob") """) should be(ValBoolean(true))

	  eval(""" contains("foobar", "of") """) should be(ValBoolean(false))
	}

	"A starts with() function" should "return if starts with match" in {

	  eval(""" starts with("foobar", "fo") """) should be(ValBoolean(true))

	  eval(""" starts with("foobar", "ba") """) should be(ValBoolean(false))
	}

	"A ends with() function" should "return if ends with match" in {

	  eval(""" ends with("foobar", "r") """) should be(ValBoolean(true))

	  eval(""" ends with("foobar", "o") """) should be(ValBoolean(false))
	}

	"A matches() function" should "return if String matches a pattern" in {

	  eval(""" matches("foobar", "^fo*b") """) should be(ValBoolean(true))

		eval(""" matches("foobar", "^fo*z") """) should be(ValBoolean(false))
	}

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

	"A decimal() function" should "return number with a given scale" in {

	  eval(" decimal((1/3), 2) ") should be(ValNumber(0.33))

	  eval(" decimal(1.5, 0) ") should be(ValNumber(2))

	  eval(" decimal(2.5, 0) ") should be(ValNumber(2))
	}

	"A floor() function" should "return greatest integer <= _" in {

	  eval(" floor(1.5) ") should be(ValNumber(1))

	  eval(" floor(-1.5) ") should be(ValNumber(-2))
	}

	"A ceiling() function" should "return smallest integer >= _" in {

	  eval(" ceiling(1.5) ") should be(ValNumber(2))

	  eval(" ceiling(-1.5) ") should be(ValNumber(-1))
	}

}
