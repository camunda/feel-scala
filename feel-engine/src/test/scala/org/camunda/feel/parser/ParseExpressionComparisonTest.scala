package org.camunda.feel.parser

import org.camunda.feel._
import org.camunda.feel.parser.FeelParser._

import org.scalatest.FlatSpec
import org.scalatest.Matchers

/**
 * @author Philipp Ossler
 */
class ParseExpressionComparisonTest extends FlatSpec with Matchers {

  "A comparison expression" should "be '='" in {

    // string
    parse("""a="b" """) should be(Equal(Ref("a"), ConstString("b")))
    // boolean
    parse("a=true") should be(Equal(Ref("a"), ConstBool(true)))
    // numeric
    parse("a=1") should be(Equal(Ref("a"), ConstNumber(1)))
    parse("(a * 2) = 4") should be(Equal( Multiplication(Ref("a"), ConstNumber(2)), ConstNumber(4) ))
    // date
    parse("""a=date("2015-09-18")""") should be(Equal(Ref("a"), ConstDate("2015-09-18")))
    // time
    parse("""a=time("10:00:00")""") should be(Equal(Ref("a"), ConstLocalTime("10:00:00")))
  }

  it should "be '!='" in {

    // string
    parse("""a!="b" """) should be(Not(Equal(Ref("a"), ConstString("b"))))
    // boolean
    parse("a!=true") should be(Not(Equal(Ref("a"), ConstBool(true))))
    // numeric
    parse("a!=1") should be(Not(Equal(Ref("a"), ConstNumber(1))))
    // date
    parse("""a!=date("2015-09-18")""") should be(Not(Equal(Ref("a"), ConstDate("2015-09-18"))))
    // time
    parse("""a!=time("10:00:00")""") should be(Not(Equal(Ref("a"), ConstLocalTime("10:00:00"))))
  }

  it should "be '<'" in {

    // numeric
    parse("a<2") should be(LessThan(Ref("a"), ConstNumber(2)))
    // date
    parse("""a<date("2015-09-18")""") should be(LessThan(Ref("a"), ConstDate("2015-09-18")))
    // time
    parse("""a<time("10:00:00")""") should be(LessThan(Ref("a"), ConstLocalTime("10:00:00")))
  }

  it should "be '<='" in {

    // numeric
    parse("a<=2") should be(LessOrEqual(Ref("a"), ConstNumber(2)))
    // date
    parse("""a<=date("2015-09-18")""") should be(LessOrEqual(Ref("a"), ConstDate("2015-09-18")))
    // time
    parse("""a<=time("10:00:00")""") should be(LessOrEqual(Ref("a"), ConstLocalTime("10:00:00")))
  }

  it should "be '>'" in {

    // numeric
    parse("a>2") should be(GreaterThan(Ref("a"), ConstNumber(2)))
    // date
    parse("""a>date("2015-09-18")""") should be(GreaterThan(Ref("a"), ConstDate("2015-09-18")))
    // time
    parse("""a>time("10:00:00")""") should be(GreaterThan(Ref("a"), ConstLocalTime("10:00:00")))
  }

  it should "be '>='" in {

    // numeric
    parse("a>=2") should be(GreaterOrEqual(Ref("a"), ConstNumber(2)))
    // date
    parse("""a>=date("2015-09-18")""") should be(GreaterOrEqual(Ref("a"), ConstDate("2015-09-18")))
    // time
    parse("""a>=time("10:00:00")""") should be(GreaterOrEqual(Ref("a"), ConstLocalTime("10:00:00")))
  }

  it should "be 'between _ and _' " in {

    // numeric
    parse("a between 2 and 4") should be(
        Conjunction(GreaterOrEqual(Ref("a"), ConstNumber(2)), LessOrEqual(Ref("a"), ConstNumber(4))))

    // time
    parse(""" a between time("10:00:00") and time("14:00:00") """) should be(
        Conjunction(GreaterOrEqual(Ref("a"), ConstLocalTime("10:00:00")), LessOrEqual(Ref("a"), ConstLocalTime("14:00:00"))))
  }

  it should "be 'in'" in {

    // endpoint
    parse("a in < 2") should be(In(Ref("a"), InputLessThan(ConstNumber(2))))
    // interval
    parse("a in (2 .. 4)") should be(In(Ref("a"),
        Interval(OpenIntervalBoundary(ConstNumber(2)), OpenIntervalBoundary(ConstNumber(4)))))
    // null value
    parse("a in null") should be(In(Ref("a"), InputEqualTo(ConstNull)))
    // multiple tests
    parse("a in (2,4,6)") should be(In(Ref("a"),
        AtLeastOne(List(
            InputEqualTo(ConstNumber(2)),
            InputEqualTo(ConstNumber(4)),
            InputEqualTo(ConstNumber(6)) ))) )
  }

  private def parse(expression: String): Exp =
    FeelParser.parseExpression(expression) match {
      case Success(exp, _) => exp
      case e: NoSuccess => throw new RuntimeException(s"failed to parse expression '$expression':\n$e")
    }

}
