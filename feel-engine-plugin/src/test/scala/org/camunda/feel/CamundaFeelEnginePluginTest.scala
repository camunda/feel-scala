package org.camunda.feel

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.bpm.engine.ProcessEngineConfiguration
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.camunda.feel.integration.CamundaFeelEngineFactory
import org.camunda.feel.script.FeelUnaryTestsScriptEngineFactory
import org.camunda.feel.script.FeelScriptEngineFactory

class CamundaFeelEnginePluginTest extends FlatSpec with Matchers {
  
  val processEngine = {
    
    val config = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration().asInstanceOf[ProcessEngineConfigurationImpl]
      .setJdbcUrl("jdbc:h2:mem:camunda-test;DB_CLOSE_DELAY=1000")
      .setJobExecutorActivate(false)
      
    config.getProcessEnginePlugins.add(new CamundaFeelEnginePlugin)  
    
    config.buildProcessEngine    
  }
  
  val dmnEngineConfig = processEngine.getProcessEngineConfiguration.asInstanceOf[ProcessEngineConfigurationImpl].getDmnEngineConfiguration
  
  "A Camunda engine with FEEL Scala plugin" should "has FEEL Scala Factory" in {
    
    Option(dmnEngineConfig.getFeelEngineFactory) should not be None
    dmnEngineConfig.getFeelEngineFactory shouldBe a [CamundaFeelEngineFactory]
  }
  
  it should "set default expression languages" in {
    
    dmnEngineConfig.getDefaultInputEntryExpressionLanguage should be (FeelUnaryTestsScriptEngineFactory.ENGINE_NAME)
    dmnEngineConfig.getDefaultOutputEntryExpressionLanguage should be (FeelScriptEngineFactory.ENGINE_NAME)
    dmnEngineConfig.getDefaultLiteralExpressionLanguage should be (FeelScriptEngineFactory.ENGINE_NAME)
    dmnEngineConfig.getDefaultInputExpressionExpressionLanguage should be (FeelScriptEngineFactory.ENGINE_NAME)
  }
  
}