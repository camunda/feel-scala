package org.camunda.feel.example

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration
import org.camunda.feel.integration.CamundaFeelEngineFactory
import org.camunda.bpm.model.dmn.Dmn

trait DmnEvaluationTest {
  
  // use alternative name because of a bug in the DMN engine regarding FEEL expressions
  val feelEngineName = "feel-scala"
  
  val dmnEngine = {
    val feelFactory = new CamundaFeelEngineFactory
    val dmnEngineConfig = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().asInstanceOf[DefaultDmnEngineConfiguration]     
    
    dmnEngineConfig.setDefaultInputEntryExpressionLanguage("feel");
    dmnEngineConfig.setDefaultOutputEntryExpressionLanguage(feelEngineName);
    dmnEngineConfig.setDefaultLiteralExpressionLanguage(feelEngineName);
    // dmnEngineConfig.setDefaultInputExpressionExpressionLanguage(feelEngineName);
    
    dmnEngineConfig.feelEngineFactory(feelFactory).buildEngine()
  }
  
  def decisionInstance(dmnFile: String) = {
    val inputStream = getClass.getResourceAsStream(dmnFile)
    
    Dmn.readModelFromStream(inputStream)
  }
  
  def evaluateDecision(dmnFile: String, decisionId: String, vars: Map[String, Any]) = 
    dmnEngine.evaluateDecision(decisionId, decisionInstance(dmnFile), toVariables(vars))
  
  private def toVariables(vars: Map[String, Any]) = vars.asJava.asInstanceOf[java.util.Map[String,Object]]
  
}