package org.camunda.feel.impl.parser

sealed trait FunctionParameters

case class PositionalFunctionParameters(params: List[Exp])
    extends FunctionParameters

case class NamedFunctionParameters(params: Map[String, Exp])
    extends FunctionParameters
