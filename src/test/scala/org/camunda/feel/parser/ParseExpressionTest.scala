package org.camunda.feel.parser

import org.camunda.feel._

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import com.sun.org.apache.xpath.internal.objects.LessThanOrEqualComparator

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
  }
  
  it should "parse a boolean" in {

    parse("true") should be(ConstBool(true))
    parse("false") should be(ConstBool(false))
  }

  it should "parse a date" in {

    parse("""date("2015-09-18")""") should be(ConstDate("2015-09-18"))
  }
  
  it should "parse a time" in {
    
    parse("""time("10:31:10")""") should be(ConstTime("10:31:10"))
  }
  
  it should "parse a duration" in {
    
    parse("""duration("P1D")""") should be(ConstDuration("P1D"))
  }
  
  it should "parse null" in {
    
    parse("null") should be(ConstNull)
  }
  
  it should "parse an expression inside brackets" in {
    
    parse("(42)") should be(ConstNumber(42))
  }
  
  it should "ignore an one line comment '// ...'" in {
    
    parse("""duration("P1D") // one day""") should be(ConstDuration("P1D"))
  }
  
  it should "ignore a multi line comment '/* ... */'" in {
    
    parse("""duration("P1DT4H") /* 
                one day and 4 hours
             */ """)
  }
  
  it should "parse an addition" in {
    
    // numeric addition
    parse("2+3") should be(Addition(ConstNumber(2), ConstNumber(3)))
    parse("2+3+4") should be(Addition(ConstNumber(2), 
        Addition( ConstNumber(3), ConstNumber(4) )))
    
    // addition with two durations
    parse("""duration("P1Y")+duration("P6M")""") should be(Addition(ConstDuration("P1Y"), ConstDuration("P6M")))
    parse("""duration("P1D")+duration("PT12H")""") should be(Addition(ConstDuration("P1D"), ConstDuration("PT12H")))
    // addition with duration and date
    parse("""duration("P1M")+date("2015-09-18")""") should be(Addition(ConstDuration("P1M"), ConstDate("2015-09-18")))
    parse("""duration("P7D")+date("2015-09-18")""") should be(Addition(ConstDuration("P7D"), ConstDate("2015-09-18")))
    // addition with duration and time
    parse("""duration("PT2H")+time("10:00:00")""") should be(Addition(ConstDuration("PT2H"), ConstTime("10:00:00")))
  }
  
  it should "parse a substraction" in {
    
    // numeric subtraction
    parse("3-2") should be(Subtraction(ConstNumber(3), ConstNumber(2)))
    // subtraction with two durations
    parse("""duration("P1Y")-duration("P6M")""") should be(Subtraction(ConstDuration("P1Y"), ConstDuration("P6M")))
    parse("""duration("P1D")-duration("PT12H")""") should be(Subtraction(ConstDuration("P1D"), ConstDuration("PT12H")))
    // subtraction with duration and date
    parse("""duration("P1M")-date("2015-09-18")""") should be(Subtraction(ConstDuration("P1M"), ConstDate("2015-09-18")))
    parse("""duration("P7D")-date("2015-09-18")""") should be(Subtraction(ConstDuration("P7D"), ConstDate("2015-09-18")))
    // subtraction with duration and time
    parse("""duration("PT2H")-time("10:00:00")""") should be(Subtraction(ConstDuration("PT2H"), ConstTime("10:00:00")))
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
    // duration
    parse("""a=duration("P1Y")""") should be(Equal(Ref("a"), ConstDuration("P1Y")))
    parse("""a=duration("PT2H")""") should be(Equal(Ref("a"), ConstDuration("PT2H")))
    // date
    parse("""a=date("2015-09-18")""") should be(Equal(Ref("a"), ConstDate("2015-09-18")))
    // time
    parse("""a=time("10:00:00")""") should be(Equal(Ref("a"), ConstTime("10:00:00")))
  }
  
  it should "parse a '!=' comparison" in {
    
    // string
    parse("""a!="b" """) should be(Not(Equal(Ref("a"), ConstString("b"))))
    // boolean
    parse("a!=true") should be(Not(Equal(Ref("a"), ConstBool(true))))
    // numeric
    parse("a!=1") should be(Not(Equal(Ref("a"), ConstNumber(1))))
    // duration
    parse("""a!=duration("P1Y")""") should be(Not(Equal(Ref("a"), ConstDuration("P1Y"))))
    parse("""a!=duration("PT2H")""") should be(Not(Equal(Ref("a"), ConstDuration("PT2H"))))
    // date
    parse("""a!=date("2015-09-18")""") should be(Not(Equal(Ref("a"), ConstDate("2015-09-18"))))
    // time
    parse("""a!=time("10:00:00")""") should be(Not(Equal(Ref("a"), ConstTime("10:00:00"))))
  }
  
  it should "parse a '<' comparison" in {
    
    // numeric
    parse("a<2") should be(LessThan(Ref("a"), ConstNumber(2)))
    // duration
    parse("""a<duration("P1Y")""") should be(LessThan(Ref("a"), ConstDuration("P1Y")))
    parse("""a<duration("PT2H")""") should be(LessThan(Ref("a"), ConstDuration("PT2H")))
    // date
    parse("""a<date("2015-09-18")""") should be(LessThan(Ref("a"), ConstDate("2015-09-18")))
    // time
    parse("""a<time("10:00:00")""") should be(LessThan(Ref("a"), ConstTime("10:00:00")))
  }
  
  it should "parse a '<=' comparison" in {
    
    // numeric
    parse("a<=2") should be(LessOrEqual(Ref("a"), ConstNumber(2)))
    // duration
    parse("""a<=duration("P1Y")""") should be(LessOrEqual(Ref("a"), ConstDuration("P1Y")))
    parse("""a<=duration("PT2H")""") should be(LessOrEqual(Ref("a"), ConstDuration("PT2H")))
    // date
    parse("""a<=date("2015-09-18")""") should be(LessOrEqual(Ref("a"), ConstDate("2015-09-18")))
    // time
    parse("""a<=time("10:00:00")""") should be(LessOrEqual(Ref("a"), ConstTime("10:00:00")))
  }
  
  it should "parse a '>' comparison" in {
    
    // numeric
    parse("a>2") should be(GreaterThan(Ref("a"), ConstNumber(2)))
    // duration
    parse("""a>duration("P1Y")""") should be(GreaterThan(Ref("a"), ConstDuration("P1Y")))
    parse("""a>duration("PT2H")""") should be(GreaterThan(Ref("a"), ConstDuration("PT2H")))
    // date
    parse("""a>date("2015-09-18")""") should be(GreaterThan(Ref("a"), ConstDate("2015-09-18")))
    // time
    parse("""a>time("10:00:00")""") should be(GreaterThan(Ref("a"), ConstTime("10:00:00")))
  }
  
  it should "parse a '>=' comparison" in {
    
    // numeric
    parse("a>=2") should be(GreaterOrEqual(Ref("a"), ConstNumber(2)))
    // duration
    parse("""a>=duration("P1Y")""") should be(GreaterOrEqual(Ref("a"), ConstDuration("P1Y")))
    parse("""a>=duration("PT2H")""") should be(GreaterOrEqual(Ref("a"), ConstDuration("PT2H")))
    // date
    parse("""a>=date("2015-09-18")""") should be(GreaterOrEqual(Ref("a"), ConstDate("2015-09-18")))
    // time
    parse("""a>=time("10:00:00")""") should be(GreaterOrEqual(Ref("a"), ConstTime("10:00:00")))
  }
  
  it should "parse a 'between _ and _' comparison " in {
    
    // numeric
    parse("a between 2 and 4") should be(
        Conjunction(GreaterOrEqual(Ref("a"), ConstNumber(2)), LessOrEqual(Ref("a"), ConstNumber(4))))
  
    // time
    parse(""" a between time("10:00:00") and time("14:00:00") """) should be(
        Conjunction(GreaterOrEqual(Ref("a"), ConstTime("10:00:00")), LessOrEqual(Ref("a"), ConstTime("14:00:00"))))
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
  
  it should "parse a context" in {
    
    parse("{}") should be(ContextEntries(Map()))
    
    parse("{ a : 1 }") should be(ContextEntries(Map( 
        "a" -> ConstNumber(1) )))
    
    parse("{ a:1, b:true }") should be(ContextEntries(Map(
        "a" -> ConstNumber(1),
        "b" -> ConstBool(true) )))
  }
  
  it should "parse a list" in {
    
    parse("[]") should be(ListEntries(List()))
    
    parse("[1]") should be(ListEntries( List(ConstNumber(1)) ))
   
    parse("[1, 2]") should be(ListEntries(List(
        ConstNumber(1), 
        ConstNumber(2) )))
  }
  
  it should "parse an if-then-else condition" in {
    
    parse(""" if (x < 5) then "low" else "high" """) should be(If(
      condition = LessThan(Ref("x"), ConstNumber(5)),
      then = ConstString("low"),
      otherwise = ConstString("high")
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
  
  private def parse(expression: String): Exp = {
    val result = FeelParser.parseExpression(expression)
    result.get
  }
  
}