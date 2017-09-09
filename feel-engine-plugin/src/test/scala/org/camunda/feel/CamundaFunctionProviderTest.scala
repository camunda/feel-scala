package org.camunda.feel

import scala.collection.JavaConversions._

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.bpm.engine.ProcessEngineConfiguration
import java.time.LocalDate
import java.time.LocalDateTime
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.camunda.bpm.engine.impl.interceptor.CommandContext
import org.camunda.bpm.engine.impl.interceptor.Command
import org.camunda.bpm.dmn.engine.DmnDecisionResult
import org.camunda.bpm.engine.impl.context.Context

/**
 * @author Philipp
 */
class CamundaFunctionProviderTest extends FlatSpec with Matchers {

	val processEngine = ProcessEngineConfiguration
		.createProcessEngineConfigurationFromResource("default-dmn-config.cfg.xml")
		.buildProcessEngine

	val config = processEngine.getProcessEngineConfiguration.asInstanceOf[ProcessEngineConfigurationImpl]	
		
	val repositoryService = processEngine.getRepositoryService
	val decisionService = processEngine.getDecisionService

	repositoryService.createDeployment()
		.addClasspathResource("functionNow.dmn")
		.addClasspathResource("functionCurrentUser.dmn")
		.addClasspathResource("functionCurrentUserGroups.dmn")
		.deploy()
	
	"The function 'now()'" should "return the current date-time" in {
		
		val result = decisionService.evaluateDecisionByKey("functionNow").evaluate()
		
		result.getSingleEntry[Any] shouldBe a [LocalDateTime]
	}
	
	"The function 'currentUser()'" should "return the current user id" in {
				
		config.getIdentityService.setAuthenticatedUserId("demo")
		
		val result = decisionService.evaluateDecisionByKey("functionCurrentUser").evaluate()
		
		result.getSingleEntry[String] should be ("demo")
	}
	
	it should "return null if no user is authenticated" in {
				
		config.getIdentityService.setAuthenticatedUserId(null)
		
		val result = decisionService.evaluateDecisionByKey("functionCurrentUser").evaluate()
		
		result.getSingleEntry[String] should be (null)
	}
	
	"The function 'currentUserGoups()'" should "return list of current group ids" in {
				
		config.getIdentityService.setAuthentication("demo", List("foo", "bar"))
		
		val result = decisionService.evaluateDecisionByKey("functionCurrentUserGroups").evaluate()
		
		result.getSingleEntry[java.util.List[String]].toList should be (List("foo", "bar"))
	}
	
	it should "return null if no group is authenticated" in {
				
		config.getIdentityService.setAuthentication("demo", null)
		
		val result = decisionService.evaluateDecisionByKey("functionCurrentUserGroups").evaluate()
		
		result.getSingleEntry[List[String]] should be (null) 
	}
	
}