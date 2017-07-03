package org.camunda.feel.interpreter

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._
import org.camunda.feel.spi._

class CustomValueMapperTest extends FlatSpec with Matchers {

	val person = new Person
	person.name.value = Some("Tom")
	person.language.value = Some(Language.EN)

	val valueMapper: ValueMapper = new CustomValueMapper
	val engine = new FeelEngine(valueMapper = valueMapper)
	val context = Map("p" -> person, "Language" -> Language)

	"A CustomValueMapper" should "convert from String" in {
		valueMapper.toVal("foo") should be(ValString("foo"))
	}

	it should "convert from Int" in {
		valueMapper.toVal(4: Int) should be(ValNumber(4))
	}

	it should "convert from custom value (enumerated)" in {
		valueMapper.toVal(Language.DE) should be(ValString("DE"))
	}

	it should "convert custom context (enumeration)" in {
		valueMapper.toVal(Language).getClass should be(classOf[ValContext])
		valueMapper.toVal(Language).asInstanceOf[ValContext].entries.filter(_._1 == "DE").head._2 should be(ValString("DE"))
	}

	it should "convert custom context (domain object)" in {
		valueMapper.toVal(person).getClass should be(classOf[ValContext])
		valueMapper.toVal(person).asInstanceOf[ValContext].entries.filter(_._1 == "language").head._2 should be(ValString("EN"))
	}

	it should "allow domain expressions" in {
		engine.evalExpression("p.name", context) should be (EvalValue("Tom"))
		engine.evalExpression("p.language", context) should be (EvalValue("EN"))
		engine.evalExpression("p.language = Language.EN", context) should be (EvalValue(true))
		engine.evalExpression("p.language = Language.DE", context) should be (EvalValue(false))
	}

}

trait Enum {
	def items: Seq[Enumerated]
}

trait Enumerated {
	def id: String
}

case class Language(val id: String) extends Enumerated

object Language extends Enum {
	object DE extends Language("DE")
	object EN extends Language("EN")
	def items = Seq(DE, EN)
}

case class Property[T](val name: String, var value: Option[T])

trait DomainObject {
	def properties: Map[String, Property[_]]
}

class Person extends DomainObject {
	val name = Property[String]("name", None)
	val language = Property[Language]("language", None)
	val properties = Map((name.name -> name), (language.name -> language))
}

class CustomValueMapper extends DefaultValueMapper {

	override def toVal(x: Any): Val = x match {
		case e: Enumerated => ValString(e.id)
		case e: Enum => ValContext(e.items.map((e) => ((e.id, toVal(e)))).toList)
		case d: DomainObject => ValContext(d.properties.values.map((p) => ((p.name, toVal(p.value)))).toList)
		case _ => super.toVal(x)
	}

}
