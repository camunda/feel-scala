package org.camunda.feel.interpreter.impl

import org.scalatest.{FlatSpec, Matchers}
import org.camunda.feel.syntaxtree._

/**
  * @author Philipp Ossler
  */
class InterpreterNumberExpressionTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A number" should "add to '4'" in {

    eval("2+4") should be(ValNumber(6))
  }

  it should "add to '4' and '6'" in {

    eval("2+4+6") should be(ValNumber(12))
  }

  it should "add to not-a-number" in {

    eval("2 + true") should be(ValNull)
    eval("false + 3") should be(ValNull)
  }

  it should "subtract from '2'" in {

    eval("4-2") should be(ValNumber(2))
  }

  it should "subtract to not-a-number" in {

    eval("2 - true") should be(ValNull)
    eval("false - 3") should be(ValNull)
  }

  it should "add and subtract" in {

    eval("2+4-3+1") should be(ValNumber(4))
  }

  it should "multiply by '3'" in {

    eval("3*3") should be(ValNumber(9))
  }

  it should "multiply to not-a-number" in {

    eval("2 * true") should be(ValNull)
    eval("false * 3") should be(ValNull)
  }

  it should "divide by '4'" in {

    eval("8/4") should be(ValNumber(2))
  }

  it should "be null if divide by zero" in {

    eval("2 / 0") should be(ValNull)
  }

  it should "divide to not-a-number" in {

    eval("2 / true") should be(ValNull)
    eval("false / 3") should be(ValNull)
  }

  it should "multiply and divide" in {

    eval("3*4/2*5") should be(ValNumber(30))
  }

  it should "exponentiate by '3'" in {

    eval("2**3") should be(ValNumber(8))
  }

  it should "exponentiate twice" in {
    // all operators are left associative
    eval("2**2**3") should be(ValNumber(64))
  }

  it should "exponentiate by '3.1'" in {

    eval("2**3.1") should be(ValNumber(8.574187700290345))
  }

  it should "negate" in {

    eval("-2") should be(ValNumber(-2))
  }

  it should "negate and multiply" in {

    eval("2 * -3") should be(ValNumber(-6))
  }

  it should "add and multiply" in {

    eval("2 + 3 * 4") should be(ValNumber(14))

    eval("2 * 3 + 4") should be(ValNumber(10))
  }

  it should "multiply and exponentiate" in {

    eval("2**3 * 4") should be(ValNumber(32))

    eval("3 * 4**2") should be(ValNumber(48))
  }

  it should "compare with '='" in {

    eval("x=2", Map("x" -> 2)) should be(ValBoolean(true))
    eval("x=2", Map("x" -> 3)) should be(ValBoolean(false))

    eval("(x * 2) = 4", Map("x" -> 2)) should be(ValBoolean(true))
    eval("(x * 2) = 4", Map("x" -> 3)) should be(ValBoolean(false))

    eval("x = -1", Map("x" -> -1)) should be(ValBoolean(true))
    eval("x = -1", Map("x" -> 1)) should be(ValBoolean(false))
  }

  it should "compare with '!='" in {

    eval("x!=2", Map("x" -> 2)) should be(ValBoolean(false))
    eval("x!=2", Map("x" -> 3)) should be(ValBoolean(true))
  }

  it should "compare with '<'" in {

    eval("x<2", Map("x" -> 1)) should be(ValBoolean(true))
    eval("x<2", Map("x" -> 2)) should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval("x<=2", Map("x" -> 2)) should be(ValBoolean(true))
    eval("x<=2", Map("x" -> 3)) should be(ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval("x>2", Map("x" -> 2)) should be(ValBoolean(false))
    eval("x>2", Map("x" -> 3)) should be(ValBoolean(true))
  }

  it should "compare with '>='" in {

    eval("x>=2", Map("x" -> 2)) should be(ValBoolean(true))
    eval("x>=2", Map("x" -> 1)) should be(ValBoolean(false))
  }

  it should "compare with null" in {

    eval("2 = null") should be(ValBoolean(false))
    eval("null = 2") should be(ValBoolean(false))
    eval("null != 2") should be(ValBoolean(true))

    eval("2 > null") should be(ValBoolean(false))
    eval("null < 2") should be(ValBoolean(false))

    eval("null in < 2") should be(ValBoolean(false))
    eval("null in (2..4)") should be(ValBoolean(false))
  }

  it should "compare with 'between _ and _'" in {

    eval("x between 2 and 4", Map("x" -> 1)) should be(ValBoolean(false))
    eval("x between 2 and 4", Map("x" -> 2)) should be(ValBoolean(true))
    eval("x between 2 and 4", Map("x" -> 3)) should be(ValBoolean(true))
    eval("x between 2 and 4", Map("x" -> 4)) should be(ValBoolean(true))
    eval("x between 2 and 4", Map("x" -> 5)) should be(ValBoolean(false))
  }

  it should "compare with 'in'" in {

    eval("x in < 2", Map("x" -> 1)) should be(ValBoolean(true))
    eval("x in < 2", Map("x" -> 2)) should be(ValBoolean(false))

    eval("x in (2 .. 4)", Map("x" -> 3)) should be(ValBoolean(true))
    eval("x in (2 .. 4)", Map("x" -> 4)) should be(ValBoolean(false))

    eval("x in (2,4,6)", Map("x" -> 4)) should be(ValBoolean(true))
    eval("x in (2,4,6)", Map("x" -> 5)) should be(ValBoolean(false))

    eval("3 in (2 .. 4)") should be(ValBoolean(true))
    eval("4 in (2 .. 4)") should be(ValBoolean(false))
  }

  it should "be null if nAn" in {

    eval("x", Map("x" -> Double.NaN)) should be(ValNull)
  }

  it should "be null if infinity" in {

    eval("x", Map("x" -> Double.PositiveInfinity)) should be(ValNull)
    eval("x", Map("x" -> Double.NegativeInfinity)) should be(ValNull)
  }

}
