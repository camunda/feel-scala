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
package org.camunda.feel.lsp.server

import java.util.logging.{Level, Logger}
import java.util.logging.{ConsoleHandler, SimpleFormatter}

object FeelLspLogging {

  private val defaultLevel: Level = Level.INFO

  def logger(name: String): Logger = {
    val level = resolveLevel()
    val l     = Logger.getLogger(name)
    l.setUseParentHandlers(false)
    l.setLevel(level)

    if (l.getHandlers.isEmpty) {
      val handler = new ConsoleHandler
      handler.setLevel(level)
      handler.setFormatter(new SimpleFormatter())
      l.addHandler(handler)
    }

    l.getHandlers.foreach(_.setLevel(level))
    l
  }

  private def resolveLevel(): Level =
    Option(System.getProperty("feel.lsp.log.level"))
      .flatMap(parseLevel)
      .getOrElse(defaultLevel)

  private def parseLevel(value: String): Option[Level] = {
    val normalized = value.trim.toUpperCase
    normalized match {
      case "SEVERE"  => Some(Level.SEVERE)
      case "WARNING" => Some(Level.WARNING)
      case "INFO"    => Some(Level.INFO)
      case "CONFIG"  => Some(Level.CONFIG)
      case "FINE"    => Some(Level.FINE)
      case "FINER"   => Some(Level.FINER)
      case "TRACE"   => Some(Level.FINEST)
      case "FINEST"  => Some(Level.FINEST)
      case _         => None
    }
  }
}
