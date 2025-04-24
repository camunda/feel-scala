/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.feel.impl.script

import fastparse.Parsed

import java.io.{Closeable, IOException, Reader}
import org.camunda.feel.FeelEngine
import org.camunda.feel.impl.SpiServiceLoader
import org.camunda.feel.syntaxtree.{Exp, ParsedExpression}

import javax.script.{AbstractScriptEngine, Bindings, Compilable, CompiledScript, ScriptContext, ScriptEngine, ScriptEngineFactory, ScriptException, SimpleBindings}
import scala.annotation.tailrec

trait FeelScriptEngine extends AbstractScriptEngine with AbstractFeelScriptEngine with ScriptEngine with Compilable {

  lazy val engine: FeelEngine =
    new FeelEngine(
      functionProvider = SpiServiceLoader.loadFunctionProvider,
      valueMapper = SpiServiceLoader.loadValueMapper,
      clock = SpiServiceLoader.loadClock
    )

  def getFactory: ScriptEngineFactory = factory

  def createBindings(): Bindings = new SimpleBindings


  def eval(reader: Reader, context: ScriptContext): Object = {
    val script = readerAsString(reader)

    eval(script, context)
  }

  def eval(script: CompiledFeelScript, context: ScriptContext): Object = {
    val engineContext = getEngineContext(context)
    val result        = engine.eval(script.expression, engineContext)

    handleEvaluationResult(result)
  }

  def compile(reader: Reader): CompiledScript = {
    val script = readerAsString(reader)

    compile(script)
  }

  def compile(script: String): CompiledScript = parse(script) match {
    case Parsed.Success(exp, _)      =>
      CompiledFeelScript(this, ParsedExpression(exp, script))
    case Parsed.Failure(_, _, extra) =>
      throw new ScriptException(
        s"failed to parse expression '$script':\n${extra.trace().aggregateMsg}"
      )
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
      case i  =>
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
