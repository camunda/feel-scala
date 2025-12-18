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
package org.camunda.feel.impl

import java.util.concurrent.{
  Callable,
  ExecutionException,
  ExecutorService,
  Executors,
  Future,
  LinkedBlockingQueue,
  ThreadFactory,
  ThreadPoolExecutor,
  TimeUnit,
  TimeoutException
}
import java.util.concurrent.{RejectedExecutionException, SynchronousQueue}

import scala.concurrent.duration.FiniteDuration
import scala.util.Try

/** Runs computations on a separate thread and interrupts them on timeout.
  *
  * Note: interruption is cooperative. Code stops promptly only if it reacts to interrupts (blocking
  * calls like Thread.sleep, or explicit interrupt checks).
  */
private[feel] object InterruptibleTimeout {

  private val ThreadsProp: String          = "org.camunda.feel.timeout.threads"
  private val QueueCapacityProp: String    = "org.camunda.feel.timeout.queueCapacity"
  private val KeepAliveSecondsProp: String = "org.camunda.feel.timeout.keepAliveSeconds"

  final case class TimedOut(timeout: FiniteDuration)
      extends RuntimeException(s"Evaluation exceeded timeout of ${timeout.toMillis}ms")

  final case class Interrupted() extends RuntimeException("Evaluation thread was interrupted")

  final case class Rejected(message: String) extends RuntimeException(message)

  private val threadFactory: ThreadFactory = new ThreadFactory {
    private val backing = Executors.defaultThreadFactory()

    override def newThread(r: Runnable): Thread = {
      val t = backing.newThread(r)
      t.setName(s"feel-eval-${t.getId}")
      t.setDaemon(true)
      t
    }
  }

  private def configuredThreads(): Int = {
    val default = math.max(2, Runtime.getRuntime.availableProcessors())
    val parsed  = sys.props.get(ThreadsProp).flatMap(v => Try(v.toInt).toOption)
    parsed.filter(_ > 0).getOrElse(default)
  }

  private def configuredQueueCapacity(): Int = {
    val default = 256
    val parsed  = sys.props.get(QueueCapacityProp).flatMap(v => Try(v.toInt).toOption)
    parsed.filter(_ >= 0).getOrElse(default)
  }

  private def configuredKeepAliveSeconds(): Long = {
    val default = 30L
    val parsed  = sys.props.get(KeepAliveSecondsProp).flatMap(v => Try(v.toLong).toOption)
    parsed.filter(_ >= 0).getOrElse(default)
  }

  /** A bounded executor to avoid unbounded thread growth under load.
    *
    * Configurable via system properties:
    *   - org.camunda.feel.timeout.threads (default: max(2, availableProcessors))
    *   - org.camunda.feel.timeout.queueCapacity (default: 256, 0 means no queue)
    *   - org.camunda.feel.timeout.keepAliveSeconds (default: 30)
    */
  private lazy val executor: ExecutorService = {
    val threads          = configuredThreads()
    val queueCapacity    = configuredQueueCapacity()
    val keepAliveSeconds = configuredKeepAliveSeconds()

    val queue =
      if (queueCapacity == 0) new SynchronousQueue[Runnable]()
      else new LinkedBlockingQueue[Runnable](queueCapacity)

    val pool = new ThreadPoolExecutor(
      threads,
      threads,
      keepAliveSeconds,
      TimeUnit.SECONDS,
      queue,
      threadFactory,
      new ThreadPoolExecutor.AbortPolicy()
    )
    pool.allowCoreThreadTimeOut(keepAliveSeconds > 0)
    pool
  }

  def run[A](timeout: FiniteDuration)(thunk: => A): A = {
    if (timeout.toMillis <= 0) throw TimedOut(timeout)

    val future: Future[A] =
      try {
        executor.submit(new Callable[A] {
          override def call(): A = thunk
        })
      } catch {
        case e: RejectedExecutionException =>
          // Fail fast on saturation instead of blocking callers indefinitely.
          throw Rejected(
            s"Evaluation rejected because the timeout executor is saturated: ${e.getMessage}"
          )
      }

    try {
      future.get(timeout.toMillis, TimeUnit.MILLISECONDS)
    } catch {
      case _: TimeoutException =>
        // Attempt to stop the worker by interrupting the evaluation thread.
        // This requires cooperative interruption inside the engine/interpreter.
        future.cancel(true)
        throw TimedOut(timeout)

      case _: InterruptedException =>
        Thread.currentThread().interrupt()
        throw Interrupted()

      case e: ExecutionException =>
        val cause = Option(e.getCause).getOrElse(e)
        cause match {
          case re: RuntimeException    => throw re
          case _: InterruptedException =>
            Thread.currentThread().interrupt()
            throw Interrupted()
          case other                   => throw new RuntimeException(other)
        }
    }
  }
}
