package org.camunda.feel.example

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration
import org.camunda.feel.integration.CamundaFeelEngineFactory
import org.camunda.bpm.model.dmn.Dmn

class BasicFeelEngineFactoryTest extends FlatSpec with Matchers with DmnEvaluationTest {

  val DMN_DT_SIMPLE_UNARY_TEST = "/basic/simpleUnaryTest.dmn"
  val DMN_DT_OUTPUT_EXPRESSION = "/basic/outputExpression.dmn"
  val DMN_LITERAL_EXPRESSION = "/basic/literalExpression.dmn"

  "The DMN engine" should "evaluate a decision table with a simple unary test" in {

    val result = evaluateDecision(DMN_DT_SIMPLE_UNARY_TEST, "decision", Map("score" -> 92))

    result.size should be(1)
    result.getSingleResult.getSingleEntry.asInstanceOf[String] should be("cool")
  }
  
  it should "evaluate a decision table with an output expression" in {
    
    val result = evaluateDecision(DMN_DT_OUTPUT_EXPRESSION, "decision", Map(
      "op" -> "add",  
      "a" -> 2,
      "b" -> 3
    ))
    
    result.size should be(1)
    result.getSingleResult.getSingleEntry.asInstanceOf[Int] should be(5)
  }
  
  it should "evaluate a decision literal expression" in {
    
    val result = evaluateDecision(DMN_LITERAL_EXPRESSION, "decision", Map(
      "a" -> 4,
      "b" -> 5
    ))
    
    result.size should be(1)
    result.getSingleResult.getSingleEntry.asInstanceOf[Int] should be(9)
  }

}