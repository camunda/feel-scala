package org.camunda.feel.interpreter.builtin

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._
import org.camunda.feel.interpreter.FeelIntegrationTest
import org.camunda.feel.interpreter._
import scala.math.BigDecimal.int2bigDecimal

/**
 * @author Philipp
 */
class BuiltinContextFunctionsTest extends FlatSpec with Matchers with FeelIntegrationTest {

  "A get entries function" should "return all entries" in {

    eval(""" get entries({foo: 123}) """) should be(ValList(List(ValContext(DefaultContext(Map(
      "key" -> ValString("foo"),
      "value" -> ValNumber(123)))))))
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
