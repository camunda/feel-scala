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

import javax.script.{ScriptEngine, ScriptEngineFactory}
import scala.jdk.CollectionConverters.SeqHasAsJava

/** @author
  *   Philipp Ossler
  */
class FeelUnaryTestsScriptEngineFactory extends ScriptEngineFactory {

  import FeelUnaryTestsScriptEngineFactory._

  def getEngineName(): String = ENGINE_NAME

  def getEngineVersion(): String = ENGINE_VERSION

  def getExtensions(): java.util.List[String] = List.empty.asJava

  def getLanguageName(): String = LANGUAGE_SHORT_NAME

  def getLanguageVersion(): String = LANGUAGE_VERSION

  def getMethodCallSyntax(x$1: String, x$2: String, x$3: String*): String =
    throw new UnsupportedOperationException()

  def getMimeTypes(): java.util.List[String] = List.empty.asJava

  def getNames(): java.util.List[String] =
    List(ENGINE_NAME, LANGUAGE_NAME, LANGUAGE_SHORT_NAME).asJava

  def getOutputStatement(x$1: String): String =
    throw new UnsupportedOperationException()

  def getParameter(key: String): Object = key match {
    case ScriptEngine.NAME             => getLanguageName
    case ScriptEngine.ENGINE           => getEngineName
    case ScriptEngine.ENGINE_VERSION   => getEngineVersion
    case ScriptEngine.LANGUAGE         => getLanguageName
    case ScriptEngine.LANGUAGE_VERSION => getLanguageVersion
    case "THREADING"                   => "STATELESS"
  }

  def getProgram(statements: String*): String =
    throw new UnsupportedOperationException()

  def getScriptEngine(): ScriptEngine = new FeelUnaryTestsScriptEngine(this)

}

object FeelUnaryTestsScriptEngineFactory {

  val ENGINE_NAME = "feel-scala-unary-tests"

  val ENGINE_VERSION = "1.0.0"

  val LANGUAGE_NAME = "Friendly Enough Expression Language - Unary Tests"

  val LANGUAGE_SHORT_NAME = "feel-unary-tests"

  val LANGUAGE_VERSION = "1.1"

}
