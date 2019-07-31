package org.camunda.feel.example

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class FunctionProviderTest
    extends FlatSpec
    with Matchers
    with DmnEvaluationTest {

  val DMN_FILE = "/function/outputEntryWithFunction.dmn"

  "The decision table" should "invoke a custom scala function" in {

    val result =
      evaluateDecision(DMN_FILE, "decision", Map("status" -> "green"))

    result.size should be(1)
    result.getSingleEntry.asInstanceOf[Int] should be(3)
  }

  it should "invoke a custom java function" in {

    val result =
      evaluateDecision(DMN_FILE, "decision", Map("status" -> "yellow"))

    result.size should be(1)
    result.getSingleEntry.asInstanceOf[Int] should be(2)
  }

  it should "invoke a custom scala function with input variable" in {

    val result =
      evaluateDecision(DMN_FILE, "decision", Map("status" -> "black"))

    result.size should be(1)
    result.getSingleEntry.asInstanceOf[Int] should be(14)
  }

}
