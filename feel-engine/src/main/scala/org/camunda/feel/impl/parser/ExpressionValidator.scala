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

import org.camunda.feel.FeelEngine.Failure
import org.camunda.feel.syntaxtree.{Comparison, _}

class ExpressionValidator(externalFunctionsEnabled: Boolean) {

  def validateExpression(expression: Exp): Option[Failure] =
    validate(expression).headOption

  private def validate(exp: Exp): List[Failure] = exp match {
    // validate expression
    case JavaFunctionInvocation(_, _, _) if !externalFunctionsEnabled =>
      List(Failure(
        "External functions are disabled. Use the FunctionProvider SPI (recommended) or enable external function in the configuration."))

    // delegate to inner expression
    case ConstList(items) => items.flatMap(validate)
    case ConstContext(entries) =>
      entries.flatMap { case (_, value) => validate(value) }

    case QualifiedFunctionInvocation(path, _, _) => validate(path)
    case FunctionDefinition(_, body)             => validate(body)

    case InputLessThan(x)       => validate(x)
    case InputLessOrEqual(x)    => validate(x)
    case InputGreaterThan(x)    => validate(x)
    case InputGreaterOrEqual(x) => validate(x)
    case InputEqualTo(x)        => validate(x)

    case c: Comparison        => validate(c.x) ++ validate(c.y)
    case Interval(start, end) => validate(start.value) ++ validate(end.value)
    case Range(start, end)    => validate(start) ++ validate(end)

    case Addition(x, y)        => validate(x) ++ validate(y)
    case Subtraction(x, y)     => validate(x) ++ validate(y)
    case Multiplication(x, y)  => validate(x) ++ validate(y)
    case Division(x, y)        => validate(x) ++ validate(y)
    case Exponentiation(x, y)  => validate(x) ++ validate(y)
    case ArithmeticNegation(x) => validate(x)

    case If(condition, statement, elseStatement) =>
      validate(condition) ++ validate(statement) ++ validate(elseStatement)
    case Disjunction(x, y) => validate(x) ++ validate(y)
    case Conjunction(x, y) => validate(x) ++ validate(y)
    case In(x, test)       => validate(x) ++ validate(test)

    case AtLeastOne(xs) => xs.flatMap(validate)
    case SomeItem(iterators, condition) =>
      iterators.flatMap { case (_, value) => validate(value) } ++ validate(
        condition)
    case EveryItem(iterators, condition) =>
      iterators.flatMap { case (_, value) => validate(value) } ++ validate(
        condition)
    case For(iterators, body) =>
      iterators.flatMap { case (_, value) => validate(value) } ++ validate(body)
    case Filter(list, filter) => validate(list) ++ validate(filter)

    case PathExpression(path, _)   => validate(path)
    case Not(x)                    => validate(x)
    case UnaryTestExpression(test) => validate(test)
    case InstanceOf(x, _)          => validate(x)

    case _ => Nil
  }

}
