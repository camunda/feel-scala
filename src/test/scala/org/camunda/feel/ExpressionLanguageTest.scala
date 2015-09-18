package org.camunda.feel

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import javax.script.ScriptEngineManager



/**
 * @author Philipp Ossler
 */
class ExpressionLanguageTest extends FlatSpec with Matchers {
  
  val scriptEngineManager = new ScriptEngineManager
    
  "Feel script engine" should "get by name" in {
    
    val engine = scriptEngineManager.getEngineByName("feel")
    
    engine.getClass should be (classOf[FeelScriptEngine])
  }
  
  it should "get by extension" in {
   
    val engine = scriptEngineManager.getEngineByExtension("feel")
    
    engine.getClass should be (classOf[FeelScriptEngine])
  }
  
}