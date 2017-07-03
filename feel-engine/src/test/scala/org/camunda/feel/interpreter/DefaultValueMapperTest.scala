package org.camunda.feel.interpreter

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._
import org.camunda.feel.spi._

/**
 * @author Philipp Ossler
 * @author Falko Menge
 */
class DefaultValueMapperTest extends FlatSpec with Matchers {

	val valueMapper: ValueMapper = new DefaultValueMapper

	"The DefaultValueMapper" should "convert from String" in {
		
		valueMapper.toVal("foo") should be(ValString("foo"))
	}
	
	it should "convert from java.lang.String" in {
		
		valueMapper.toVal(new java.lang.String("foo")) should be(ValString("foo"))
	}
	
	it should "convert from Int" in {
		
		valueMapper.toVal(4: Int) should be(ValNumber(4))
	}
	
	it should "convert from java.lang.Integer" in {
		
		valueMapper.toVal(new java.lang.Integer(4)) should be(ValNumber(4))
	}
	
	it should "convert from Long" in {
		
		valueMapper.toVal(4: Long) should be(ValNumber(4))
	}
	
	it should "convert from java.lang.Long" in {
		
		valueMapper.toVal(new java.lang.Long(4)) should be(ValNumber(4))
	}
	
	it should "convert from Float" in {
		
		valueMapper.toVal(2.4f: Float) should be(ValNumber(2.4f))
	}
	
	it should "convert from java.lang.Float" in {
		
		valueMapper.toVal(new java.lang.Float(2.4)) should be(ValNumber(2.4f))
	}
	
	it should "convert from Double" in {
		
		valueMapper.toVal(2.4: Double) should be(ValNumber(2.4))
	}
	
	it should "convert from java.lang.Double" in {
		
		valueMapper.toVal(new java.lang.Double(2.4)) should be(ValNumber(2.4))
	}
	
	it should "convert from BigDecimal" in {
		
		valueMapper.toVal(2.4: BigDecimal) should be(ValNumber(2.4))
	}
	
	it should "convert from java.math.BigDecimal" in {
		
		valueMapper.toVal(new java.math.BigDecimal("2.4")) should be(ValNumber(2.4))
	}
	
	it should "convert from Boolean" in {
		
		valueMapper.toVal(true) should be(ValBoolean(true))
	}
	
	it should "convert from java.lang.Boolean" in {
		
		valueMapper.toVal(java.lang.Boolean.TRUE) should be(ValBoolean(true))
	}
	
	it should "convert from List" in {
		
		valueMapper.toVal(List(1,2)) should be(ValList(
				List(ValNumber(1), ValNumber(2))))
	}
	
	it should "convert from java.util.List" in {
		
		valueMapper.toVal(java.util.Arrays.asList(1,2)) should be(ValList(
				List(ValNumber(1), ValNumber(2))))
	}
	
	it should "convert from Map" in {
		
		valueMapper.toVal(Map("a" -> 2)) should be(ValContext(
				List("a" -> ValNumber(2))))
	}
	
	it should "convert from java.util.Map" in {
		
		val map = new java.util.HashMap[String, Object]
		map.put("a", new java.lang.Integer(2))
		
		valueMapper.toVal(map) should be(ValContext(
				List("a" -> ValNumber(2))))
	}
	
	it should "convert from LocalDate" in {
		
		valueMapper.toVal(java.time.LocalDate.parse("2017-04-02")) should be(ValDate("2017-04-02"))
	}
	
	it should "convert from LocalTime" in {
		
		valueMapper.toVal(java.time.LocalTime.parse("12:04:30")) should be(ValTime("12:04:30"))
	}
	
	it should "convert from LocalDateTime" in {
		
		valueMapper.toVal(java.time.LocalDateTime.parse("2017-04-02T12:04:30")) should be(ValDateTime("2017-04-02T12:04:30"))
	}
	
	it should "convert from java.util.Date" in {
		
		val format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
		
		valueMapper.toVal(format.parse("2017-04-02T12:04:30")) should be(ValDateTime("2017-04-02T12:04:30"))
	}
	
	it should "convert from Period" in {
		
		valueMapper.toVal(java.time.Period.parse("P2Y4M")) should be(ValYearMonthDuration("P2Y4M"))
	}
	
	it should "convert from Duration" in {
		
		valueMapper.toVal(java.time.Duration.parse("PT4H22M")) should be(ValDayTimeDuration("PT4H22M"))
	}
	
	it should "convert from org.joda.time.LocalDate" in {
		
		valueMapper.toVal(org.joda.time.LocalDate.parse("2017-04-02")) should be(ValDate("2017-04-02"))
	}
	
	it should "convert from org.joda.time.LocalTime" in {
		
		valueMapper.toVal(org.joda.time.LocalTime.parse("12:04:30")) should be(ValTime("12:04:30"))
	}
	
	it should "convert from org.joda.time.LocalDateTime" in {
		
		valueMapper.toVal(org.joda.time.LocalDateTime.parse("2017-04-02T12:04:30")) should be(ValDateTime("2017-04-02T12:04:30"))
	}
	
	it should "convert from org.joda.time.Period" in {
		
		valueMapper.toVal(org.joda.time.Period.parse("P2Y4M")) should be(ValYearMonthDuration("P2Y4M"))
	}
	
	it should "convert from org.joda.time.Duration" in {
		
		valueMapper.toVal(org.joda.time.Duration.standardHours(4)) should be(ValDayTimeDuration("PT4H"))
	}
	
	it should "convert from object" in {
		
		case class Obj(a: Int, b: String)
		
		valueMapper.toVal(Obj(2, "foo")) shouldBe a[ValContext]
	}
	
	it should "convert from Some" in {
		
		valueMapper.toVal(Some("foo")) should be(ValString("foo")) 
	}
	
	it should "convert from None" in {
		
		valueMapper.toVal(None) should be(ValNull)
	}
	
	it should "convert from Enumeration" in {
		
		object WeekDay extends Enumeration {
      type WeekDay = Value
      val Mon, Tue, Wed, Thu, Fri, Sat, Sun = Value
    }
    		
		valueMapper.toVal(WeekDay.Mon) should be(ValString("Mon"))
		valueMapper.toVal(WeekDay.Fri) should be(ValString("Fri"))
	}
	
	it should "convert from null" in {

		valueMapper.toVal(null) should be(ValNull)
	}
	
}