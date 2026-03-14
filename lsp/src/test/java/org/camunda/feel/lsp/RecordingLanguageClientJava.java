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
package org.camunda.feel.lsp;

import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.services.LanguageClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

public class RecordingLanguageClientJava implements LanguageClient {

  private final LinkedBlockingQueue<PublishDiagnosticsParams> diagnosticsQueue =
      new LinkedBlockingQueue<>();

  @Override
  public void telemetryEvent(Object any) {
    // no-op
  }

  @Override
  public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
    diagnosticsQueue.offer(diagnostics);
  }

  @Override
  public void showMessage(MessageParams messageParams) {
    // no-op
  }

  @Override
  public CompletableFuture<MessageActionItem> showMessageRequest(
      ShowMessageRequestParams requestParams) {
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public void logMessage(MessageParams message) {
    // no-op
  }

  public PublishDiagnosticsParams awaitDiagnostics(long timeoutMillis) throws InterruptedException {
    return diagnosticsQueue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
  }

  public List<PublishDiagnosticsParams> drainDiagnostics(long idleTimeoutMillis)
      throws InterruptedException {
    final var collected = new ArrayList<PublishDiagnosticsParams>();
    PublishDiagnosticsParams next = awaitDiagnostics(idleTimeoutMillis);

    while (next != null) {
      collected.add(next);
      next = awaitDiagnostics(idleTimeoutMillis);
    }

    return collected;
  }
}

