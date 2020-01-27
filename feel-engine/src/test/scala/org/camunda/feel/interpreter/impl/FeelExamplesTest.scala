package org.camunda.feel.interpreter.impl

import org.scalatest.{FlatSpec, Matchers}

class FeelExamplesTest extends FlatSpec with Matchers with FeelIntegrationTest {

  val context: Val = eval(
    """

      {
        applicant: {
          age: 51,
          maritalStatus: "M",
          existingCustomer: false,
          monthly: {
            income: 10000,
            repayments: 2500,
            expenses: 3000
          }
        },
        requested_product: {
          product_type: "STANDARD LOAN",
          rate: 0.25,
          term: 36,
          amount: 100000
        },
        monthly_income: applicant.monthly.income,
        monthly_outgoings: [applicant.monthly.repayments, applicant.monthly.expenses],
        credit_history: [
          {
            record_date: date("2008-03-12"),
            event: "home mortgage",
            weight: 100
          },
          {
            record_date: date("2011-04-01"),
            event: "foreclosure warning",
            weight: 150
          }
        ],
        PMT: function(rate, term, amount) (amount *rate/12) / (1 - (1 + rate/12)**-term)
      }

    """)

  "The FEEL engine" should "calculate" in {

    evalWithContext(""" monthly_income * 12  """) should be(ValNumber(120000))

  }

  it should "evaluate an if,in" in {

    evalWithContext(
      """ if applicant.maritalStatus in ("M","S") then "valid" else "not valid" """) should be(
      ValString("valid"))

  }

  it should "sum entries of a list" in {

    evalWithContext(""" sum(monthly_outgoings) """) should be(ValNumber(5500))

  }

  it should "invoke an user-defined function" in {

    val rate: BigDecimal = 0.25
    val term: BigDecimal = 36
    val amount: BigDecimal = 100000

    evalWithContext(""" PMT(
                             requested_product . rate,
                             requested_product . term,
                             requested_product . amount
                           )

    """) should be(
      ValNumber((amount * rate / 12) / (1 - (1 + rate / 12)
        .pow(-36)))) // ~ 3975.982590125562
  }

  it should "sum a filtered list of context" in {

    evalWithContext(
      """ sum( credit_history[record_date > date("2011-01-01")].weight) """) should be(
      ValNumber(150))

  }

  it should "determine if list satisfies" in {

    evalWithContext(
      """ some ch in credit_history satisfies ch.event = "bankruptcy" """) should be(
      ValBoolean(false))
  }

  it should "execute nested path and filter expressions" in {

    val ctx = Map(
      "EmployeeTable" -> List(
        Map("id" -> 7792, "deptNum" -> 10, "name" -> "Clark"),
        Map("id" -> 7934, "deptNum" -> 10, "name" -> "Miller"),
        Map("id" -> 7976, "deptNum" -> 20, "name" -> "Adams"),
        Map("id" -> 7902, "deptNum" -> 20, "name" -> "Ford"),
        Map("id" -> 7900, "deptNum" -> 30, "name" -> "James")
      ),
      "DeptTable" -> List(
        Map("number" -> 10, "name" -> "Sales", "manager" -> "Smith"),
        Map("number" -> 20, "name" -> "Finance", "manager" -> "Jones"),
        Map("number" -> 30, "name" -> "Engineering", "manager" -> "King")
      ),
      "LastName" -> "Clark"
    )

    eval(
      "DeptTable[number = EmployeeTable[name=LastName].deptNum[1]].manager[1]",
      ctx) should be(ValString("Smith"))
  }

  private def evalWithContext(exp: String) = {
    val ctx = context.asInstanceOf[ValContext].context
    eval(exp, ctx)
  }

}
