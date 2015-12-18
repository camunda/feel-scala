package org.camunda.feel.parser

import org.camunda.feel._

import org.scalatest.FlatSpec
import org.scalatest.Matchers

/**
 * @author Philipp Ossler
 */
class ParseSimpleExpressionTest extends FlatSpec with Matchers {
 
  "A parser for simple expression" should "parse number" in {
    
    parse("3") should be(ConstNumber(3))
    parse("3.2") should be(ConstNumber(3.2))
    parse(".2") should be(ConstNumber(.2))
  }
  
  it should "parse a string" in {
    
    parse(""" "a" """) should be(ConstString("a"))
  }
  
  it should "parse a qualified name" in {
    
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
  
  private def parse(expression: String): Exp = {
    val result = FeelParser.parseExpression(expression)
    result.get
  }
  
}