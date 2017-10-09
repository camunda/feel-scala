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

  private val functions: Map[(String, Int), ValFunction] = Map(
    ("now", 0) -> nowFunction,
    ("currentUser", 0) -> currentUserFunction,
    ("currentUserGroups", 0) -> currentUserGroupsFunction
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

  override def getFunction(functionName: String, argumentCount: Int): Option[ValFunction] =
    functions.get((functionName, argumentCount))

}
