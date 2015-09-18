package org.camunda.feel

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import javax.script.ScriptEngineManager

import scala.collection.JavaConversions._


/**
 * @author Philipp Ossler
 */
class ScriptEngineManagerTest extends FlatSpec with Matchers {
  
  val scriptEngineManager = new ScriptEngineManager
    
  "Script engine manager" should "get feel script entgine by name" in {
    
    val engine = scriptEngineManager.getEngineByName("feel")
    
    engine.getClass should be (classOf[FeelScriptEngine])
  }
  
  it should "get feel script engine by extension" in {
   
    val engine = scriptEngineManager.getEngineByExtension("feel")
    
    engine.getClass should be (classOf[FeelScriptEngine])
  }
  
  it should "contains feel script engine factotry" in {
    
    val factories = scriptEngineManager.getEngineFactories
    
    (factories map (f => f.getClass)) should contain (classOf[FeelScriptEngineFactory])
  }
  
}