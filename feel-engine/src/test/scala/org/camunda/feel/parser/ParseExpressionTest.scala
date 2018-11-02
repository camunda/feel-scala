package org.camunda.feel.parser

import org.camunda.feel._
import org.camunda.feel.parser.FeelParser._

import org.scalatest.FlatSpec
import org.scalatest.Matchers

/**
 * @author Philipp Ossler
 */
class ParseExpressionTest extends FlatSpec with Matchers {

  "An expression" should "be a name" in {

    parse("b") should be(Ref("b"))

    // contains a reserved word
    parse("forename") should be(Ref("forename"))
    parse("before") should be(Ref("before"))
  }

  it should "be a quoted name 'a'" in {

    parse(" 'a' ") should be(Ref("a"))
  }

  it should "be a quoted name 'a b'" in {

    parse(" 'a b' ") should be(Ref("a b"))
  }

  it should "be a quoted name 'a-b'" in {

    parse(" 'a-b' ") should be(Ref("a-b"))
  }
  it should "parse null" in {

    parse("null") should be(ConstNull)
  }

  it should "parse ?" in {

    parse("?") should be(ConstInputValue)
  }

  it should "parse an expression inside brackets" in {

    parse("(42)") should be(ConstNumber(42))
  }

  it should "ignore an one line comment '// ...'" in {

    parse("""duration("P1D") // one day""") should be(ConstDayTimeDuration("P1D"))
  }

  it should "ignore a multi line comment '/* ... */'" in {

    parse("""duration("P1DT4H") /*
                one day and 4 hours
             */ """)
  }

  it should "parse an addition" in {

    // numeric addition
    parse("2+3") should be(Addition(ConstNumber(2), ConstNumber(3)))
    parse("2+3+4") should be(Addition(Addition(
      ConstNumber(2),
      ConstNumber(3)), ConstNumber(4)))
  }

  it should "parse a substraction" in {

    // numeric subtraction
    parse("3-2") should be(Subtraction(ConstNumber(3), ConstNumber(2)))
  }

  it should "parse a multiplication" in {

    parse("2*4") should be(Multiplication(ConstNumber(2), ConstNumber(4)))
  }

  it should "parse a division" in {

    parse("4/2") should be(Division(ConstNumber(4), ConstNumber(2)))
  }

  it should "parse an exponentiation" in {

    parse("2**4") should be(Exponentiation(ConstNumber(2), ConstNumber(4)))
  }

  it should "parse an arithmetic negation" in {

    parse("-4") should be(ArithmeticNegation(ConstNumber(4)))
  }

  it should "parse a function definition" in {

    parse("function() 42") should be(FunctionDefinition(
      parameters = List(),
      body = ConstNumber(42)))

    parse("function(a,b) a + b") should be(FunctionDefinition(
      parameters = List("a", "b"),
      body = Addition(Ref("a"), Ref("b"))))
  }

  it should "parse a function invocation without parameters" in {

    parse("f()") should be(FunctionInvocation(
      "f",
      params = PositionalFunctionParameters(List())))
  }

  it should "parse a function invocation with positional parameters" in {

    parse("fib(1)") should be(FunctionInvocation(
      "fib",
      params = PositionalFunctionParameters(List(ConstNumber(1)))))

    parse("""concat("in", x)""") should be(FunctionInvocation(
      "concat",
      params = PositionalFunctionParameters(List(
        ConstString("in"),
        Ref("x")))))
  }

  it should "parse a function invocation with named parameters" in {

    parse("f(a:1)") should be(FunctionInvocation(
      "f",
      params = NamedFunctionParameters(Map("a" -> ConstNumber(1)))))

    parse("f(a:1, b:true)") should be(FunctionInvocation(
      "f",
      params = NamedFunctionParameters(Map(
        "a" -> ConstNumber(1),
        "b" -> ConstBool(true)))))
  }

  it should "parse a function invocation with escaped name" in {

    parse(" 'a b'(1) ") should be(FunctionInvocation(
      "a b",
      params = PositionalFunctionParameters(List(ConstNumber(1)))))
  }

  it should "parse a function invocation with ? argument" in {

    parse("f(?, 21)") should be(FunctionInvocation(
      "f",
      params = PositionalFunctionParameters(List(
        ConstInputValue,
        ConstNumber(21)))))
  }

  it should "parse an if-then-else condition" in {

    parse(""" if (x < 5) then "low" else "high" """) should be(If(
      condition = LessThan(Ref("x"), ConstNumber(5)),
      statement = ConstString("low"),
      elseStatement = ConstString("high")))
  }

  it should "parse a disjunction" in {

    parse("true or false") should be(Disjunction(
      ConstBool(true), ConstBool(false)))

    parse("(a < 5) or (b < 10)") should be(Disjunction(
      LessThan(Ref("a"), ConstNumber(5)),
      LessThan(Ref("b"), ConstNumber(10))))
  }

  it should "parse a conjunction" in {

    parse("a and b") should be(Conjunction(Ref("a"), Ref("b")))
  }

  it should "parse a simple positive unary test" in {

    parse("< 3") should be(InputLessThan(ConstNumber(3)))

    parse("[2..4]") should be(
      Interval(
        start = ClosedIntervalBoundary(ConstNumber(2)),
        end = ClosedIntervalBoundary(ConstNumber(4))))
  }

  it should "parse a 'instance of'" in {

    parse("x instance of number") should be(InstanceOf(Ref("x"), "number"))
  }

  it should "parse a 'some' expression" in {

    parse("some x in [1,2] satisfies x < 3") should be(SomeItem(
      List("x" -> ConstList(List(ConstNumber(1), ConstNumber(2)))),
      LessThan(Ref("x"), ConstNumber(3))))

    parse("some x in [1,2], y in [3,4] satisfies x < y") should be(SomeItem(
      List(
        "x" -> ConstList(List(ConstNumber(1), ConstNumber(2))),
        "y" -> ConstList(List(ConstNumber(3), ConstNumber(4)))),
      LessThan(Ref("x"), Ref("y"))))
  }

  it should "parse an 'every' expression" in {

    parse("every x in [1,2] satisfies x < 3") should be(EveryItem(
      List("x" -> ConstList(List(ConstNumber(1), ConstNumber(2)))),
      LessThan(Ref("x"), ConstNumber(3))))

    parse("every x in [1,2], y in [3,4] satisfies x < y") should be(EveryItem(
      List(
        "x" -> ConstList(List(ConstNumber(1), ConstNumber(2))),
        "y" -> ConstList(List(ConstNumber(3), ConstNumber(4)))),
      LessThan(Ref("x"), Ref("y"))))
  }

  it should "parse a 'for' expression" in {

    parse("for x in [1,2] return x") should be(For(
      List("x" -> ConstList(List(ConstNumber(1), ConstNumber(2)))),
      Ref("x")))

    parse("for x in [1,2], y in [3,4] return x + y") should be(For(
      List(
        "x" -> ConstList(List(ConstNumber(1), ConstNumber(2))),
        "y" -> ConstList(List(ConstNumber(3), ConstNumber(4)))),
      Addition(Ref("x"), Ref("y"))))
  }

  it should "parse a 'for' expression with range" in {

    parse("for x in 1..5 return x") should be(For(
      List("x" -> Range(ConstNumber(1), ConstNumber(5))),
      Ref("x")))

    parse("for x in 1..5, y in a..b return x + y") should be(For(
      List(
        "x" -> Range(ConstNumber(1), ConstNumber(5)),
        "y" -> Range(Ref("a"), Ref("b"))),
      Addition(Ref("x"), Ref("y"))))
  }

  it should "parse a 'filter' expression" in {

    parse("[1,2][item < 1]") should be(Filter(
      ConstList(List(ConstNumber(1), ConstNumber(2))),
      LessThan(Ref("item"), ConstNumber(1))))
  }

  it should "parse a path expression" in {

    parse("a.b") should be(PathExpression(Ref("a"), "b"))

    parse("a.b.c") should be(PathExpression(
      PathExpression(
        Ref("a"), "b"), "c"))
  }

  it should "parse a path expression with escaped name" in {

    parse(" 'a b'.'c d'") should be(PathExpression(Ref("a b"), "c d"))
  }

  private def parse(expression: String): Exp =
    FeelParser.parseExpression(expression) match {
      case Success(exp, _) => exp
      case e: NoSuccess    => throw new RuntimeException(s"failed to parse expression '$expression':\n$e")
    }

}
