package org.camunda.feel.parser

import org.camunda.feel._
import org.camunda.feel.parser.FeelParser._

import org.scalatest.FlatSpec
import org.scalatest.Matchers

/**
 * @author Philipp Ossler
 */
class ParseExpressionTest extends FlatSpec with Matchers {

  "A parser for expression" should "parse number" in {

    parse("3") should be(ConstNumber(3))
    parse("3.2") should be(ConstNumber(3.2))
    parse(".2") should be(ConstNumber(.2))
  }

  it should "parse a string" in {

    parse(""" "a" """) should be(ConstString("a"))
  }

  it should "parse a name" in {

    parse("b") should be(Ref("b"))

    // contains a reserved word
    parse("forename") should be(Ref("forename"))
    parse("before") should be(Ref("before"))
  }
  
  it should "parse an escaped name" in {

    parse(" 'a' ") should be(Ref("a"))
    parse(" 'a b' ") should be(Ref("a b"))
    parse(" 'a-b' ") should be(Ref("a-b"))
  }

  it should "parse a boolean" in {

    parse("true") should be(ConstBool(true))
    parse("false") should be(ConstBool(false))
  }

  it should "parse a date" in {

    parse("""date("2015-09-18")""") should be(ConstDate("2015-09-18"))
  }

  it should "parse a time" in {

    parse("""time("10:31:10")""") should be(ConstLocalTime("10:31:10"))
  }

  it should "parse a time with offset" in {

    parse("""time("10:31:10+01:00")""") should be(ConstTime("10:31:10+01:00"))
    parse("""time("10:31:10-02:00")""") should be(ConstTime("10:31:10-02:00"))
  }
  
  it should "parse a time with zone" in {

    parse("""time("10:31:10@Europe/Paris")""") should be(ConstTime("10:31:10@Europe/Paris"))
    parse("""time("10:31:10@Etc/UTC")""") should be(ConstTime("10:31:10@Etc/UTC"))
  }

  it should "parse a date-time" in {

    parse("""date and time("2015-09-18T10:31:10")""") should be(ConstLocalDateTime("2015-09-18T10:31:10"))
  }

  it should "parse a date-time with offset" in {

    parse("""date and time("2015-09-18T10:31:10+01:00")""") should be(ConstDateTime("2015-09-18T10:31:10+01:00"))
    parse("""date and time("2015-09-18T10:31:10-02:00")""") should be(ConstDateTime("2015-09-18T10:31:10-02:00"))
  }

  it should "parse a year-month-duration" in {

    parse("""duration("P1Y2M")""") should be(ConstYearMonthDuration("P1Y2M"))
  }

  it should "parse a day-time-duration" in {

    parse("""duration("P1DT2H3M4S")""") should be(ConstDayTimeDuration("P1DT2H3M4S"))
  }

  it should "parse null" in {

    parse("null") should be(ConstNull)
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
    parse("2+3+4") should be(Addition(Addition(ConstNumber(2),
        ConstNumber(3)), ConstNumber(4) ))
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

  it should "parse a '=' comparison" in {

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

  it should "parse a '!=' comparison" in {

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

  it should "parse a '<' comparison" in {

    // numeric
    parse("a<2") should be(LessThan(Ref("a"), ConstNumber(2)))
    // date
    parse("""a<date("2015-09-18")""") should be(LessThan(Ref("a"), ConstDate("2015-09-18")))
    // time
    parse("""a<time("10:00:00")""") should be(LessThan(Ref("a"), ConstLocalTime("10:00:00")))
  }

  it should "parse a '<=' comparison" in {

    // numeric
    parse("a<=2") should be(LessOrEqual(Ref("a"), ConstNumber(2)))
    // date
    parse("""a<=date("2015-09-18")""") should be(LessOrEqual(Ref("a"), ConstDate("2015-09-18")))
    // time
    parse("""a<=time("10:00:00")""") should be(LessOrEqual(Ref("a"), ConstLocalTime("10:00:00")))
  }

  it should "parse a '>' comparison" in {

    // numeric
    parse("a>2") should be(GreaterThan(Ref("a"), ConstNumber(2)))
    // date
    parse("""a>date("2015-09-18")""") should be(GreaterThan(Ref("a"), ConstDate("2015-09-18")))
    // time
    parse("""a>time("10:00:00")""") should be(GreaterThan(Ref("a"), ConstLocalTime("10:00:00")))
  }

  it should "parse a '>=' comparison" in {

    // numeric
    parse("a>=2") should be(GreaterOrEqual(Ref("a"), ConstNumber(2)))
    // date
    parse("""a>=date("2015-09-18")""") should be(GreaterOrEqual(Ref("a"), ConstDate("2015-09-18")))
    // time
    parse("""a>=time("10:00:00")""") should be(GreaterOrEqual(Ref("a"), ConstLocalTime("10:00:00")))
  }

  it should "parse a 'between _ and _' comparison " in {

    // numeric
    parse("a between 2 and 4") should be(
        Conjunction(GreaterOrEqual(Ref("a"), ConstNumber(2)), LessOrEqual(Ref("a"), ConstNumber(4))))

    // time
    parse(""" a between time("10:00:00") and time("14:00:00") """) should be(
        Conjunction(GreaterOrEqual(Ref("a"), ConstLocalTime("10:00:00")), LessOrEqual(Ref("a"), ConstLocalTime("14:00:00"))))
  }

  it should "parse a 'in' comparision" in {

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

  it should "parse a function definition" in {

    parse("function() 42") should be(FunctionDefinition(
        parameters = List(),
        body = ConstNumber(42) ))

    parse("function(a,b) a + b") should be(FunctionDefinition(
        parameters = List("a","b"),
        body = Addition(Ref("a"), Ref("b")) ))
  }

  it should "parse a function invocation without parameters" in {

    parse("f()") should be(FunctionInvocation("f",
      params = PositionalFunctionParameters( List())) )
  }

  it should "parse a function invocation with positional parameters" in {

    parse("fib(1)") should be(FunctionInvocation("fib",
      params = PositionalFunctionParameters( List(ConstNumber(1)))) )

    parse("""concat("in", x)""") should be(FunctionInvocation("concat",
      params = PositionalFunctionParameters( List(
        ConstString("in"),
        Ref("x")))) )
  }

  it should "parse a function invocation with named parameters" in {

    parse("f(a:1)") should be(FunctionInvocation("f",
      params = NamedFunctionParameters( Map("a" -> ConstNumber(1)))) )

    parse("f(a:1, b:true)") should be(FunctionInvocation("f",
      params = NamedFunctionParameters( Map(
          "a" -> ConstNumber(1),
          "b" -> ConstBool(true)))) )
  }
  
  it should "parse a function invocation with escaped name" in {

    parse(" 'a b'(1) ") should be(FunctionInvocation("a b",
      params = PositionalFunctionParameters( List(ConstNumber(1)))) )
  }

  it should "parse a context" in {

    parse("{}") should be(ConstContext(List()))

    parse("{ a : 1 }") should be(ConstContext(List(
        ("a" -> ConstNumber(1)) )))

    parse("{ a:1, b:true }") should be(ConstContext(List(
        ("a" -> ConstNumber(1)),
        ("b" -> ConstBool(true)) )))
  }

  it should "parse a list" in {

    parse("[]") should be(ConstList(List()))

    parse("[1]") should be(ConstList( List(ConstNumber(1)) ))

    parse("[1, 2]") should be(ConstList(List(
        ConstNumber(1),
        ConstNumber(2) )))
  }

  it should "parse an if-then-else condition" in {

    parse(""" if (x < 5) then "low" else "high" """) should be(If(
      condition = LessThan(Ref("x"), ConstNumber(5)),
      statement = ConstString("low"),
      elseStatement = ConstString("high")
    ))
  }

  it should "parse a disjunction" in {

    parse("true or false") should be(Disjunction(
        ConstBool(true), ConstBool(false)) )

    parse("(a < 5) or (b < 10)") should be(Disjunction(
        LessThan(Ref("a"), ConstNumber(5)),
        LessThan(Ref("b"), ConstNumber(10) )))
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
  			List("x" -> ConstList(List(ConstNumber(1), ConstNumber(2))) ),
  			LessThan(Ref("x"), ConstNumber(3)) ))

  	parse("some x in [1,2], y in [3,4] satisfies x < y") should be(SomeItem(
  			List(
  			    "x" -> ConstList(List(ConstNumber(1), ConstNumber(2))),
  			    "y" -> ConstList(List(ConstNumber(3), ConstNumber(4))) ),
  			LessThan(Ref("x"), Ref("y")) ))
  }

  it should "parse an 'every' expression" in {

  	parse("every x in [1,2] satisfies x < 3") should be(EveryItem(
  			List("x" -> ConstList(List(ConstNumber(1), ConstNumber(2))) ),
  			LessThan(Ref("x"), ConstNumber(3)) ))

  	parse("every x in [1,2], y in [3,4] satisfies x < y") should be(EveryItem(
  			List(
  			    "x" -> ConstList(List(ConstNumber(1), ConstNumber(2))),
  			    "y" -> ConstList(List(ConstNumber(3), ConstNumber(4))) ),
  			LessThan(Ref("x"), Ref("y")) ))
  }

  it should "parse a 'for' expression" in {

    parse("for x in [1,2] return x") should be(For(
        List("x" -> ConstList(List( ConstNumber(1), ConstNumber(2) ))),
        Ref("x") ))

     parse("for x in [1,2], y in [3,4] return x + y") should be(For(
        List(
            "x" -> ConstList(List( ConstNumber(1), ConstNumber(2) )),
            "y" -> ConstList(List( ConstNumber(3), ConstNumber(4) ))),
        Addition(Ref("x"), Ref("y")) ))
  }

  it should "parse a 'filter' expression" in {

    parse("[1,2][item < 1]") should be(Filter(
        ConstList(List( ConstNumber(1), ConstNumber(2) )),
        LessThan(Ref("item"), ConstNumber(1)) ))
  }

  it should "parse a path expression" in {

    parse("a.b") should be(PathExpression(Ref("a"), "b"))

    parse("a.b.c") should be(PathExpression(
        PathExpression(
            Ref("a"), "b"
            ), "c"))
  }
  
  it should "parse a path expression with escaped name" in {

    parse(" 'a b'.'c d'") should be(PathExpression(Ref("a b"), "c d"))
  }

  private def parse(expression: String): Exp =
    FeelParser.parseExpression(expression) match {
      case Success(exp, _) => exp
      case e: NoSuccess => throw new RuntimeException(s"failed to parse expression '$expression':\n$e")
    }

}
