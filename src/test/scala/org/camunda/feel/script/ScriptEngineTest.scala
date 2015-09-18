package org.camunda.feel.script

import org.scalatest.FlatSpec
import org.scalatest.Matchers

/**
 * @author Philipp Ossler
 */
class ScriptEngineTest extends FlatSpec with Matchers {
  
  val scriptEngine = new FeelScriptEngine
  
  "The feel script engine" should "get the script engine factory" in {
    
    Option(scriptEngine.getFactory) should not be None
    scriptEngine.getClass should be (classOf[FeelScriptEngine])
  }
  
}