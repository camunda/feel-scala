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

import org.camunda.feel.lsp.server.FeelLanguageServerJava
import org.camunda.feel.lsp.server.FeelLspLogging
import org.eclipse.lsp4j.launch.LSPLauncher

object FeelLspLauncher {

  private val logger = FeelLspLogging.logger(getClass.getName)

  def main(args: Array[String]): Unit = {
    val server   = new FeelLanguageServerJava()
    val launcher =
      LSPLauncher.createServerLauncher(server, System.in, System.out)

    server.connect(launcher.getRemoteProxy)
    val listening = launcher.startListening()

    logger.info("FEEL LSP server started and listening on stdio")
    listening.get()
  }
}

