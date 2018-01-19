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

  def getFunction(name: String): List[ValFunction] = functions.getOrElse(name, List.empty)

  val functions: Map[String, List[ValFunction]] = Map(
     "now" -> List(nowFunction),
     "currentUser" -> List(currentUserFunction),
     "currentUserGroups" -> List(currentUserGroupsFunction)
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
