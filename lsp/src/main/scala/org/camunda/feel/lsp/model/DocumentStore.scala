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
package org.camunda.feel.lsp.model

import org.camunda.feel.lsp.analysis.{AnalysisResult, FeelAnalyzer}
import org.eclipse.lsp4j.TextDocumentContentChangeEvent

import scala.collection.concurrent.TrieMap
import scala.jdk.CollectionConverters.CollectionHasAsScala

class DocumentStore {

  private val documents = TrieMap.empty[String, DocumentState]

  def put(uri: String, version: Int, text: String, analyzer: FeelAnalyzer): DocumentState = {
    val state = DocumentState(uri, version, text, analyzer.analyzeFast(text))
    documents.put(uri, state)
    state
  }

  def update(
      uri: String,
      version: Int,
      changes: java.util.List[TextDocumentContentChangeEvent],
      analyzer: FeelAnalyzer
  ): Option[DocumentState] = {
    documents.get(uri) match {
      case Some(current) if version < current.version =>
        None

      case Some(current) =>
        val updatedText = applyChanges(current.text, changes)
        Some(put(uri, version, updatedText, analyzer))

      case None =>
        val text = applyChanges("", changes)
        Some(put(uri, version, text, analyzer))
    }
  }

  def get(uri: String): Option[DocumentState] = documents.get(uri)

  def withInterpreterDiagnostics(
      uri: String,
      version: Int,
      diagnostics: List[org.eclipse.lsp4j.Diagnostic]
  ): Option[DocumentState] = {
    documents.get(uri) match {
      case Some(current) if current.version == version =>
        val mergedState = current.copy(
          analysis = current.analysis.copy(
            diagnostics = current.analysis.diagnostics ++ diagnostics
          )
        )
        documents.put(uri, mergedState)
        Some(mergedState)

      case _ =>
        None
    }
  }

  def remove(uri: String): Unit = {
    documents.remove(uri)
    ()
  }

  private def applyChanges(
      initialText: String,
      changes: java.util.List[TextDocumentContentChangeEvent]
  ): String = {
    changes.asScala.foldLeft(initialText) { (text, change) =>
      if (change.getRange == null) {
        change.getText
      } else {
        val start = FeelAnalyzer.positionToOffset(text, change.getRange.getStart)
        val end   = FeelAnalyzer.positionToOffset(text, change.getRange.getEnd)

        text.substring(0, start) + change.getText + text.substring(end)
      }
    }
  }
}

case class DocumentState(
    uri: String,
    version: Int,
    text: String,
    analysis: AnalysisResult
)

