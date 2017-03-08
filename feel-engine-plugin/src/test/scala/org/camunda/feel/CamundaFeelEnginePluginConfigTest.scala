package org.camunda.feel

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.bpm.engine.ProcessEngineConfiguration
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.camunda.feel.integration.CamundaFeelEngineFactory
import org.camunda.feel.script.FeelUnaryTestsScriptEngineFactory
import org.camunda.feel.script.FeelScriptEngineFactory

class CamundaFeelEnginePluginConfigTest extends FlatSpec with Matchers {
 
  "A Camunda engine with FEEL Scala plugin" should "set the DMN engine configuration" in {
    
    val processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("default-dmn-config.cfg.xml").buildProcessEngine
    
    val dmnEngineConfig = processEngine.getProcessEngineConfiguration.asInstanceOf[ProcessEngineConfigurationImpl].getDmnEngineConfiguration
    
    Option(dmnEngineConfig.getFeelEngineFactory) should not be None
    dmnEngineConfig.getFeelEngineFactory shouldBe a [CamundaFeelEngineFactory]
    
    dmnEngineConfig.getDefaultInputEntryExpressionLanguage should be (FeelUnaryTestsScriptEngineFactory.ENGINE_NAME)
    dmnEngineConfig.getDefaultOutputEntryExpressionLanguage should be (FeelScriptEngineFactory.ENGINE_NAME)
    dmnEngineConfig.getDefaultLiteralExpressionLanguage should be (FeelScriptEngineFactory.ENGINE_NAME)
    dmnEngineConfig.getDefaultInputExpressionExpressionLanguage should be (FeelScriptEngineFactory.ENGINE_NAME)
  }
  
  it should "extend an existing DMN engine configuration" in {
    
    val processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("mixed-dmn-config.cfg.xml").buildProcessEngine
    
    val dmnEngineConfig = processEngine.getProcessEngineConfiguration.asInstanceOf[ProcessEngineConfigurationImpl].getDmnEngineConfiguration
    
    dmnEngineConfig.getCustomPostDecisionEvaluationListeners contains (new CustomDecisionEvaluationListener)
    
    Option(dmnEngineConfig.getFeelEngineFactory) should not be None
    dmnEngineConfig.getFeelEngineFactory shouldBe a [CamundaFeelEngineFactory]
    
    dmnEngineConfig.getDefaultInputEntryExpressionLanguage should be (FeelUnaryTestsScriptEngineFactory.ENGINE_NAME)
    dmnEngineConfig.getDefaultOutputEntryExpressionLanguage should be (FeelScriptEngineFactory.ENGINE_NAME)
    dmnEngineConfig.getDefaultLiteralExpressionLanguage should be (FeelScriptEngineFactory.ENGINE_NAME)
    dmnEngineConfig.getDefaultInputExpressionExpressionLanguage should be (FeelScriptEngineFactory.ENGINE_NAME)    
  }
  
  it should "allow to override the default expression languages" in {
    
    val processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("override-dmn-config.cfg.xml").buildProcessEngine
    
    val dmnEngineConfig = processEngine.getProcessEngineConfiguration.asInstanceOf[ProcessEngineConfigurationImpl].getDmnEngineConfiguration
    
    Option(dmnEngineConfig.getFeelEngineFactory) should not be None
    dmnEngineConfig.getFeelEngineFactory shouldBe a [CamundaFeelEngineFactory]
    
    dmnEngineConfig.getDefaultInputEntryExpressionLanguage should be (FeelUnaryTestsScriptEngineFactory.ENGINE_NAME)
    dmnEngineConfig.getDefaultOutputEntryExpressionLanguage should be (FeelScriptEngineFactory.ENGINE_NAME)
    dmnEngineConfig.getDefaultLiteralExpressionLanguage should be (FeelScriptEngineFactory.ENGINE_NAME)
    dmnEngineConfig.getDefaultInputExpressionExpressionLanguage should be ("groovy")
  }
    
}