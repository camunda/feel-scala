package org.camunda.feel.interpreter

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._

/**
 * @author Philipp Ossler
 * @author Falko Menge
 */
class ValueMapperTest extends FlatSpec with Matchers {

	"The Value mapper" should "convert from String" in {

		ValueMapper.toVal("foo") should be(ValString("foo"))
	}

	it should "convert from java.lang.String" in {

		ValueMapper.toVal(new java.lang.String("foo")) should be(ValString("foo"))
	}

	it should "convert from Int" in {

		ValueMapper.toVal(4: Int) should be(ValNumber(4))
	}

	it should "convert from java.lang.Integer" in {

		ValueMapper.toVal(new java.lang.Integer(4)) should be(ValNumber(4))
	}

	it should "convert from Long" in {

		ValueMapper.toVal(4: Long) should be(ValNumber(4))
	}

	it should "convert from java.lang.Long" in {

		ValueMapper.toVal(new java.lang.Long(4)) should be(ValNumber(4))
	}

	it should "convert from Float" in {

		ValueMapper.toVal(2.4f: Float) should be(ValNumber(2.4f))
	}

	it should "convert from java.lang.Float" in {

		ValueMapper.toVal(new java.lang.Float(2.4)) should be(ValNumber(2.4f))
	}

	it should "convert from Double" in {

		ValueMapper.toVal(2.4: Double) should be(ValNumber(2.4))
	}

	it should "convert from java.lang.Double" in {

		ValueMapper.toVal(new java.lang.Double(2.4)) should be(ValNumber(2.4))
	}

	it should "convert from BigDecimal" in {

		ValueMapper.toVal(2.4: BigDecimal) should be(ValNumber(2.4))
	}

	it should "convert from java.math.BigDecimal" in {

		ValueMapper.toVal(new java.math.BigDecimal("2.4")) should be(ValNumber(2.4))
	}

	it should "convert from Boolean" in {

		ValueMapper.toVal(true) should be(ValBoolean(true))
	}

	it should "convert from java.lang.Boolean" in {

		ValueMapper.toVal(java.lang.Boolean.TRUE) should be(ValBoolean(true))
	}

	it should "convert from List" in {

		ValueMapper.toVal(List(1,2)) should be(ValList(
				List(ValNumber(1), ValNumber(2))))
	}

	it should "convert from java.util.List" in {

		ValueMapper.toVal(java.util.Arrays.asList(1,2)) should be(ValList(
				List(ValNumber(1), ValNumber(2))))
	}

	it should "convert from Map" in {

		ValueMapper.toVal(Map("a" -> 2)) should be(ValContext(
				List("a" -> ValNumber(2))))
	}

	it should "convert from java.util.Map" in {

		val map = new java.util.HashMap[String, Object]
		map.put("a", new java.lang.Integer(2))

		ValueMapper.toVal(map) should be(ValContext(
				List("a" -> ValNumber(2))))
	}

	it should "convert from LocalDate" in {

		ValueMapper.toVal(java.time.LocalDate.parse("2017-04-02")) should be(ValDate("2017-04-02"))
	}

	it should "convert from LocalTime" in {

		ValueMapper.toVal(java.time.LocalTime.parse("12:04:30")) should be(ValTime("12:04:30"))
	}

	it should "convert from LocalDateTime" in {

		ValueMapper.toVal(java.time.LocalDateTime.parse("2017-04-02T12:04:30")) should be(ValDateTime("2017-04-02T12:04:30"))
	}

	it should "convert from java.util.Date" in {

		val format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

		ValueMapper.toVal(format.parse("2017-04-02T12:04:30")) should be(ValDateTime("2017-04-02T12:04:30"))
	}

	it should "convert from Period" in {

		ValueMapper.toVal(java.time.Period.parse("P2Y4M")) should be(ValYearMonthDuration("P2Y4M"))
	}

	it should "convert from Duration" in {

		ValueMapper.toVal(java.time.Duration.parse("PT4H22M")) should be(ValDayTimeDuration("PT4H22M"))
	}

	it should "convert from org.joda.time.LocalDate" in {

		ValueMapper.toVal(org.joda.time.LocalDate.parse("2017-04-02")) should be(ValDate("2017-04-02"))
	}

	it should "convert from org.joda.time.LocalTime" in {

		ValueMapper.toVal(org.joda.time.LocalTime.parse("12:04:30")) should be(ValTime("12:04:30"))
	}

	it should "convert from org.joda.time.LocalDateTime" in {

		ValueMapper.toVal(org.joda.time.LocalDateTime.parse("2017-04-02T12:04:30")) should be(ValDateTime("2017-04-02T12:04:30"))
	}

	it should "convert from org.joda.time.Period" in {

		ValueMapper.toVal(org.joda.time.Period.parse("P2Y4M")) should be(ValYearMonthDuration("P2Y4M"))
	}

	it should "convert from org.joda.time.Duration" in {

		ValueMapper.toVal(org.joda.time.Duration.standardHours(4)) should be(ValDayTimeDuration("PT4H"))
	}

	it should "convert from object" in {

		case class Obj(a: Int, b: String)

		ValueMapper.toVal(Obj(2, "foo")) shouldBe a[ValContext]
	}

	it should "convert from null" in {

		ValueMapper.toVal(null) should be(ValNull)
	}

}