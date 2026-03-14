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
package org.camunda.feel.lsp.analysis

import fastparse.Parsed
import org.camunda.feel.FeelEngine
import org.camunda.feel.api.{EvaluationFailure, FeelEngineApi}
import org.camunda.feel.impl.interpreter.BuiltinFunctions
import org.camunda.feel.impl.parser.FeelParser
import org.camunda.feel.syntaxtree.ValFunction
import org.camunda.feel.valuemapper.ValueMapper
import org.eclipse.lsp4j.{
  CompletionItem,
  CompletionItemKind,
  Diagnostic,
  DiagnosticSeverity,
  Hover,
  MarkupContent,
  MarkupKind,
  Position,
  Range,
  TextDocumentPositionParams
}

import java.util
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.{MapHasAsJava, SeqHasAsJava}

class FeelAnalyzer(engineApi: FeelEngineApi = FeelAnalyzer.defaultEngineApi) {

  val feelLanguageVersion: String = FeelAnalyzer.FeelLanguageVersion

  def analyze(text: String): AnalysisResult = {
    val parseResult = FeelParser.parseExpression(text)
    parseResult match {
      case Parsed.Success(_, _) =>
        val variableNames = engineApi
          .parseExpression(text)
          .variableReferences
          .map(_.variableName)
          .filterNot(_ == "<empty>")
          .toSet
        AnalysisResult(
          diagnostics = interpreterDiagnostics(text),
          variableNames = variableNames
        )

      case failure: Parsed.Failure =>
        AnalysisResult(
          diagnostics = List(parserDiagnostic(text, failure)),
          variableNames = Set.empty
        )
    }
  }

  def completionItems(text: String, analysis: AnalysisResult): util.List[CompletionItem] = {
    val keywordItems  = FeelAnalyzer.Keywords.map(keywordCompletion)
    val builtinItems  = FeelAnalyzer.builtinSignatures.toList
      .sortBy(_._1)
      .map { case (name, signatures) => builtinCompletion(name, signatures) }
    val variableItems = analysis.variableNames.toList.sorted.map(variableCompletion)

    (keywordItems ++ builtinItems ++ variableItems).toList.asJava
  }

  def hover(text: String, params: TextDocumentPositionParams): Hover = {
    val token = FeelAnalyzer.tokenAt(text, params.getPosition)
    token match {
      case Some(name) if FeelAnalyzer.builtinSignatures.contains(name) =>
        val signatures = FeelAnalyzer.builtinSignatures(name).sorted
        markdownHover(s"### `$name`\n\n" + signatures.map(s => s"- `$s`").mkString("\n"))

      case Some(keyword) if FeelAnalyzer.KeywordDescriptions.contains(keyword) =>
        markdownHover(s"`$keyword` - ${FeelAnalyzer.KeywordDescriptions(keyword)}")

      case _ => null
    }
  }

  def semanticTokenData(text: String, analysis: AnalysisResult): util.List[Integer] = {
    val variableNames = analysis.variableNames
    val tokens        = ListBuffer.empty[SemanticToken]

    var line   = 0
    var column = 0
    var index  = 0

    while (index < text.length) {
      text.charAt(index) match {
        case '\n' =>
          line += 1
          column = 0
          index += 1

        case '"' =>
          val startLine   = line
          val startColumn = column
          index += 1
          column += 1

          var escaped = false
          var closed  = false
          while (index < text.length && !closed) {
            val ch = text.charAt(index)
            if (ch == '\n') {
              line += 1
              column = 0
            } else {
              column += 1
            }

            if (escaped) {
              escaped = false
            } else if (ch == '\\') {
              escaped = true
            } else if (ch == '"') {
              closed = true
            }

            index += 1
          }

          if (startLine == line && column > startColumn) {
            tokens += SemanticToken(startLine, startColumn, column - startColumn, "string")
          }

        case ch if ch.isDigit =>
          val startColumn = column
          index += 1
          column += 1

          while (index < text.length && FeelAnalyzer.isNumberChar(text.charAt(index))) {
            index += 1
            column += 1
          }

          tokens += SemanticToken(line, startColumn, column - startColumn, "number")

        case ch if FeelAnalyzer.isIdentifierStart(ch) =>
          val start       = index
          val startColumn = column

          index += 1
          column += 1

          while (index < text.length && FeelAnalyzer.isIdentifierPart(text.charAt(index))) {
            index += 1
            column += 1
          }

          val identifier = text.substring(start, index)
          val tokenType  =
            if (FeelAnalyzer.KeywordsSet.contains(identifier)) {
              Some("keyword")
            } else if (
              FeelAnalyzer.builtinSignatures.contains(identifier) && FeelAnalyzer
                .isFunctionInvocationAt(text, index)
            ) {
              Some("function")
            } else if (variableNames.contains(identifier)) {
              Some("variable")
            } else {
              None
            }

          tokenType.foreach(t =>
            tokens += SemanticToken(line, startColumn, column - startColumn, t)
          )

        case _ =>
          index += 1
          column += 1
      }
    }

    FeelAnalyzer.encodeSemanticTokens(tokens.toList)
  }

  private def interpreterDiagnostics(text: String): List[Diagnostic] = {
    val evaluationResult = engineApi.evaluateExpression(text)

    evaluationResult.suppressedFailures.map(failure => warningDiagnostic(text, failure))
  }

  private def parserDiagnostic(text: String, failure: Parsed.Failure): Diagnostic = {
    val message =
      s"Parse error: ${failure.trace().aggregateMsg}"
    val range   = FeelAnalyzer.rangeAtOffsets(text, failure.index, failure.index + 1)

    val diagnostic = new Diagnostic(range, message)
    diagnostic.setSeverity(DiagnosticSeverity.Error)
    diagnostic.setSource("feel-parser")
    diagnostic
  }

  private def warningDiagnostic(text: String, failure: EvaluationFailure): Diagnostic = {
    val diagnostic = new Diagnostic(
      FeelAnalyzer.fullRange(text),
      s"${failure.failureType}: ${failure.failureMessage}"
    )
    diagnostic.setSeverity(DiagnosticSeverity.Warning)
    diagnostic.setSource("feel-interpreter")
    diagnostic
  }

  private def keywordCompletion(keyword: String): CompletionItem = {
    val item = new CompletionItem(keyword)
    item.setKind(CompletionItemKind.Keyword)
    item.setSortText(s"1-$keyword")
    item
  }

  private def builtinCompletion(name: String, signatures: List[String]): CompletionItem = {
    val item = new CompletionItem(name)
    item.setKind(CompletionItemKind.Function)
    item.setDetail(signatures.headOption.getOrElse(name))
    item.setDocumentation(signatures.mkString("\n"))
    item.setSortText(s"2-$name")
    item
  }

  private def variableCompletion(name: String): CompletionItem = {
    val item = new CompletionItem(name)
    item.setKind(CompletionItemKind.Variable)
    item.setSortText(s"3-$name")
    item
  }

  private def markdownHover(markdown: String): Hover = {
    val markup = new MarkupContent
    markup.setKind(MarkupKind.MARKDOWN)
    markup.setValue(markdown)

    new Hover(markup)
  }
}

case class AnalysisResult(
    diagnostics: List[Diagnostic],
    variableNames: Set[String]
)

object FeelAnalyzer {

  val FeelLanguageVersion: String = "1.3"

  private val defaultEngineApi: FeelEngineApi =
    new FeelEngineApi(new FeelEngine())

  val Keywords: List[String] = List(
    "for",
    "return",
    "if",
    "then",
    "else",
    "some",
    "every",
    "satisfies",
    "in",
    "instance",
    "of",
    "function",
    "and",
    "or",
    "not",
    "between",
    "true",
    "false",
    "null"
  )

  val KeywordsSet: Set[String] = Keywords.toSet

  val SemanticTokenTypes: List[String] = List(
    "keyword",
    "function",
    "variable",
    "string",
    "number"
  )

  private val SemanticTokenTypeIndex: Map[String, Int] =
    SemanticTokenTypes.zipWithIndex.toMap

  val KeywordDescriptions: Map[String, String] = Map(
    "if"       -> "Conditional expression: if <condition> then <value> else <value>",
    "for"      -> "Iteration expression over a list or range",
    "some"     -> "Quantified expression that succeeds if at least one item matches",
    "every"    -> "Quantified expression that succeeds if all items match",
    "function" -> "Anonymous function declaration"
  )

  val builtinSignatures: Map[String, List[String]] = {
    val builtins =
      new BuiltinFunctions(FeelEngine.defaultClock, ValueMapper.defaultValueMapper).functions

    builtins.map { case (name, overloads) =>
      name -> overloads.map(signature(name, _)).distinct
    }
  }

  def fullRange(text: String): Range = {
    val start = new Position(0, 0)
    val end   = offsetToPosition(text, text.length)
    new Range(start, end)
  }

  def rangeAtOffsets(text: String, startOffset: Int, endOffset: Int): Range = {
    val safeStart = startOffset.max(0).min(text.length)
    val safeEnd   = endOffset.max(safeStart).min(text.length)

    val start = offsetToPosition(text, safeStart)
    val end   = offsetToPosition(text, safeEnd)

    new Range(start, end)
  }

  def tokenAt(text: String, position: Position): Option[String] = {
    val offset = positionToOffset(text, position)
    if (offset < 0 || offset > text.length) {
      None
    } else {
      val start = text.lastIndexWhere(ch => !(ch.isLetterOrDigit || ch == '_'), offset - 1) + 1
      val end   = text.indexWhere(ch => !(ch.isLetterOrDigit || ch == '_'), offset) match {
        case -1 => text.length
        case i  => i
      }

      val token = text.substring(start, end)
      if (token.nonEmpty) Some(token) else None
    }
  }

  def positionToOffset(text: String, position: Position): Int = {
    val targetLine = position.getLine.max(0)
    val targetChar = position.getCharacter.max(0)

    var line   = 0
    var column = 0
    var index  = 0

    while (index < text.length && line < targetLine) {
      if (text.charAt(index) == '\n') {
        line += 1
      }
      index += 1
    }

    while (index < text.length && column < targetChar && text.charAt(index) != '\n') {
      index += 1
      column += 1
    }

    index
  }

  def offsetToPosition(text: String, offset: Int): Position = {
    val safeOffset = offset.max(0).min(text.length)

    var line   = 0
    var column = 0
    var index  = 0

    while (index < safeOffset) {
      if (text.charAt(index) == '\n') {
        line += 1
        column = 0
      } else {
        column += 1
      }
      index += 1
    }

    new Position(line, column)
  }

  private def signature(name: String, function: ValFunction): String = {
    val args = if (function.hasVarArgs && function.params.nonEmpty) {
      function.params.dropRight(1) :+ s"${function.params.last}..."
    } else {
      function.params
    }

    s"$name(${args.mkString(", ")})"
  }

  private def encodeSemanticTokens(tokens: List[SemanticToken]): util.List[Integer] = {
    val sorted = tokens.sortBy(token => (token.line, token.startChar))
    val data   = ListBuffer.empty[Integer]

    var previousLine      = 0
    var previousStartChar = 0

    sorted.foreach { token =>
      val lineDelta  = token.line - previousLine
      val startDelta = if (lineDelta == 0) token.startChar - previousStartChar else token.startChar

      data += lineDelta
      data += startDelta
      data += token.length
      data += SemanticTokenTypeIndex(token.tokenType)
      data += 0 // no token modifiers for now

      previousLine = token.line
      previousStartChar = token.startChar
    }

    data.toList.asJava
  }

  private def isIdentifierStart(ch: Char): Boolean = ch.isLetter || ch == '_'

  private def isIdentifierPart(ch: Char): Boolean = ch.isLetterOrDigit || ch == '_'

  private def isNumberChar(ch: Char): Boolean =
    ch.isDigit || ch == '.' || ch == '_' || ch == 'e' || ch == 'E' || ch == '+' || ch == '-'

  private def isFunctionInvocationAt(text: String, indexAfterIdentifier: Int): Boolean = {
    var idx = indexAfterIdentifier
    while (idx < text.length && text.charAt(idx).isWhitespace && text.charAt(idx) != '\n') {
      idx += 1
    }

    idx < text.length && text.charAt(idx) == '('
  }
}

private case class SemanticToken(
    line: Int,
    startChar: Int,
    length: Int,
    tokenType: String
)
