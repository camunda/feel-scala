package org.camunda.feel.impl.spi

import java.util.ServiceLoader

import org.camunda.feel.impl.FeelEngine
import org.camunda.feel.impl.interpreter.{FunctionProvider, ValueMapper}
import org.camunda.feel.impl.interpreter.{FunctionProvider, ValueMapper}

import scala.collection.JavaConverters._
import scala.reflect.{ClassTag, classTag}

object SpiServiceLoader {

  def loadValueMapper: ValueMapper = {
    val customValueMappers = loadServiceProvider[CustomValueMapper]()
    ValueMapper.CompositeValueMapper(customValueMappers)
  }

  def loadFunctionProvider: FunctionProvider =
    loadServiceProvider[CustomFunctionProvider]() match {
      case Nil      => FunctionProvider.EmptyFunctionProvider
      case p :: Nil => p
      case ps       => FunctionProvider.CompositeFunctionProvider(ps)
    }

  private def loadServiceProvider[T: ClassTag](): List[T] =
    try {
      val loader =
        ServiceLoader.load(classTag[T].runtimeClass.asInstanceOf[Class[T]])
      loader.iterator.asScala.toList
    } catch {
      case t: Throwable =>
        System.err.println(
          s"Failed to load service provider: ${classTag[T].runtimeClass.getSimpleName}")
        t.printStackTrace()
        throw t
    }

}
