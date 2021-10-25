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
import javax.script.ScriptEngineManager
import scala.collection.JavaConverters._

/**
  * @author Philipp Ossler
  */
class ScriptEngineManagerTest extends AnyFlatSpec with Matchers {

  val scriptEngineManager = new ScriptEngineManager

  "The script engine manager" should "get feel script engine by name" in {

    val engine = scriptEngineManager.getEngineByName("feel-scala")

    engine.getClass should be(classOf[FeelExpressionScriptEngine])
  }

  it should "get feel script engine by short language name" in {

    val engine = scriptEngineManager.getEngineByName("feel")

    engine.getClass should be(classOf[FeelExpressionScriptEngine])
  }

  it should "get feel script engine by qualified name" in {

    val engine = scriptEngineManager.getEngineByName(
      "http://www.omg.org/spec/FEEL/20140401")

    engine.getClass should be(classOf[FeelExpressionScriptEngine])
  }

  it should "get feel script engine by full name" in {

    val engine =
      scriptEngineManager.getEngineByName("Friendly Enough Expression Language")

    engine.getClass should be(classOf[FeelExpressionScriptEngine])
  }

  it should "get feel script engine by extension" in {

    val engine = scriptEngineManager.getEngineByExtension("feel")

    engine.getClass should be(classOf[FeelExpressionScriptEngine])
  }

  it should "contains feel script engine factotry" in {

    val factories = scriptEngineManager.getEngineFactories

    (factories.asScala.map(f => f.getClass)) should contain allOf (classOf[
      FeelScriptEngineFactory], classOf[FeelUnaryTestsScriptEngineFactory])
  }

  it should "get feel unary tests script engine by name" in {

    val engine = scriptEngineManager.getEngineByName("feel-scala-unary-tests")

    engine.getClass should be(classOf[FeelUnaryTestsScriptEngine])
  }

  it should "get feel unary tests script engine by short language name" in {

    val engine = scriptEngineManager.getEngineByName("feel-unary-tests")

    engine.getClass should be(classOf[FeelUnaryTestsScriptEngine])
  }

}
