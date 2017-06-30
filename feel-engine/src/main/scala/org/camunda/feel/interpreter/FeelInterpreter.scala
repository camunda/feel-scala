package org.camunda.feel.interpreter

import org.camunda.feel._
import org.camunda.feel.parser._
import com.sun.org.apache.xerces.internal.impl.dv.xs.DayTimeDurationDV
import java.time.Duration
import java.time.Period

/**
 * @author Philipp Ossler
 */
class FeelInterpreter {

  def eval(expression: Exp)(implicit context: Context = Context.empty): Val = expression match {

  	// literals
    case ConstNumber(x) => ValNumber(x)
    case ConstBool(b) => ValBoolean(b)
    case ConstString(s) => ValString(s)
    case ConstDate(d) => ValDate(d)
    case ConstTime(t) => ValTime(t)
    case ConstDateTime(dt) => ValDateTime(dt)
    case ConstYearMonthDuration(d) => ValYearMonthDuration(d)
    case ConstDayTimeDuration(d) => ValDayTimeDuration(d)
    case ConstNull => ValNull
    case ConstList(items) => ValList(items.map( item => withVal(eval(item), x => x)) )
    case ConstContext((k,v) :: entries) => ValContext( entries.foldLeft( evalContextEntry(k,v) ){ (ctx, entry) => ctx ++ evalContextEntry(entry._1, entry._2)(context ++ ctx.toMap) } )

    // simple unary tests
    case InputEqualTo(x) => unaryOpAny(eval(x), _ == _, ValBoolean)
    case InputLessThan(x) => unaryOp(eval(x), _ < _, ValBoolean)
    case InputLessOrEqual(x) => unaryOp(eval(x), _ <= _, ValBoolean)
    case InputGreaterThan(x) => unaryOp(eval(x), _ > _, ValBoolean)
    case InputGreaterOrEqual(x) => unaryOp(eval(x), _ >= _, ValBoolean)
    case interval @ Interval(start, end) => unaryOpDual(eval(start.value), eval(end.value), isInInterval(interval), ValBoolean)

    // arithmetic operations
    case Addition(x,y) => addOp(eval(x), eval(y))
    case Subtraction(x,y) => subOp(eval(x), eval(y))
    case Multiplication(x,y) => mulOp(eval(x), eval(y))
    case Division(x,y) => divOp(eval(x), eval(y))
    case Exponentiation(x,y) => dualNumericOp(eval(x), eval(y), _ pow _.toInt, ValNumber)
    case ArithmeticNegation(x) => withNumber(eval(x), x => ValNumber(-x))

    // dual comparators
    case Equal(x,y) => dualOpAny(eval(x), eval(y), _ == _, ValBoolean)
    case LessThan(x,y) => dualOp(eval(x), eval(y), _ < _, ValBoolean)
    case LessOrEqual(x,y) => dualOp(eval(x), eval(y), _ <= _, ValBoolean)
    case GreaterThan(x,y) => dualOp(eval(x), eval(y), _ > _, ValBoolean)
    case GreaterOrEqual(x,y) => dualOp(eval(x), eval(y), _ >= _, ValBoolean)

    // combinators
    case AtLeastOne(xs) => atLeastOne(xs, ValBoolean)
    case Not(x) => withBooleanOrNull(eval(x), x => ValBoolean(!x))
    case Disjunction(x,y) => atLeastOne(x :: y :: Nil, ValBoolean)
    case Conjunction(x,y) => all(x :: y :: Nil, ValBoolean)

    // control structures
    case If(condition, then, otherwise) => withBoolean(eval(condition), isMet => if(isMet) { eval(then) } else { eval(otherwise) } )
    case In(x, test) => withVal(eval(x), x => eval(test)(context + (context.inputKey -> x)) )
    case InstanceOf(x, typeName) => withVal(eval(x), x => withType(x, t => ValBoolean(t == typeName)))

    // context
    case Ref(names) => ref(context(names.head), names.tail)
    case PathExpression(exp, key) => withVal(eval(exp), v => path(v, key))

    // list
    case SomeItem(iterators, condition) => withCartesianProduct(iterators, p => atLeastOne( p.map(vars => () => eval(condition)(context ++ vars)), ValBoolean ))
    case EveryItem(iterators, condition) => withCartesianProduct(iterators, p => all( p.map(vars => () => eval(condition)(context ++ vars)), ValBoolean))
    case For(iterators, exp) => withCartesianProduct(iterators, p => ValList( p.map(vars => eval(exp)(context ++ vars) )) )
    case Filter(list, filter) => withList(eval(list), l => filterList(l.items, item => eval(filter)(filterContext(item)) ))

    // functions
    case FunctionInvocation(name, params) => withFunction(context.function(name, params.size), f => withParameters(params, f, params => f.invoke(params) ))
    case QualifiedFunctionInvocation(path, params) => withFunction(eval(path), f => withParameters(params, f, params => f.invoke(params) ))
    case FunctionDefinition(params, body) => ValFunction(params, paramValues => body match {
      case JavaFunctionInvocation(className, methodName, arguments) => invokeJavaFunction(className, methodName, arguments, paramValues)
      case _ => eval(body)(context ++ (params zip paramValues).toMap)
    })

    // unsupported expression
    case exp => ValError(s"unsupported expression '$exp'")

  }

  private def error(x: Val, message: String) = x match {
    case e: ValError => e
    case _ => ValError(message)
  }

  private def unaryOpAny(x: Val, c: (Any, Any) => Boolean, f: Boolean => Val)(implicit context: Context): Val =
    if (input == ValNull || x == ValNull) {
      f(c(input, x))
    } else {
      withVal(input, _ match {
        case ValNumber(i) => withNumber(x, x => f(c(i, x)))
        case ValBoolean(i) => withBoolean(x, x => f(c(i, x)))
        case ValString(i) => withString(x, x => f(c(i, x)))
        case ValDate(i) => withDate(x, x => f(c(i, x)))
        case ValTime(i) => withTime(x, x => f(c(i, x)))
        case ValDateTime(i) => withDateTime(x, x => f(c(i, x)))
        case ValYearMonthDuration(i) => withYearMonthDuration(x, x => f(c(i,x)))
        case ValDayTimeDuration(i) => withDayTimeDuration(x, x => f(c(i,x)))
        case _ => ValError(s"expected Number, Boolean, String, Date, Time or Duration but found '$input'")
      })
  	}

  private def unaryOp(x: Val, c: (Compareable[_], Compareable[_]) => Boolean, f: Boolean => Val)(implicit context: Context): Val =
    withVal(input, _ match {
      case ValNumber(i) => withNumber(x, x => f(c(i, x)))
      case ValDate(i) => withDate(x, x => f(c(i, x)))
      case ValTime(i) => withTime(x, x => f(c(i, x)))
      case ValYearMonthDuration(i) => withYearMonthDuration(x, x => f(c(i,x)))
      case ValDayTimeDuration(i) => withDayTimeDuration(x, x => f(c(i,x)))
      case _ => ValError(s"expected Number, Date, Time or Duration but found '$input'")
    })

  private def unaryOpDual(x: Val, y: Val, c: (Compareable[_], Compareable[_], Compareable[_]) => Boolean, f: Boolean => Val)(implicit context: Context): Val =
    withVal(input, _ match {
      case ValNumber(i) => withNumbers(x, y, (x, y) => f(c(i, x, y)))
      case ValDate(i) => withDates(x, y, (x, y) => f(c(i, x, y)))
      case ValTime(i) => withTimes(x, y, (x,y) => f(c(i, x, y)))
      case ValDateTime(i) => withDateTimes(x, y, (x,y) => f(c(i, x, y)))
      case ValYearMonthDuration(i) => withYearMonthDurations(x, y, (x,y) => f(c(i, x, y)))
      case ValDayTimeDuration(i) => withDayTimeDurations(x, y, (x,y) => f(c(i, x, y)))
      case _ => ValError(s"expected Number, Date, Time or Duration but found '$input'")
    })

  private def withNumbers(x: Val, y: Val, f: (Number, Number) => Val): Val =
    withNumber(x, x => {
      withNumber(y, y => {
        f(x, y)
      })
    })

  private def withNumber(x: Val, f: Number => Val): Val = x match {
    case ValNumber(x) => f(x)
    case _ => error(x, s"expected Number but found '$x'")
  }

  private def withBoolean(x: Val, f: Boolean => Val): Val = x match {
    case ValBoolean(x) => f(x)
    case _ => error(x, s"expected Boolean but found '$x'")
  }

  private def withBooleanOrNull(x: Val, f: Boolean => Val): Val = x match {
    case ValBoolean(x) => f(x)
    case _ => ValNull
  }

  private def withString(x: Val, f: String => Val): Val = x match {
    case ValString(x) => f(x)
    case _ => error(x, s"expected String but found '$x'")
  }

  private def withDates(x: Val, y: Val, f: (Date, Date) => Val): Val =
    withDate(x, x => {
      withDate(y, y => {
        f(x, y)
      })
    })

  private def withDate(x: Val, f: Date => Val): Val = x match {
    case ValDate(x) => f(x)
    case _ => error(x, s"expected Date but found '$x'")
  }

  private def withTimes(x: Val, y: Val, f: (Time, Time) => Val): Val =
    withTime(x, x => {
      withTime(y, y => {
        f(x, y)
      })
    })

  private def withTime(x: Val, f: Time => Val): Val = x match {
    case ValTime(x) => f(x)
    case _ => error(x, s"expect Time but found '$x'")
  }

  private def withDateTimes(x: Val, y: Val, f: (DateTime, DateTime) => Val): Val =
    withDateTime(x, x => {
      withDateTime(y, y => {
        f(x, y)
      })
    })

  private def withDateTime(x: Val, f: DateTime => Val): Val = x match {
    case ValDateTime(x) => f(x)
    case _ => error(x, s"expect Date Time but found '$x'")
  }

  private def withYearMonthDurations(x: Val, y: Val, f: (YearMonthDuration, YearMonthDuration) => Val): Val =
    withYearMonthDuration(x, x => {
      withYearMonthDuration(y, y => {
        f(x, y)
      })
    })

  private def withDayTimeDurations(x: Val, y: Val, f: (DayTimeDuration, DayTimeDuration) => Val): Val =
    withDayTimeDuration(x, x => {
      withDayTimeDuration(y, y => {
        f(x, y)
      })
    })

  private def withYearMonthDuration(x: Val, f: YearMonthDuration => Val): Val = x match {
    case ValYearMonthDuration(x) => f(x)
    case _ => error(x, s"expect Year-Month-Duration but found '$x'")
  }

  private def withDayTimeDuration(x: Val, f: DayTimeDuration => Val): Val = x match {
    case ValDayTimeDuration(x) => f(x)
    case _ => error(x, s"expect Day-Time-Duration but found '$x'")
  }

  private def withVal(x: Val, f: Val => Val): Val = x match {
    case e: ValError => e
    case _ => f(x)
  }

  private def isInInterval(interval: Interval): (Compareable[_], Compareable[_], Compareable[_]) => Boolean =
    (i, x, y) => {
      val inStart: Boolean = interval.start match {
        case OpenIntervalBoundary(_) => i > x
        case ClosedIntervalBoundary(_) => i >= x
      }
      val inEnd = interval.end match {
        case OpenIntervalBoundary(_) => i < y
        case ClosedIntervalBoundary(_) => i <= y
      }
      inStart && inEnd
    }

  private def atLeastOne(xs: List[Exp], f: Boolean => Val)(implicit context: Context): Val =
  	atLeastOne( xs map( x => () => eval(x)), f)

  private def atLeastOne(xs: List[() => Val], f: Boolean => Val): Val = xs match {
    case Nil => f(false)
    case x :: xs => x() match {
      case ValBoolean(true) => f(true)
      case ValBoolean(false)  => atLeastOne(xs, f)
      case other => atLeastOne(xs, f) match {
        case ValBoolean(true) => f(true)
        case _ => ValNull
      }
    }
  }

  private def all(xs: List[Exp], f: Boolean => Val)(implicit context: Context): Val =
		  all( xs map ( x => () => eval(x)), f)

  private def all(xs: List[() => Val], f: Boolean => Val): Val = xs match {
    case Nil => f(true)
    case x :: xs => x() match {
      case ValBoolean(false) => f(false)
      case ValBoolean(true)  => all(xs, f)
      case other => all(xs, f) match {
        case ValBoolean(false) => f(false)
        case _ => ValNull
      }
    }
  }

  private def input(implicit context: Context): Val = context.input

  private def dualNumericOp(x: Val, y: Val, op: (Number,Number) => Number, f: Number => Val)(implicit context: Context): Val =
    x match {
      case ValNumber(x) => withNumber(y, y => f(op(x,y)))
      case _ => error(x, s"expected Number but found '$x'")
    }

  private def dualOpAny(x: Val, y: Val, c: (Any, Any) => Boolean, f: Boolean => Val)(implicit context: Context): Val =
    if (x == ValNull || y == ValNull) {
      f(c(x, y))
    } else {
      x match {
        case ValNumber(x) => withNumber(y, y => f(c(x, y)))
        case ValBoolean(x) => withBoolean(y, y => f(c(x, y)))
        case ValString(x) => withString(y, y => f(c(x, y)))
        case ValDate(x) => withDate(y, y => f(c(x, y)))
        case ValTime(x) => withTime(y, y => f(c(x, y)))
        case ValDateTime(x) => withDateTime(y, y => f(c(x, y)))
        case ValYearMonthDuration(x) => withYearMonthDuration(y, y => f(c(x,y)))
        case ValDayTimeDuration(x) => withDayTimeDuration(y, y => f(c(x,y)))
        case _ => error(x, s"expected Number, Boolean, String, Date, Time or Duration but found '$x'")
      }
    }

  private def dualOp(x: Val, y: Val, c: (Compareable[_], Compareable[_]) => Boolean, f: Boolean => Val)(implicit context: Context): Val =
    x match {
      case ValNumber(x) => withNumber(y, y => f(c(x, y)))
      case ValDate(x) => withDate(y, y => f(c(x, y)))
      case ValTime(x) => withTime(y, y => f(c(x, y)))
      case ValDateTime(x) => withDateTime(y, y => f(c(x, y)))
      case ValYearMonthDuration(x) => withYearMonthDuration(y, y => f(c(x,y)))
      case ValDayTimeDuration(x) => withDayTimeDuration(y, y => f(c(x,y)))
      case _ => error(x, s"expected Number, Date, Time or Duration but found '$x'")
    }

  private def addOp(x: Val, y: Val): Val = x match {
  	case ValNumber(x) => withNumber(y, y => ValNumber(x + y))
  	case ValString(x) => withString(y, y => ValString(x + y))
  	case ValTime(x) => withDayTimeDuration(y, y => ValTime( x.plus(y) ))
  	case ValDateTime(x) => y match {
  		case ValYearMonthDuration(y) => ValDateTime( x.plus(y) )
  		case ValDayTimeDuration(y) => ValDateTime( x.plus(y) )
  		case _ => error(y, s"expect Year-Month-/Day-Time-Duration but found '$x'")
  	}
  	case ValYearMonthDuration(x) => y match {
  		case ValYearMonthDuration(y) => ValYearMonthDuration( x.plus(y) )
  		case ValDateTime(y) => ValDateTime( y.plus(x) )
  		case _ => error(y, s"expect Date-Time, or Year-Month-Duration but found '$x'")
  	}
  	case ValDayTimeDuration(x) => y match {
  		case ValDayTimeDuration(y) => ValDayTimeDuration( x.plus(y) )
  		case ValDateTime(y) => ValDateTime( y.plus(x) )
  		case ValTime(y) => ValTime( y.plus(x) )
  		case _ => error(y, s"expect Date-Time, Time, or Day-Time-Duration but found '$x'")
  	}
  	case _ => error(x, s"expected Number, String, Date, Time or Duration but found '$x'")
  }

  private def subOp(x: Val, y: Val): Val = x match {
  	case ValNumber(x) => withNumber(y, y => ValNumber(x - y))
  	case ValTime(x) => y match {
  		case ValTime(y) => ValDayTimeDuration( Duration.between(y, x) )
  		case ValDayTimeDuration(y) => ValTime( x.minus(y) )
  		case _ => error(y, s"expect Time, or Day-Time-Duration but found '$x'")
  	}
  	case ValDateTime(x) => y match {
  		case ValDateTime(y) => ValDayTimeDuration( Duration.between(y, x) )
  		case ValYearMonthDuration(y) => ValDateTime( x.minus(y) )
  		case ValDayTimeDuration(y) => ValDateTime( x.minus(y) )
  		case _ => error(y, s"expect Time, or Year-Month-/Day-Time-Duration but found '$x'")
  	}
  	case ValYearMonthDuration(x) => withYearMonthDuration(y, y => ValYearMonthDuration( x.minus(y).normalized ))
  	case ValDayTimeDuration(x) => withDayTimeDuration(y, y => ValDayTimeDuration( x.minus(y) ))
  	case _ => error(x, s"expected Number, Date, Time or Duration but found '$x'")
  }

  private def mulOp(x: Val, y: Val): Val = x match {
  	case ValNumber(x) => y match {
  		case ValNumber(y) => ValNumber( x * y )
  		case ValYearMonthDuration(y) => ValYearMonthDuration( y.multipliedBy(x.intValue).normalized )
  		case ValDayTimeDuration(y) => ValDayTimeDuration( y.multipliedBy(x.intValue) )
  		case _ => error(y, s"expect Number, or Year-Month-/Day-Time-Duration but found '$x'")
  	}
  	case ValYearMonthDuration(x) => withNumber(y, y => ValYearMonthDuration( x.multipliedBy(y.intValue).normalized ))
  	case ValDayTimeDuration(x) => withNumber(y, y => ValDayTimeDuration( x.multipliedBy(y.intValue) ))
  	case _ => error(x, s"expected Number, or Duration but found '$x'")
  }

  private def divOp(x: Val, y: Val): Val = x match {
  	case ValNumber(x) => withNumber(y, y => ValNumber(x / y))
  	case ValYearMonthDuration(x) => withNumber(y, y => ValYearMonthDuration( Period.ofMonths((x.toTotalMonths() / y).intValue).normalized() ))
  	case ValDayTimeDuration(x) => withNumber(y, y => ValDayTimeDuration( Duration.ofMillis((x.toMillis() / y).intValue) ))
  	case _ => error(x, s"expected Number, or Duration but found '$x'")
  }

  private def withFunction(x: Val, f: ValFunction => Val): Val = x match {
    case x: ValFunction => f(x)
    case _ => error(x, s"expect Function but found '$x'")
  }

  private def withParameters(params: FunctionParameters, function: ValFunction, f: List[Val] => Val)(implicit context: Context): Val = {
    val paramList = params match {
      case PositionalFunctionParameters(params) => {

        if (params.size != function.params.size) {
          return ValError(s"expected ${function.params.size} parameters but found ${params.size}")
        }

        params map eval
      }
      case NamedFunctionParameters(params) => {

        val missingParameters = function.params.filter( p => !params.contains(p) )
        if (!missingParameters.isEmpty) {
          return ValError(s"expected parameter '${missingParameters.head}' but not found")
        }

        val unknownParameters = params.filter( p => !function.params.exists(_ == p._1) )
        if (!unknownParameters.isEmpty) {
          return ValError(s"unexpected parameter '${unknownParameters.head._1}'")
        }

       function.params map ( p => eval(params(p)) )
      }
    }

    if (function.requireInputVariable) {
      f(context.input :: paramList)
    } else {
      f(paramList)
    }
  }

  private def withType(x: Val, f: String => ValBoolean): Val = x match {
    case ValNumber(_) => f("number")
    case ValBoolean(_) => f("boolean")
    case ValString(_) => f("string")
    case ValDate(_) => f("date")
    case ValTime(_) => f("time")
    case ValDateTime(_) => f("date time")
    case ValYearMonthDuration(_) => f("year-month-duration")
    case ValDayTimeDuration(_) => f("day-time-duration")
    case ValNull => f("null")
    case ValList(_) => f("list")
    case ValContext(_) => f("context")
    case ValFunction(_, _, _) => f("function")
    case _ => error(x, s"unexpected type '${x.getClass.getName}'")
  }

  private def withList(x: Val, f: ValList => Val): Val = x match {
    case x: ValList => f(x)
    case _ => error(x, s"expect List but found '$x'")
  }

  private def withLists(lists: List[(String, Val)], f: List[(String, ValList)] => Val)(implicit context: Context): Val = {
    lists
      .map { case (name, it) => name -> withList(it, list => list)  }
      .find( _._2.isInstanceOf[ValError]) match {
        case Some(Tuple2(_, e: Val)) => e
        case None => f( lists.asInstanceOf[List[(String, ValList)]] )
      }
  }

  private def withCartesianProduct(iterators: List[(String, Exp)], f: List[Map[String, Val]] => Val)(implicit context: Context): Val = withLists(
      iterators.map{ case (name, it) => name -> eval(it) }, lists =>
        f( flattenAndZipLists(lists) ))

  private def flattenAndZipLists(lists: List[(String, ValList)]): List[Map[String, Val]] = lists match {
    case Nil => List()
    case (name, list) :: Nil => list.items map( v => Map(name -> v ) ) // flatten
    case (name, list) :: tail => for { v <- list.items; values <- flattenAndZipLists(tail) } yield values + (name -> v) // zip
  }

  private def filterList(list: List[Val], filter: Val => Val): Val = list match {
    case Nil => ValList(List())
    case x :: xs => withBoolean(filter(x), _ match {
      case false => filterList(xs, filter)
      case true => withList(filterList(xs, filter), l => ValList(x :: l.items) )
    })
  }

  private def withContext(x: Val, f: ValContext => Val): Val = x match {
    case x: ValContext => f(x)
    case _ => error(x, s"expect Context but found '$x'")
  }

  private def filterContext(x: Val)(implicit context: Context): Context = x match {
    case ValContext(ctx) => context ++ ctx.toMap + ("item" -> x)
    case v => context + ("item" -> v)
  }

  private def ref(x: Val, names: List[String])(implicit context: Context): Val = names match {
    case Nil => x
    case n :: ns => withContext(x, { case ValContext(ctx) =>
      ctx.toMap.get(n) match {
        case Some(entry) => ref(entry, ns)
        case None => ValError(s"context contains no entry with key '$n'")
      }
    })
  }

  private def path(v: Val, key: String): Val = v match {
    case ValContext(ctx) => ctx.toMap.get(key) match {
      case Some(entry) =>  entry
      case None => ValError(s"context contains no entry with key '$key'")
    }
    case ValList(list) => ValList( list map (item => path(item, key)) )
    case e => error(e, s"expected Context or List of Contextes but found '$e'")
  }

  private def evalContextEntry(key: String, exp: Exp)(implicit context: Context): List[(String, Val)] =
    List( key -> withVal(eval(exp), value => value))

  private def invokeJavaFunction(className: String, methodName: String, arguments: List[String], paramValues: List[Val]): Val = {
    try {

	    val clazz = JavaClassMapper.loadClass(className)

	    val argTypes = arguments map JavaClassMapper.loadClass

	    val method = clazz.getDeclaredMethod(methodName, argTypes: _*)

	    val argValues = paramValues map ValueMapper.unpackVal
	    val argJavaObjects = argValues zip argTypes map { case (obj,clazz) => JavaClassMapper.asJavaObject(obj, clazz) }

	    val result = method.invoke(null, argJavaObjects: _*)

	    ValueMapper.toVal(result)

    } catch {
    	case e: ClassNotFoundException => ValError(s"fail to load class '$className'")
    	case e: NoSuchMethodException => ValError(s"fail to get method with name '$methodName' and arguments '$arguments' from class '$className'")
    	case _: Throwable => ValError(s"fail to invoke method with name '$methodName' and arguments '$arguments' from class '$className'")
    }
  }

}
