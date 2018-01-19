package org.camunda.feel

import scala.collection.JavaConverters._
import org.scalatest._
import org.camunda.bpm.engine.ProcessEngineConfiguration
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.camunda.bpm.engine.impl.interceptor.CommandContext
import org.camunda.bpm.engine.impl.interceptor.Command
import org.camunda.bpm.dmn.engine.DmnDecisionResult
import org.camunda.bpm.engine.impl.context.Context
import java.time.LocalDate
import java.time.ZoneId

class FeelDataTypeTest extends FlatSpec with Matchers with BeforeAndAfter {

	val processEngine = ProcessEngineConfiguration
		.createProcessEngineConfigurationFromResource("default-dmn-config.cfg.xml")
		.buildProcessEngine

	val config = processEngine.getProcessEngineConfiguration.asInstanceOf[ProcessEngineConfigurationImpl]

	val repositoryService = processEngine.getRepositoryService
	val decisionService = processEngine.getDecisionService

	repositoryService.createDeployment()
		.addClasspathResource("feelLocalDateTime.dmn")
		.addClasspathResource("feelDateTime.dmn")
		.addClasspathResource("feelLocalTime.dmn")
		.addClasspathResource("feelTime.dmn")
		.addClasspathResource("feelDate.dmn")
		.deploy()

 after {
    processEngine.close
	}

	"The data type 'feel:local-date-time'" should "accept java.util.Date" in {

		val result = decisionService.evaluateDecisionByKey("local-date-time-decision")
		  .variables(Map[String,Object]("date" -> java.util.Date.from(LocalDate.of(2001, 1, 17).atStartOfDay(ZoneId.systemDefault()).toInstant())).asJava)
		  .evaluate()

		result.getSingleEntry[Boolean] should be(true)
	}
	
	it should "accept java.time.OffsetDateTime" in {

		val result = decisionService.evaluateDecisionByKey("local-date-time-decision")
		  .variables(Map[String,Object]("date" -> java.time.OffsetDateTime.parse("2001-01-17T00:00:00+01:00")).asJava)
		  .evaluate()

		result.getSingleEntry[Boolean] should be(true)
	}
	
	it should "accept java.time.ZonedDateTime" in {

		val result = decisionService.evaluateDecisionByKey("local-date-time-decision")
		  .variables(Map[String,Object]("date" -> java.time.OffsetDateTime.parse("2001-01-17T00:00:00+01:00")).asJava)
		  .evaluate()

		result.getSingleEntry[Boolean] should be(true)
	}
	
	it should "accept java.time.LocalDateTime" in {

		val result = decisionService.evaluateDecisionByKey("local-date-time-decision")
		  .variables(Map[String,Object]("date" -> java.time.LocalDateTime.parse("2001-01-17T00:00:00")).asJava)
		  .evaluate()

		result.getSingleEntry[Boolean] should be(true)
	}
	
	"The data type 'feel:date-time'" should "accept java.util.Date" in {

		val result = decisionService.evaluateDecisionByKey("date-time-decision")
		  .variables(Map[String,Object]("date" -> java.util.Date.from(LocalDate.of(2001, 1, 17).atStartOfDay(ZoneId.systemDefault()).toInstant())).asJava)
		  .evaluate()

		result.getSingleEntry[Boolean] should be(true)
	}
	
	it should "accept java.time.OffsetDateTime" in {

		val result = decisionService.evaluateDecisionByKey("date-time-decision")
		  .variables(Map[String,Object]("date" -> java.time.OffsetDateTime.parse("2001-01-17T00:00:00+01:00")).asJava)
		  .evaluate()

		result.getSingleEntry[Boolean] should be(true)
	}
	
	it should "accept java.time.ZonedDateTime" in {

		val result = decisionService.evaluateDecisionByKey("date-time-decision")
		  .variables(Map[String,Object]("date" -> java.time.ZonedDateTime.parse("2001-01-17T00:00:00+01:00")).asJava)
		  .evaluate()

		result.getSingleEntry[Boolean] should be(true)
	}
	
	it should "accept java.time.LocalDateTime" in {

		val result = decisionService.evaluateDecisionByKey("date-time-decision")
		  .variables(Map[String,Object]("date" -> java.time.LocalDateTime.parse("2001-01-17T00:00:00")).asJava)
		  .evaluate()

		result.getSingleEntry[Boolean] should be(true)
	}

	"The data type 'feel:local-time'" should "accept java.time.OffsetTime" in {

		val result = decisionService.evaluateDecisionByKey("local-time-decision")
		  .variables(Map[String,Object]("time" -> java.time.OffsetTime.parse("13:00:00+01:00")).asJava)
		  .evaluate()

		result.getSingleEntry[Boolean] should be(true)
	}
	
	it should "accept java.time.LocalTime" in {

		val result = decisionService.evaluateDecisionByKey("local-time-decision")
		  .variables(Map[String,Object]("time" -> java.time.LocalTime.parse("13:00:00")).asJava)
		  .evaluate()

		result.getSingleEntry[Boolean] should be(true)
	}
	
	"The data type 'feel:time'" should "accept java.time.OffsetTime" in {

		val result = decisionService.evaluateDecisionByKey("time-decision")
		  .variables(Map[String,Object]("time" -> java.time.OffsetTime.parse("13:00:00+01:00")).asJava)
		  .evaluate()

		result.getSingleEntry[Boolean] should be(true)
	}
	
	"The data type 'feel:date'" should "accept java.time.LocalDate" in {

		val result = decisionService.evaluateDecisionByKey("date-decision")
		  .variables(Map[String,Object]("date" -> java.time.LocalDate.parse("2001-01-17")).asJava)
		  .evaluate()

		result.getSingleEntry[Boolean] should be(true)
	}
	
}
