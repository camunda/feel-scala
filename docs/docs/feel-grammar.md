---
id: feel-grammar
title: FEEL Grammar
---

## EBNF

This is the original grammar from the spec.

```
1. expression = 
  a. textual expression |
  b. boxed expression ;
  
2. textual expression =
  a. function definition | for expression | if expression | quantified expression |
  b. disjunction |
  c. conjunction |
  d. comparison |
  e. arithmetic expression |
  f. instance of |
  g. path expression |
  h. filter expression | function invocation |
  i. literal | simple positive unary test | name | "(" , textual expression , ")" ;
  
3. textual expressions = textual expression , { "," , textual expression } ;

4. arithmetic expression =
  a. addition | subtraction |
  b. multiplication | division |
  c. exponentiation |
  d. arithmetic negation ;
  
5. simple expression = arithmetic expression | simple value ;

6. simple expressions = simple expression , { "," , simple expression } ;

7. simple positive unary test =
  a. [ "<" | "<=" | ">" | ">=" ] , endpoint |
  b. interval ;
  
8. interval = ( open interval start | closed interval start ) , endpoint , ".." , endpoint , ( open interval end | closed interval
end ) ;

9. open interval start = "(" | "]" ;

10. closed interval start = "[" ;

11. open interval end = ")" | "[" ;

12. closed interval end = "]" ;

13. simple positive unary tests = simple positive unary test , { "," , simple positive unary test } ;

14. simple unary tests =
  a. simple positive unary tests |
  b. "not", "(", simple positive unary tests, ")" |
  c. "-";

15. positive unary test = simple positive unary test | "null" ;

16. positive unary tests = positive unary test , { "," , positive unary test } ;

17. unary tests =
  a. positive unary tests |
  b. "not", " (", positive unary tests, ")" |
  c. "-"

18. endpoint = simple value ;

19. simple value = qualified name | simple literal ;

20. qualified name = name , { "." , name } ;

21. addition = expression , "+" , expression ;

22. subtraction = expression , "-" , expression ;

23. multiplication = expression , "*" , expression ;

24. division = expression , "/" , expression ;

25. exponentiation = expression, "**", expression ;

26. arithmetic negation = "-" , expression ;

27. name = name start , { name part | additional name symbols } ;

28. name start = name start char, { name part char } ;

29. name part = name part char , { name part char } ;

30. name start char = "?" | [A-Z] | "_" | [a-z] | [\uC0-\uD6] | [\uD8-\uF6] | [\uF8-\u2FF] | [\u370-\u37D] | [\u37F-\u1FFF] |
[\u200C-\u200D] | [\u2070-\u218F] | [\u2C00-\u2FEF] | [\u3001-\uD7FF] | [\uF900-\uFDCF] | [\uFDF0-\uFFFD] |
[\u10000-\uEFFFF] ;

31. name part char = name start char | digit | \uB7 | [\u0300-\u036F] | [\u203F-\u2040] ;

32. additional name symbols = "." | "/" | "-" | "’" | "+" | "*" ;

33. literal = simple literal | "null" ;

34. simple literal = numeric literal | string literal | Boolean literal | date time literal ;

35. string literal = '"' , { character – ('"' | vertical space) }, '"' ;

36. Boolean literal = "true" | "false" ;

37. numeric literal = [ "-" ] , ( digits , [ ".", digits ] | "." , digits ) ;

38. digit = [0-9] ;

39. digits = digit , {digit} ;

40. function invocation = expression , parameters ;

41. parameters = "(" , ( named parameters | positional parameters ) , ")" ;

42. named parameters = parameter name , ":" , expression ,
{ "," , parameter name , ":" , expression } ;

43. parameter name = name ;

44. positional parameters = [ expression , { "," , expression } ] ;

45. path expression = expression , "." , name ;

46. for expression = "for" , name , "in" , expression { "," , name , "in" , expression } , "return" , expression ;

47. if expression = "if" , expression , "then" , expression , "else" expression ;

48. quantified expression = ("some" | "every") , name , "in" , expression , { name , "in" , expression } , "satisfies" ,
expression ;

49. disjunction = expression , "or" , expression ;

50. conjunction = expression , "and" , expression ;

51. comparison =
  a. expression , ( "=" | "!=" | "<" | "<=" | ">" | ">=" ) , expression |
  b. expression , "between" , expression , "and" , expression |
  c. expression , "in" , positive unary test ;
  d. expression , "in" , " (", positive unary tests, ")" ;

52. filter expression = expression , "[" , expression , "]" ;

53. instance of = expression , "instance" , "of" , type ;

54. type = qualified name ;

55. boxed expression = list | function definition | context ;

56. list = "[" [ expression , { "," , expression } ] , "]" ;

57. function definition = "function" , "(" , [ formal parameter { "," , formal parameter } ] , ")" ,
[ "external" ] , expression ;

58. formal parameter = parameter name ;

59. context = "{" , [context entry , { "," , context entry } ] , "}" ;

60. context entry = key , ":" , expression ;

61. key = name | string literal ;

62. date time literal = ( "date" | "time" | "date and time" | "duration" ) , "(" , string literal , ")" ;
```

## PEG

Rewritten grammar which is used by the parser.

```
// 1 a)
expression = textualExpression
// 1 b)
expression10 = boxedExpression

// 3
textualExpressions = textualExpression ( "," textualExpression )*

// 2 a)
textualExpression = functionDefinition / forExpression / ifExpression / quantifiedExpression / expression2
// 2 b) 
expression2 = disjunction
// 2 c)
expression3 = conjunction
// 2 d)
expression4 = comparison / expression5
// 2 e)
expression5 = arithmeticExpression
// 2 f)
expression6 = instanceOf / expression7
// 2 g)
expression7 = pathExpression
// 2 h)
expression8 = filterExpression / functionInvocation / expression9
// 2 i)
expression9 = literal / name / simplePositiveUnaryTest / ( "(" textualExpression ")" ) / expression10

// 6
simpleExpressions = simpleExpression ( "," simple expression )*
  
// 5
simpleExpression = arithmeticExpression / simpleValue

// 4 a) -> 21+22
arithmeticExpression = arithmeticExpression2 ( "+" arithmeticExpression2 / "-" arithmeticExpression2 )*
// 4 b) -> 23+24
arithmeticExpression2 = arithmeticExpression3 ( "*" arithmeticExpression3 / "/" arithmeticExpression3 )*
// 4 c) -> 25
arithmeticExpression3 = arithmeticExpression4 ( "**" arithmeticExpression4 )*
// 4 d) -> 26
arithmeticExpression4 = ("-")? expression6

// 17
unaryTests = "-" / ( "not" "(" positiveUnaryTests ")" ) / positiveUnaryTests

// 16
positiveUnaryTests = positiveUnaryTest ( "," positiveUnaryTest )*

// 15
positiveUnaryTest = "null" / simplePositiveUnaryTest

// 14
simpleUnaryTests = "-" / ( "not" "(" simplePositiveUnaryTests ")" ) / simplePositiveUnaryTests

// 13
simplePositiveUnaryTests = simplePositiveUnaryTest ( "," simplePositiveUnaryTest )*

// 7
simplePositiveUnaryTest = ( ( "<" / "<=" / ">" / ">=" )? endpoint ) / interval

// 18
endpoint = simpleValue

// 19
simpleValue = simpleLiteral / qualifiedName

// 33
literal = "null" / simpleLiteral

// 34
simpleLiteral = booleanLiteral / dateTimeLiteral / stringLiteral / numericLiteral  

// 36
booleanLiteral = "true" / "false"

// 62
dateTimeLiteral = ( "date" / "time" / "date and time" / "duration" ) "(" stringLiteral ")"

// 35 
stringLiteral = '"' ( !('"' / verticalSpace) character )* '"'

// 37
numericLiteral = ( "-" )? ( ( digits ( "." digits )? ) / ( "." digits ) )

// 39
digits = digit ( digit )*

// 38
digit = [0-9]

// 20
qualifiedName = name ( "." name )*

// 27
name = nameStart ( namePart / additionalNameSymbols )*

// 28
nameStart = nameStartChar ( namePartChar )*

// 29
namePart = ( namePartChar )+

// 30
nameStartChar = "?" / [A-Z] / "_" / [a-z] / [\uC0-\uD6] / [\uD8-\uF6] / [\uF8-\u2FF] / [\u370-\u37D] / [\u37F-\u1FFF] /
[\u200C-\u200D] / [\u2070-\u218F] / [\u2C00-\u2FEF] / [\u3001-\uD7FF] / [\uF900-\uFDCF] / [\uFDF0-\uFFFD] /
[\u10000-\uEFFFF]

// 31
namePartChar = nameStartChar / digit / [\uB7] / [\u0300-\u036F] / [\u203F-\u2040] 

// 32
additionalNameSymbols = "." / "/" / "-" / "’" / "+" / "*" 
  
// 8
interval = ( openIntervalStart / closedIntervalStart ) endpoint ".." endpoint ( openIntervalEnd / closedIntervalEnd )

// 9
openIntervalStart = "(" / "]"

// 10
closedIntervalStart = "["

// 11
openIntervalEnd = ")" / "["

// 12
closedIntervalEnd = "]"

// 46
forExpression = "for" name "in" expression ( "," name "in" expression )* "return" expression

// 47
ifExpression = "if" expression "then" expression "else" expression

// 48
quantifiedExpression = ("some" / "every") (name "in" expression)+ "satisfies" expression

// 49
disjunction = expression3 ( "or" expression3 )*

// 50
conjunction = expression4 ( "and" expression )*

// 51
comparison =  ( expression5 ( "=" / "!=" / "<" / "<=" / ">" / ">=" ) expression5 ) /
              ( expression5 "between" expression "and" expression ) /
              ( expression5 "in" "(" positiveUnaryTests ")" ) /
              ( expression5 "in" positiveUnaryTest )               

// 53
instanceOf = expression7 "instance" "of" type

// 54
type = qualifiedName
 
// 45
pathExpression = expression8 ( "." name )* ( "[" expression "]" )

// 52
filterExpression = expression9 "[" expression "]"
              
// 40
functionInvocation = expression9 parameters

// 41
parameters = "(" namedParameters / positionalParameters ")"

// 42
namedParameters = parameterName ":" expression ( "," parameterName ":" expression )*

// 43
parameterName = name

// 44
positionalParameters = ( expression ( "," expression )* )?

// 55
boxedExpression = list / functionDefinition / context 

// 56
list = "[" ( expression ( "," expression )* )? "]" 

// 57
functionDefinition = "function" "(" ( formalParameter ( "," formalParameter )* )? ")" ( "external" )? expression

// 58
formalParameter = parameterName

// 59
context = "{" ( contextEntry ( "," contextEntry )* )? "}" 

// 60
contextEntry = key ":" expression 

// 61
key = name / stringLiteral 
```
