package org.camunda.feel.interpreter.impl.builtin

import org.camunda.feel.interpreter.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree._
import org.scalatest.{FlatSpec, Matchers}

/**
  * @author Philipp
  */
class BuiltinFunctionsTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A built-in function" should "return null if arguments doesn't match" in {

    eval("date(true)") should be(ValNull)
    eval("number(false)") should be(ValNull)
  }

  "A not() function" should "negate Boolean" in {

    eval(" not(true) ") should be(ValBoolean(false))
    eval(" not(false) ") should be(ValBoolean(true))
  }

}
