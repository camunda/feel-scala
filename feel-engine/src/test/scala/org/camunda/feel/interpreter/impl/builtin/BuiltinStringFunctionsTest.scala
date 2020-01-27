package org.camunda.feel.interpreter.impl.builtin

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel.impl._
import org.camunda.feel.interpreter.impl._
import org.camunda.feel.interpreter.impl.FeelIntegrationTest
import org.camunda.feel.interpreter.impl.{
  ValBoolean,
  ValList,
  ValNumber,
  ValString
}

import scala.math.BigDecimal.int2bigDecimal

/**
  * @author Philipp
  */
class BuiltinStringFunctionsTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A substring() function" should "return string with _ characters" in {

    eval(""" substring("foobar",3) """) should be(ValString("obar"))
  }

  it should "return string with _ characters starting at _" in {

    eval(""" substring("foobar",3,3) """) should be(ValString("oba"))
  }

  it should "return string with _ characters starting at negative _" in {

    eval(""" substring("foobar",-2,1) """) should be(ValString("a"))
  }

  "A string length() function" should "return the length of a String" in {

    eval(""" string length("foo") """) should be(ValNumber(3))
  }

  "A upper case() function" should "return uppercased String" in {

    eval(""" upper case("aBc4") """) should be(ValString("ABC4"))
  }

  "A lower case() function" should "return lowercased String" in {

    eval(""" lower case("aBc4") """) should be(ValString("abc4"))
  }

  "A substring before() function" should "return substring before match" in {

    eval(""" substring before("foobar", "bar") """) should be(ValString("foo"))

    eval(""" substring before("foobar", "xyz") """) should be(ValString(""))
  }

  "A substring after() function" should "return substring after match" in {

    eval(""" substring after("foobar", "ob") """) should be(ValString("ar"))

    eval(""" substring after("", "a") """) should be(ValString(""))

    eval(""" substring after("foo", "") """) should be(ValString("foo"))
  }

  "A replace() function" should "replace a String" in {

    eval(""" replace("abcd", "(ab)|(a)", "[1=$1][2=$2]") """) should be(
      ValString("[1=ab][2=]cd"))
  }

  it should "replace a String with regex pattern" in (eval(
    """ replace("0123456789", "(\d{3})(\d{3})(\d{4})", "($1) $2-$3") """) should be(
    ValString("(012) 345-6789")))

  "A contains() function" should "return if contains the match" in {

    eval(""" contains("foobar", "ob") """) should be(ValBoolean(true))

    eval(""" contains("foobar", "of") """) should be(ValBoolean(false))
  }

  "A starts with() function" should "return if starts with match" in {

    eval(""" starts with("foobar", "fo") """) should be(ValBoolean(true))

    eval(""" starts with("foobar", "ba") """) should be(ValBoolean(false))
  }

  "A ends with() function" should "return if ends with match" in {

    eval(""" ends with("foobar", "r") """) should be(ValBoolean(true))

    eval(""" ends with("foobar", "o") """) should be(ValBoolean(false))
  }

  "A matches() function" should "return if String matches a pattern" in {

    eval(""" matches("foobar", "^fo*b") """) should be(ValBoolean(true))

    eval(""" matches("foobar", "^fo*z") """) should be(ValBoolean(false))
  }

  "A split() function" should "return a list of substrings" in {

    eval(""" split("John Doe", "\s") """) should be(
      ValList(List(ValString("John"), ValString("Doe"))))

    eval(""" split("a;b;c;;", ";") """) should be(
      ValList(
        List(ValString("a"),
             ValString("b"),
             ValString("c"),
             ValString(""),
             ValString(""))))
  }

}
