package org.camunda.feel.example.spec

import java.time.LocalDate

import org.camunda.feel.example.DmnEvaluationTest
import org.camunda.feel.example.spec.Context.{
  Applicant,
  BalanceSummery,
  CreditHistoryRecord
}
import org.scalatest.{FlatSpec, Matchers}

class SpecExampleTest extends FlatSpec with Matchers with DmnEvaluationTest {

  val DMN_FILE = "/spec/SpecExample.dmn"

  val context = Map(
    "applicant" -> Applicant(
      maritalStatus = "M",
      monthly = BalanceSummery(
        income = 10000,
        repayments = 2500,
        expenses = 3000
      )
    ),
    "credit_history" -> List(
      CreditHistoryRecord(
        record_date = LocalDate.parse("2008-03-12"),
        event = "home mortgage",
        weight = 100
      ),
      CreditHistoryRecord(
        record_date = LocalDate.parse("2011-04-01"),
        event = "foreclosure warning",
        weight = 150
      )
    )
  )

  "The applicant" should "have a yearly income of 120000" in {

    val result = evaluateDecision(DMN_FILE, "yearly_income", context)

    result.getSingleEntry.asInstanceOf[Long] should be(120000)
  }

  it should "have a valid marital status" in {

    val result = evaluateDecision(DMN_FILE, "marital_status_check", context)

    result.getSingleEntry.asInstanceOf[String] should be("valid")
  }

  it should "have a total expense of 5500" in {

    val result = evaluateDecision(DMN_FILE, "total_expenses", context)

    result.getSingleEntry.asInstanceOf[Long] should be(5500)
  }

  "The credit history" should "have a total weight of 150" in {

    val result = evaluateDecision(DMN_FILE, "sum_credit", context)

    result.getSingleEntry.asInstanceOf[Long] should be(150)
  }

  it should "contains no bankruptcy" in {

    val result = evaluateDecision(DMN_FILE, "bankruptcy_check", context)

    result.getSingleEntry.asInstanceOf[Boolean] should be(false)
  }

}
