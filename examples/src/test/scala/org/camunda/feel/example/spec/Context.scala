package org.camunda.feel.example.spec

import java.time.LocalDate

object Context {
  
  case class Applicant(maritalStatus: String, monthly: BalanceSummery)
  
  case class BalanceSummery(income: Int, repayments: Int, expenses: Int)
  
  case class CreditHistoryRecord(record_date: LocalDate, event: String, weight: Int)
  
}