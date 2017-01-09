package org.camunda.feel.parser

sealed trait FunctionParameters {
	
	def size: Int
}

case class PositionalFunctionParameters(params: List[Exp]) extends FunctionParameters {
	
	def size = params.size
}

case class NamedFunctionParameters(params: Map[String, Exp]) extends FunctionParameters {
	
	def size = params.size
}