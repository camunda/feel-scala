package org.camunda.feel.spi

import java.util.ServiceLoader

import org.camunda.feel.interpreter.{
  DefaultValueMapper,
  FunctionProvider,
  ValueMapper
}
import org.camunda.feel.logger

import scala.reflect.{ClassTag, classTag}
import scala.collection.JavaConverters._

object SpiServiceLoader {

  def loadValueMapper: ValueMapper =
    loadServiceProvider[CustomValueMapper]() match {
      case Nil      => DefaultValueMapper.instance
      case l :: Nil => l
      case ls =>
        logger.warn(
          "Found more than one custom value mapper: {}. Use the first one.",
          ls)
        ls.head
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
