---
title: FEEL Language Reference: Expression
---

## Expression

An expression can contain literals, operators and function calls.

### Literal

A single value of one of the [types](feel-data-types).

```js
null
21
"valid"
```

### Path Expression

Access a value by its name/path. For example, a given variable from the input/context.

```js
x + y
```

If the value is a context (or data object/POJO) then the inner values can be accessed by `context.key`.

```js
x.y
// return 1 if x is {y: 1}
```

Also, directly on a context.

```js
{x: 2}.x
// 2

{x: {y: "valid"}}.x
// {y: "valid"}

{x: {y: "valid"}}.x.y
// "valid"
```

Inside a context, the previous values can be accessed.

```js
{
  a: 1,
  b: 2,
  c: a + b
}
```

If the name or path contains any special character (e.g. whitespace, dash, etc.) then the name needs to be wrapped into single quotes `'foo bar'`.

```js
'name with whitespace'.'name+operator'
```

### Addition

* supported types: number, string, day-time-duration, year-month-duration

```js
2 + 3
// 5

"foo" + "bar"
// "foobar"

duration("P1D") + duration("PT6H")
// duration("P1DT6H")
```

### Subtraction 

* supported types: number, time, date-time, day-time-duration, year-month-duration

```js
5 - 3
// 2

time("10:30:00") - time("09:00:00")
// duration("PT1H30M")

time("10:30:00") - duration("PT1H") 
// time("09:30:00")
```

### Multiplication

* supported types: number, day-time-duration, year-month-duration

```js
5 * 3        
// 15

3 * duration("P2Y")      
// duration("P6Y") 
```

### Division 

* supported types: number, day-time-duration, year-month-duration

```js
6 / 2  
// 3

duration("P1Y") / 2 
// duration("P6M")

duration("P1Y") / duration("P1M")
// 12
```

### Exponentiation 

* supported types: number

```js
2 ** 3   
// 8
```

### Comparison

| operator | symbol | example |
|----------|-----------------|---------|
| equal to | `=` | `x = "valid"` |
| not equal to | `!=` | `x != "valid"` |
| less than | `<`  | `< 10` |
| less than or equal | `<=` | `<= 10` |
| greater than | `>` | `> 10` |
| greater than or equal | `>=` | `>= 10` |
| between | `between _ and _` | `x between 3 and 9` |

* less than/greater than/between are only supported for: 
  * number
  * date
  * time
  * date-time
  * year-month-duration
  * day-time-duration 

### Disjunction and Conjunction

Combine two boolean values.

```js
true and true   
// true

true and false        
// false

true and null        
// false

false and null
// false
```

```js
true or false   
// true

false or false  
// false

true or null   
// true

false or null  
// false
```

### If Expression

```js
if (x < 5) then "low" else "high"
```

### For Expressions

Iterate over a list and apply an expression (i.e. aka `map`). The result is again a list.

```js
for x in [1,2] return x * 2 
// [2,4]
```

Iterate over multiple lists.

```js
for x in [1,2], y in [3,4] return x * y  
// [3,4,6,8]
```

Iterate over a range - forward or backward.

```js
for x in 1..3 return x * 2                  
// [2,4,6]

for x in 3..1 return x * 2       
// [6,4,2]
```

The previous results of the iterator can be accessed by the variable `partial`. 

```js
for x in 1..5 return x + sum(partial)       
// [1,3,7,15,31]
```

### Some/Every Expression

Test if at least one element of the list satisfies the expression.

```js
some x in [1,2,3] satisfies x > 2         
// true

some x in [1,2,3] satisfies x > 3   
// false

some x in [1,2], y in [2,3] satisfies x < y  
// true
```

Test if all elements of the list satisfies the expression.

```js
every x in [1,2,3] satisfies x >= 1   
// true

every x in [1,2,3] satisfies x >= 2     
// false

every x in [1,2], y in [2,3] satisfies x < y 
// false
```

### Filter Expression

Filter a list of elements by an expression. The expression can access the current element by `item`. The result is a list again.

```js
[1,2,3,4][item > 2]   
// [3,4]
```

An element of a list can be accessed by its index. The index starts at `1`. A negative index starts at the end by `-1`.

```js
[1,2,3,4][1]           
// 1

[1,2,3,4][4]                                   
// 4

[1,2,3,4][-1]                                  
// 4

[1,2,3,4][-2]                                  
// 3

[1,2,3,4][5]                                   
// null
```

If the elements are contextes then the nested value of the current element can be accessed directly by its name.

```js
[ {a: "foo", b: 5},  {a: "bar", b: 10} ][b > 7] 
// {a : "bar", b: 10}
```

The nested values of a specific key can be extracted by `.key`.

```js
[ {a : "foo", b: 5 }, {a: "bar", b: 10} ].a     
// ["foo", "bar"]
```

### Evaluate a Unary Tests

Evaluates a [unary-tests expression](feel-unary-tests) with the given value. 

```js
x in (2..4)

x in < 3
```

### Instance-Of Expression

Checks the type of the value.

```js
"foo" instance of number                      
// false

"bar" instance of string                            
// true
```

### Functions

Invoke a user-defined or built-in function by its name. The arguments can be passed positional or named.

```js
add(1,2)
// or
add(x:1, y:2)
```

A function (body) can be defined using `function(arguments) expression`. For example, inside a context. 

```js
{
    add : function(x,y) x + y
}
```

It is also possible to define an external function which calls a Java method. (Usually, it's better to use the Custom Function SPI.)

```js
function(x,y) external { 
    java: { 
        class: "java.lang.Math", 
        method signature: "max(int, int)" 
    } 
}
```

### Special Properties

Values of type date, time, date-time and duration have special properties to access their individual parts.

```js
date("2017-03-10").year                   
date("2017-03-10").month                
date("2017-03-10").day                    

time("11:45:30+02:00").hour            
time("11:45:30+02:00").minute         
time("11:45:30+02:00").second        
time("11:45:30+02:00").time offset   

date and time("2017-03-10T11:45:30+02:00")    
// all properties of date and time

duration("P2Y3M").years                  
duration("P2Y3M").months               

duration("P1DT2H10M30S").days      
duration("P1DT2H10M30S").hours     
duration("P1DT2H10M30S").minutes 
duration("P1DT2H10M30S").seconds 
```
