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
package org.camunda.feel.lsp

import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.{
  MessageActionItem,
  MessageParams,
  PublishDiagnosticsParams,
  ShowMessageRequestParams
}

import java.util.concurrent.{CompletableFuture, LinkedBlockingQueue, TimeUnit}
import scala.collection.mutable.ListBuffer

class RecordingLanguageClient extends LanguageClient {

  private val diagnosticsQueue = new LinkedBlockingQueue[PublishDiagnosticsParams]()

  override def telemetryEvent(any: Any): Unit = ()

  override def publishDiagnostics(diagnostics: PublishDiagnosticsParams): Unit = {
    diagnosticsQueue.offer(diagnostics)
    ()
  }

  override def showMessage(messageParams: MessageParams): Unit = ()

  override def showMessageRequest(
      requestParams: ShowMessageRequestParams
  ): CompletableFuture[MessageActionItem] = CompletableFuture.completedFuture(null)

  override def logMessage(message: MessageParams): Unit = ()

  def awaitDiagnostics(timeoutMillis: Long = 3000): PublishDiagnosticsParams =
    diagnosticsQueue.poll(timeoutMillis, TimeUnit.MILLISECONDS)

  def drainDiagnostics(idleTimeoutMillis: Long = 50): List[PublishDiagnosticsParams] = {
    val collected = ListBuffer.empty[PublishDiagnosticsParams]
    var next      = awaitDiagnostics(idleTimeoutMillis)

    while (next != null) {
      collected += next
      next = awaitDiagnostics(idleTimeoutMillis)
    }

    collected.toList
  }
}

