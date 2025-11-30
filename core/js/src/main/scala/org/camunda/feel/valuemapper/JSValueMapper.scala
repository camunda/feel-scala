package org.camunda.feel.valuemapper

import org.camunda.feel.context.Context
import org.camunda.feel.syntaxtree._

import scala.scalajs.js

class JSValueMapper extends ValueMapper {

  override def toVal(x: Any): Val = x match {
    case v: Val         => v
    case null           => ValNull
    case v: Boolean     => ValBoolean(v)
    case v: Int         => ValNumber(v)
    case v: Long        => ValNumber(v)
    case v: Float       => ValNumber(v.toDouble)
    case v: Double      => ValNumber(v)
    case v: BigDecimal  => ValNumber(v)
    case v: String      => ValString(v)
    case v: Seq[_]      => ValList(v.map(toVal))
    case v: Map[_, _]   =>
      ValContext(
        Context.StaticContext(
          v.map { case (key, value) => key.toString -> toVal(value) }
        )
      )
    case v: js.Array[_] =>
      ValList(v.toSeq.map(toVal))
    case v: js.Object   =>
      val dict = v.asInstanceOf[js.Dictionary[Any]]
      ValContext(
        Context.StaticContext(
          dict.toMap.map { case (key, value) => key -> toVal(value) }
        )
      )
    case Some(v)        => toVal(v)
    case None           => ValNull
    case ()             => ValNull
    case other          =>
      if (js.isUndefined(other)) ValNull
      else {
        throw new IllegalArgumentException(s"Unsupported type: ${x.getClass.getName}")
      }
  }

  override def unpackVal(value: Val): Any = {
    val jsAny: js.Any = value match {
      case ValNumber(value)            => value.toDouble
      case ValBoolean(value)           => value
      case ValString(value)            => value
      case ValDate(value)              => value.toString
      case ValLocalTime(value)         => value.toString
      case ValTime(value)              => value.toString
      case ValLocalDateTime(value)     => value.toString
      case ValDateTime(value)          => value.toString
      case ValYearMonthDuration(value) => value.toString
      case ValDayTimeDuration(value)   => value.toString
      case ValError(error)             => js.Dynamic.literal("error" -> error)
      case ValFatalError(error)        => js.Dynamic.literal("fatalError" -> error)
      case ValNull                     => null
      case ValFunction(_, invoke, _)   =>
        val jsFunc: js.Function1[js.Array[Any], Any] = { args =>
          val valArgs = args.toList.map(toVal)
          val result  = invoke(valArgs)
          result match {
            case v: Val => unpackVal(v)
            case other  => other
          }
        }
        jsFunc
      case ValContext(context)         =>
        val dict = js.Dictionary[Any]()
        context.variableProvider.getVariables.foreach { case (key, v) =>
          val unpacked = v match {
            case packed: Val => unpackVal(packed)
            case other       => other
          }
          dict(key) = unpacked
        }
        dict
      case ValList(itemsAsSeq)         =>
        js.Array(itemsAsSeq.map(unpackVal): _*)
      case ValRange(start, endValue)   =>
        js.Dynamic.literal(
          "start" -> unpackVal(start.value).asInstanceOf[js.Any],
          "end"   -> unpackVal(endValue.value).asInstanceOf[js.Any]
        )
    }
    jsAny
  }
}
