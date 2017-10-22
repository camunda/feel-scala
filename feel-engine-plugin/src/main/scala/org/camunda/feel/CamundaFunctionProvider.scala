package org.camunda.feel

import scala.collection.JavaConversions._
import org.camunda.feel.interpreter._
import org.camunda.feel.spi.CustomFunctionProvider
import java.time.LocalDateTime
import org.camunda.bpm.engine.impl.context.Context
import org.camunda.bpm.engine.impl.interceptor.CommandContext

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
    invoke = { case _ => ValDateTime(LocalDateTime.now()) }
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
          .map(ids => ValList(ids.map(id => ValString(id)).toList))
          .getOrElse(ValNull)
    }
  )

  private def getContext = Option(Context.getCommandContext)

}
