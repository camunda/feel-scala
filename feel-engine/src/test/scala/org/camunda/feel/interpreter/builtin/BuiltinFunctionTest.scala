package org.camunda.feel.interpreter.builtin

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._
import org.camunda.feel.interpreter._
import org.camunda.feel.interpreter.FeelIntegrationTest
import scala.math.BigDecimal.double2bigDecimal
import scala.math.BigDecimal.int2bigDecimal

/**
 * @author Philipp
 */
class BuiltinFunctionsTest extends FlatSpec with Matchers with FeelIntegrationTest {

	"A built-in function" should "return null if arguments doesn't match" in {

		eval("date(true)") should be(ValNull)
		eval("number(false)") should be(ValNull)
	}

	"A not() function" should "negate Boolean" in {

	  eval(" not(true) ") should be(ValBoolean(false))
	  eval(" not(false) ") should be(ValBoolean(true))
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
