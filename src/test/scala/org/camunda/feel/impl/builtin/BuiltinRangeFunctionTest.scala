package org.camunda.feel.impl.builtin

import org.camunda.feel.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree.ValBoolean
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

class BuiltinRangeFunctionTest
    extends AnyFlatSpec
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

  it should "return false when a number is not in range and after in value with variables" in {

    eval(" before(point:11, range:[5..10])") should be(ValBoolean(false))
  }

  it should "return true when range end is included and range start is not included using variables for range" in {

    eval(" before(range1:[1..10], range2:(10..20])") should be(ValBoolean(true))
  }

  it should "return false when a high number is entered before a low number with variables" in {

    eval(" before(point1:10, point2:1)") should be(ValBoolean(false))
  }

  it should "return true when a high number is entered before a low number with variables" in {

    eval(" before(point1:1, point2:10)") should be(ValBoolean(true))
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

  "A meets() function" should "return true when range1 end incl and range2 start incl and range1.end equals range2.start" in {

    eval(" meets([1..5], [5..10]) ") should be(ValBoolean(true))
  }

  it should "return false if range1 end not incl. and range2 start incl. even when range1.end equal range2.start" in {

    eval(" meets([1..5),[5..10]) ") should be(ValBoolean(false))
  }

  it should "return false if range1 end incl. and range2 start not incl. even when range1.end equal range2.start" in {

    eval(" meets([1..5],(5..10]) ") should be(ValBoolean(false))
  }

  it should "return false if range1 end incl. and range2 start incl. but range1.end is not equal range2.start" in {

    eval(" meets([1..5],[6..10]) ") should be(ValBoolean(false))
  }

  "A met by() function" should "return true when range1 start incl and range2 end incl and range1.start equals range2.end" in {

    eval(" met by([5..10], [1..5]) ") should be(ValBoolean(true))
  }

  it should "return false if range1 start incl. and range2 end not incl. even when range1.start equal range2.end" in {

    eval(" met by([5..10],[1..5)) ") should be(ValBoolean(false))
  }

  it should "return false if range1 start not incl. and range2 end incl. even when range1.start equal range2.end" in {

    eval(" met by((5..10],[1..5]) ") should be(ValBoolean(false))
  }

  it should "return false if range1 start incl. and range2 end incl. but range1.start is not equal range2.end" in {

    eval(" met by([6..10],[1..5]) ") should be(ValBoolean(false))
  }

  "An overlaps() function" should "return true when smaller value range1 and higher value range2 overlaps" in {

    eval(" overlaps([1..5], [3..8]) ") should be(ValBoolean(true))
  }

  it should "return true when higher value range1 and smaller value range2 overlaps" in {

    eval(" overlaps([3..8],[1..5]) ") should be(ValBoolean(true))
  }

  it should "return true when range1 fully overlaps range2" in {

    eval(" overlaps([1..8],[3..5]) ") should be(ValBoolean(true))
  }

  it should "return true when range2 fully overlaps range1" in {

    eval(" overlaps([3..5],[1..8]) ") should be(ValBoolean(true))
  }

  it should "return false when smaller value range1 has no overlap to higher value range2" in {

    eval(" overlaps([1..5],[6..8]) ") should be(ValBoolean(false))
  }

  it should "return false when smaller value range2 has no overlap to higher value range1" in {

    eval(" overlaps([6..8],[1..5]) ") should be(ValBoolean(false))
  }

  it should "return true when range1 end overlaps range2 start both included" in {

    eval(" overlaps([1..5],[5..8]) ") should be(ValBoolean(true))
  }

  it should "return false when range1 end overlaps range2 start which is not included" in {

    eval(" overlaps([1..5],(5..8]) ") should be(ValBoolean(false))
  }

  it should "return false when range1 end overlaps range2 start but is not included" in {

    eval(" overlaps([1..5),[5..8]) ") should be(ValBoolean(false))
  }

  it should "return false when range1 end overlaps range2 start where both is not included" in {

    eval(" overlaps([1..5),(5..8]) ") should be(ValBoolean(false))
  }

  it should "return true when range1 start overlaps range2 end and both is included" in {

    eval(" overlaps([5..8],[1..5]) ") should be(ValBoolean(true))
  }

  it should "return false when range1 start overlaps range2 end but is not included" in {

    eval(" overlaps((5..8],[1..5]) ") should be(ValBoolean(false))
  }

  it should "return false when range1 start overlaps range2 end which is not included" in {

    eval(" overlaps([5..8],[1..5)) ") should be(ValBoolean(false))
  }

  it should "return false when range1 start overlaps range2 end where both is not included" in {

    eval(" overlaps((5..8],[1..5)) ") should be(ValBoolean(false))
  }

  "An overlaps before() function" should "return true when smaller value range1 and higher value range2 overlaps" in {

    eval(" overlaps before([1..5], [3..8]) ") should be(ValBoolean(true))
  }

  it should "return false when range1 has no overlaps to range2" in {

    eval(" overlaps before([1..5],[6..8]) ") should be(ValBoolean(false))
  }

  it should "return true when range1 end and range2 start overlaps and both are included" in {

    eval(" overlaps before([1..5],[5..8]) ") should be(ValBoolean(true))
  }

  it should "return false when range1 end and range2 start overlaps but range2 start not included" in {

    eval(" overlaps before([1..5],(5..8]) ") should be(ValBoolean(false))
  }

  it should "return false when range1 end and range2 start overlaps but range1 end not included" in {

    eval(" overlaps before([1..5),[5..8]) ") should be(ValBoolean(false))
  }

  it should "return true when range1 and range2 are the same except range2 start and range1 end is not included" in {

    eval(" overlaps before([1..5),(1..5]) ") should be(ValBoolean(true))
  }

  it should "return true when range1 and range2 are the same except range2 start is not included" in {

    eval(" overlaps before([1..5],(1..5]) ") should be(ValBoolean(true))
  }

  it should "return false when range1 and range2 are the same except range1 end is not included" in {

    eval(" overlaps before([1..5),[1..5]) ") should be(ValBoolean(false))
  }

  it should "return false when range1 and range2 are the same" in {

    eval(" overlaps before([1..5],[1..5]) ") should be(ValBoolean(false))
  }

  "An overlaps after() function" should "return true when higher value range1 and higher value range2 overlaps" in {

    eval(" overlaps after([3..8],[1..5]) ") should be(ValBoolean(true))
  }

  it should "return false when range1 has no overlap on range2" in {

    eval(" overlaps after([6..8],[1..5]) ") should be(ValBoolean(false))
  }

  it should "return true when range1 start overlaps range2 end where both is included" in {

    eval(" overlaps after([5..8],[1..5]) ") should be(ValBoolean(true))
  }

  it should "return false when range1 start overlaps range2 end which is not included" in {

    eval(" overlaps after([5..8],[1..5)) ") should be(ValBoolean(false))
  }

  it should "return ture when range1 start not incl overlaps range2 end not included" in {

    eval(" overlaps after((1..5],[1..5)) ") should be(ValBoolean(true))
  }

  it should "return true when range1 start not incl. overlaps range2 all included" in {

    eval(" overlaps after((1..5],[1..5]) ") should be(ValBoolean(true))
  }

  it should "return false when range1 all incl. overlaps range2 end not included" in {

    eval(" overlaps after([1..5],[1..5)) ") should be(ValBoolean(false))
  }

  it should "return false when range1 and range2 are the same" in {

    eval(" overlaps after([1..5],[1..5]) ") should be(ValBoolean(false))
  }

  "A finishes() function" should "return true when point is equal to range end included" in {

    eval(" finishes(10,[1..10]) ") should be(ValBoolean(true))
  }

  it should "return false when point is same as range end not included" in {

    eval(" finishes(10,[1..10)) ") should be(ValBoolean(false))
  }

  it should "return true when range1 end is same as range2 end both included" in {

    eval(" finishes([5..10],[1..10]) ") should be(ValBoolean(true))
  }

  it should "return false when range1 end is same as range2 end but range1 end not included" in {

    eval(" finishes([5..10),[1..10]) ") should be(ValBoolean(false))
  }

  it should "return true when range1 end is same as range2 end both not included" in {

    eval(" finishes([5..10),[1..10)) ") should be(ValBoolean(true))
  }

  it should "return true when range1 is same as range2 all included" in {

    eval(" finishes([1..10],[1..10]) ") should be(ValBoolean(true))
  }

  it should "return true when range1 is same as range2 but range1 start not included" in {

    eval(" finishes((1..10],[1..10]) ") should be(ValBoolean(true))
  }

  "A started by() function" should "return true when range start is equal to point" in {

    eval(" started by([1..10], 1) ") should be(ValBoolean(true))
  }

  it should "return false when range start is equal to point but range start not included" in {

    eval(" started by((1..10], 1) ") should be(ValBoolean(false))
  }

  it should "return false when range start is not equal to point" in {

    eval(" started by((1..10], 2) ") should be(ValBoolean(false))
  }

  it should "return true when range1 start is equal to range2 start" in {

    eval(" started by([1..10], [1..5]) ") should be(ValBoolean(true))
  }

  it should "return true when range1 start is equal to range2 start even when both ranges does not have start included" in {

    eval(" started by((1..10], (1..5]) ") should be(ValBoolean(true))
  }

  it should "return false when range1 start is equal to range2 start which is not included" in {

    eval(" started by([1..10], (1..5]) ") should be(ValBoolean(false))
  }

  it should "return false when range1 start is equal to range2 start but range1 start is not included" in {

    eval(" started by((1..10], [1..5]) ") should be(ValBoolean(false))
  }

  it should "return true when range1 is equal to range2" in {

    eval(" started by([1..10], [1..10]) ") should be(ValBoolean(true))
  }

  it should "return true when range1 is equal to range2 where range2 end not included" in {

    eval(" started by([1..10], [1..10)) ") should be(ValBoolean(true))
  }

  it should "return true when range1 is equal to range2 where both ranges does not include end and start" in {

    eval(" started by((1..10), (1..10)) ") should be(ValBoolean(true))
  }

  "A coincides() function" should "return true when point1 is equal to point2" in {

    eval(" coincides(5, 5) ") should be(ValBoolean(true))
  }

  it should "return false when point1 is not equal to point2" in {

    eval(" coincides(3, 4) ") should be(ValBoolean(false))
  }

  it should "return true when range1 is equal to range2" in {

    eval(" coincides([1..5], [1..5]) ") should be(ValBoolean(true))
  }

  it should "return false when range1 is not equal to range2 because of range1 end and start is not included" in {

    eval(" coincides((1..5), [1..5]) ") should be(ValBoolean(false))
  }

  it should "return false when range1 is not equal to range2" in {

    eval(" coincides([1..5], [2..6]) ") should be(ValBoolean(false))
  }
}
