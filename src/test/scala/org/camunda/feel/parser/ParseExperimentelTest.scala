package org.camunda.feel.parser

import org.scalatest.FlatSpec
import org.scalatest.Matchers

/**
 * @author Philipp Ossler
 */
class ParseExperimentelsTest extends FlatSpec with Matchers {

  "A parser" should "parse a function invocation" in {

    parse("fib(5)") should be(FunctionInvocation("fib",
      params = List(ConstNumber(5))))

    parse("""concat("in", x)""") should be(FunctionInvocation("concat",
      params = List(
        ConstString("in"),
        Ref("x"))))
  }

  private def parse(expression: String): Exp = {
    val result = FeelParser.parseExpression(expression)
    result.get
  }

}