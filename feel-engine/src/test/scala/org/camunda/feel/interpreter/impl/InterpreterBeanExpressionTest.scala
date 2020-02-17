package org.camunda.feel.interpreter.impl

import org.scalatest.{FlatSpec, Matchers}
import org.camunda.feel.syntaxtree._

/**
  * @author Philipp Ossler
  */
class InterpreterBeanExpressionTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A bean" should "access a field" in {

    class A(val b: Int)

    eval("a.b", Map("a" -> new A(2))) should be(ValNumber(2))

  }

  it should "access a getter method as field" in {

    class A(b: Int) { def getFoo() = b + 1 }

    eval("a.foo", Map("a" -> new A(2))) should be(ValNumber(3))

  }

  it should "invoke a method without arguments" in {

    class A { def foo() = "foo" }

    eval("a.foo()", Map("a" -> new A())) should be(ValString("foo"))

  }

  it should "invoke a method with one argument" in {

    class A { def incr(x: Int) = x + 1 }

    eval("a.incr(1)", Map("a" -> new A())) should be(ValNumber(2))

  }

  it should "access a nullable field" in {

    class A(val a: String, val b: String)

    eval(""" a.a = null """, Map("a" -> new A("not null", null))) should be(
      ValBoolean(false))
    eval(""" a.b = null """, Map("a" -> new A("not null", null))) should be(
      ValBoolean(true))
    eval(""" null = a.a """, Map("a" -> new A("not null", null))) should be(
      ValBoolean(false))
    eval(""" null = a.b""", Map("a" -> new A("not null", null))) should be(
      ValBoolean(true))
    eval(""" a.a = a.b """, Map("a" -> new A("not null", "not null"))) should be(
      ValBoolean(true))
    eval(""" a.a = a.b """, Map("a" -> new A("not null", null))) should be(
      ValBoolean(false))
    eval(""" a.a = a.b """, Map("a" -> new A(null, "not null"))) should be(
      ValBoolean(false))
    eval(""" a.a = a.b """, Map("a" -> new A(null, null))) should be(
      ValBoolean(true))
  }

}
