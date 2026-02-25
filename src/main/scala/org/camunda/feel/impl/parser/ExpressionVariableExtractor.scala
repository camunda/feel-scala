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

import org.camunda.feel.api.VariableReference
import org.camunda.feel.syntaxtree.{
  Addition,
  ArithmeticNegation,
  AtLeastOne,
  Comparison,
  Conjunction,
  ConstContext,
  ConstList,
  ConstRange,
  Disjunction,
  Division,
  EveryItem,
  Exp,
  Exponentiation,
  Filter,
  For,
  FunctionDefinition,
  FunctionInvocation,
  If,
  In,
  InputEqualTo,
  InputGreaterOrEqual,
  InputGreaterThan,
  InputInRange,
  InputLessOrEqual,
  InputLessThan,
  InstanceOf,
  IterationContext,
  Multiplication,
  NamedFunctionParameters,
  Not,
  PathExpression,
  PositionalFunctionParameters,
  QualifiedFunctionInvocation,
  Ref,
  SomeItem,
  Subtraction,
  UnaryTestExpression
}

object ExpressionVariableExtractor {

  private val FILTER_ITEM_VARIABLE_NAME = new VariableReference("item")

  def getVariableReferences(expression: Exp): Set[VariableReference] = expression match {
    // variable references
    case Ref(names) => Set(VariableReference(names))

    // composite expressions
    case ConstList(items) => items.toSet.flatMap(getVariableReferences)

    case ConstContext(entries) =>
      val contextEntryKeys = getKeys(entries)
      entries
        .flatMap { case (_, value) => getVariableReferences(value) }
        .toSet
        .diff(contextEntryKeys)

    case ConstRange(start, end) =>
      getVariableReferences(start.value) ++ getVariableReferences(end.value)

    case InputLessThan(x)       => getVariableReferences(x)
    case InputLessOrEqual(x)    => getVariableReferences(x)
    case InputGreaterThan(x)    => getVariableReferences(x)
    case InputGreaterOrEqual(x) => getVariableReferences(x)
    case InputEqualTo(x)        => getVariableReferences(x)
    case InputInRange(x)        => getVariableReferences(x)

    case c: Comparison                => getVariableReferences(c.x) ++ getVariableReferences(c.y)
    case IterationContext(start, end) => getVariableReferences(start) ++ getVariableReferences(end)

    case Addition(x, y)        => getVariableReferences(x) ++ getVariableReferences(y)
    case Subtraction(x, y)     => getVariableReferences(x) ++ getVariableReferences(y)
    case Multiplication(x, y)  => getVariableReferences(x) ++ getVariableReferences(y)
    case Division(x, y)        => getVariableReferences(x) ++ getVariableReferences(y)
    case Exponentiation(x, y)  => getVariableReferences(x) ++ getVariableReferences(y)
    case ArithmeticNegation(x) => getVariableReferences(x)

    case If(condition, statement, elseStatement) =>
      getVariableReferences(condition) ++ getVariableReferences(statement) ++ getVariableReferences(
        elseStatement
      )
    case Disjunction(x, y)                       => getVariableReferences(x) ++ getVariableReferences(y)
    case Conjunction(x, y)                       => getVariableReferences(x) ++ getVariableReferences(y)
    case In(x, test)                             => getVariableReferences(x) ++ getVariableReferences(test)

    case AtLeastOne(xs)                  => xs.flatMap(getVariableReferences).toSet
    case SomeItem(iterators, condition)  => getVariableNames(iterators, condition)
    case EveryItem(iterators, condition) => getVariableNames(iterators, condition)
    case For(iterators, body)            => getVariableNames(iterators, body)

    case Filter(list, filter) =>
      val contextEntryNames = getContextEntryKeys(list)
      getVariableReferences(list) ++ getVariableReferences(filter)
        .filterNot(
          _ == FILTER_ITEM_VARIABLE_NAME
        )
        .diff(contextEntryNames)

    case PathExpression(path, _)   => getVariableReferences(path)
    case Not(x)                    => getVariableReferences(x)
    case UnaryTestExpression(test) => getVariableReferences(test)
    case InstanceOf(x, _)          => getVariableReferences(x)

    case FunctionInvocation(_, PositionalFunctionParameters(params)) =>
      params.toSet.flatMap(getVariableReferences)

    case FunctionInvocation(_, NamedFunctionParameters(params)) =>
      params.flatMap { case (_, value) => getVariableReferences(value) }.toSet

    case QualifiedFunctionInvocation(path, _, PositionalFunctionParameters(params)) =>
      getVariableReferences(path) ++ params.toSet.flatMap(getVariableReferences)

    case QualifiedFunctionInvocation(path, _, NamedFunctionParameters(params)) =>
      getVariableReferences(path) ++ params.flatMap { case (_, value) =>
        getVariableReferences(value)
      }.toSet

    case FunctionDefinition(parameters, body) =>
      val parameterNames = parameters.map(new VariableReference(_)).toSet
      getVariableReferences(body).diff(parameterNames)

    case _ => Set.empty
  }

  private def getKeys(entries: List[(String, Exp)]): Set[VariableReference] = {
    entries.map { case (name, _) => new VariableReference(name) }.toSet
  }

  private def getVariableNames(
      iterators: List[(String, Exp)],
      body: Exp
  ): Set[VariableReference] = {
    val iteratorNames = getKeys(iterators)
    iterators.flatMap { case (_, value) =>
      getVariableReferences(value)
    }.toSet ++ getVariableReferences(body).diff(iteratorNames)
  }

  private def getContextEntryKeys(list: Exp): Set[VariableReference] = list match {
    case ConstList(items) =>
      items.flatMap {
        case ConstContext(entries) => getKeys(entries)
        case _                     => Set.empty
      }.toSet
    case _                => Set.empty
  }

}
