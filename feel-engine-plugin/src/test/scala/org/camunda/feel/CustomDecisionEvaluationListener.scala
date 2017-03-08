package org.camunda.feel

import org.camunda.bpm.dmn.engine.delegate.DmnDecisionEvaluationListener
import org.camunda.bpm.dmn.engine.delegate.DmnDecisionEvaluationEvent

class CustomDecisionEvaluationListener extends DmnDecisionEvaluationListener {
    
  def notify(event: DmnDecisionEvaluationEvent) {
    // foo
  }
    
}