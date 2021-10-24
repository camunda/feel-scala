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

    eval(" before(1, [1..10])") should be(ValBoolean(false))
  }

  it should "return true when a number is in the range" in {

    eval(" before(1, (1..10])") should be(ValBoolean(true))
  }

  it should "return true when a number is not in range" in {

    eval(" before(1, [5..10])") should be(ValBoolean(true))
  }

  it should "return false when range is including end which is the same as number" in {

    eval(" before([1..10], 10)") should be(ValBoolean(false))
  }

  it should "return true when range is not including end which is same as number" in {

    eval(" before([1..10), 10)") should be(ValBoolean(true))
  }

  it should "return true when range is before number" in {

    eval(" before([1..10], 15)") should be(ValBoolean(true))
  }

  it should "return true when range is before another range" in {

    eval(" before([1..10], [15..20])") should be(ValBoolean(true))
  }

  it should "return false when range end is included an is start of another range" in {

    eval(" before([1..10], [10..20])") should be(ValBoolean(false))
  }

  it should "return true when range end is not included an is start of another range" in {

    eval(" before([1..10), [10..20])") should be(ValBoolean(true))
  }

  it should "return true when range end is included and range start is not included" in {

    eval(" before([1..10], (10..20])") should be(ValBoolean(true))
  }

  it should "return true because range end is included and point is higher" in {

    eval(" before([1..10], 20)") should be(ValBoolean(true))
  }

  "An after() function" should "return true when a low is entered after a high number" in {

    eval(" after(10, 5) ") should be(ValBoolean(true))
  }

  it should "return false when low number is entered after high number" in {

    eval(" after(5, 10)") should be(ValBoolean(false))
  }

  it should "return true when number is after range" in {

    eval(" after(12, [1..10])") should be(ValBoolean(true))
  }

  it should "return true when number is after range if number not included in range" in {

    eval(" after(10, [1..10))") should be(ValBoolean(true))
  }

  it should "return false when number is range end which is included" in {

    eval(" after(10, [1..10])") should be(ValBoolean(false))
  }

  it should "return false when range includes number" in {

    eval(" after([11..20), 12)") should be(ValBoolean(false))
  }

  it should "return true range is after number" in {

    eval(" after([11..20], 10)") should be(ValBoolean(true))
  }

  it should "return true when range is after even when number is same as start which is not included in range" in {

    eval(" after((11..20], 11)") should be(ValBoolean(true))
  }

  it should "return false when range is after but when number is same as start of range" in {

    eval(" after([11..20], 11)") should be(ValBoolean(false))
  }

  it should "return true when range is after another range" in {

    eval(" after([11..20], [1..10])") should be(ValBoolean(true))
  }

  it should "return false when range is not after another range" in {

    eval(" after([1..10], [11..20])") should be(ValBoolean(false))
  }

  it should "return true when range is after another range even when end of 2nd range overlaps" in {

    eval(" after([11..20], [1..11))") should be(ValBoolean(true))
  }

  it should "return true when 1st range is after another range even when start of 1st range overlaps but not included" in {

    eval(" after((11..20], [1..11])") should be(ValBoolean(true))
  }
}
