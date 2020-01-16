package org.camunda.feel.impl.interpreter

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel.impl._

/**
  * @author Philipp Ossler
  */
class InterpreterStringExpressionTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A string" should "concatenates to another String" in {

    eval(""" "a" + "b" """) should be(ValString("ab"))
  }

  it should "compare with '='" in {

    eval(""" "a" = "a" """) should be(ValBoolean(true))
    eval(""" "a" = "b" """) should be(ValBoolean(false))
  }

  it should "compare with '!='" in {

    eval(""" "a" != "a" """) should be(ValBoolean(false))
    eval(""" "a" != "b" """) should be(ValBoolean(true))
  }

  it should "compare with '<'" in {

    eval(""" "a" < "b" """) should be(ValBoolean(true))
    eval(""" "b" < "a" """) should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(""" "a" <= "a" """) should be(ValBoolean(true))
    eval(""" "b" <= "a" """) should be(ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(""" "b" > "a" """) should be(ValBoolean(true))
    eval(""" "a" > "b" """) should be(ValBoolean(false))
  }

  it should "compare with '>='" in {

    eval(""" "b" >= "b" """) should be(ValBoolean(true))
    eval(""" "a" >= "b" """) should be(ValBoolean(false))
  }

  it should "compare with null" in {

    eval(""" "a" = null """) should be(ValBoolean(false))
    eval(""" null = "a" """) should be(ValBoolean(false))
    eval(""" "a" != null """) should be(ValBoolean(true))
  }

}
