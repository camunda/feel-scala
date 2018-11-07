package org.camunda.feel.script

import org.camunda.feel._
import org.camunda.feel.spi._
import org.camunda.feel.interpreter._
import org.camunda.feel.parser.FeelParser._
import org.camunda.feel.parser.Exp

import scala.annotation.tailrec
import scala.reflect._
import scala.collection.JavaConverters._

import java.io.Reader
import java.io.IOException
import java.io.Closeable
import javax.script._
import java.util.ServiceLoader

trait FeelScriptEngine
    extends AbstractScriptEngine
    with ScriptEngine
    with Compilable {

  val eval: (String, Map[String, Any]) => EvalResult

  val parse: String => ParseResult[Exp]

  val factory: ScriptEngineFactory

  lazy val engine: org.camunda.feel.FeelEngine =
    new FeelEngine(functionProvider, valueMapper)

  private def valueMapper: ValueMapper =
    loadServiceProvider[CustomValueMapper]() match {
      case Nil        => DefaultValueMapper.instance
      case l: List[_] => l.head
    }

  private def functionProvider: FunctionProvider =
    loadServiceProvider[CustomFunctionProvider]() match {
      case Nil      => FunctionProvider.EmptyFunctionProvider
      case p :: Nil => p
      case ps       => new FunctionProvider.CompositeFunctionProvider(ps)
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
    case Success(exp, _) =>
      CompiledFeelScript(this, ParsedExpression(exp, script))
    case e: NoSuccess =>
      throw new ScriptException(s"failed to parse expression '$script':\n$e")
  }

  private def handleEvaluationResult(result: EvalResult): Object =
    result match {
      case EvalValue(value)    => value.asInstanceOf[AnyRef]
      case EvalFailure(cause)  => throw new ScriptException(cause)
      case ParseFailure(cause) => throw new ScriptException(cause)
    }

  private def getEngineContext(context: ScriptContext): Map[String, Any] = {
    List(ScriptContext.GLOBAL_SCOPE, ScriptContext.ENGINE_SCOPE)
      .flatMap(scope => Option(context.getBindings(scope)))
      .flatMap(_.asScala)
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
  private def read(reader: Reader,
                   buffer: StringBuffer = new StringBuffer): String = {
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

  private def loadServiceProvider[T: ClassTag](): List[T] =
    try {
      val loader =
        ServiceLoader.load(classTag[T].runtimeClass.asInstanceOf[Class[T]])
      loader.iterator.asScala.toList
    } catch {
      case t: Throwable => {
        System.err.println(
          s"Failed to load service provider: ${classTag[T].runtimeClass.getSimpleName}")
        t.printStackTrace()
        throw (t)
      }
    }

}
