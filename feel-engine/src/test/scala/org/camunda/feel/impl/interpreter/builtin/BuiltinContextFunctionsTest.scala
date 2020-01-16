package org.camunda.feel.impl.interpreter.builtin

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel.impl._
import org.camunda.feel.impl.interpreter.FeelIntegrationTest
import org.camunda.feel.impl.interpreter._
import scala.math.BigDecimal.int2bigDecimal

/**
  * @author Philipp
  */
class BuiltinContextFunctionsTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A get entries function" should "return all entries" in {

    val list = eval(""" get entries({foo: 123}) """)
    list shouldBe a[ValList]

    val items = list.asInstanceOf[ValList].items
    items should have size 1
    val context = items(0)
    context
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariables should be(
      Map("key" -> ValString("foo"), "value" -> ValNumber(123)))
  }

  it should "return empty list if emtpy" in {

    eval(""" get entries({}) """) should be(ValList(List()))
  }

  "A get value function" should "return the value" in {

    eval(""" get value({foo: 123}, "foo") """) should be(ValNumber(123))
  }

  it should "return null if not contains" in {

    eval(""" get value({}, "foo") """) should be(ValNull)
  }

}
