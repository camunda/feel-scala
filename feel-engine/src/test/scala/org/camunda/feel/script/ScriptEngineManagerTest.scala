package org.camunda.feel.script

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import javax.script.ScriptEngineManager
import scala.collection.JavaConversions._

/**
 * @author Philipp Ossler
 */
class ScriptEngineManagerTest extends FlatSpec with Matchers {
  
  val scriptEngineManager = new ScriptEngineManager
    
  "The script engine manager" should "get feel script engine by name" in {
    
    val engine = scriptEngineManager.getEngineByName("feel-scala")
    
    engine.getClass should be (classOf[FeelExpressionScriptEngine])
  }
  
  it should "get feel script engine by short language name" in {
    
    val engine = scriptEngineManager.getEngineByName("feel")
    
    engine.getClass should be (classOf[FeelExpressionScriptEngine])
  }
  
  it should "get feel script engine by qualified name" in {
    
    val engine = scriptEngineManager.getEngineByName("http://www.omg.org/spec/FEEL/20140401")
    
    engine.getClass should be (classOf[FeelExpressionScriptEngine])
  }

  it should "get feel script engine by full name" in {
    
    val engine = scriptEngineManager.getEngineByName("Friendly Enough Expression Language")
    
    engine.getClass should be (classOf[FeelExpressionScriptEngine])
  }
  
  it should "get feel script engine by extension" in {
   
    val engine = scriptEngineManager.getEngineByExtension("feel")
    
    engine.getClass should be (classOf[FeelExpressionScriptEngine])
  }
  
  it should "contains feel script engine factotry" in {
    
    val factories = scriptEngineManager.getEngineFactories
    
    (factories map (f => f.getClass)) should contain allOf (classOf[FeelScriptEngineFactory], classOf[FeelUnaryTestsScriptEngineFactory])
  }
  
  it should "get feel unary tests script engine by name" in {
    
    val engine = scriptEngineManager.getEngineByName("feel-scala-unary-tests")
    
    engine.getClass should be (classOf[FeelUnaryTestsScriptEngine])
  }
  
  it should "get feel unary tests script engine by short language name" in {
    
    val engine = scriptEngineManager.getEngineByName("feel-unary-tests")
    
    engine.getClass should be (classOf[FeelUnaryTestsScriptEngine])
  }
  
}