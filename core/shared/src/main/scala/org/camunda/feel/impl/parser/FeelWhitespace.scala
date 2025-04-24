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
package org.camunda.feel.impl.parser

import fastparse.{ParsingRun, Whitespace}
import fastparse.internal.{Msgs, Util}

import scala.annotation.{switch, tailrec}

/** Whitespace syntax for FEEL.
  *
  * <p> This is a copy of [[fastparse.JavaWhitespace]] with adjustments for additional space
  * characters that are listed in the DMN 1.5 standard (chapter 10.3.1.2, page 103, grammar rules
  * 61+62).
  */
object FeelWhitespace {

  /** Checks if the given character is a whitespace according to FEEL.
    *
    * @param char
    *   the character to check
    * @return
    *   true if the given character is a FEEL whitespace
    */
  def isWhitespace(char: Char): Boolean = char match {
    case '\u0009' | '\u0020' | '\u0085' | '\u00A0' | '\u1680' | '\u180E' | '\u2000' | '\u2001' |
        '\u2002' | '\u2003' | '\u2004' | '\u2005' | '\u2006' | '\u2007' | '\u2008' | '\u2009' |
        '\u200A' | '\u200B' | '\u2028' | '\u2029' | '\u202F' | '\u205F' | '\u3000' | '\uFEFF' |
        '\u000A' | '\u000B' | '\u000C' | '\u000D' =>
      true
    case _ => false
  }

  implicit object whitespace extends Whitespace {
    def apply(ctx: ParsingRun[_]) = {
      val input                                                    = ctx.input
      @tailrec def rec(current: Int, state: Int): ParsingRun[Unit] = {
        if (!input.isReachable(current)) {
          if (state == 0 || state == 1) {
            if (ctx.verboseFailures) ctx.reportTerminalMsg(current, Msgs.empty)
            ctx.freshSuccessUnit(current)
          } else if (state == 2) {
            if (ctx.verboseFailures) ctx.reportTerminalMsg(current, Msgs.empty)
            ctx.freshSuccessUnit(current - 1)
          } else {
            ctx.cut = true
            val res = ctx.freshFailure(current)
            if (ctx.verboseFailures) ctx.reportTerminalMsg(current, () => Util.literalize("*/"))
            res
          }
        } else {
          val currentChar = input(current)
          (state: @switch) match {
            case 0 =>
              (currentChar: @switch) match {
                case ' ' | '\t' | '\n' | '\r' | _ if isWhitespace(currentChar) =>
                  rec(current + 1, state)
                case '/'                                                       => rec(current + 1, state = 2)
                case _                                                         =>
                  if (ctx.verboseFailures) ctx.reportTerminalMsg(current, Msgs.empty)
                  ctx.freshSuccessUnit(current)
              }
            case 1 => rec(current + 1, state = if (currentChar == '\n') 0 else state)
            case 2 =>
              (currentChar: @switch) match {
                case '/' => rec(current + 1, state = 1)
                case '*' => rec(current + 1, state = 3)
                case _   =>
                  if (ctx.verboseFailures) ctx.reportTerminalMsg(current, Msgs.empty)
                  ctx.freshSuccessUnit(current - 1)
              }
            case 3 => rec(current + 1, state = if (currentChar == '*') 4 else state)
            case 4 =>
              (currentChar: @switch) match {
                case '/' => rec(current + 1, state = 0)
                case '*' => rec(current + 1, state = 4)
                case _   => rec(current + 1, state = 3)
              }
            //            rec(current + 1, state = if (currentChar == '/') 0 else 3)
          }
        }
      }
      rec(current = ctx.index, state = 0)
    }
  }

}
