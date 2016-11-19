package org.camunda.feel.interpreter

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._
import org.camunda.feel.parser.FeelParser

/**
 * @author Philipp
 */
class BuiltinFunctionTest extends FlatSpec with Matchers {

	val interpreter = new FeelInterpreter

	"A date() function" should "convert String" in {

		eval(""" date(x) """, Map("x" -> "2012-12-25")) should be(ValDate("2012-12-25"))
	}
	
	it should "convert Date-Time" in {
		
		eval(""" date( date and time("2012-12-25T11:00:00") ) """) should be(ValDate("2012-12-25"))
	}
	
	it should "convert (year,month,day)" in {
		
		eval(""" date(2012, 12, 25) """) should be(ValDate("2012-12-25"))
	}
	
	"A date-and-time() function" should "convert String" in {

		eval(""" date_and_time(x) """, Map("x" -> "2012-12-24T23:59:00")) should be(ValDateTime("2012-12-24T23:59:00"))
	}

	it should "convert (Date,Time)" in {

		eval(""" date_and_time(date("2012-12-24"),time("T23:59:00")) """) should be(ValDateTime("2012-12-24T23:59:00"))
	}
	
	it should "convert (DateTime,Time)" in {

		eval(""" date_and_time(date and time("2012-12-24T10:24:00"),time("T23:59:00")) """) should be(ValDateTime("2012-12-24T23:59:00"))
	}
	
	"A time() function" should "convert String" in {

		eval(""" time(x) """, Map("x" -> "23:59:00")) should be(ValTime("23:59:00"))
	}
	
	it should "convert Date-Time" in {
		
		eval(""" time( date and time("2012-12-25T11:00:00") ) """) should be(ValTime("11:00:00"))
	}
	
	it should "convert (hour,minute,second)" in {
		
		eval(""" time(23, 59, 0) """) should be(ValTime("23:59:00"))
	}
	
	it should "convert (hour,minute,second, offset)" in {
		
		eval(""" time(14, 30, 0, duration("PT1H")) """) should be(ValTime("15:30:00"))
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
	}
	
	it should "convert Date-Time" in {

		eval(""" string(date and time("2012-12-25T11:00:00")) """) should be(ValString("2012-12-25T11:00:00"))
	}
	
	it should "convert Duration" in {

		eval(""" string(duration("PT1H")) """) should be(ValString("PT1H"))
	}
	
	"A duration() function" should "convert String" in {

		eval(""" duration(x) """, Map("x" -> "P2DT20H14M")) should be(ValDuration("P2DT20H14M"))
	}
	
	"A years and months duration(from,to) function" should "convert (Date,Date)" in {

		eval(""" years_and_months_duration( date("2011-12-22"), date("2013-08-24") ) """) should be(ValDuration("P1Y8M"))
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
	
	"A string_length() function" should "return the length of a String" in {
	  
	  eval(""" string_length("foo") """) should be(ValNumber(3))
	}
	
	"A upper_case() function" should "return uppercased String" in {
	  
	  eval(""" upper_case("aBc4") """) should be(ValString("ABC4"))
	}
	
	"A lower_case() function" should "return lowercased String" in {
	  
	  eval(""" lower_case("aBc4") """) should be(ValString("abc4"))
	}
	
	"A substring_before() function" should "return substring before match" in {
	  
	  eval(""" substring_before("foobar", "bar") """) should be(ValString("foo"))
	  
	  eval(""" substring_before("foobar", "xyz") """) should be(ValString(""))
	}
	
	"A substring_after() function" should "return substring after match" in {
	  
	  eval(""" substring_after("foobar", "ob") """) should be(ValString("ar"))
	  
	  eval(""" substring_after("", "a") """) should be(ValString(""))
	}
	
	"A contains() function" should "return if contains the match" in {
	  
	  eval(""" contains("foobar", "ob") """) should be(ValBoolean(true))
	  
	  eval(""" contains("foobar", "of") """) should be(ValBoolean(false))
	}
	
	private def eval(expression: String, variables: Map[String, Any] = Map()): Val = {
    val exp = FeelParser.parseExpression(expression)
    interpreter.eval(exp.get)(Context(variables))
  }

}