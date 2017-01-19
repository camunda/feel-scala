package org.camunda.feel.script

import java.io.Reader
import javax.script._
import scala.collection.JavaConversions._
import org.camunda.feel._
import scala.util.parsing.input.StreamReader
import java.io.IOException
import java.io.Closeable
import scala.annotation.tailrec
import org.camunda.feel.parser.FeelParser._
import org.camunda.feel.parser.FeelParser
import org.camunda.feel.parser.Exp
import org.camunda.feel.spi.FunctionProvider
import java.util.ServiceLoader
import org.camunda.feel.spi.FunctionProvider.EmptyFunctionProvider
import org.camunda.feel.spi.FunctionProvider.CompositeFunctionProvider

trait FeelScriptEngine extends AbstractScriptEngine with ScriptEngine with Compilable {

  val eval: (String, Map[String, Any]) => EvalResult

  val parse: String => ParseResult[Exp]
  
  val factory: ScriptEngineFactory
  
  lazy val engine = new FeelEngine(functionProvider)
  
  private def functionProvider: FunctionProvider = 
    try {
        val loader = ServiceLoader.load(classOf[FunctionProvider])
        val functionProviders = loader.iterator.toList
        
        functionProviders match {
          case Nil => EmptyFunctionProvider
          case p :: Nil => p
          case ps => new CompositeFunctionProvider(ps)
        }
        
    } catch {
      case t: Throwable => {
        System.err.println("Failed to load function provider")
        t.printStackTrace()
        
        EmptyFunctionProvider
      }
    }
  
  def getFactory(): ScriptEngineFactory = factory
  
  def createBindings(): Bindings = new SimpleBindings

  def eval(reader: Reader, context: ScriptContext): Object = {
    val script = readerAsString(reader)

    eval(script, context)
  }
         
  def eval(script: String, context: ScriptContext): Object = {
  	val engineContext = getEngineContext(context)
  	val result = eval(script, engineContext)
  	
  	handleEvaluationResult(result)
  }
          
  def eval(script: CompiledFeelScript, context: ScriptContext): Object = {
  	val engineContext = getEngineContext(context)
  	val result = engine.eval(script.expression, engineContext)
  	
  	handleEvaluationResult(result)
  }

  def compile(reader: Reader): CompiledScript = {
  	val script = readerAsString(reader)
  	
  	compile(script)
  }
          
  def compile(script: String): CompiledScript = parse(script) match {
          case Success(exp, _) => CompiledFeelScript(this, exp)
          case e: NoSuccess => throw new ScriptException(s"failed to parse expression '$script':\n$e")    
  }
  
  private def handleEvaluationResult(result: EvalResult): Object = result match {
    case EvalValue(value) => value.asInstanceOf[AnyRef]
    case EvalFailure(cause) => throw new ScriptException(cause)
    case ParseFailure(cause) => throw new ScriptException(cause)
  } 

  private def getEngineContext(context: ScriptContext): Map[String, Any] = {
    List(ScriptContext.GLOBAL_SCOPE, ScriptContext.ENGINE_SCOPE)
      .flatMap(scope => Option(context.getBindings(scope)))
      .flatMap(_.toMap)
      .toMap
  }

  private def readerAsString(reader: Reader): String = {
    try {
      read(reader)
    } catch {
      case e: IOException => throw new ScriptException(e)
    } finally {
      closeSilently(reader)
    }
  }
  
  @tailrec
  private def read(reader: Reader, buffer: StringBuffer = new StringBuffer): String = {
    val chars = new Array[Char](16 * 1024)

    reader.read(chars, 0, chars.length) match {
      case -1 => buffer.toString
      case i => {
        buffer.append(chars, 0, i)
        read(reader, buffer)
      }
    }
  }

  private def closeSilently(closable: Closeable) {
    try {
      closable.close()
    } catch {
      case _: IOException => // ignore
    }
  }

}