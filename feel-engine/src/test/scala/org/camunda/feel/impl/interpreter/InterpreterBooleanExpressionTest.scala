package org.camunda.feel.impl.interpreter

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel.impl._

/**
  * @author Philipp Ossler
  */
class InterpreterBooleanExpressionTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A boolean" should "compare with '='" in {

    eval("true = true") should be(ValBoolean(true))
    eval("true = false") should be(ValBoolean(false))
  }

  it should "compare with null" in {

    eval(""" true = null """) should be(ValBoolean(false))
    eval(""" null = false """) should be(ValBoolean(false))
    eval(""" true != null """) should be(ValBoolean(true))
    eval(""" null != false """) should be(ValBoolean(true))
  }

  it should "be in conjunction" in {

    eval("true and true") should be(ValBoolean(true))
    eval("true and false") should be(ValBoolean(false))

    eval("true and true and false") should be(ValBoolean(false))

    eval("true and 2") should be(ValNull)
    eval("false and 2") should be(ValBoolean(false))

    eval("2 and true") should be(ValNull)
    eval("2 and false") should be(ValBoolean(false))

    eval("2 and 4") should be(ValNull)
  }

  it should "be in disjunction" in {

    eval("false or true") should be(ValBoolean(true))
    eval("false or false") should be(ValBoolean(false))

    eval("false or false or true") should be(ValBoolean(true))

    eval("true or 2") should be(ValBoolean(true))
    eval("false or 2") should be(ValNull)

    eval("2 or true") should be(ValBoolean(true))
    eval("2 or false") should be(ValNull)

    eval("2 or 4") should be(ValNull)
  }

  it should "negate" in {

    eval("not(true)") should be(ValBoolean(false))
    eval("not(false)") should be(ValBoolean(true))

    eval("not(2)") should be(ValNull)
  }

}
