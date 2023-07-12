package org.camunda.feel.impl.interpreter;

import org.camunda.feel.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree._
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

class InterpreterNonExistingVariableExpressionTest
        extends AnyFlatSpec
        with Matchers
        with FeelIntegrationTest {

  "a non existing variable" should "compare with '='" in {
    eval("x = 1") should be(ValBoolean(false))
    eval("1 = x") should be(ValBoolean(false))
    eval("x = true") should be(ValBoolean(false))
    eval("true = x") should be(ValBoolean(false))
    eval(""" x = "string" """) should be(ValBoolean(false))
    eval(""" "string" = x """) should be(ValBoolean(false))
    eval("x = null") should be(ValBoolean(true))
    eval("null = x") should be(ValBoolean(true))
    eval("x = y") should be(ValBoolean(true))
  }
  
  it should "compare with `<`" in {
    eval("x < 1") should be(ValNull)
    eval("1 < x") should be(ValNull)
    eval("x < true") should be(ValNull)
    eval("true < x") should be(ValNull)
    eval(""" x < "string" """) should be(ValNull)
    eval(""" "string" < x """) should be(ValNull)
    eval("x < null") should be(ValNull)
    eval("null < x") should be(ValNull)
    eval("x < y") should be(ValNull)
  }

  it should "compare with `>`" in {
    eval("x > 1") should be(ValNull)
    eval("1 > x") should be(ValNull)
    eval("x > true") should be(ValNull)
    eval("true > x") should be(ValNull)
    eval(""" x > "string" """) should be(ValNull)
    eval(""" "string" > x """) should be(ValNull)
    eval("x > null") should be(ValNull)
    eval("null > x") should be(ValNull)
    eval("x > y") should be(ValNull)
  }

  it should "compare with `<=`" in {
    eval("x <= 1") should be(ValNull)
    eval("1 <= x") should be(ValNull)
    eval("x <= true") should be(ValNull)
    eval("true <= x") should be(ValNull)
    eval(""" x <= "string" """) should be(ValNull)
    eval(""" "string" <= x """) should be(ValNull)
    eval("x <= null") should be(ValNull)
    eval("null <= x") should be(ValNull)
    eval("x <= y") should be(ValNull)
  }

  it should "compare with `>=`" in {
    eval("x >= 1") should be(ValNull)
    eval("1 >= x") should be(ValNull)
    eval("x >= true") should be(ValNull)
    eval("true >= x") should be(ValNull)
    eval(""" x >= "string" """) should be(ValNull)
    eval(""" "string" >= x """) should be(ValNull)
    eval("x >= null") should be(ValNull)
    eval("null >= x") should be(ValNull)
    eval("x >= y") should be(ValNull)
  }

  it should "compare with `between _ and _`" in {
    eval("x between 1 and 3") should be(ValNull)
    eval("1 between x and 3") should be(ValNull)
    eval("3 between 1 and x") should be(ValNull)
    eval("x between y and 3") should be(ValNull)
    eval("x between 1 and y") should be(ValNull)
    eval("x between y and z") should be(ValNull)
  }
}
