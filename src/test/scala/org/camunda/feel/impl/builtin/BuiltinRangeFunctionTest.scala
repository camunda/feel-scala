package org.camunda.feel.impl.builtin

import org.camunda.feel.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree.ValBoolean
import org.scalatest.{FlatSpec, Matchers}

class BuiltinRangeFunctionTest
  extends FlatSpec
  with Matchers
  with FeelIntegrationTest {

  "A before() function" should "return true when a low number is entered before a high number" in {

    eval(" before(1, 10) ") should be(ValBoolean(true))
  }

  it should "return false when a high number is entered before a low number" in {

    eval(" before(10, 1)") should be(ValBoolean(false))
  }

  it should "return false when a number is in the range" in {

    eval(" before(1, \"[1..10]\")") should be(ValBoolean(false))
  }

  "An after() function" should "return true when a low is entered after a high number" in {

    eval(" after(10, 5) ") should be(ValBoolean(true))
  }

  it should "return false when low number is entered after high number" in {

    eval(" after(5, 10)") should be(ValBoolean(false))
  }

}
