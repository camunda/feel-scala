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

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

/** @author
  *   Philipp Ossler
  */
class ScriptEngineFactoryTest extends AnyFlatSpec with Matchers {

  val scriptEngineFactory = new FeelScriptEngineFactory

  "The feel script engine factory" should "has engine name 'feel-scala'" in {

    scriptEngineFactory.getEngineName should be("feel-scala")
  }

  it should "has language name 'feel'" in {

    scriptEngineFactory.getLanguageName should be("feel")
  }

  it should "has language version '1.1'" in {

    scriptEngineFactory.getLanguageVersion should be("1.1")
  }

  it should "has extension 'feel'" in {

    scriptEngineFactory.getExtensions should contain("feel")
  }

  it should "get a script engine" in {

    val scriptEngine = scriptEngineFactory.getScriptEngine

    Option(scriptEngine) should not be None
    scriptEngine.getClass should be(classOf[FeelExpressionScriptEngine])
  }

}
