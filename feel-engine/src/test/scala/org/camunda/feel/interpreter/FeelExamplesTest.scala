package org.camunda.feel.interpreter

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._

class FeelExamplesTest extends FlatSpec with Matchers with FeelIntegrationTest {
  
  val context = eval("""

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
    
    evalWithContext(""" if applicant.maritalStatus in ("M","S") then "valid" else "not valid" """) should be(ValString("valid"))
    
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
    
    """) should be(ValNumber(
    		(amount * rate / 12) / (1 - (1 + rate/12).pow(-36))) ) // ~ 3975.982590125562
  }
  
  it should "sum a filtered list of context" in {
    
    evalWithContext(""" sum( credit_history[record_date > date("2011-01-01")].weight) """) should be(ValNumber(150))
    
  }
  
  it should "determine if list satisfies" in {
    
    evalWithContext(""" some ch in credit_history satisfies ch.event = "bankruptcy" """) should be(ValBoolean(false))
  }
  
  private def evalWithContext(exp: String) = eval(exp, context.asInstanceOf[ValContext].entries.toMap)
  
}