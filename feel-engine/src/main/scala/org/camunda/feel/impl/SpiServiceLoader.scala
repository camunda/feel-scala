package org.camunda.feel.impl

import java.util.ServiceLoader

import org.camunda.feel.context.{CustomFunctionProvider, FunctionProvider}
import org.camunda.feel.valuemapper.{CustomValueMapper, ValueMapper}

import scala.reflect.{ClassTag, classTag}
import scala.collection.JavaConverters._

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
