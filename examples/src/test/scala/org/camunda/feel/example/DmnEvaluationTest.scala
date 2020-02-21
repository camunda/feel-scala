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
package org.camunda.feel.example

import scala.collection.JavaConverters._
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration
import org.camunda.bpm.dmn.feel.impl.scala.CamundaFeelEngineFactory
import org.camunda.bpm.model.dmn.Dmn

trait DmnEvaluationTest {

  val dmnEngine = {
    val dmnEngineConfig = DmnEngineConfiguration
      .createDefaultDmnEngineConfiguration()
      .asInstanceOf[DefaultDmnEngineConfiguration]

    dmnEngineConfig.setDefaultInputEntryExpressionLanguage(
      "feel-scala-unary-tests");
    dmnEngineConfig.setDefaultOutputEntryExpressionLanguage("feel-scala");
    dmnEngineConfig.setDefaultLiteralExpressionLanguage("feel-scala");
    dmnEngineConfig.setDefaultInputExpressionExpressionLanguage("feel-scala");

    dmnEngineConfig
      .feelEngineFactory(new CamundaFeelEngineFactory)
      .buildEngine()
  }

  def decisionInstance(dmnFile: String) = {
    val inputStream = getClass.getResourceAsStream(dmnFile)

    Dmn.readModelFromStream(inputStream)
  }

  def evaluateDecision(dmnFile: String,
                       decisionId: String,
                       vars: Map[String, Any]) =
    dmnEngine.evaluateDecision(decisionId,
                               decisionInstance(dmnFile),
                               toVariables(vars))

  private def toVariables(vars: Map[String, Any]) =
    vars.asJava.asInstanceOf[java.util.Map[String, Object]]

}
