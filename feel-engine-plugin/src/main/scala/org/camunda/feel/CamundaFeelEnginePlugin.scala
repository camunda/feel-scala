package org.camunda.feel

import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.camunda.feel.integration.CamundaFeelEngineFactory
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration
import org.camunda.feel.script.FeelScriptEngineFactory
import org.camunda.feel.script.FeelUnaryTestsScriptEngineFactory
import scala.beans.BeanProperty
import org.camunda.feel.integration.transformer.FeelDataTypeTransformerRegistry

class CamundaFeelEnginePlugin extends AbstractProcessEnginePlugin {
  
  @BeanProperty var defaultInputEntryExpressionLanguage = FeelUnaryTestsScriptEngineFactory.ENGINE_NAME
  @BeanProperty var defaultOutputEntryExpressionLanguage = FeelScriptEngineFactory.ENGINE_NAME
  @BeanProperty var defaultLiteralExpressionLanguage = FeelScriptEngineFactory.ENGINE_NAME
  @BeanProperty var defaultInputExpressionExpressionLanguage = FeelScriptEngineFactory.ENGINE_NAME
  
  override def preInit(config: ProcessEngineConfigurationImpl) {
    
    val dmnEngineConfig = Option(config.getDmnEngineConfiguration) getOrElse defaultDmnEngineConfig 
    
    dmnEngineConfig.setDefaultInputEntryExpressionLanguage(defaultInputEntryExpressionLanguage)
    dmnEngineConfig.setDefaultOutputEntryExpressionLanguage(defaultOutputEntryExpressionLanguage)
    dmnEngineConfig.setDefaultLiteralExpressionLanguage(defaultLiteralExpressionLanguage)
    dmnEngineConfig.setDefaultInputExpressionExpressionLanguage(defaultInputExpressionExpressionLanguage)
    
    dmnEngineConfig.feelEngineFactory(new CamundaFeelEngineFactory)
    
    val dataTypeRegistry =  dmnEngineConfig.getTransformer.getDataTypeTransformerRegistry
    FeelDataTypeTransformerRegistry.registerBy(dataTypeRegistry)
    
    config.setDmnEngineConfiguration(dmnEngineConfig)    
  }
  
  private def defaultDmnEngineConfig = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().asInstanceOf[DefaultDmnEngineConfiguration]  
  
}