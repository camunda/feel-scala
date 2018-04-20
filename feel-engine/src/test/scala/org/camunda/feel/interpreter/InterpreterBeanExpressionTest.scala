package org.camunda.feel.interpreter

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._

/**
 * @author Philipp Ossler
 */
class InterpreterBeanExpressionTest extends FlatSpec with Matchers with FeelIntegrationTest {
  
  "A bean" should "be handled as context" in {

    class A {
      val b: Int = 0
      val c: Int = 1
      def foo() = "foo"
      def getBar() = "bar"
      def incr(x: Int) = x + 1
    }

    val obj = new A
    val c = eval("a", Map("a" -> obj))

    c should be(ValContext(ObjectContext(obj)))
    c.asInstanceOf[ValContext].context.variable("b") shouldBe a [ValNumber]
    c.asInstanceOf[ValContext].context.variable("foo") shouldBe a [ValString]
    c.asInstanceOf[ValContext].context.variable("bar") shouldBe a [ValString]
    c.asInstanceOf[ValContext].context.function("foo", 0) shouldBe a [ValFunction]
    c.asInstanceOf[ValContext].context.function("getBar", 0) shouldBe a [ValFunction]
    c.asInstanceOf[ValContext].context.function("incr", 1) shouldBe a [ValFunction]
    c.asInstanceOf[ValContext].context.function("incr", Set("x")) shouldBe a [ValFunction]
  }

  it should "access a field" in {

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

     eval(""" a.a = null """, Map("a" -> new A("not null", null))) should be(ValBoolean(false))
     eval(""" a.b = null """, Map("a" -> new A("not null", null))) should be(ValBoolean(true))
     eval(""" null = a.a """, Map("a" -> new A("not null", null))) should be(ValBoolean(false))
     eval(""" null = a.b""", Map("a" -> new A("not null", null))) should be(ValBoolean(true))
     eval(""" a.a = a.b """, Map("a" -> new A("not null", "not null"))) should be(ValBoolean(true))
     eval(""" a.a = a.b """, Map("a" -> new A("not null", null))) should be(ValBoolean(false))
     eval(""" a.a = a.b """, Map("a" -> new A(null, "not null"))) should be(ValBoolean(false))
     eval(""" a.a = a.b """, Map("a" -> new A(null, null))) should be(ValBoolean(true))
   }

}
