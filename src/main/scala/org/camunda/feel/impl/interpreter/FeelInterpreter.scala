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
package org.camunda.feel.impl.interpreter

import org.camunda.feel.FeelEngine.UnaryTests
import org.camunda.feel.api.EvaluationFailureType
import org.camunda.feel.context.Context
import org.camunda.feel.syntaxtree._
import org.camunda.feel.valuemapper.ValueMapper
import org.camunda.feel.{Date, DateTime, DayTimeDuration, LocalDateTime, LocalTime, Number, Time, YearMonthDuration}

import java.time.{Duration, Period}

/**
  * @author Philipp Ossler
  */
class FeelInterpreter {

  def eval(expression: Exp)(implicit context: EvalContext): Val =
    expression match {

      // literals
      case ConstNull                 => ValNull
      case ConstInputValue           => input
      case ConstNumber(x)            => ValNumber(x)
      case ConstBool(b)              => ValBoolean(b)
      case ConstString(s)            => ValString(s)
      case ConstDate(d)              => ValDate(d)
      case ConstLocalTime(t)         => ValLocalTime(t)
      case ConstTime(t)              => ValTime(t)
      case ConstLocalDateTime(dt)    => ValLocalDateTime(dt)
      case ConstDateTime(dt)         => ValDateTime(dt)
      case ConstYearMonthDuration(d) => ValYearMonthDuration(d.normalized)
      case ConstDayTimeDuration(d)   => ValDayTimeDuration(d)

      case ConstList(items) =>
        mapEither[Exp, Val](items, item => eval(item).toEither, ValList)

      case ConstContext(entries) =>
        foldEither[(String, Exp), EvalContext](
          EvalContext.empty(context.valueMapper),
          entries, {
            case (ctx, (key, value)) =>
              eval(value)(context.merge(ctx)).toEither.map(v => ctx.add(key -> v))
          },
          ValContext
        )

      case range: ConstRange => toRange(range)

      // simple unary tests
      case InputEqualTo(x) =>
        withVal(input, i => checkEquality(i, eval(x), _ == _, ValBoolean))
      case InputLessThan(x) =>
        withVal(input, i => dualOp(i, eval(x), _ < _, ValBoolean))
      case InputLessOrEqual(x) =>
        withVal(input, i => dualOp(i, eval(x), _ <= _, ValBoolean))
      case InputGreaterThan(x) =>
        withVal(input, i => dualOp(i, eval(x), _ > _, ValBoolean))
      case InputGreaterOrEqual(x) =>
        withVal(input, i => dualOp(i, eval(x), _ >= _, ValBoolean))
      case InputInRange(range @ ConstRange(start, end)) =>
        unaryOpDual(eval(start.value),
                    eval(end.value),
                    isInRange(range),
                    ValBoolean)

      case UnaryTestExpression(x) => withVal(eval(x), unaryTestExpression)

      // arithmetic operations
      case Addition(x, y)       => withValOrNull(addOp(eval(x), eval(y)))
      case Subtraction(x, y)    => withValOrNull(subOp(eval(x), eval(y)))
      case Multiplication(x, y) => withValOrNull(mulOp(eval(x), eval(y)))
      case Division(x, y)       => withValOrNull(divOp(eval(x), eval(y)))
      case Exponentiation(x, y) =>
        withValOrNull(
          dualNumericOp(eval(x),
                        eval(y),
                        (x, y) =>
                          if (y.isWhole) {
                            x.pow(y.toInt)
                          } else {
                            math.pow(x.toDouble, y.toDouble)
                        },
                        ValNumber))
      case ArithmeticNegation(x) =>
        withValOrNull(withNumber(eval(x), x => ValNumber(-x)))

      // dual comparators
      case Equal(x, y)          => checkEquality(eval(x), eval(y), _ == _, ValBoolean)
      case LessThan(x, y)       => dualOp(eval(x), eval(y), _ < _, ValBoolean)
      case LessOrEqual(x, y)    => dualOp(eval(x), eval(y), _ <= _, ValBoolean)
      case GreaterThan(x, y)    => dualOp(eval(x), eval(y), _ > _, ValBoolean)
      case GreaterOrEqual(x, y) => dualOp(eval(x), eval(y), _ >= _, ValBoolean)

      // combinators
      case AtLeastOne(xs)    => atLeastOne(xs, ValBoolean)
      case Not(x)            => withBooleanOrNull(eval(x), x => ValBoolean(!x))
      case Disjunction(x, y) => atLeastOne(x :: y :: Nil, ValBoolean)
      case Conjunction(x, y) => all(x :: y :: Nil, ValBoolean)

      // control structures
      case If(condition, statement, elseStatement) =>
        withBooleanOrFalse(eval(condition),
                           isMet =>
                             if (isMet) {
                               eval(statement)
                             } else {
                               eval(elseStatement)
                           })
      case In(x, test) =>
        withVal(eval(x), x => eval(test)(context.add(inputKey -> x)))
      case InstanceOf(x, typeName) =>
        withVal(eval(x), x => {
          typeName match {
            case "Any" if x != ValNull => ValBoolean(true)
            case "years and months duration" => withType(x, t => ValBoolean(t == "year-month-duration"))
            case "days and time duration" => withType(x, t => ValBoolean(t == "day-time-duration"))
            case "date and time" => withType(x, t => ValBoolean(t == "date time"))
            case _                     => withType(x, t => ValBoolean(t == typeName))
          }
        })

      // context
      case Ref(names) =>
        val name = names.head
        context.variable(name) match {
          case _: ValError => error(EvaluationFailureType.NO_VARIABLE_FOUND, s"No variable found with name '$name'")
          case value => ref(value, names.tail)
        }
      case PathExpression(exp, key) => withVal(eval(exp), v => path(v, key))

      // list
      case SomeItem(iterators, condition) =>
        withCartesianProduct(
          iterators,
          p =>
            atLeastOneValue(p.map(vars => () => eval(condition)(context.addAll(vars))),
                       ValBoolean))
      case EveryItem(iterators, condition) =>
        withCartesianProduct(
          iterators,
          p =>
            allValues(p.map(vars => () => eval(condition)(context.addAll(vars))),
                ValBoolean))
      case For(iterators, exp) =>
        withCartesianProduct(
          iterators,
          p =>
            ValList((List[Val]() /: p) {
              case (partial, vars) => {
                val iterationContext = context.addAll(vars).add("partial" -> ValList(partial))
                val value = eval(exp)(iterationContext)
                partial ++ (value :: Nil)
              }
            })
        )
      case Filter(list, filter) =>
        withList(
          eval(list),
          l => {
            val evalFilterWithItem =
              (item: Val) => eval(filter)(filterContext(item))

            filter match {
              case ConstNumber(index) => filterList(l.items, index)
              case ArithmeticNegation(ConstNumber(index)) =>
                filterList(l.items, -index)
              case _: Comparison | _: FunctionInvocation |
                  _: QualifiedFunctionInvocation =>
                filterList(l.items, evalFilterWithItem)
              case _ =>
                eval(filter) match {
                  case ValNumber(index) => filterList(l.items, index)
                  case _                => filterList(l.items, evalFilterWithItem)
                }
            }
          }
        )
      case IterationContext(start, end) =>
        withNumbers(eval(start), eval(end), (x, y) => {
          val range = if (x < y) {
            (x to y).by(1)
          } else {
            (x to y).by(-1)
          }
          ValList(range.map(ValNumber).toList)
        })

      // functions
      case FunctionInvocation(name, params) =>
        withFunction(findFunction(context, name, params),
          f => invokeFunction(f, params) match {
            case ValError(failure) =>
              error(EvaluationFailureType.FUNCTION_INVOCATION_FAILURE, s"Failed to invoke function '$name': $failure")
              ValNull
            case result => result
          })
      case QualifiedFunctionInvocation(path, name, params) =>
        withContext(
          eval(path),
          c =>
            withFunction(
              findFunction(EvalContext.wrap(c.context, context.valueMapper),
                           name,
                           params),
              f => invokeFunction(f, params)))
      case FunctionDefinition(params, body) =>
        ValFunction(
          params,
          paramValues =>
            body match {
              case JavaFunctionInvocation(className, methodName, arguments) =>
                invokeJavaFunction(className,
                                   methodName,
                                   arguments,
                                   paramValues,
                                   context.valueMapper)
              case _ => eval(body)(context.addAll((params zip paramValues).toMap))
          }
        )

      // unsupported expression
      case exp => error(s"Unsupported expression '$exp'")

    }

  private def mapEither[T, R](it: Iterable[T],
                              f: T => Either[ValError, R],
                              resultMapping: List[R] => Val): Val = {

    foldEither[T, List[R]](List(), it, {
      case (xs, x) => f(x).map(xs :+ _)
    }, resultMapping)
  }

  private def foldEither[T, R](start: R,
                               it: Iterable[T],
                               op: (R, T) => Either[ValError, R],
                               resultMapping: R => Val): Val = {

    val result = it.foldLeft[Either[ValError, R]](Right(start)) { (result, x) =>
      result.flatMap(xs => op(xs, x))
    }

    result match {
      case Right(v) => resultMapping(v)
      case Left(e)  => e
    }
  }

  private def error(message: String)(implicit context: EvalContext): ValError = error(
    failureType = EvaluationFailureType.UNKNOWN,
    failureMessage = message
  )

  private def error(failureType: EvaluationFailureType, failureMessage: String)(implicit context: EvalContext): ValError = {
      context.addFailure(failureType, failureMessage)
      ValError(failureMessage)
  }

  private def withValOrNull(x: Val): Val = x match {
    case _: ValError => ValNull
    case _ => x
  }

  private def unaryOpDual(
                           x: Val,
                           y: Val,
                           c: (Val, Val, Val) => Boolean,
                           f: Boolean => Val)(implicit context: EvalContext): Val =
    withVal(
      input, {
        case i if (!i.isComparable || !x.isComparable || !y.isComparable) =>
          error(EvaluationFailureType.NOT_COMPARABLE, s"Can't compare $input with $x and $y")
          ValNull
        case i if (i.getClass != x.getClass || i.getClass != y.getClass) =>
          error(EvaluationFailureType.NOT_COMPARABLE, s"Can't compare $input with $x and $y")
        case i => f(c(i, x, y))
      }
    )

  private def withNumbers(x: Val, y: Val, f: (Number, Number) => Val)(implicit context: EvalContext): Val =
    withNumber(x, x => {
      withNumber(y, y => {
        f(x, y)
      })
    })

  private def withNumber(x: Val, f: Number => Val)(implicit context: EvalContext): Val = x match {
    case ValNumber(x) => f(x)
    case _            => error(EvaluationFailureType.INVALID_TYPE, s"Expected Number but found '$x'")
  }

  private def withBoolean(x: Val, f: Boolean => Val)(implicit context: EvalContext): Val = x match {
    case ValBoolean(x) => f(x)
    case _             => error(EvaluationFailureType.INVALID_TYPE,s"Expected Boolean but found '$x'")
  }

  private def withBooleanOrNull(x: Val, f: Boolean => Val)(implicit context: EvalContext): Val = x match {
    case ValBoolean(x) => f(x)
    case _             =>
      error(EvaluationFailureType.INVALID_TYPE, s"Expected Boolean but found '$x'")
      ValNull
  }

  private def withBooleanOrFalse(x: Val, f: Boolean => Val)(implicit context: EvalContext): Val = x match {
    case ValBoolean(x) => f(x)
    case _ =>
      error(EvaluationFailureType.INVALID_TYPE, s"Expected Boolean but found '$x'")
      f(false)
  }

  private def withString(x: Val, f: String => Val)(implicit context: EvalContext): Val = x match {
    case ValString(x) => f(x)
    case _            => error(EvaluationFailureType.INVALID_TYPE, s"expected String but found '$x'")
  }

  private def withDate(x: Val, f: Date => Val)(implicit context: EvalContext): Val = x match {
    case ValDate(x) => f(x)
    case _          => error(EvaluationFailureType.INVALID_TYPE, s"Expected Date but found '$x'")
  }

  private def withLocalTime(x: Val, f: LocalTime => Val)(implicit context: EvalContext): Val = x match {
    case ValLocalTime(x) => f(x)
    case _               => error(EvaluationFailureType.INVALID_TYPE, s"Expected Local Time but found '$x'")
  }

  private def withTime(x: Val, f: Time => Val)(implicit context: EvalContext): Val = x match {
    case ValTime(x) => f(x)
    case _          => error(EvaluationFailureType.INVALID_TYPE, s"Expected Time but found '$x'")
  }

  private def withDateTime(x: Val, f: DateTime => Val)(implicit context: EvalContext): Val = x match {
    case ValDateTime(x) => f(x)
    case _              => error(EvaluationFailureType.INVALID_TYPE, s"Expected Date Time but found '$x'")
  }

  private def withLocalDateTime(x: Val, f: LocalDateTime => Val)(implicit context: EvalContext): Val =
    x match {
      case ValLocalDateTime(x) => f(x)
      case _                   => error(EvaluationFailureType.INVALID_TYPE, s"Expected Local Date Time but found '$x'")
    }

  private def withYearMonthDuration(x: Val, f: YearMonthDuration => Val)(implicit context: EvalContext): Val =
    x match {
      case ValYearMonthDuration(x) => f(x)
      case _                       => error(EvaluationFailureType.INVALID_TYPE, s"Expected Years-Months-Duration but found '$x'")
    }

  private def withDayTimeDuration(x: Val, f: DayTimeDuration => Val)(implicit context: EvalContext): Val =
    x match {
      case ValDayTimeDuration(x) => f(x)
      case _                     => error(EvaluationFailureType.INVALID_TYPE, s"Expected Days-Time-Duration but found '$x'")
    }

  private def withVal(x: Val, f: Val => Val): Val = x match {
    case e: ValError => e
    case _           => f(x)
  }

  private def isInRange(range: ConstRange): (Val, Val, Val) => Boolean =
    (i, x, y) => {
      val inStart: Boolean = range.start match {
        case OpenConstRangeBoundary(_)   => i > x
        case ClosedConstRangeBoundary(_) => i >= x
      }
      val inEnd = range.end match {
        case OpenConstRangeBoundary(_)   => i < y
        case ClosedConstRangeBoundary(_) => i <= y
      }
      inStart && inEnd
    }

  private def atLeastOne(xs: List[Exp], f: Boolean => Val)(
      implicit context: EvalContext): Val =
    atLeastOneValue(xs map (x => () => eval(x)), f)

  private def atLeastOneValue(items: List[() => Val], f: Boolean => Val)(implicit context: EvalContext): Val = {
    items.foldLeft(f(false)) {
      case (ValBoolean(true), _) => f(true)
      case (ValNull, item) =>
        item() match {
          case ValBoolean(true) => f(true)
          case _                => ValNull
        }
      case (_, item) => withBooleanOrNull(item(), f)
    }
  }

  private def all(xs: List[Exp], f: Boolean => Val)(
      implicit context: EvalContext): Val =
    allValues(xs map (x => () => eval(x)), f)

  private def allValues(items: List[() => Val], f: Boolean => Val)(implicit context: EvalContext): Val = {
    items.foldLeft(f(true)) {
      case (ValBoolean(false), _) => f(false)
      case (ValNull, item) =>
        item() match {
          case ValBoolean(false) => f(false)
          case _                 => ValNull
        }
      case (_, item) => withBooleanOrNull(item(), f)
    }
  }

  private def inputKey(implicit context: EvalContext): String =
    context.variable(UnaryTests.inputVariable) match {
      case ValString(inputVariableName) => inputVariableName
      case _                            => UnaryTests.defaultInputVariable
    }

  private def input(implicit context: EvalContext): Val =
    context.variable(inputKey) match {
      case _: ValError => error(EvaluationFailureType.NO_VARIABLE_FOUND, s"No input value found.")
      case inputValue => inputValue
    }

  private def dualNumericOp(
      x: Val,
      y: Val,
      op: (Number, Number) => Number,
      f: Number => Val)(implicit context: EvalContext): Val =
    x match {
      case ValNumber(x) => withNumber(y, y => f(op(x, y)))
      case _            => error(s"expected Number but found '$x'")
    }

  private def checkEquality(
      x: Val,
      y: Val,
      c: (Any, Any) => Boolean,
      f: Boolean => Val)(implicit context: EvalContext): Val =
    x match {
      case ValNull                 => f(c(ValNull, y.toOption.getOrElse(ValNull)))
      case x if (y == ValNull)     => f(c(x.toOption.getOrElse(ValNull), ValNull))
      case _ : ValError            => f(c(ValNull, y.toOption.getOrElse(ValNull)))
      case _ if (y.isInstanceOf[ValError]) => f(c(ValNull, x.toOption.getOrElse(ValNull)))
      case ValNumber(x)            => withNumber(y, y => f(c(x, y)))
      case ValBoolean(x)           => withBoolean(y, y => f(c(x, y)))
      case ValString(x)            => withString(y, y => f(c(x, y)))
      case ValDate(x)              => withDate(y, y => f(c(x, y)))
      case ValLocalTime(x)         => withLocalTime(y, y => f(c(x, y)))
      case ValTime(x)              => withTime(y, y => f(c(x, y)))
      case ValLocalDateTime(x)     => withLocalDateTime(y, y => f(c(x, y)))
      case ValDateTime(x)          => withDateTime(y, y => f(c(x, y)))
      case ValYearMonthDuration(x) => withYearMonthDuration(y, y => f(c(x, y)))
      case ValDayTimeDuration(x)   => withDayTimeDuration(y, y => f(c(x, y)))
      case ValList(x) =>
        withList(
          y,
          y => {
            if (x.size != y.items.size) {
              f(false)

            } else {
              val isEqual = x.zip(y.items).foldRight(true) {
                case ((x, y), listIsEqual) =>
                  listIsEqual && {
                    checkEquality(x, y, c, f) match {
                      case ValBoolean(itemIsEqual) => itemIsEqual
                      case _                       => false
                    }
                  }
              }
              f(isEqual)
            }
          }
        )
      case ValContext(x) =>
        withContext(
          y,
          y => {
            val xVars = x.variableProvider.getVariables
            val yVars = y.context.variableProvider.getVariables

            if (xVars.keys != yVars.keys) {
              f(false)

            } else {
              val isEqual = xVars.keys.foldRight(true) {
                case (key, contextIsEqual) =>
                  contextIsEqual && {
                    val xVal = context.valueMapper.toVal(xVars(key))
                    val yVal = context.valueMapper.toVal(yVars(key))

                    checkEquality(xVal, yVal, c, f) match {
                      case ValBoolean(entryIsEqual) => entryIsEqual
                      case _                        => false
                    }
                  }
              }
              f(isEqual)
            }
          }
        )
      case _ =>
        error(
          s"expected Number, Boolean, String, Date, Time, Duration, List or Context but found '$x'")
    }

  private def dualOp(x: Val,
                     y: Val,
                     c: (Val, Val) => Boolean,
                     f: Boolean => Val)(implicit context: EvalContext): Val =
    x match {
      case _ if (!x.isComparable || !y.isComparable) =>
        error(EvaluationFailureType.NOT_COMPARABLE, s"Can't compare $x with $y")
        ValNull
      case _ if (x.getClass != y.getClass) =>
        error(EvaluationFailureType.NOT_COMPARABLE, s"Can't compare $x with $y")
      case _ => f(c(x, y))
    }

  private def addOp(x: Val, y: Val)(implicit context: EvalContext): Val = x match {
    case ValNumber(x)    => withNumber(y, y => ValNumber(x + y))
    case ValString(x)    => withString(y, y => ValString(x + y))
    case ValLocalTime(x) => withDayTimeDuration(y, y => ValLocalTime(x.plus(y)))
    case ValTime(x)      => withDayTimeDuration(y, y => ValTime(x.plus(y)))
    case ValLocalDateTime(x) =>
      y match {
        case ValYearMonthDuration(y) => ValLocalDateTime(x.plus(y))
        case ValDayTimeDuration(y)   => ValLocalDateTime(x.plus(y))
        case _ =>
          error(s"expect Year-Month-/Day-Time-Duration but found '$x'")
      }
    case ValDateTime(x) =>
      y match {
        case ValYearMonthDuration(y) => ValDateTime(x.plus(y))
        case ValDayTimeDuration(y)   => ValDateTime(x.plus(y))
        case _ => error(s"expect Year-Month-/Day-Time-Duration but found '$x'")
      }
    case ValYearMonthDuration(x) =>
      y match {
        case ValYearMonthDuration(y) =>
          ValYearMonthDuration(x.plus(y).normalized)
        case ValLocalDateTime(y) => ValLocalDateTime(y.plus(x))
        case ValDateTime(y)      => ValDateTime(y.plus(x))
        case ValDate(y)          => ValDate(y.plus(x))
        case _ => error(
            s"expect Date-Time, Date, or Year-Month-Duration but found '$x'")
      }
    case ValDayTimeDuration(x) =>
      y match {
        case ValDayTimeDuration(y) => ValDayTimeDuration(x.plus(y))
        case ValLocalDateTime(y)   => ValLocalDateTime(y.plus(x))
        case ValDateTime(y)        => ValDateTime(y.plus(x))
        case ValLocalTime(y)       => ValLocalTime(y.plus(x))
        case ValTime(y)            => ValTime(y.plus(x))
        case ValDate(y)            => ValDate(y.atStartOfDay().plus(x).toLocalDate())
        case _ => error(
            s"expect Date-Time, Date, Time, or Day-Time-Duration but found '$x'")
      }
    case ValDate(x) =>
      y match {
        case ValDayTimeDuration(y) =>
          ValDate(x.atStartOfDay().plus(y).toLocalDate())
        case ValYearMonthDuration(y) => ValDate(x.plus(y))
        case _ =>
          error(s"expect Year-Month-/Day-Time-Duration but found '$x'")
      }
    case _ =>
      error(s"expected Number, String, Date, Time or Duration but found '$x'")
  }

  private def subOp(x: Val, y: Val)(implicit context: EvalContext): Val = x match {
    case ValNumber(x) => withNumber(y, y => ValNumber(x - y))
    case ValLocalTime(x) =>
      y match {
        case ValLocalTime(y)       => ValDayTimeDuration(Duration.between(y, x))
        case ValDayTimeDuration(y) => ValLocalTime(x.minus(y))
        case _                     => error(s"expect Time, or Day-Time-Duration but found '$x'")
      }
    case ValTime(x) =>
      y match {
        case ValTime(y)            => ValDayTimeDuration(ZonedTime.between(x, y))
        case ValDayTimeDuration(y) => ValTime(x.minus(y))
        case _                     => error(s"expect Time, or Day-Time-Duration but found '$x'")
      }
    case ValLocalDateTime(x) =>
      y match {
        case ValLocalDateTime(y)     => ValDayTimeDuration(Duration.between(y, x))
        case ValYearMonthDuration(y) => ValLocalDateTime(x.minus(y))
        case ValDayTimeDuration(y)   => ValLocalDateTime(x.minus(y))
        case _ =>
          error(s"expect Time, or Year-Month-/Day-Time-Duration but found '$x'")
      }
    case ValDateTime(x) =>
      y match {
        case ValDateTime(y)          => ValDayTimeDuration(Duration.between(y, x))
        case ValYearMonthDuration(y) => ValDateTime(x.minus(y))
        case ValDayTimeDuration(y)   => ValDateTime(x.minus(y))
        case _ =>
          error(s"expect Time, or Year-Month-/Day-Time-Duration but found '$x'")
      }
    case ValDate(x) =>
      y match {
        case ValDate(y) =>
          ValDayTimeDuration(Duration.between(y.atStartOfDay, x.atStartOfDay))
        case ValYearMonthDuration(y) => ValDate(x.minus(y))
        case ValDayTimeDuration(y) =>
          ValDate(x.atStartOfDay.minus(y).toLocalDate())
        case _ =>
          error(s"expect Date, or Year-Month-/Day-Time-Duration but found '$x'")
      }
    case ValYearMonthDuration(x) =>
      withYearMonthDuration(y, y => ValYearMonthDuration(x.minus(y).normalized))
    case ValDayTimeDuration(x) =>
      withDayTimeDuration(y, y => ValDayTimeDuration(x.minus(y)))
    case _ =>
      error(s"expected Number, Date, Time or Duration but found '$x'")
  }

  private def mulOp(x: Val, y: Val)(implicit context: EvalContext): Val = x match {
    case ValNumber(x) =>
      y match {
        case ValNumber(y) => ValNumber(x * y)
        case ValYearMonthDuration(y) =>
          ValYearMonthDuration(y.multipliedBy(x.intValue).normalized)
        case ValDayTimeDuration(y) =>
          ValDayTimeDuration(y.multipliedBy(x.intValue))
        case _ =>
          error(
            s"expect Number, or Year-Month-/Day-Time-Duration but found '$x'")
      }
    case ValYearMonthDuration(x) =>
      withNumber(
        y,
        y => ValYearMonthDuration(x.multipliedBy(y.intValue).normalized))
    case ValDayTimeDuration(x) =>
      withNumber(y, y => ValDayTimeDuration(x.multipliedBy(y.intValue)))
    case _ => error(s"expected Number, or Duration but found '$x'")
  }

  private def divOp(x: Val, y: Val)(implicit context: EvalContext): Val = y match {
    case ValNumber(y) if (y != 0) =>
      x match {
        case ValNumber(x) => ValNumber(x / y)
        case ValYearMonthDuration(x) =>
          ValYearMonthDuration(
            Period.ofMonths((x.toTotalMonths() / y).intValue).normalized)
        case ValDayTimeDuration(x) =>
          ValDayTimeDuration(Duration.ofMillis((x.toMillis() / y).intValue))
        case _ => error(s"expected Number, or Duration but found '$x'")
      }

    case ValYearMonthDuration(y) if (!y.isZero) =>
      withYearMonthDuration(x,
                            x => ValNumber(x.toTotalMonths / y.toTotalMonths))
    case ValDayTimeDuration(y) if (!y.isZero) =>
      withDayTimeDuration(x, x => ValNumber(x.toMillis / y.toMillis))

    case _ => error(s"'$x / $y' is not allowed")
  }

  private def unaryTestExpression(x: Val)(implicit context: EvalContext): Val =
    withVal(
      input,
      i =>
        if (x == ValBoolean(true)) {
          ValBoolean(true)

        } else if (checkEquality(i, x, _ == _, ValBoolean) == ValBoolean(true)) {
          ValBoolean(true)

        } else {
          x match {
            case ValList(ys) => ValBoolean(ys.contains(i))
            case _           => ValBoolean(false)
          }
      }
    )

  private def withFunction(x: Val, f: ValFunction => Val)(implicit context: EvalContext): Val = x match {
    case x: ValFunction => f(x)
    case ValError(failure) =>
      error(EvaluationFailureType.NO_FUNCTION_FOUND, failure)
      ValNull
    case _ =>
      error(EvaluationFailureType.INVALID_TYPE, s"Expected function but found '$x'")
      ValNull
  }

  private def invokeFunction(function: ValFunction, params: FunctionParameters)(
      implicit context: EvalContext): Val = {
    val paramList: List[Val] = params match {
      case PositionalFunctionParameters(params) => {

        if (function.hasVarArgs && function.params.size > 0) {
          val size = function.params.size - 1

          val args: List[Val] = params take (size) map eval

          val varArgs: Val = (params drop (size) map eval) match {
            case Nil                                 => ValList(List())
            case ValList(list) :: Nil if (size == 0) => ValList(list)
            case list                                => ValList(list)
          }

          args :+ varArgs

        } else {
          params map eval
        }
      }
      case NamedFunctionParameters(params) => {

        // if a parameter is not set then it's replaced with null
        function.params map (p => (params.get(p).map(eval _)) getOrElse ValNull)
      }
    }

    function.invoke(paramList) match {
      case e: ValError => e
      case result => context.valueMapper.toVal(result)
    }
  }

  private def findFunction(ctx: EvalContext,
                           name: String,
                           params: FunctionParameters): Val = params match {
    case PositionalFunctionParameters(params) => ctx.function(name, params.size)
    case NamedFunctionParameters(params)      => ctx.function(name, params.keySet)
  }

  private def withType(x: Val, f: String => ValBoolean)(implicit context: EvalContext): Val = x match {
    case ValNumber(_)            => f("number")
    case ValBoolean(_)           => f("boolean")
    case ValString(_)            => f("string")
    case ValDate(_)              => f("date")
    case ValLocalTime(_)         => f("time")
    case ValTime(_)              => f("time")
    case ValLocalDateTime(_)     => f("date time")
    case ValDateTime(_)          => f("date time")
    case ValYearMonthDuration(_) => f("year-month-duration")
    case ValDayTimeDuration(_)   => f("day-time-duration")
    case ValNull                 => f("null")
    case ValList(_)              => f("list")
    case ValContext(_)           => f("context")
    case ValFunction(_, _, _)    => f("function")
    case _                       => error(s"unexpected type '${x.getClass.getName}'")
  }

  private def withList(x: Val, f: ValList => Val)(implicit context: EvalContext): Val = x match {
    case x: ValList => f(x)
    case _          => error(s"expect List but found '$x'")
  }

  private def withLists(
      lists: List[(String, Val)],
      f: List[(String, ValList)] => Val)(implicit context: EvalContext): Val = {
    lists
      .map { case (name, it) => name -> withList(it, list => list) }
      .find(_._2.isInstanceOf[ValError]) match {
      case Some(Tuple2(_, e: Val)) => e
      case None                    => f(lists.asInstanceOf[List[(String, ValList)]])
    }
  }

  private def withCartesianProduct(
      iterators: List[(String, Exp)],
      f: List[Map[String, Val]] => Val)(implicit context: EvalContext): Val =
    withLists(iterators.map { case (name, it) => name -> eval(it) },
              lists => f(flattenAndZipLists(lists)))

  private def flattenAndZipLists(
      lists: List[(String, ValList)]): List[Map[String, Val]] = lists match {
    case Nil                 => List()
    case (name, list) :: Nil => list.items map (v => Map(name -> v)) // flatten
    case (name, list) :: tail =>
      for { v <- list.items; values <- flattenAndZipLists(tail) } yield
        values + (name -> v) // zip
  }

  private def filterList(list: List[Val], filter: Val => Val)(
    implicit context: EvalContext): Val = {
    val conditionNotFulfilled = ValString("_")

    val withBooleanFilter = (list: List[Val]) => mapEither[Val, Val](
      list,
      item =>
        (filter(item) match {
          case ValBoolean(true) => item
          case _ => conditionNotFulfilled
        }).toEither,
      items => ValList(items.filterNot(_ == conditionNotFulfilled))
    )

    // The filter function could return a boolean or a number. If it returns a number then we use
    // the number as the index for the list. Otherwise, the boolean function determine if the
    // condition is fulfilled for the given item.
    // Note that the code could look more elegant but we want to avoid unintended invocations of
    // the function because the invocations could be observed by the function provider (see #359).
    list.headOption.map(head =>
      withVal(filter(head), {
        case ValNumber(index) => filterList(list, index)
        case ValBoolean(isFulFilled) => withBooleanFilter(list.tail) match {
          case ValList(fulFilledItems) if isFulFilled => ValList(head :: fulFilledItems)
          case fulFilledItems: ValList => fulFilledItems
          case error => error
        }
        case _ => withBooleanFilter(list.tail) match {
          case ValList(fulFilledItems) => ValList(fulFilledItems)
          case error => error
        }
      })
    ).getOrElse(
      // Return always an empty list if the given list is empty. Note that we would return `null`
      // instead, if the filter is a number. But if it is a function, we would need to evaluate the
      // function first to see that it returns a number.
      ValList(List.empty)
    )
  }

  private def filterList(list: List[Val], index: Number): Val = {

    val i = {
      if (index > 0) {
        index - 1
      } else {
        list.size + index
      }
    }

    if (i < 0 || i >= list.size) {
      ValNull
    } else {
      list(i.toInt)
    }
  }

  private def withContext(x: Val, f: ValContext => Val)(implicit context: EvalContext): Val = x match {
    case x: ValContext => f(x)
    case _             => error(s"expect Context but found '$x'")
  }

  private def filterContext(x: Val)(
      implicit context: EvalContext): EvalContext =
    x match {
      case ValContext(ctx: Context) => context.add("item" -> x).merge(ctx)
      case v                        => context.add("item" -> v)
    }

  private def ref(x: Val, names: List[String])(
      implicit context: EvalContext): Val =
    names match {
      case Nil => x
      case n :: ns =>
        withVal(
          path(x, n),
          value => ref(value, ns)
        )
    }

  private def path(v: Val, key: String)(implicit context: EvalContext): Val =
    v match {
      case ctx: ValContext =>
        EvalContext.wrap(ctx.context, context.valueMapper).variable(key) match {
          case _: ValError =>
            val detailedMessage = ctx.context.variableProvider.keys match {
              case Nil => "The context is empty"
              case keys => s"Available keys: ${keys.mkString(", ")}"
            }
            error(
              failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
              failureMessage = s"No context entry found with key '$key'. $detailedMessage"
            )
            ValNull
          case x: Val => x
        }
      case ValList(list) => ValList(list map (item => path(item, key)))
      case ValNull =>
        error(
          failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
          failureMessage = s"No context entry found with key '$key'. The context is null"
        )
        ValNull
      case value =>
        value.property(key).getOrElse {
          val propertyNames: String = value.propertyNames().mkString(",")
          error(
            failureType = EvaluationFailureType.NO_PROPERTY_FOUND,
            failureMessage = s"No property found with name '$key' of value '$value'. Available properties: $propertyNames"
          )
        }
    }

  private def evalContextEntry(key: String, exp: Exp)(
      implicit context: EvalContext): Val =
    withVal(eval(exp), value => value)

  private def invokeJavaFunction(className: String,
                                 methodName: String,
                                 arguments: List[String],
                                 paramValues: List[Val],
                                 valueMapper: ValueMapper)(implicit context: EvalContext): Val = {
    try {

      val clazz = JavaClassMapper.loadClass(className)

      val argTypes = arguments map JavaClassMapper.loadClass

      val method = clazz.getDeclaredMethod(methodName, argTypes: _*)

      val argJavaObjects = paramValues zip argTypes map {
        case (obj, clazz) => JavaClassMapper.asJavaObject(obj, clazz)
      }

      val result = method.invoke(null, argJavaObjects: _*)

      valueMapper.toVal(result)

    } catch {
      case e: ClassNotFoundException =>
        error(s"fail to load class '$className'")
      case e: NoSuchMethodException =>
        error(
          s"fail to get method with name '$methodName' and arguments '$arguments' from class '$className'")
      case _: Throwable =>
        error(
          s"fail to invoke method with name '$methodName' and arguments '$arguments' from class '$className'")
    }
  }

  private def toRange(range: ConstRange)(implicit context: EvalContext): Val = {
    withVal(
      eval(range.start.value),
      startValue =>
        withVal(
          eval(range.end.value),
          endValue =>
            if (isValidRange(startValue, endValue)) {
              ValRange(
                start = toRangeBoundary(range.start, startValue),
                end = toRangeBoundary(range.end, endValue)
              )
            } else {
              error(s"invalid range definition '$range'")
          }
      )
    )
  }

  private def isValidRange(startValue: Val, endValue: Val): Boolean =
    (startValue, endValue) match {
      case (ValNumber(_), ValNumber(_))                       => true
      case (ValDate(_), ValDate(_))                           => true
      case (ValTime(_), ValTime(_))                           => true
      case (ValLocalTime(_), ValLocalTime(_))                 => true
      case (ValDateTime(_), ValDateTime(_))                   => true
      case (ValLocalDateTime(_), ValLocalDateTime(_))         => true
      case (ValYearMonthDuration(_), ValYearMonthDuration(_)) => true
      case (ValDayTimeDuration(_), ValDayTimeDuration(_))     => true
      case _                                                  => false
    }

  private def toRangeBoundary(boundary: ConstRangeBoundary,
                              value: Val): RangeBoundary = {
    boundary match {
      case OpenConstRangeBoundary(_)   => OpenRangeBoundary(value)
      case ClosedConstRangeBoundary(_) => ClosedRangeBoundary(value)
    }
  }

}
