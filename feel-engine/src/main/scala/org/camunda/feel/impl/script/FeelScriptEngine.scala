package org.camunda.feel.impl.script

import org.camunda.feel.impl._
import org.camunda.feel.impl.spi._
import org.camunda.feel.impl.parser.FeelParser._
import org.camunda.feel.impl.parser.Exp
import org.camunda.feel.impl.FeelEngine.EvalExpressionResult

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import java.io.Reader
import java.io.IOException
import java.io.Closeable

import javax.script._
import org.camunda.feel.impl.FeelEngine

trait FeelScriptEngine
    extends AbstractScriptEngine
    with ScriptEngine
    with Compilable {

  val eval: (String, Map[String, Any]) => EvalExpressionResult

  val parse: String => ParseResult[Exp]

  val factory: ScriptEngineFactory

  lazy val engine: FeelEngine =
    new FeelEngine(functionProvider = SpiServiceLoader.loadFunctionProvider,
                   valueMapper = SpiServiceLoader.loadValueMapper)

  def getFactory: ScriptEngineFactory = factory

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

  private def handleEvaluationResult(result: EvalExpressionResult): Object =
    result match {
      case Right(value)  => value.asInstanceOf[AnyRef]
      case Left(failure) => throw new ScriptException(failure.message)
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
      case i =>
        buffer.append(chars, 0, i)
        read(reader, buffer)
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
