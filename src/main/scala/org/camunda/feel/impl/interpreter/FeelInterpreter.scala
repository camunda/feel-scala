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
import org.camunda.feel.impl.interpreter.FeelInterpreter.INPUT_VALUE_SYMBOL
import org.camunda.feel.syntaxtree._
import org.camunda.feel.valuemapper.ValueMapper
import org.camunda.feel.{
  Date,
  DateTime,
  DayTimeDuration,
  LocalDateTime,
  LocalTime,
  Number,
  Time,
  YearMonthDuration
}

import java.time.{Duration, Period}
import scala.reflect.ClassTag

/** @author
  *   Philipp Ossler
  */
class FeelInterpreter(private val valueMapper: ValueMapper) {

  private val valueComparator = new ValComparator(valueMapper)

  def eval(expression: Exp)(implicit context: EvalContext): Val = {
    // Check if the current thread was interrupted, otherwise long-running evaluations can not be interrupted and fully block the thread
    if (Thread.interrupted()) {
      throw new InterruptedException()
    }

    expression match {

      // literals
      case ConstNull                 => ValNull
      case ConstInputValue           => getInputValueBySymbol
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
          entries,
          { case (ctx, (key, value)) =>
            eval(value)(context.merge(ctx)).toEither.map(v => ctx.add(key -> v))
          },
          ValContext
        )

      case range: ConstRange => toRange(range)

      // simple unary tests
      case InputEqualTo(x)                              =>
        withVal(getImplicitInputValue, i => checkEquality(i, eval(x)))
      case InputLessThan(x)                             =>
        withVal(getImplicitInputValue, i => dualOp(i, eval(x), _ < _, ValBoolean))
      case InputLessOrEqual(x)                          =>
        withVal(getImplicitInputValue, i => dualOp(i, eval(x), _ <= _, ValBoolean))
      case InputGreaterThan(x)                          =>
        withVal(getImplicitInputValue, i => dualOp(i, eval(x), _ > _, ValBoolean))
      case InputGreaterOrEqual(x)                       =>
        withVal(getImplicitInputValue, i => dualOp(i, eval(x), _ >= _, ValBoolean))
      case InputInRange(range @ ConstRange(start, end)) =>
        unaryOpDual(eval(start.value), eval(end.value), isInRange(range), ValBoolean)

      case UnaryTestExpression(x) => unaryTestExpression(x)

      // arithmetic operations
      case Addition(x, y)        => withValOrNull(addOp(eval(x), eval(y)))
      case Subtraction(x, y)     => withValOrNull(subOp(eval(x), eval(y)))
      case Multiplication(x, y)  => withValOrNull(mulOp(eval(x), eval(y)))
      case Division(x, y)        => withValOrNull(divOp(eval(x), eval(y)))
      case Exponentiation(x, y)  =>
        withValOrNull(
          withNumbers(
            eval(x),
            eval(y),
            (x, y) => {
              val result: Number = if (y.isWhole) {
                x.pow(y.toInt)
              } else {
                math.pow(x.toDouble, y.toDouble)
              }
              ValNumber(result)
            }
          )
        )
      case ArithmeticNegation(x) =>
        withValOrNull(withNumber(eval(x), x => ValNumber(-x)))

      // dual comparators
      case Equal(x, y)          => checkEquality(eval(x), eval(y))
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
        withBooleanOrFalse(
          eval(condition),
          isMet =>
            if (isMet) {
              eval(statement)
            } else {
              eval(elseStatement)
            }
        )
      case In(x, test)                             =>
        withVal(eval(x), x => eval(test)(context.add(getInputVariableName -> x)))
      case InstanceOf(x, typeName)                 =>
        withVal(
          eval(x),
          x => {
            val valueType = getTypeName(x.getClass)

            typeName match {
              case "Any"                 => ValBoolean(x != ValNull)
              case "date time"           => ValBoolean("date and time" == valueType)
              case "year-month-duration" => ValBoolean("years and months duration" == valueType)
              case "day-time-duration"   => ValBoolean("days and time duration" == valueType)
              case _                     => ValBoolean(typeName == valueType)
            }
          }
        )

      // context
      case Ref(names)               =>
        val name = names.head
        context.variable(name) match {
          case _: ValError =>
            error(EvaluationFailureType.NO_VARIABLE_FOUND, s"No variable found with name '$name'")
            ValNull
          case value       => ref(value, names.tail)
        }
      case PathExpression(exp, key) => withVal(eval(exp), v => path(v, key))

      // list
      case SomeItem(iterators, condition)  =>
        withValOrNull(
          withCartesianProduct(
            iterators,
            p =>
              atLeastOneValue(
                p.map(vars => () => eval(condition)(context.addAll(vars))),
                ValBoolean
              )
          )
        )
      case EveryItem(iterators, condition) =>
        withValOrNull(
          withCartesianProduct(
            iterators,
            p => allValues(p.map(vars => () => eval(condition)(context.addAll(vars))), ValBoolean)
          )
        )
      case For(iterators, exp)             =>
        withValOrNull(
          withCartesianProduct(
            iterators,
            p =>
              ValList((List[Val]() /: p) {
                case (partial, vars) => {
                  val iterationContext = context.addAll(vars).add("partial" -> ValList(partial))
                  val value            = eval(exp)(iterationContext)
                  partial ++ (value :: Nil)
                }
              })
          )
        )
      case Filter(list, filter)            =>
        withValOrNull(
          withList(
            eval(list),
            l => {
              val evalFilterWithItem =
                (item: Val) => eval(filter)(filterContext(item))

              filter match {
                case ConstNumber(index)                                                     => filterList(l.items, index)
                case ArithmeticNegation(ConstNumber(index))                                 =>
                  filterList(l.items, -index)
                case _: Comparison | _: FunctionInvocation | _: QualifiedFunctionInvocation =>
                  filterList(l.items, evalFilterWithItem)
                case _                                                                      =>
                  eval(filter) match {
                    case ValNumber(index) => filterList(l.items, index)
                    case _                => filterList(l.items, evalFilterWithItem)
                  }
              }
            }
          )
        )
      case IterationContext(start, end)    =>
        withNumbers(
          eval(start),
          eval(end),
          (x, y) => {
            val range = if (x < y) {
              (x to y).by(1)
            } else {
              (x to y).by(-1)
            }
            ValList(range.map(ValNumber).toList)
          }
        )

      // functions
      case FunctionInvocation(name, params)                =>
        withValOrNull(
          withFunction(
            findFunction(context, name, params),
            f =>
              invokeFunction(f, params) match {
                case fatalError: ValFatalError             => fatalError
                case ValError(failure) if name == "assert" =>
                  error(EvaluationFailureType.ASSERT_FAILURE, failure)
                  ValError(failure)
                case ValError(failure)                     =>
                  error(
                    EvaluationFailureType.FUNCTION_INVOCATION_FAILURE,
                    s"Failed to invoke function '$name': $failure"
                  )
                  ValNull
                case result                                => result
              }
          )
        )
      case QualifiedFunctionInvocation(path, name, params) =>
        withContext(
          eval(path),
          c =>
            withFunction(
              findFunction(EvalContext.wrap(c.context, context.valueMapper), name, params),
              f => invokeFunction(f, params)
            )
        )
      case FunctionDefinition(params, body)                =>
        ValFunction(
          params,
          paramValues =>
            body match {
              case JavaFunctionInvocation(className, methodName, arguments) =>
                invokeJavaFunction(
                  className,
                  methodName,
                  arguments,
                  paramValues,
                  context.valueMapper
                )
              case _                                                        => eval(body)(context.addAll((params zip paramValues).toMap))
            }
        )

      // unsupported expression
      case exp => error(EvaluationFailureType.UNKNOWN, s"Unsupported expression '$exp'")

    }
  }

  // ======== helpers ====================

  private def mapEither[T, R](
      it: Iterable[T],
      f: T => Either[ValError, R],
      resultMapping: List[R] => Val
  ): Val = {

    foldEither[T, List[R]](
      List(),
      it,
      { case (xs, x) =>
        f(x).map(xs :+ _)
      },
      resultMapping
    )
  }

  private def foldEither[T, R](
      start: R,
      it: Iterable[T],
      op: (R, T) => Either[ValError, R],
      resultMapping: R => Val
  ): Val = {

    val result = it.foldLeft[Either[ValError, R]](Right(start)) { (result, x) =>
      result.flatMap(xs => op(xs, x))
    }

    result match {
      case Right(v) => resultMapping(v)
      case Left(e)  => e
    }
  }

  // =========================================

  private def error(failureType: EvaluationFailureType, failureMessage: String)(implicit
      context: EvalContext
  ): ValError = {
    context.addFailure(failureType, failureMessage)
    ValError(failureMessage)
  }

  private def withVal(x: Val, f: Val => Val): Val = x match {
    case fatalError: ValFatalError => fatalError
    case error: ValError           => error
    case value                     => f(value)
  }

  private def withValOrNull(x: Val): Val = x.toOption.getOrElse(ValNull)

  private def withValues(x: Val, y: Val, f: (Val, Val) => Val) =
    withVal(x, valueX => withVal(y, valueY => f(valueX, valueY)))

  private def withValueType[T <: Val](value: Val, f: T => Val)(implicit
      context: EvalContext,
      tag: ClassTag[T]
  ): Val = {
    value match {
      case fatalError: ValFatalError                  => fatalError
      case error: ValError                            => error
      case v: T if tag.runtimeClass.isInstance(value) => f(v)
      case other                                      =>
        error(
          EvaluationFailureType.INVALID_TYPE,
          s"Expected ${getTypeName(tag.runtimeClass)} but found '$other'"
        )
    }
  }

  private def getTypeName(valueType: Class[_]): String = valueType match {
    case _ if valueType == ValNull.getClass              => "null"
    case _ if valueType == classOf[ValNumber]            => "number"
    case _ if valueType == classOf[ValBoolean]           => "boolean"
    case _ if valueType == classOf[ValString]            => "string"
    case _ if valueType == classOf[ValDate]              => "date"
    case _ if valueType == classOf[ValTime]              => "time"
    case _ if valueType == classOf[ValLocalTime]         => "time"
    case _ if valueType == classOf[ValDateTime]          => "date and time"
    case _ if valueType == classOf[ValLocalDateTime]     => "date and time"
    case _ if valueType == classOf[ValYearMonthDuration] => "years and months duration"
    case _ if valueType == classOf[ValDayTimeDuration]   => "days and time duration"
    case _ if valueType == classOf[ValList]              => "list"
    case _ if valueType == classOf[ValContext]           => "context"
    case _ if valueType == classOf[ValFunction]          => "function"
    case _ if valueType == classOf[ValRange]             => "range"
    case _ if valueType == classOf[ValError]             => "error"
    case _ if valueType == classOf[ValFatalError]        => "fatal error"
    case other                                           => other.getSimpleName
  }

  private def isComparable(values: Val*): Boolean = values.forall(_.isComparable)

  private def hasSameType(values: Val*): Boolean = values.map(_.getClass).distinct.size == 1

  // ======== type checks ====================

  private def withNumber(x: Val, f: Number => Val)(implicit context: EvalContext): Val =
    withValueType[ValNumber](x, number => f(number.value))

  private def withNumbers(x: Val, y: Val, f: (Number, Number) => Val)(implicit
      context: EvalContext
  ): Val =
    withNumber(x, x => withNumber(y, y => f(x, y)))

  private def withBoolean(x: Val, f: Boolean => Val)(implicit context: EvalContext): Val =
    withValueType[ValBoolean](x, boolean => f(boolean.value))

  private def withBooleanOrNull(x: Val, f: Boolean => Val)(implicit context: EvalContext): Val =
    withBoolean(x, f) match {
      case _: ValError => ValNull
      case value       => value
    }

  private def withBooleanOrFalse(x: Val, f: Boolean => Val)(implicit context: EvalContext): Val =
    withBoolean(x, f) match {
      case _: ValError => f(false)
      case value       => value
    }

  private def withFunction(x: Val, f: ValFunction => Val)(implicit context: EvalContext): Val =
    withValueType[ValFunction](x, f)

  private def withList(x: Val, f: ValList => Val)(implicit context: EvalContext): Val =
    withValueType[ValList](x, f)

  private def withContext(x: Val, f: ValContext => Val)(implicit context: EvalContext): Val =
    withValueType[ValContext](x, f)

  // =========================================

  private def getInputVariableName(implicit context: EvalContext): String = {
    context.variable(UnaryTests.inputVariable) match {
      case ValString(inputVariableName) => inputVariableName
      case _                            => UnaryTests.defaultInputVariable
    }
  }

  private def getImplicitInputValue(implicit context: EvalContext): Val = {
    context.variable(getInputVariableName).toOption.getOrElse {
      error(EvaluationFailureType.NO_VARIABLE_FOUND, "No input value found.")
      ValNull
    }
  }

  private def getInputValueBySymbol(implicit context: EvalContext): Val = {
    context.variable(INPUT_VALUE_SYMBOL).toOption.getOrElse {
      ValFatalError(
        s"No input value available. '$INPUT_VALUE_SYMBOL' can only be used inside an unary-test expression."
      )
    }
  }

  private def unaryOpDual(x: Val, y: Val, c: (Val, Val, Val) => Boolean, f: Boolean => Val)(implicit
      context: EvalContext
  ): Val =
    withVal(
      getImplicitInputValue,
      {
        case inputValue if !isComparable(inputValue, x, y) || !hasSameType(inputValue, x, y) =>
          error(
            EvaluationFailureType.NOT_COMPARABLE,
            s"Can't compare '$getImplicitInputValue' with '$x' and '$y'"
          )
          ValNull
        case inputValue                                                                      => f(c(inputValue, x, y))
      }
    )

  private def isInRange(range: ConstRange): (Val, Val, Val) => Boolean =
    (i, x, y) => {
      val inStart: Boolean = range.start match {
        case OpenConstRangeBoundary(_)   => i > x
        case ClosedConstRangeBoundary(_) => i >= x
      }
      val inEnd            = range.end match {
        case OpenConstRangeBoundary(_)   => i < y
        case ClosedConstRangeBoundary(_) => i <= y
      }
      inStart && inEnd
    }

  private def atLeastOne(xs: List[Exp], f: Boolean => Val)(implicit context: EvalContext): Val =
    atLeastOneValue(xs map (x => () => eval(x)), f)

  private def atLeastOneValue(items: List[() => Val], f: Boolean => Val)(implicit
      context: EvalContext
  ): Val = {
    items.foldLeft(f(false)) {
      case (ValBoolean(true), _)          => f(true)
      case (fatalError: ValFatalError, _) => fatalError
      case (ValNull, item)                =>
        item() match {
          case ValBoolean(true)          => f(true)
          case fatalError: ValFatalError => fatalError
          case _                         => ValNull
        }
      case (_, item)                      => withBooleanOrNull(item(), f)
    }
  }

  private def all(xs: List[Exp], f: Boolean => Val)(implicit context: EvalContext): Val =
    allValues(xs map (x => () => eval(x)), f)

  private def allValues(items: List[() => Val], f: Boolean => Val)(implicit
      context: EvalContext
  ): Val = {
    items.foldLeft(f(true)) {
      case (ValBoolean(false), _)         => f(false)
      case (fatalError: ValFatalError, _) => fatalError
      case (ValNull, item)                =>
        item() match {
          case ValBoolean(false)         => f(false)
          case fatalError: ValFatalError => fatalError
          case _                         => ValNull
        }
      case (_, item)                      => withBooleanOrNull(item(), f)
    }
  }

  private def checkEquality(x: Val, y: Val)(implicit context: EvalContext): Val =
    withValues(
      x,
      y,
      (x, y) =>
        valueComparator.compare(x, y).toOption.getOrElse {
          error(EvaluationFailureType.NOT_COMPARABLE, s"Can't compare '$x' with '$y'")
          ValNull
        }
    )

  private def dualOp(x: Val, y: Val, c: (Val, Val) => Boolean, f: Boolean => Val)(implicit
      context: EvalContext
  ): Val = {
    withValues(
      x,
      y,
      {
        case _ if !isComparable(x, y) || !hasSameType(x, y) =>
          error(EvaluationFailureType.NOT_COMPARABLE, s"Can't compare '$x' with '$y'")
          ValNull
        case _                                              => f(c(x, y))
      }
    )
  }

  private def addOp(x: Val, y: Val)(implicit context: EvalContext): Val =
    withValues(
      x,
      y,
      {
        case (ValNumber(x), ValNumber(y)) => ValNumber(x + y)
        case (ValString(x), ValString(y)) => ValString(x + y)

        case (ValLocalTime(x), ValDayTimeDuration(y)) => ValLocalTime(x.plus(y))
        case (ValTime(x), ValDayTimeDuration(y))      => ValTime(x.plus(y))

        case (ValLocalDateTime(x), ValYearMonthDuration(y)) => ValLocalDateTime(x.plus(y))
        case (ValLocalDateTime(x), ValDayTimeDuration(y))   => ValLocalDateTime(x.plus(y))
        case (ValDateTime(x), ValYearMonthDuration(y))      => ValDateTime(x.plus(y))
        case (ValDateTime(x), ValDayTimeDuration(y))        => ValDateTime(x.plus(y))

        case (ValYearMonthDuration(x), ValYearMonthDuration(y)) =>
          ValYearMonthDuration(x.plus(y).normalized)
        case (ValYearMonthDuration(x), ValLocalDateTime(y))     => ValLocalDateTime(y.plus(x))
        case (ValYearMonthDuration(x), ValDateTime(y))          => ValDateTime(y.plus(x))
        case (ValYearMonthDuration(x), ValDate(y))              => ValDate(y.plus(x))

        case (ValDayTimeDuration(x), ValDayTimeDuration(y)) => ValDayTimeDuration(x.plus(y))
        case (ValDayTimeDuration(x), ValLocalDateTime(y))   => ValLocalDateTime(y.plus(x))
        case (ValDayTimeDuration(x), ValDateTime(y))        => ValDateTime(y.plus(x))
        case (ValDayTimeDuration(x), ValLocalTime(y))       => ValLocalTime(y.plus(x))
        case (ValDayTimeDuration(x), ValTime(y))            => ValTime(y.plus(x))
        case (ValDayTimeDuration(x), ValDate(y))            => ValDate(y.atStartOfDay().plus(x).toLocalDate)

        case (ValDate(x), ValDayTimeDuration(y))   => ValDate(x.atStartOfDay().plus(y).toLocalDate)
        case (ValDate(x), ValYearMonthDuration(y)) => ValDate(x.plus(y))

        case _ => error(EvaluationFailureType.INVALID_TYPE, s"Can't add '$y' to '$x'")
      }
    )

  private def subOp(x: Val, y: Val)(implicit context: EvalContext): Val =
    withValues(
      x,
      y,
      {
        case (ValNumber(x), ValNumber(y)) => ValNumber(x - y)

        case (ValLocalTime(x), ValLocalTime(y))       => ValDayTimeDuration(Duration.between(y, x))
        case (ValLocalTime(x), ValDayTimeDuration(y)) => ValLocalTime(x.minus(y))

        case (ValTime(x), ValTime(y))            => ValDayTimeDuration(ZonedTime.between(x, y))
        case (ValTime(x), ValDayTimeDuration(y)) => ValTime(x.minus(y))

        case (ValLocalDateTime(x), ValLocalDateTime(y))     =>
          ValDayTimeDuration(Duration.between(y, x))
        case (ValLocalDateTime(x), ValYearMonthDuration(y)) => ValLocalDateTime(x.minus(y))
        case (ValLocalDateTime(x), ValDayTimeDuration(y))   => ValLocalDateTime(x.minus(y))

        case (ValDateTime(x), ValDateTime(y))          => ValDayTimeDuration(Duration.between(y, x))
        case (ValDateTime(x), ValYearMonthDuration(y)) => ValDateTime(x.minus(y))
        case (ValDateTime(x), ValDayTimeDuration(y))   => ValDateTime(x.minus(y))

        case (ValDate(x), ValDate(y))              =>
          ValDayTimeDuration(Duration.between(y.atStartOfDay, x.atStartOfDay))
        case (ValDate(x), ValYearMonthDuration(y)) => ValDate(x.minus(y))
        case (ValDate(x), ValDayTimeDuration(y))   => ValDate(x.atStartOfDay.minus(y).toLocalDate)

        case (ValYearMonthDuration(x), ValYearMonthDuration(y)) =>
          ValYearMonthDuration(x.minus(y).normalized)
        case (ValDayTimeDuration(x), ValDayTimeDuration(y))     => ValDayTimeDuration(x.minus(y))

        case _ => error(EvaluationFailureType.INVALID_TYPE, s"Can't subtract '$y' from '$x'")
      }
    )

  private def mulOp(x: Val, y: Val)(implicit context: EvalContext): Val =
    withValues(
      x,
      y,
      {
        case (ValNumber(x), ValNumber(y))            => ValNumber(x * y)
        case (ValNumber(x), ValYearMonthDuration(y)) =>
          ValYearMonthDuration(y.multipliedBy(x.intValue).normalized)
        case (ValNumber(x), ValDayTimeDuration(y))   => ValDayTimeDuration(y.multipliedBy(x.intValue))

        case (ValYearMonthDuration(x), ValNumber(y)) =>
          ValYearMonthDuration(x.multipliedBy(y.intValue).normalized)
        case (ValDayTimeDuration(x), ValNumber(y))   => ValDayTimeDuration(x.multipliedBy(y.intValue))

        case _ => error(EvaluationFailureType.INVALID_TYPE, s"Can't multiply '$x' by '$y'")
      }
    )

  private def divOp(x: Val, y: Val)(implicit context: EvalContext): Val =
    withValues(
      x,
      y,
      {
        case (ValNumber(x), ValNumber(y)) if (y != 0) => ValNumber(x / y)

        case (ValYearMonthDuration(x), ValNumber(y)) if (y != 0)               =>
          ValYearMonthDuration(Period.ofMonths((x.toTotalMonths / y).intValue).normalized)
        case (ValYearMonthDuration(x), ValYearMonthDuration(y)) if (!y.isZero) =>
          ValNumber(x.toTotalMonths / y.toTotalMonths)

        case (ValDayTimeDuration(x), ValDayTimeDuration(y)) if (!y.isZero) =>
          ValNumber(x.toMillis / y.toMillis)
        case (ValDayTimeDuration(x), ValNumber(y)) if (y != 0)             =>
          ValDayTimeDuration(Duration.ofMillis((x.toMillis / y).intValue))

        case _ => error(EvaluationFailureType.INVALID_TYPE, s"Can't divide '$x' by '$y'")
      }
    )

  private def unaryTestExpression(expression: Exp)(implicit context: EvalContext): Val = {
    withVal(
      getImplicitInputValue,
      inputValue =>
        eval(expression) match {
          case _: ValFatalError                       =>
            eval(expression)(context.add(INPUT_VALUE_SYMBOL -> inputValue)) match {
              case ValBoolean(true)  => ValBoolean(true)
              case ValBoolean(false) => ValBoolean(false)
              case _                 => ValNull
            }
          case ValBoolean(true)                       => ValBoolean(true)
          case ValList(ys) if ys.contains(inputValue) =>
            // the expression contains the input value
            ValBoolean(true)
          case x                                      =>
            checkEquality(inputValue, x) match {
              case ValBoolean(true)             =>
                // the expression is the input value
                ValBoolean(true)
              case _ if x == ValBoolean(false)  =>
                // the expression is false
                ValBoolean(false)
              case _ if x.isInstanceOf[ValList] =>
                // the expression is a list but doesn't contain the input value
                ValBoolean(false)
              case ValNull                      => ValNull
              case _                            =>
                // the expression is not the input value
                ValBoolean(false)
            }
        }
    )
  }

  private def findFunction(ctx: EvalContext, name: String, params: FunctionParameters)(implicit
      context: EvalContext
  ): Val = {
    val function = params match {
      case PositionalFunctionParameters(params) => ctx.function(name, params.size)
      case NamedFunctionParameters(params)      => ctx.function(name, params.keySet)
    }

    function match {
      case ValError(failure) => error(EvaluationFailureType.NO_FUNCTION_FOUND, failure)
      case _                 => function
    }
  }

  private def invokeFunction(function: ValFunction, params: FunctionParameters)(implicit
      context: EvalContext
  ): Val = {
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

    // validate parameters
    if (paramList.exists(_.isInstanceOf[ValFatalError])) {
      paramList.find(_.isInstanceOf[ValFatalError]).get

    } else if (paramList.exists(_.isInstanceOf[ValError])) {
      paramList.find(_.isInstanceOf[ValError]).get

    } else {
      function.invoke(paramList) match {
        case fatalError: ValFatalError => fatalError
        case e: ValError               => e
        case result                    => context.valueMapper.toVal(result)
      }
    }
  }

  private def withLists(lists: List[(String, Val)], f: List[(String, ValList)] => Val)(implicit
      context: EvalContext
  ): Val = {
    lists
      .map { case (name, it) => name -> withList(it, list => list) }
      .find { case (_, value) => !value.isInstanceOf[ValList] } match {
      case Some(Tuple2(_, error: Val)) => error
      case None                        => f(lists.asInstanceOf[List[(String, ValList)]])
    }
  }

  private def withCartesianProduct(
      iterators: List[(String, Exp)],
      f: List[Map[String, Val]] => Val
  )(implicit context: EvalContext): Val = {
    withLists(
      iterators.map { case (name, it) => name -> eval(it) },
      lists => f(flattenAndZipLists(lists))
    )
  }

  private def flattenAndZipLists(lists: List[(String, ValList)]): List[Map[String, Val]] =
    lists match {
      case Nil                  => List()
      case (name, list) :: Nil  => list.items map (v => Map(name -> v)) // flatten
      case (name, list) :: tail =>
        for {
          v <- list.items; values <- flattenAndZipLists(tail)
        } yield values + (name -> v) // zip
    }

  private def filterList(list: List[Val], filter: Val => Val)(implicit
      context: EvalContext
  ): Val = {
    val conditionNotFulfilled = ValString("_")

    val withBooleanFilter = (list: List[Val]) =>
      mapEither[Val, Val](
        list,
        item =>
          (filter(item) match {
            case ValBoolean(true) => item
            case _                => conditionNotFulfilled
          }).toEither,
        items => ValList(items.filterNot(_ == conditionNotFulfilled))
      )

    // The filter function could return a boolean or a number. If it returns a number then we use
    // the number as the index for the list. Otherwise, the boolean function determine if the
    // condition is fulfilled for the given item.
    // Note that the code could look more elegant but we want to avoid unintended invocations of
    // the function because the invocations could be observed by the function provider (see #359).
    list.headOption
      .map(head =>
        withVal(
          filter(head),
          {
            case ValNumber(index)        => filterList(list, index)
            case ValBoolean(isFulFilled) =>
              withBooleanFilter(list.tail) match {
                case ValList(fulFilledItems) if isFulFilled => ValList(head :: fulFilledItems)
                case fulFilledItems: ValList                => fulFilledItems
                case error                                  => error
              }
            case _                       =>
              withBooleanFilter(list.tail) match {
                case ValList(fulFilledItems) => ValList(fulFilledItems)
                case error                   => error
              }
          }
        )
      )
      .getOrElse(
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

  private def filterContext(x: Val)(implicit context: EvalContext): EvalContext =
    x match {
      case ValContext(ctx: Context) => context.add("item" -> x).merge(ctx)
      case v                        => context.add("item" -> v)
    }

  private def ref(x: Val, names: List[String])(implicit context: EvalContext): Val =
    names match {
      case Nil     => x
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
              case Nil  => "The context is empty"
              case keys => s"Available keys: ${keys.map("'" + _ + "'").mkString(", ")}"
            }
            error(
              failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
              failureMessage = s"No context entry found with key '$key'. $detailedMessage"
            )
            ValNull
          case x: Val      => x
        }
      case ValList(list)   => ValList(list map (item => path(item, key)))
      case ValNull         =>
        error(
          failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
          failureMessage = s"No context entry found with key '$key'. The context is null"
        )
        ValNull
      case value           =>
        value.property(key).getOrElse {
          val propertyNames: String = value.propertyNames().map("'" + _ + "'").mkString(", ")
          error(
            failureType = EvaluationFailureType.NO_PROPERTY_FOUND,
            failureMessage =
              s"No property found with name '$key' of value '$value'. Available properties: $propertyNames"
          )
          ValNull
        }
    }

  private def invokeJavaFunction(
      className: String,
      methodName: String,
      arguments: List[String],
      paramValues: List[Val],
      valueMapper: ValueMapper
  )(implicit context: EvalContext): Val = {
    try {

      val clazz = JavaClassMapper.loadClass(className)

      val argTypes = arguments map JavaClassMapper.loadClass

      val method = clazz.getDeclaredMethod(methodName, argTypes: _*)

      val argJavaObjects = paramValues zip argTypes map { case (obj, clazz) =>
        JavaClassMapper.asJavaObject(obj, clazz)
      }

      val result = method.invoke(null, argJavaObjects: _*)

      valueMapper.toVal(result)

    } catch {
      case e: ClassNotFoundException =>
        error(
          EvaluationFailureType.FUNCTION_INVOCATION_FAILURE,
          s"Failed to load class '$className'"
        )
      case e: NoSuchMethodException  =>
        error(
          EvaluationFailureType.FUNCTION_INVOCATION_FAILURE,
          s"Failed to get method with name '$methodName' and arguments '$arguments' from class '$className'"
        )
      case _: Throwable              =>
        error(
          EvaluationFailureType.FUNCTION_INVOCATION_FAILURE,
          s"Failed to invoke method with name '$methodName' and arguments '$arguments' from class '$className'"
        )
    }
  }

  private def toRange(range: ConstRange)(implicit context: EvalContext): Val = {
    withValues(
      eval(range.start.value),
      eval(range.end.value),
      (startValue, endValue) => {
        if (isValidRange(startValue, endValue)) {
          ValRange(
            start = toRangeBoundary(range.start, startValue),
            end = toRangeBoundary(range.end, endValue)
          )
        } else {
          error(EvaluationFailureType.INVALID_TYPE, s"Invalid range definition '$range'")
        }
      }
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

  private def toRangeBoundary(boundary: ConstRangeBoundary, value: Val): RangeBoundary = {
    boundary match {
      case OpenConstRangeBoundary(_)   => OpenRangeBoundary(value)
      case ClosedConstRangeBoundary(_) => ClosedRangeBoundary(value)
    }
  }

}

object FeelInterpreter {

  val INPUT_VALUE_SYMBOL: String = "?"

}
