package org.camunda.feel.interpreter

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._

/**
  * @author Philipp Ossler
  */
class InterpreterContextExpressionTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A context" should "be accessed" in {

    eval("ctx.a", Map("ctx" -> Map("a" -> 1))) should be(ValNumber(1))

    eval("{ a: 1 }.a") should be(ValNumber(1))
  }

  it should "be accessed in a nested context" in {

    eval("{ a: { b:1 } }.a") should be(
      ValContext(DefaultContext(Map("b" -> ValNumber(1)))))

    eval("{ a: { b:1 } }.a.b") should be(ValNumber(1))
  }

  it should "be accessed in a list" in {

    eval("[ {a:1, b:2}, {a:3, b:4} ].a") should be(
      ValList(List(ValNumber(1), ValNumber(3))))
  }

  it should "be accessed in same context" in {

    eval("{ a:1, b:(a+1), c:(b+1)}.c") should be(ValNumber(3))

    eval("{ a: { b: 1 }, c: (1 + a.b) }.c") should be(ValNumber(2))
  }

  it should "be filtered in a list" in {

    eval("[ {a:1, b:2}, {a:3, b:4} ][a > 2]") should be(
      ValList(List(ValContext(
        DefaultContext(Map("a" -> ValNumber(3), "b" -> ValNumber(4)))))))
  }

  it should "be accessed and filtered in a list" in {

    eval("[ {a:1, b:2}, {a:3, b:4} ].a[1]") should be(ValNumber(1))
  }

  it should "access a variable in a nested context" in {

    eval("{ a:1, b:{ c:a+2 } }.b.c") should be(ValNumber(3))
  }

  it should "not override variables of nested context" in {

    eval("{ a:1, b:{ a:2, c:a+3 } }.b.c") should be(ValNumber(5))
  }

  it should "fail if one entry fails" in {

    eval(" { a:1, b: {}.x } ") should be(
      ValError("context contains no entry with key 'x'"))
  }

}
