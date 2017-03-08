package org.camunda.feel.example

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration
import org.camunda.feel.integration.CamundaFeelEngineFactory
import org.camunda.bpm.model.dmn.Dmn

class FunctionProviderTest extends FlatSpec with Matchers with DmnEvaluationTest {

  val DMN_FILE = "/function/outputEntryWithFunction.dmn"

  "The decision table" should "invoke a custom scala function" in {

    val result = evaluateDecision(DMN_FILE, "decision", Map("status" -> "green"))

    result.size should be(1)
    result.getSingleEntry.asInstanceOf[Int] should be(3)
  }
  
}