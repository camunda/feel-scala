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

		eval(""" date("2012-12-25") """) should be(ValDate("2012-12-25"))
	}
	
	it should "convert Date-Time" in {
		
		eval(""" date( date and time("2012-12-25T11:00:00") ) """) should be(ValDate("2012-12-25"))
	}
	
	it should "convert (year,month,day)" in {
		
		eval(""" date(2012, 12, 25) """) should be(ValDate("2012-12-25"))
	}
	
	"A date-and-time() function" should "convert String" in {

		eval(""" date_and_time("2012-12-24T23:59:00") """) should be(ValDateTime("2012-12-24T23:59:00"))
	}

	it should "convert (Date,Time)" in {

		eval(""" date_and_time(date("2012-12-24"),time("T23:59:00")) """) should be(ValDateTime("2012-12-24T23:59:00"))
	}
	
	it should "convert (DateTime,Time)" in {

		eval(""" date_and_time(date and time("2012-12-24T10:24:00"),time("T23:59:00")) """) should be(ValDateTime("2012-12-24T23:59:00"))
	}
	
	"A time() function" should "convert String" in {

		eval(""" time("23:59:00") """) should be(ValTime("23:59:00"))
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
	
	
	private def eval(expression: String, variables: Map[String, Any] = Map()): Val = {
    val exp = FeelParser.parseExpression(expression)
    interpreter.eval(exp.get)(Context(variables))
  }

}