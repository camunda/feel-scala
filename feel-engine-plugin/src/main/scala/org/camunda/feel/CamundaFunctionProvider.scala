package org.camunda.feel

import org.camunda.feel.interpreter._
import org.camunda.feel.spi.CustomFunctionProvider
import org.camunda.bpm.engine.impl.context.Context
import org.camunda.bpm.engine.impl.interceptor.CommandContext
import scala.collection.JavaConverters._
import java.time.OffsetDateTime
import java.time.ZonedDateTime

/**
  * @author Philipp
  */
class CamundaFunctionProvider extends CustomFunctionProvider {

  def getFunction(name: String): Option[ValFunction] = functions.get(name)

  override def functionNames: Iterable[String] = functions.keys

  val functions: Map[String, ValFunction] = Map(
    "now" -> nowFunction,
    "currentUser" -> currentUserFunction,
    "currentUserGroups" -> currentUserGroupsFunction
  )

  private def nowFunction = ValFunction(
    params = List(),
    invoke = { case _ => ValDateTime(ZonedDateTime.now()) }
  )

  private def currentUserFunction = ValFunction(
    params = List(),
    invoke = {
      case _ =>
        getContext
          .flatMap(ctx => Option(ctx.getAuthenticatedUserId))
          .map(userId => ValString(userId))
          .getOrElse(ValNull)
    }
  )

  private def currentUserGroupsFunction = ValFunction(
    params = List(),
    invoke = {
      case _ =>
        getContext
          .flatMap(ctx => Option(ctx.getAuthenticatedGroupIds))
          .map(ids => ValList(ids.asScala.map(id => ValString(id)).toList))
          .getOrElse(ValNull)
    }
  )

  private def getContext = Option(Context.getCommandContext)

}
