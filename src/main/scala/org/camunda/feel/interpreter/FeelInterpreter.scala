package org.camunda.feel.interpreter

import org.camunda.feel._
import org.camunda.feel.parser._

/**
 * @author Philipp Ossler
 */
class FeelInterpreter {

  def eval(expression: Exp)(implicit context: Context = Context()): Val = expression match {
    // literals
    case ConstNumber(x) => ValNumber(x)
    case ConstBool(b) => ValBoolean(b)
    case ConstString(s) => ValString(s)
    case ConstDate(d) => ValDate(d)
    case ConstTime(t) => ValTime(t)
    case ConstDuration(d) => ValDuration(d)
    case ConstNull => ValNull
    case ConstList(items) => ValList(items.map( item => withVal(eval(item), x => x)) )
    case ConstContext(entries) => ValContext(entries.map( entry => entry._1 -> withVal(eval(entry._2), x => x) ))
    // simple unary tests
    case InputEqualTo(x) => unaryOpAny(eval(x), _ == _, ValBoolean)
    case InputLessThan(x) => unaryOp(eval(x), _ < _, ValBoolean)
    case InputLessOrEqual(x) => unaryOp(eval(x), _ <= _, ValBoolean)
    case InputGreaterThan(x) => unaryOp(eval(x), _ > _, ValBoolean)
    case InputGreaterOrEqual(x) => unaryOp(eval(x), _ >= _, ValBoolean)
    case interval @ Interval(start, end) => unaryOpDual(eval(start.value), eval(end.value), isInInterval(interval), ValBoolean)
    // arithmetic operations
    // TODO support duration, date and time for add. and sub.
    case Addition(x,y) => dualNumericOp(eval(x), eval(y), _ + _, ValNumber)
    case Subtraction(x,y) => dualNumericOp(eval(x), eval(y), _ - _, ValNumber)
    case Multiplication(x,y) => dualNumericOp(eval(x), eval(y), _ * _, ValNumber)
    case Division(x,y) => dualNumericOp(eval(x), eval(y), _ / _, ValNumber)
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
    case Not(x) => withBoolean(eval(x), x => ValBoolean(!x))
    case Disjunction(x,y) => atLeastOne(x :: y :: Nil, ValBoolean)
    case Conjunction(x,y) => all(x :: y :: Nil, ValBoolean)
    // control structures
    case If(condition, then, otherwise) => withBoolean(eval(condition), isMet => if(isMet) { eval(then) } else { eval(otherwise) } ) 
    case In(x, test) => withVal(eval(x), x => eval(test)(context + (Context.inputKey -> x)) )
    case InstanceOf(x, typeName) => withVal(eval(x), x => withType(x, t => ValBoolean(t == typeName)))
    // context
    case Ref(name) => context(name)
    // list
    case SomeItem(name, list, condition) => withList(eval(list), l => atLeastOne(l.items map( item => () => eval(condition)(context + (name -> item) )), ValBoolean))
    case EveryItem(name, list, condition) => withList(eval(list), l => all(l.items map( item => () => eval(condition)(context + (name -> item) )), ValBoolean))
    case For(iterators, exp) => withLists( iterators.map{ case (name, it) => name -> eval(it) }, lists => ValList( flattenAndZipLists(lists).map(vars => eval(exp)(context ++ vars)) ) )
    case Filter(list, filter) => withList(eval(list), l => filterList(l.items, item => eval(filter)(context + ("item" -> item)) ))
    // functions
    case FunctionInvocation(name, params) => withFunction(context(name), f => withParameters(params, f, params => f.invoke(params) ))
    case FunctionDefinition(params, body) => ValFunction(params, paramValues => eval(body)(context ++ (params zip paramValues).toMap))
    // unsupported expression
    case exp => ValError(s"unsupported expression '$exp'")
  }

  private def unaryOpAny(x: Val, c: (Any, Any) => Boolean, f: Boolean => Val)(implicit context: Context): Val =
    withVal(input, _ match {
      case ValNumber(i) => withNumber(x, x => f(c(i, x)))
      case ValBoolean(i) => withBoolean(x, x => f(c(i, x)))
      case ValString(i) => withString(x, x => f(c(i, x)))
      case ValDate(i) => withDate(x, x => f(c(i, x)))
      case ValTime(i) => withTime(x, x => f(c(i, x)))
      case ValDuration(i) => withDuration(x, x => f(c(i,x)))
      case _ => ValError(s"expected Number, Boolean, String, Date, Time or Duration but found '$input'")
    })

  private def unaryOp(x: Val, c: (Compareable[_], Compareable[_]) => Boolean, f: Boolean => Val)(implicit context: Context): Val =
    withVal(input, _ match {
      case ValNumber(i) => withNumber(x, x => f(c(i, x)))
      case ValDate(i) => withDate(x, x => f(c(i, x)))
      case ValTime(i) => withTime(x, x => f(c(i, x)))
      case ValDuration(i) => withDuration(x, x => f(c(i, x)))
      case _ => ValError(s"expected Number, Date, Time or Duration but found '$input'")
    })

  private def unaryOpDual(x: Val, y: Val, c: (Compareable[_], Compareable[_], Compareable[_]) => Boolean, f: Boolean => Val)(implicit context: Context): Val =
    withVal(input, _ match {
      case ValNumber(i) => withNumbers(x, y, (x, y) => f(c(i, x, y)))
      case ValDate(i) => withDates(x, y, (x, y) => f(c(i, x, y)))
      case ValTime(i) => withTimes(x, y, (x,y) => f(c(i, x, y)))
      case ValDuration(i) => withDurations(x, y, (x,y) => f(c(i, x, y)))
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
    case _ => ValError(s"expected Number but found '$x'")
  }

  private def withBoolean(x: Val, f: Boolean => Val): Val = x match {
    case ValBoolean(x) => f(x)
    case _ => ValError(s"expected Boolean but found '$x'")
  }

  private def withString(x: Val, f: String => Val): Val = x match {
    case ValString(x) => f(x)
    case _ => ValError(s"expected String but found '$x'")
  }
  
  private def withDates(x: Val, y: Val, f: (Date, Date) => Val): Val =
    withDate(x, x => {
      withDate(y, y => {
        f(x, y)
      })
    })

  private def withDate(x: Val, f: Date => Val): Val = x match {
    case ValDate(x) => f(x)
    case _ => ValError(s"expected Date but found '$x'")
  }

  private def withTimes(x: Val, y: Val, f: (Time, Time) => Val): Val =
    withTime(x, x => {
      withTime(y, y => {
        f(x, y)
      })
    })
  
  private def withTime(x: Val, f: Time => Val): Val = x match {
    case ValTime(x) => f(x)
    case _ => ValError(s"expect Time but found '$x'")
  }
  
  private def withDurations(x: Val, y: Val, f: (Duration, Duration) => Val): Val =
    withDuration(x, x => {
      withDuration(y, y => {
        f(x, y)
      })
    })

  private def withDuration(x: Val, f: Duration => Val): Val = x match {
    case ValDuration(x) => f(x)
    case _ => ValError(s"expect Duration but found '$x'")
  }

  private def withVal(x: Val, f: Val => Val): Val = x match {
    case ValError(_) => ValError(s"expected value but found '$x'")
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
    case x :: xs => withBoolean(x(), _ match {
      case true => f(true)
      case false => atLeastOne(xs, f)
    })
  }
  
  private def all(xs: List[Exp], f: Boolean => Val)(implicit context: Context): Val =
		  all( xs map ( x => () => eval(x)), f)
  
  private def all(xs: List[() => Val], f: Boolean => Val): Val = xs match {
    case Nil => f(true)
    case x :: xs => withBoolean(x(), _ match {
      case false => f(false)
      case true => all(xs, f)
    })
  }

  private def input(implicit context: Context): Val = context.input
  
  private def dualNumericOp(x: Val, y: Val, op: (Number,Number) => Number, f: Number => Val)(implicit context: Context): Val =
    x match {
      case ValNumber(x) => withNumber(y, y => f(op(x,y)))
      case _ => ValError(s"expected Number but found '$x'")
    }
  
  private def dualOpAny(x: Val, y: Val, c: (Any, Any) => Boolean, f: Boolean => Val)(implicit context: Context): Val =
    x match {
      case ValNumber(x) => withNumber(y, y => f(c(x, y)))
      case ValBoolean(x) => withBoolean(y, y => f(c(x, y)))
      case ValString(x) => withString(y, y => f(c(x, y)))
      case ValDate(x) => withDate(y, y => f(c(x, y)))
      case ValTime(x) => withTime(y, y => f(c(x, y)))
      case ValDuration(x) => withDuration(y, y => f(c(x,y)))
      case _ => ValError(s"expected Number, Boolean, String, Date, Time or Duration but found '$x'")
    }
  
  private def dualOp(x: Val, y: Val, c: (Compareable[_], Compareable[_]) => Boolean, f: Boolean => Val)(implicit context: Context): Val =
    x match {
      case ValNumber(x) => withNumber(y, y => f(c(x, y)))
      case ValDate(x) => withDate(y, y => f(c(x, y)))
      case ValTime(x) => withTime(y, y => f(c(x, y)))
      case ValDuration(x) => withDuration(y, y => f(c(x,y)))
      case _ => ValError(s"expected Number, Date, Time or Duration but found '$x'")
    }
  
  private def withFunction(x: Val, f: ValFunction => Val): Val = x match {
    case x: ValFunction => f(x)
    case _ => ValError(s"expect Function but found '$x'")
  }
  
  private def withParameters(params: FunctionParameters, function: ValFunction, f: List[Val] => Val): Val = params match {
    case PositionalFunctionParameters(params) => {
      
      if (params.size != function.params.size) {
        return ValError(s"expected ${function.params.size} parameters but found ${params.size}")
      } 
      
      f(params map eval)
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
              
      val paramList = function.params map ( p => eval(params(p)) )
      
      f(paramList)
    }
  }
  
  private def withType(x: Val, f: String => ValBoolean): Val = x match {
    case ValNumber(_) => f("number")
    case ValBoolean(_) => f("boolean")
    case ValString(_) => f("string")
    case ValDate(_) => f("date")
    case ValTime(_) => f("time")
    case ValDuration(_) => f("duration")
    case ValNull => f("null")
    case ValList(_) => f("list")
    case ValContext(_) => f("context")
    case ValFunction(_, _) => f("function")
    case _ => ValError(s"unexpected type '${x.getClass.getName}'")
  }
  
  private def withList(x: Val, f: ValList => Val): Val = x match {
    case x: ValList => f(x)
    case _ => ValError(s"expect List but found '$x'")
  }
      
  private def withLists(lists: List[(String, Val)], f: List[(String, ValList)] => Val)(implicit context: Context): Val = {
    lists
      .map { case (name, it) => name -> withList(it, list => list)  }
      .find( _._2.isInstanceOf[ValError]) match {
        case Some(Tuple2(_, e: Val)) => e
        case None => f( lists.asInstanceOf[List[(String, ValList)]] )
      }
  }
  
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
  
}
