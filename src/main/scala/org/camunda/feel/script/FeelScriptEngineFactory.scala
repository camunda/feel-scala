package org.camunda.feel.script

import javax.script.ScriptEngineFactory
import javax.script.ScriptEngine
import scala.collection.JavaConversions._

/**
 * @author Philipp Ossler
 */
class FeelScriptEngineFactory extends ScriptEngineFactory {
  
  import FeelScriptEngineFactory._
  
  def getEngineName(): String = ENGINE_NAME

  def getEngineVersion(): String = ENGINE_VERSION

  def getExtensions(): java.util.List[String] = EXTENSIONS

  def getLanguageName(): String = LANGUAGE_NAME

  def getLanguageVersion(): String = LANGUAGE_VERSION

  def getMethodCallSyntax(x$1: String, x$2: String, x$3: String*): String = throw new UnsupportedOperationException()

  def getMimeTypes(): java.util.List[String] = List()

  def getNames(): java.util.List[String] = List(ENGINE_NAME, LANGUAGE_NAME, "scala-feel")

  def getOutputStatement(x$1: String): String = throw new UnsupportedOperationException()

  def getParameter(key: String): Object = key match {
    case ScriptEngine.NAME              => getLanguageName
    case ScriptEngine.ENGINE            => getEngineName
    case ScriptEngine.ENGINE_VERSION    => getEngineVersion
    case ScriptEngine.LANGUAGE          => getLanguageName
    case ScriptEngine.LANGUAGE_VERSION  => getLanguageVersion
  }

  def getProgram(statements: String*): String = throw new UnsupportedOperationException()

  def getScriptEngine(): ScriptEngine = new FeelScriptEngine
  
}

object FeelScriptEngineFactory {
  
  val ENGINE_NAME = "feel"
  
  val ENGINE_VERSION = "1.0.0"
  
  val LANGUAGE_NAME = "Friendly Enough Expression Language"
  
  val LANGUAGE_VERSION = "1.0"
  
  val EXTENSIONS = List("feel")
  
}