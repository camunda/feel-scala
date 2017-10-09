package org.camunda.feel.spi

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._
import org.camunda.feel.interpreter._

class CustomValueMapperTest extends FlatSpec with Matchers {

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

  class MyCustomContext(val d: DomainObject, override val valueMapper: ValueMapper) extends CustomContext {
    override val variableProvider = new VariableProvider {
      override def getVariable(name: String): Option[Val] = d.properties.get(name) match {
        case Some(x: Property[_]) => Some(valueMapper.toVal(x.value))
        case _ => None
      }
    }
  }

  class MyCustomValueMapper extends CustomValueMapper {
    override def toVal(x: Any): Val = {
      x match {
        case e: Enumerated => ValString(e.id)
        case e: Enum => ValContext(DefaultContext(e.items.map((e) => (e.id -> toVal(e))).toMap))
        case d: DomainObject => ValContext(new MyCustomContext(d, this))
        case _ => super.toVal(x)
      }
    }
  }

  val valueMapper: ValueMapper = new MyCustomValueMapper
  val engine = new FeelEngine(valueMapper = valueMapper)

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
    valueMapper.toVal(Language).asInstanceOf[ValContext].context.variable("DE") should be(ValString("DE"))
  }

  it should "convert custom context (domain object)" in {

    val person = new Person
    person.language.value = Some(Language.EN)

    valueMapper.toVal(person) shouldBe a [ValContext]
    valueMapper.toVal(person).asInstanceOf[ValContext].context.variable("language") should be(ValString("EN"))
    valueMapper.toVal(person).asInstanceOf[ValContext].context.variable("not-here") shouldBe a [ValError]
  }

  it should "allow domain expressions" in {

    val person = new Person
    person.name.value = Some("Tom")
    person.language.value = Some(Language.EN)

    val context = Map("p" -> person, "Language" -> Language)

    engine.evalExpression("p.name", context) should be (EvalValue("Tom"))
    engine.evalExpression("p.language", context) should be (EvalValue("EN"))
    engine.evalExpression("p.language = Language.EN", context) should be (EvalValue(true))
    engine.evalExpression("p.language = Language.DE", context) should be (EvalValue(false))
  }

}
