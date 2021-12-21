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

import java.time.{Duration, Period}
import org.camunda.feel.FeelEngine.UnaryTests
import org.camunda.feel.context.Context
import org.camunda.feel.valuemapper.ValueMapper
import org.camunda.feel.syntaxtree.{
  Addition,
  ArithmeticNegation,
  AtLeastOne,
  ClosedConstRangeBoundary,
  ClosedRangeBoundary,
  Comparison,
  Conjunction,
  ConstBool,
  ConstContext,
  ConstDate,
  ConstDateTime,
  ConstDayTimeDuration,
  ConstInputValue,
  ConstList,
  ConstLocalDateTime,
  ConstLocalTime,
  ConstNull,
  ConstNumber,
  ConstRange,
  ConstRangeBoundary,
  ConstString,
  ConstTime,
  ConstYearMonthDuration,
  Disjunction,
  Division,
  Equal,
  EveryItem,
  Exp,
  Exponentiation,
  Filter,
  For,
  FunctionDefinition,
  FunctionInvocation,
  FunctionParameters,
  GreaterOrEqual,
  GreaterThan,
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
  JavaFunctionInvocation,
  LessOrEqual,
  LessThan,
  Multiplication,
  NamedFunctionParameters,
  Not,
  OpenConstRangeBoundary,
  OpenRangeBoundary,
  PathExpression,
  PositionalFunctionParameters,
  QualifiedFunctionInvocation,
  RangeBoundary,
  Ref,
  SomeItem,
  Subtraction,
  UnaryTestExpression,
  Val,
  ValBoolean,
  ValContext,
  ValDate,
  ValDateTime,
  ValDayTimeDuration,
  ValError,
  ValFunction,
  ValList,
  ValLocalDateTime,
  ValLocalTime,
  ValNull,
  ValNumber,
  ValRange,
  ValString,
  ValTime,
  ValYearMonthDuration,
  ZonedTime
}
import org.camunda.feel.{
  Date,
  DateTime,
  DayTimeDuration,
  LocalDateTime,
  LocalTime,
  Number,
  Time,
  YearMonthDuration,
  logger
}

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
          EvalContext.wrap(Context.EmptyContext)(context.valueMapper),
          entries, {
            case (ctx, (key, value)) =>
              eval(value)(context + ctx).toEither.map(v => ctx + (key -> v))
          },
          ValContext
        )

      case ConstRange(start, end) =>
        withVal(eval(start.value),
                startValue =>
                  withVal(eval(end.value),
                          endValue =>
                            ValRange(
                              start = toRangeBoundary(start, startValue),
                              end = toRangeBoundary(end, endValue)
                          )))

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
        withVal(eval(x), x => eval(test)(context + (inputKey -> x)))
      case InstanceOf(x, typeName) =>
        withVal(eval(x), x => {
          typeName match {
            case "Any" if x != ValNull => ValBoolean(true)
            case _                     => withType(x, t => ValBoolean(t == typeName))
          }
        })

      // context
      case Ref(names)               => ref(context.variable(names.head), names.tail)
      case PathExpression(exp, key) => withVal(eval(exp), v => path(v, key))

      // list
      case SomeItem(iterators, condition) =>
        withCartesianProduct(
          iterators,
          p =>
            atLeastOne(p.map(vars => () => eval(condition)(context ++ vars)),
                       ValBoolean))
      case EveryItem(iterators, condition) =>
        withCartesianProduct(
          iterators,
          p =>
            all(p.map(vars => () => eval(condition)(context ++ vars)),
                ValBoolean))
      case For(iterators, exp) =>
        withCartesianProduct(
          iterators,
          p =>
            ValList((List[Val]() /: p) {
              case (partial, vars) => {
                val iterationContext = context ++ vars + ("partial" -> partial)
                val value = eval(exp)(iterationContext)
                partial ++ (value :: Nil)
              }
            })
        )
      case Filter(list, filter) =>
        withList(
          eval(list),
          l =>
            filter match {
              case ConstNumber(index) => filterList(l.items, index)
              case ArithmeticNegation(ConstNumber(index)) =>
                filterList(l.items, -index)
              case comparison: Comparison =>
                filterList(l.items,
                           item => eval(comparison)(filterContext(item)))
              case _ =>
                eval(filter) match {
                  case ValNumber(index) => filterList(l.items, index)
                  case _ =>
                    filterList(l.items,
                               item => eval(filter)(filterContext(item)))
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
                     f => invokeFunction(f, params))
      case QualifiedFunctionInvocation(path, name, params) =>
        withContext(
          eval(path),
          c =>
            withFunction(
              findFunction(EvalContext.wrap(c.context)(context.valueMapper),
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
              case _ => eval(body)(context ++ (params zip paramValues).toMap)
          }
        )

      // unsupported expression
      case exp => ValError(s"unsupported expression '$exp'")

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

  private def error(x: Val, message: String) = x match {
    case e: ValError => e
    case _           => ValError(message)
  }

  private def withValOrNull(x: Val): Val = x match {
    case ValError(e) => {
      logger.warn(s"Suppressed failure: $e")
      ValNull
    }
    case _ => x
  }

  private def unaryOpDual(
      x: Val,
      y: Val,
      c: (Val, Val, Val) => Boolean,
      f: Boolean => Val)(implicit context: EvalContext): Val =
    withVal(
      input,
      _ match {
        case ValNull                             => f(false)
        case _ if (x == ValNull || y == ValNull) => f(false)
        case i if (!i.isComparable)              => ValError(s"$i is not comparable")
        case _ if (!x.isComparable)              => ValError(s"$x is not comparable")
        case _ if (!y.isComparable)              => ValError(s"$y is not comparable")
        case i if (i.getClass != x.getClass) =>
          ValError(s"$i can not be compared to $x")
        case i if (i.getClass != y.getClass) =>
          ValError(s"$i can not be compared to $y")
        case i => f(c(i, x, y))
      }
    )

  private def withNumbers(x: Val, y: Val, f: (Number, Number) => Val): Val =
    withNumber(x, x => {
      withNumber(y, y => {
        f(x, y)
      })
    })

  private def withNumber(x: Val, f: Number => Val): Val = x match {
    case ValNumber(x) => f(x)
    case _            => error(x, s"expected Number but found '$x'")
  }

  private def withBoolean(x: Val, f: Boolean => Val): Val = x match {
    case ValBoolean(x) => f(x)
    case _             => error(x, s"expected Boolean but found '$x'")
  }

  private def withBooleanOrNull(x: Val, f: Boolean => Val): Val = x match {
    case ValBoolean(x) => f(x)
    case _             => ValNull
  }

  private def withBooleanOrFalse(x: Val, f: Boolean => Val): Val = x match {
    case ValBoolean(x) => f(x)
    case _ => {
      logger.warn(s"Suppressed failure: expected Boolean but found '$x'")
      f(false)
    }
  }

  private def withString(x: Val, f: String => Val): Val = x match {
    case ValString(x) => f(x)
    case _            => error(x, s"expected String but found '$x'")
  }

  private def withDates(x: Val, y: Val, f: (Date, Date) => Val): Val =
    withDate(x, x => {
      withDate(y, y => {
        f(x, y)
      })
    })

  private def withDate(x: Val, f: Date => Val): Val = x match {
    case ValDate(x) => f(x)
    case _          => error(x, s"expected Date but found '$x'")
  }

  private def withTimes(x: Val, y: Val, f: (Time, Time) => Val): Val =
    withTime(x, x => {
      withTime(y, y => {
        f(x, y)
      })
    })

  private def withLocalTimes(x: Val,
                             y: Val,
                             f: (LocalTime, LocalTime) => Val): Val =
    withLocalTime(x, x => {
      withLocalTime(y, y => {
        f(x, y)
      })
    })

  private def withLocalTime(x: Val, f: LocalTime => Val): Val = x match {
    case ValLocalTime(x) => f(x)
    case _               => error(x, s"expect Local Time but found '$x'")
  }

  private def withTime(x: Val, f: Time => Val): Val = x match {
    case ValTime(x) => f(x)
    case _          => error(x, s"expect Time but found '$x'")
  }

  private def withDateTimes(x: Val,
                            y: Val,
                            f: (DateTime, DateTime) => Val): Val =
    withDateTime(x, x => {
      withDateTime(y, y => {
        f(x, y)
      })
    })

  private def withLocalDateTimes(
      x: Val,
      y: Val,
      f: (LocalDateTime, LocalDateTime) => Val): Val =
    withLocalDateTime(x, x => {
      withLocalDateTime(y, y => {
        f(x, y)
      })
    })

  private def withDateTime(x: Val, f: DateTime => Val): Val = x match {
    case ValDateTime(x) => f(x)
    case _              => error(x, s"expect Date Time but found '$x'")
  }

  private def withLocalDateTime(x: Val, f: LocalDateTime => Val): Val =
    x match {
      case ValLocalDateTime(x) => f(x)
      case _                   => error(x, s"expect Local Date Time but found '$x'")
    }

  private def withYearMonthDurations(
      x: Val,
      y: Val,
      f: (YearMonthDuration, YearMonthDuration) => Val): Val =
    withYearMonthDuration(x, x => {
      withYearMonthDuration(y, y => {
        f(x, y)
      })
    })

  private def withDayTimeDurations(
      x: Val,
      y: Val,
      f: (DayTimeDuration, DayTimeDuration) => Val): Val =
    withDayTimeDuration(x, x => {
      withDayTimeDuration(y, y => {
        f(x, y)
      })
    })

  private def withYearMonthDuration(x: Val, f: YearMonthDuration => Val): Val =
    x match {
      case ValYearMonthDuration(x) => f(x)
      case _                       => error(x, s"expect Year-Month-Duration but found '$x'")
    }

  private def withDayTimeDuration(x: Val, f: DayTimeDuration => Val): Val =
    x match {
      case ValDayTimeDuration(x) => f(x)
      case _                     => error(x, s"expect Day-Time-Duration but found '$x'")
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
    atLeastOne(xs map (x => () => eval(x)), f)

  private def atLeastOne(items: List[() => Val], f: Boolean => Val): Val = {
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
    all(xs map (x => () => eval(x)), f)

  private def all(items: List[() => Val], f: Boolean => Val): Val = {
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
    context.variable(inputKey)

  private def dualNumericOp(
      x: Val,
      y: Val,
      op: (Number, Number) => Number,
      f: Number => Val)(implicit context: EvalContext): Val =
    x match {
      case ValNumber(x) => withNumber(y, y => f(op(x, y)))
      case _            => error(x, s"expected Number but found '$x'")
    }

  private def checkEquality(
      x: Val,
      y: Val,
      c: (Any, Any) => Boolean,
      f: Boolean => Val)(implicit context: EvalContext): Val =
    x match {
      case ValNull                 => f(c(ValNull, y.toOption.getOrElse(ValNull)))
      case x if (y == ValNull)     => f(c(x.toOption.getOrElse(ValNull), ValNull))
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
          x,
          s"expected Number, Boolean, String, Date, Time, Duration, List or Context but found '$x'")
    }

  private def dualOp(x: Val,
                     y: Val,
                     c: (Val, Val) => Boolean,
                     f: Boolean => Val)(implicit context: EvalContext): Val =
    x match {
      case ValNull                => withVal(y, y => ValBoolean(false))
      case _ if (y == ValNull)    => withVal(x, x => ValBoolean(false))
      case _ if (!x.isComparable) => ValError(s"$x is not comparable")
      case _ if (!y.isComparable) => ValError(s"$y is not comparable")
      case _ if (x.getClass != y.getClass) =>
        ValError(s"$x can not be compared to $y")
      case _ => f(c(x, y))
    }

  private def addOp(x: Val, y: Val): Val = x match {
    case ValNumber(x)    => withNumber(y, y => ValNumber(x + y))
    case ValString(x)    => withString(y, y => ValString(x + y))
    case ValLocalTime(x) => withDayTimeDuration(y, y => ValLocalTime(x.plus(y)))
    case ValTime(x)      => withDayTimeDuration(y, y => ValTime(x.plus(y)))
    case ValLocalDateTime(x) =>
      y match {
        case ValYearMonthDuration(y) => ValLocalDateTime(x.plus(y))
        case ValDayTimeDuration(y)   => ValLocalDateTime(x.plus(y))
        case _ =>
          error(y, s"expect Year-Month-/Day-Time-Duration but found '$x'")
      }
    case ValDateTime(x) =>
      y match {
        case ValYearMonthDuration(y) => ValDateTime(x.plus(y))
        case ValDayTimeDuration(y)   => ValDateTime(x.plus(y))
        case _ =>
          error(y, s"expect Year-Month-/Day-Time-Duration but found '$x'")
      }
    case ValYearMonthDuration(x) =>
      y match {
        case ValYearMonthDuration(y) =>
          ValYearMonthDuration(x.plus(y).normalized)
        case ValLocalDateTime(y) => ValLocalDateTime(y.plus(x))
        case ValDateTime(y)      => ValDateTime(y.plus(x))
        case ValDate(y)          => ValDate(y.plus(x))
        case _ =>
          error(
            y,
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
        case _ =>
          error(
            y,
            s"expect Date-Time, Date, Time, or Day-Time-Duration but found '$x'")
      }
    case ValDate(x) =>
      y match {
        case ValDayTimeDuration(y) =>
          ValDate(x.atStartOfDay().plus(y).toLocalDate())
        case ValYearMonthDuration(y) => ValDate(x.plus(y))
        case _ =>
          error(y, s"expect Year-Month-/Day-Time-Duration but found '$x'")
      }
    case _ =>
      error(x,
            s"expected Number, String, Date, Time or Duration but found '$x'")
  }

  private def subOp(x: Val, y: Val): Val = x match {
    case ValNumber(x) => withNumber(y, y => ValNumber(x - y))
    case ValLocalTime(x) =>
      y match {
        case ValLocalTime(y)       => ValDayTimeDuration(Duration.between(y, x))
        case ValDayTimeDuration(y) => ValLocalTime(x.minus(y))
        case _                     => error(y, s"expect Time, or Day-Time-Duration but found '$x'")
      }
    case ValTime(x) =>
      y match {
        case ValTime(y)            => ValDayTimeDuration(ZonedTime.between(x, y))
        case ValDayTimeDuration(y) => ValTime(x.minus(y))
        case _                     => error(y, s"expect Time, or Day-Time-Duration but found '$x'")
      }
    case ValLocalDateTime(x) =>
      y match {
        case ValLocalDateTime(y)     => ValDayTimeDuration(Duration.between(y, x))
        case ValYearMonthDuration(y) => ValLocalDateTime(x.minus(y))
        case ValDayTimeDuration(y)   => ValLocalDateTime(x.minus(y))
        case _ =>
          error(y,
                s"expect Time, or Year-Month-/Day-Time-Duration but found '$x'")
      }
    case ValDateTime(x) =>
      y match {
        case ValDateTime(y)          => ValDayTimeDuration(Duration.between(y, x))
        case ValYearMonthDuration(y) => ValDateTime(x.minus(y))
        case ValDayTimeDuration(y)   => ValDateTime(x.minus(y))
        case _ =>
          error(y,
                s"expect Time, or Year-Month-/Day-Time-Duration but found '$x'")
      }
    case ValDate(x) =>
      y match {
        case ValDate(y) =>
          ValDayTimeDuration(Duration.between(y.atStartOfDay, x.atStartOfDay))
        case ValYearMonthDuration(y) => ValDate(x.minus(y))
        case ValDayTimeDuration(y) =>
          ValDate(x.atStartOfDay.minus(y).toLocalDate())
        case _ =>
          error(y,
                s"expect Date, or Year-Month-/Day-Time-Duration but found '$x'")
      }
    case ValYearMonthDuration(x) =>
      withYearMonthDuration(y, y => ValYearMonthDuration(x.minus(y).normalized))
    case ValDayTimeDuration(x) =>
      withDayTimeDuration(y, y => ValDayTimeDuration(x.minus(y)))
    case _ =>
      error(x, s"expected Number, Date, Time or Duration but found '$x'")
  }

  private def mulOp(x: Val, y: Val): Val = x match {
    case ValNumber(x) =>
      y match {
        case ValNumber(y) => ValNumber(x * y)
        case ValYearMonthDuration(y) =>
          ValYearMonthDuration(y.multipliedBy(x.intValue).normalized)
        case ValDayTimeDuration(y) =>
          ValDayTimeDuration(y.multipliedBy(x.intValue))
        case _ =>
          error(
            y,
            s"expect Number, or Year-Month-/Day-Time-Duration but found '$x'")
      }
    case ValYearMonthDuration(x) =>
      withNumber(
        y,
        y => ValYearMonthDuration(x.multipliedBy(y.intValue).normalized))
    case ValDayTimeDuration(x) =>
      withNumber(y, y => ValDayTimeDuration(x.multipliedBy(y.intValue)))
    case _ => error(x, s"expected Number, or Duration but found '$x'")
  }

  private def divOp(x: Val, y: Val): Val = y match {
    case ValNumber(y) if (y != 0) =>
      x match {
        case ValNumber(x) => ValNumber(x / y)
        case ValYearMonthDuration(x) =>
          ValYearMonthDuration(
            Period.ofMonths((x.toTotalMonths() / y).intValue).normalized)
        case ValDayTimeDuration(x) =>
          ValDayTimeDuration(Duration.ofMillis((x.toMillis() / y).intValue))
        case _ => error(x, s"expected Number, or Duration but found '$x'")
      }

    case ValYearMonthDuration(y) if (!y.isZero) =>
      withYearMonthDuration(x,
                            x => ValNumber(x.toTotalMonths / y.toTotalMonths))
    case ValDayTimeDuration(y) if (!y.isZero) =>
      withDayTimeDuration(x, x => ValNumber(x.toMillis / y.toMillis))

    case _ => ValError(s"'$x / $y' is not allowed")
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

  private def withFunction(x: Val, f: ValFunction => Val): Val = x match {
    case x: ValFunction => f(x)
    case _              => error(x, s"expect Function but found '$x'")
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
      case ValError(failure) => {
        // TODO (saig0): customize error handling (#260)
        logger.warn(s"Failed to invoke function: $failure")
        ValNull
      }
      case result => context.valueMapper.toVal(result)
    }
  }

  private def findFunction(ctx: EvalContext,
                           name: String,
                           params: FunctionParameters): Val = params match {
    case PositionalFunctionParameters(params) => ctx.function(name, params.size)
    case NamedFunctionParameters(params)      => ctx.function(name, params.keySet)
  }

  private def withType(x: Val, f: String => ValBoolean): Val = x match {
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
    case _                       => error(x, s"unexpected type '${x.getClass.getName}'")
  }

  private def withList(x: Val, f: ValList => Val): Val = x match {
    case x: ValList => f(x)
    case _          => error(x, s"expect List but found '$x'")
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

  private def filterList(list: List[Val], filter: Val => Val): Val = {
    val conditionNotFulfilled = ValString("_")

    mapEither[Val, Val](
      list,
      item =>
        withBoolean(filter(item), {
          case true  => item
          case false => conditionNotFulfilled
        }).toEither,
      items => ValList(items.filterNot(_ == conditionNotFulfilled))
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

  private def withContext(x: Val, f: ValContext => Val): Val = x match {
    case x: ValContext => f(x)
    case _             => error(x, s"expect Context but found '$x'")
  }

  private def filterContext(x: Val)(
      implicit context: EvalContext): EvalContext =
    x match {
      case ValContext(ctx: Context) => context + ctx + ("item" -> x)
      case v                        => context + ("item" -> v)
    }

  private def ref(x: Val, names: List[String])(
      implicit context: EvalContext): Val =
    names match {
      case Nil => x
      case n :: ns =>
        withContext(
          x, {
            case ctx: ValContext =>
              EvalContext
                .wrap(ctx.context)(context.valueMapper)
                .variable(n) match {
                case e: ValError => e
                case x: Val      => ref(x, ns)
              }
            case e => error(e, s"context contains no entry with key '$n'")
          }
        )
    }

  private def path(v: Val, key: String)(implicit context: EvalContext): Val =
    v match {
      case ctx: ValContext =>
        EvalContext.wrap(ctx.context)(context.valueMapper).variable(key) match {
          case _: ValError =>
            ValError(s"context contains no entry with key '$key'")
          case x: Val => x
          case _      => ValError(s"context contains no entry with key '$key'")
        }
      case ValList(list) => ValList(list map (item => path(item, key)))
      case value =>
        value.property(key).getOrElse {
          val propertyNames: String = value.propertyNames().mkString(",")
          error(
            value,
            s"No property found with name '$key' of value '$value'. Available properties: $propertyNames")
        }
    }

  private def evalContextEntry(key: String, exp: Exp)(
      implicit context: EvalContext): Val =
    withVal(eval(exp), value => value)

  private def invokeJavaFunction(className: String,
                                 methodName: String,
                                 arguments: List[String],
                                 paramValues: List[Val],
                                 valueMapper: ValueMapper): Val = {
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
        ValError(s"fail to load class '$className'")
      case e: NoSuchMethodException =>
        ValError(
          s"fail to get method with name '$methodName' and arguments '$arguments' from class '$className'")
      case _: Throwable =>
        ValError(
          s"fail to invoke method with name '$methodName' and arguments '$arguments' from class '$className'")
    }
  }

  private def toRangeBoundary(boundary: ConstRangeBoundary,
                              value: Val): RangeBoundary = {
    boundary match {
      case OpenConstRangeBoundary(_)   => OpenRangeBoundary(value)
      case ClosedConstRangeBoundary(_) => ClosedRangeBoundary(value)
    }
  }

}
