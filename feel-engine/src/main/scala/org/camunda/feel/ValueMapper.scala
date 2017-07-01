package org.camunda.feel

import org.camunda.feel._
import org.camunda.feel.interpreter._
import java.time.LocalDate
import java.time.ZoneId
import java.time.LocalTime
import java.time.LocalDateTime
import java.lang.reflect.Method
import java.lang.reflect.Field
import java.time.Duration
import java.time.Period
import scala.collection.JavaConverters._

/**
 * @author Philipp Ossler
 * @author Falko Menge
 */
object ValueMapper {
  
  def toVal(x: Any): Val = x match {
    case x: Val => x
    case null => ValNull
    // scala types
    case x: Int => ValNumber(x)
    case x: Long => ValNumber(x)
    case x: Float => ValNumber(x)
    case x: Double => ValNumber(x)
    case x: BigDecimal => ValNumber(x)
    case x: Boolean => ValBoolean(x)
    case x: String => ValString(x)
    case x: Date => ValDate(x)
    case x: Time => ValTime(x)
    case x: DateTime => ValDateTime(x)
    case x: YearMonthDuration => ValYearMonthDuration(x)
    case x: DayTimeDuration => ValDayTimeDuration(x)
    case x: List[_] => ValList( x map toVal )
    case x: Map[_,_] => ValContext( x map { case (key, value) => key.toString -> toVal(value)} toList)
    case Some(x) => toVal(x)
    case None => ValNull
    case x: Enumeration$Val => ValString(x.toString)
    // extended java types
    case x: java.math.BigDecimal => ValNumber(x)
    case x: java.util.Date => ValDateTime(x.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
    case x: java.util.List[_] => ValList( x.asScala.toList map toVal )
    case x: java.util.Map[_,_] => ValContext( x.asScala map { case (key, value) => key.toString -> toVal(value)} toList)
    case x: java.lang.Enum[_] => ValString(x.name)
    // joda-time
    case x: org.joda.time.LocalDate => ValDate(LocalDate.of(x.getYear, x.getMonthOfYear, x.getDayOfMonth))
    case x: org.joda.time.LocalTime => ValTime(LocalTime.of(x.getHourOfDay, x.getMinuteOfHour, x.getSecondOfMinute))
    case x: org.joda.time.LocalDateTime => ValDateTime(LocalDateTime.of(x.getYear, x.getMonthOfYear, x.getDayOfMonth, x.getHourOfDay, x.getMinuteOfHour, x.getSecondOfMinute))
    case x: org.joda.time.Duration => ValDayTimeDuration( Duration.ofMillis( x.getMillis ) )
    case x: org.joda.time.Period => ValYearMonthDuration( Period.of(x.getYears, x.getMonths, 0) )
    // other objects
    case x: Throwable => ValError(x.getMessage)
    case x => useObjectAsContext(x)
  }

  private def useObjectAsContext(obj: Any): Val =
    try {
      
      val fields = obj.getClass().getDeclaredFields filter(!_.isSynthetic) map(field => field.getName -> {
        field.setAccessible(true)
        val value = field.get(obj)        
        if (value == null || value.getClass != obj.getClass) {
          toVal(value)        
        } else {
          ValError(s"can't access self-reference field '${field.getName}'")
        }
      }) toList
      
      val fieldNames = fields map ( _._1 )
      
      val methods= obj.getClass().getDeclaredMethods filter(!_.isSynthetic) filter( m => !fieldNames.contains(m.getName)) toList

      val getters = methods
        .filter(_.getName.startsWith("get"))
        .filter(_.getParameterCount == 0)
        .map( method => getGetterName(method) -> method)
        .filter{ case (name,_) => !fieldNames.contains(name) } 
        .map{ case (name,method) => {
        
        method.setAccessible(true)
        val returnValue = method.invoke(obj)
        
        val value = if (returnValue != obj) {
          toVal(returnValue)        
        } else {
          ValError(s"can't access self-reference getter '${method.getName}'")
        }
        
        name -> value
      }}

      val functions = methods.map( method => {
        
        val name = method.getName
        val paramNames = method.getParameters.map( param => param.getName) toList
        
        val function = ValFunction(paramNames, params => {
          
          val paramValues = params map ValueMapper.unpackVal
          val paramJavaObjects = paramValues zip method.getParameterTypes map { case (obj,clazz) => JavaClassMapper.asJavaObject(obj, clazz) }
          
          val result = method.invoke(obj, paramJavaObjects: _*)
          
          toVal(result)
        })
        
        name -> function
      })
      
      
      ValContext(fields ++ getters ++ functions)

    } catch {
      case t: Throwable => {
        ValError(s"unsupported type '$obj' of class '${obj.getClass}'")
      }
    }
    
  private def getGetterName(method: Method): String = {
    val methodName = method.getName
    val firstChar = methodName.charAt(3) toLower
    
    if (methodName.size == 4) {
      firstChar toString
    } else {
      firstChar + methodName.substring(4)
    }
  }
  
  def unpackVal(value: Val): Any = value match {
    case ValNull => null
    case ValBoolean(boolean) => boolean
    case ValNumber(number) => number
    case ValString(string) => string
    case ValDate(date) => date
    case ValTime(time) => time
    case ValDateTime(dateTime) => dateTime
    case ValYearMonthDuration(duration) => duration
    case ValDayTimeDuration(duration) => duration
    case ValList(list) => list map unpackVal
    case ValContext(context) => context map { case (key, value) => key -> unpackVal(value) } toMap
    case ValError(error) => new Exception(error)
    case _ => throw new IllegalArgumentException(s"unexpected val '$value'")
  }
  
}