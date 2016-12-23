package org.camunda.feel.script

import org.scalatest.FlatSpec
import org.scalatest.Matchers

/**
 * @author Philipp Ossler
 */
class ScriptEngineFactoryTest extends FlatSpec with Matchers {
  
  val scriptEngineFactory = new FeelScriptEngineFactory
  
  "The feel script engine factory" should "has engine name 'feel-scala'" in {
    
    scriptEngineFactory.getEngineName should be ("feel-scala")
  }  
  
  it should "has language name 'feel'" in {
    
    scriptEngineFactory.getLanguageName should be ("feel")
  }
  
  it should "has language version '1.1'" in {
    
    scriptEngineFactory.getLanguageVersion should be ("1.1")
  }
  
  it should "has extension 'feel'" in {
    
    scriptEngineFactory.getExtensions should contain ("feel")
  }
  
  it should "get a script engine" in {
    
    val scriptEngine = scriptEngineFactory.getScriptEngine
    
    Option(scriptEngine) should not be None
    scriptEngine.getClass should be (classOf[FeelScriptEngine])
  }
  
}