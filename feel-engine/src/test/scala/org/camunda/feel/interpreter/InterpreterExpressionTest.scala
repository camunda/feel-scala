package org.camunda.feel.interpreter

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._
import org.camunda.feel.parser.FeelParser


/**
 * @author Philipp Ossler
 */
class InterpreterExpressionTest extends FlatSpec with Matchers with FeelIntegrationTest {
  
  "An expression" should "be a number" in {
    
    eval("2") should be(ValNumber(2))
  }
  
  it should "be a string" in {
    
    eval(""" "a" """) should be(ValString("a"))
  }
  
  it should "be a boolean" in {
    
    eval("true") should be(ValBoolean(true))
  }
  
  it should "be null" in {
    
    eval("null") should be(ValNull)
  }
  
  it should "be an if-then-else" in {
    
    val exp = """ if (x < 5) then "low" else "high" """
    
    eval(exp, Map("x" -> 2)) should be(ValString("low"))
    eval(exp, Map("x" -> 7)) should be(ValString("high"))
  }
  
  it should "be a disjunction" in {
    
    eval("a or b", Map("a" -> ValBoolean(false), "b" -> ValBoolean(true))) should be(ValBoolean(true))
    eval("a or b", Map("a" -> ValBoolean(false), "b" -> ValBoolean(false))) should be(ValBoolean(false))
    
    eval("false or false or true") should be(ValBoolean(true))
  }
  
  it should "be a conjunction" in {
    
    eval("a and b", Map("a" -> ValBoolean(true), "b" -> ValBoolean(true))) should be(ValBoolean(true))
    eval("a and b", Map("a" -> ValBoolean(true), "b" -> ValBoolean(false))) should be(ValBoolean(false))
    
    eval("true and true and false") should be(ValBoolean(false))
  }
  
  it should "be a simple positive unary test" in {
    
    eval("< 3", Map(Context.defaultInputVariable -> 2)) should be(ValBoolean(true))
    
    eval("(2 .. 4)", Map(Context.defaultInputVariable -> 5)) should be(ValBoolean(false))
  }
  
  it should "be an instance of" in {
    
    eval("x instance of number", Map("x" -> 1)) should be(ValBoolean(true))
    eval("x instance of number", Map("x" -> "NaN")) should be(ValBoolean(false))
    
    eval("x instance of boolean", Map("x" -> true)) should be(ValBoolean(true))
    eval("x instance of boolean", Map("x" -> 0)) should be(ValBoolean(false))
    
    eval("x instance of string", Map("x" -> "yes")) should be(ValBoolean(true))
    eval("x instance of string", Map("x" -> 0)) should be(ValBoolean(false))
  }
  
  it should "be a context" in {
    
    eval("{ a : 1 }") should be(ValContext(List( "a" -> ValNumber(1) )))
    
    eval("""{ a:1, b:"foo" }""") should be(ValContext(List( 
        "a" -> ValNumber(1),
        "b" -> ValString("foo") )))
    
    // nested
    eval("{ a : { b : 1 } }") should be(ValContext(List(
        "a" -> ValContext(List(
            "b" -> ValNumber(1) )))))
  }
  
  it should "be a list" in {
    
    eval("[1]") should be(ValList(List( ValNumber(1) )))
    
    eval("[1,2]") should be(ValList(List( 
        ValNumber(1),
        ValNumber(2) )))
    
    // nested
    eval("[ [1], [2] ]") should be(ValList(List( 
        ValList(List(ValNumber(1))),
        ValList(List(ValNumber(2))) )))
  }
  
  "A number" should "add to '4'" in {
    
    eval("2+4") should be(ValNumber(6))
  }
  
  it should "add to '4' and '6'" in {
    
    eval("2+4+6") should be(ValNumber(12))
  }
  
  it should "subtract from '2'" in {
    
    eval("4-2") should be(ValNumber(2))
  }
  
  it should "add and subtract" in {
    
    eval("2+4-3+1") should be(ValNumber(4))
  }
  
  it should "multiply by '3'" in {
    
    eval("3*3") should be(ValNumber(9))
  }
  
  it should "divide by '4'" in {
    
    eval("8/4") should be(ValNumber(2))
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
  
  it should "compare with 'between _ and _'" in {
    
    eval("x between 2 and 4", Map("x" -> 1)) should be (ValBoolean(false))
    eval("x between 2 and 4", Map("x" -> 2)) should be (ValBoolean(true))
    eval("x between 2 and 4", Map("x" -> 3)) should be (ValBoolean(true))
    eval("x between 2 and 4", Map("x" -> 4)) should be (ValBoolean(true))
    eval("x between 2 and 4", Map("x" -> 5)) should be (ValBoolean(false))
  }
  
  it should "compare with 'in'" in {
    
    eval("x in < 2", Map("x" -> 1)) should be(ValBoolean(true))
    eval("x in < 2", Map("x" -> 2)) should be(ValBoolean(false))
    
    eval("x in (2 .. 4)", Map("x" -> 3)) should be (ValBoolean(true))
    eval("x in (2 .. 4)", Map("x" -> 4)) should be (ValBoolean(false))

    eval("x in (2,4,6)", Map("x" -> 4)) should be(ValBoolean(true))
    eval("x in (2,4,6)", Map("x" -> 5)) should be(ValBoolean(false))
    
    eval("3 in (2 .. 4)") should be(ValBoolean(true))
    eval("4 in (2 .. 4)") should be(ValBoolean(false))
  }
  
  // TODO add test case for add. and sub. of durations
  // TODO add tests cases for compare op's with date, time and duration
  
  "A function definition" should "be interpeted" in {
    
    val function = eval("function(x) x + 1")
    
    function shouldBe a [ValFunction]
    function.asInstanceOf[ValFunction].params should be(List("x")) 
  }
  
  it should "be invoked without parameter" in {

    val variables = Map("f" -> eval("""function() "invoked" """))

    eval("f()", variables) should be(ValString("invoked"))
  }

  it should "be invoked with one positional parameter" in {

    val variables = Map("f" -> eval("function(x) x + 1"))

    eval("f(1)", variables) should be(ValNumber(2))
    eval("f(2)", variables) should be(ValNumber(3))
  }
  
  it should "be invoked with positional parameters" in {

    val variables = Map("add" -> eval("function(x,y) x + y"))

    eval("add(1,2)", variables) should be(ValNumber(3))
    eval("add(2,3)", variables) should be(ValNumber(5))
  }
  
  it should "be invoked with one named parameter" in {

    val variables = Map("f" -> eval("function(x) x + 1"))

    eval("f(x:1)", variables) should be(ValNumber(2))
    eval("f(x:2)", variables) should be(ValNumber(3))
  }
  
  it should "be invoked with named parameters" in {

    val variables = Map("sub" -> eval("function(x,y) x - y"))

    eval("sub(x:4,y:2)", variables) should be(ValNumber(2))
    eval("sub(y:2,x:4)", variables) should be(ValNumber(2))    
  }
  
  it should "fail to invoke with wrong number of parameters" in {
    
    val variables = Map("f" -> eval("function(x,y) true"))

    eval("f()", variables) should be(ValError("expected 2 parameters but found 0"))
    eval("f(1)", variables) should be(ValError("expected 2 parameters but found 1"))
    
    eval("f(x:1)", variables) should be(ValError("expected parameter 'y' but not found"))
    eval("f(y:1)", variables) should be(ValError("expected parameter 'x' but not found"))
    eval("f(x:1,y:2,z:3)", variables) should be(ValError("unexpected parameter 'z'"))
  }
  
  "An external java function definition" should "be invoked with one double parameter" in {

    val variables = Map("cos" -> eval(""" function(angle) external { java: { class: "java.lang.Math", method_signature: "cos(double)" } } """))

    eval("cos(0)", variables) should be(ValNumber(1))
    eval("cos(1)", variables) should be(ValNumber( Math.cos(1) ))
  }
  
  it should "be invoked with two int parameters" in {

    val variables = Map("max" -> eval(""" function(x,y) external { java: { class: "java.lang.Math", method_signature: "max(int, int)" } } """))

    eval("max(1,2)", variables) should be(ValNumber(2))
  }
  
  it should "be invoked with one long parameters" in {

    val variables = Map("abs" -> eval(""" function(a) external { java: { class: "java.lang.Math", method_signature: "abs(long)" } } """))

    eval("abs(-1)", variables) should be(ValNumber(1))
  }
  
  it should "be invoked with one float parameters" in {

    val variables = Map("round" -> eval(""" function(a) external { java: { class: "java.lang.Math", method_signature: "round(float)" } } """))

    eval("round(3.2)", variables) should be(ValNumber(3))
  }
  
  "A list" should "be checked with 'some'" in {
  	
  	eval("some x in [1,2,3] satisfies x > 2") should be(ValBoolean(true))
  	eval("some x in [1,2,3] satisfies x > 3") should be(ValBoolean(false))
  	
  	eval("some x in xs satisfies x > 2", Map("xs" -> List(1,2,3))) should be(ValBoolean(true))
  	eval("some x in xs satisfies x > 2", Map("xs" -> List(1,2))) should be(ValBoolean(false))
  }
  
  it should "be checked with 'every'" in {
  	
  	eval("every x in [1,2,3] satisfies x >= 1") should be(ValBoolean(true))
  	eval("every x in [1,2,3] satisfies x >= 2") should be(ValBoolean(false))
  	
  	eval("every x in xs satisfies x >= 1", Map("xs" -> List(1,2,3))) should be(ValBoolean(true))
  	eval("every x in xs satisfies x >= 1", Map("xs" -> List(0,1,2,3))) should be(ValBoolean(false))
  }
  
  it should "be a processed in a for-expression" in {
    
    eval("for x in [1,2] return x * 2") should be(ValList(List(
        ValNumber(2), 
        ValNumber(4) )))
    
    eval("for x in [1,2], y in [3,4] return x * y") should be(ValList(List(
        ValNumber(3), 
        ValNumber(4),
        ValNumber(6),
        ValNumber(8) )))
    
    eval("for x in xs return x * 2", Map("xs" -> List(1,2))) should be(ValList(List(
        ValNumber(2), 
        ValNumber(4) )))
  }
  
  it should "be filtered" in {
    
    eval("[1,2,3,4][item > 2]") should be(ValList(List(
        ValNumber(3), ValNumber(4))))
    
    eval("xs [item > 2]", Map("xs" -> List(1,2,3,4))) should be(ValList(List(
        ValNumber(3), ValNumber(4))))
  }
    
  "A context" should "be accessed" in {
    
    eval("ctx.a", Map("ctx" -> Map("a" -> 1))) should be(ValNumber(1))
    
    eval("{ a: 1 }.a") should be(ValNumber(1))
  }
  
  it should "be accessed in a nested context" in {
    
    eval("{ a: { b:1 } }.a") should be(ValContext(List(
        "b" -> ValNumber(1)) )) 
    
    eval("{ a: { b:1 } }.a.b") should be(ValNumber(1))  
  }
  
  it should "be accessed in a list" in {
    
    eval("[ {a:1, b:2}, {a:3, b:4} ].a") should be(ValList(List(
        ValNumber(1), ValNumber(3) )))  
  }
  
  it should "be accessed in same context" in {
    
    eval("{ a:1, b:(a+1), c:(b+1)}.c") should be(ValNumber(3))

    eval("{ a: { b: 1 }, c: (1 + a.b) }.c") should be(ValNumber(2))
  }
  
  it should "be filtered in a list" in {
    
    eval("[ {a:1, b:2}, {a:3, b:4} ][a > 2]") should be(ValList(List(
        ValContext(List(
            "a" -> ValNumber(3),
            "b" -> ValNumber(4) )) )))
  }
  
}