---
title: FEEL language
layout: page
---


## Data Types

| FEEL type | mapped Java type| examples |
|-----------|-------------------|---------|
| number | java.math.BigDecimal | 1, 2.3, .4 |
| string | java.lang.String | "foo" |
| boolean | java.lang.Boolean | true, false |
| date | java.time.LocalDate | 2017-03-10 _(yyyy-MM-dd)_ |
| time (local) | java.time.LocalTime | 11:45:30, 13:30 _(HH:mm:ss)_ |
| time | java.time.OffsetTime | 11:45:30+02:00 |
| date-time (local) | java.time.LocalDateTime | 2017-03-10T11:45:30 _(yyyy-MM-dd'T'HH:mm:ss)_ |
| date-time | java.time.DateTime | 2017-03-10T11:45:30-05:00 |
| day-time-duration | java.time.Duration | P4D, PT2H, PT30M, P2DT12H30M12S |
| year-month-duration | java.time.Period | P2Y, P6M, P1Y6M |
| list | java.util.List | [ ], [1,2,3], ["a","b"] |
| context | java.util.Map | { }, { "a" : 2 }, { "b" : "foo", "c" : { "d" : 42 }}  |
| null | null | null |

## Unary Tests

Used for an **input entry** of a decision table. The value of the input expression is used **implicitly** as the first argument. The result must be either true or false.

### Compare Operators

| operator | supported types | example |
|----------|-----------------|---------|
| equal to | _all_ | 2, "foo" |
| less than | number, date, time, date-time, year-month-duration, day-time-duration  | < 4, < 2017-01-01 |
| less than or equal | number, date, time, date-time, year-month-duration, day-time-duration | <= 4, <= P2Y |
| greater than | number, date, time, date-time, year-month-duration, day-time-duration | > 2, > 10:00 |
| greater than or equal | number, date, time, date-time, year-month-duration, day-time-duration | >= 2, >= PT3H |
| interval | number, date, time, date-time, year-month-duration, day-time-duration | (2..7), [3..5] |

### Disjunction

At least of the tests must be true.

```
2,4,6                // must be 2, 4 or 6

"foo","bar"          // must be 'foo' or 'bar'

< 2,> 10             // must be less than 2 or greater than 10
```

### Negation

The test must be false.

```
not(2,4)             // must not be 2 or 4

not("foo")           // must not be 'foo'
```

### Input Value

Usually, the input value is applied implicitly. However, it can also be accessed by the special variable `?`. 

This is useful for invoking a built-in function or writing a more complex test.

```
ends with(?, "@camunda.com")
```

## Expression

Can be used for all other expressions, e.g. in a decision table as input expression or output entry. An expression takes no implicit arguments like _unary tests_.

### Addition

_Supported types:_ number, string, day-time-duration, year-month-duration

```
2 + 3                                                    // 5

"foo" + "bar"                                            // "foobar"

duration("P1D") + duration("PT6H")                       // P1DT6H

duration("PT1H") + date and time("2017-01-10T10:30:00")  // 2017-01-10T11:30:00

duration("PT1H") + time("10:30:00")                      // 11:30:00

duration("P2M") + duration("P3M")                        // P5M

duration("P1M") + date and time("2017-01-10T10:30:00")   // 2017-02-10T10:30:00
```

### Subtraction 

_Supported types:_ number, time, date-time, day-time-duration, year-month-duration

```
5 - 3                                                                         // 2

time("10:30:00") - time("09:00:00")                                           // PT1H30M

date and time("2017-01-10T10:30:00") - date and time("2017-01-01T10:00:00")   // P9DT30M

duration("P1Y") - duration("P3M")                                             // P9M

date and time("2017-01-10T10:30:00") - duration("P1M")                        // 2016-12-10T10:30:00

duration("PT6H") - duration("PT2H")                                           // PT4H

date and time("2017-01-10T10:30:00") - duration("PT1H")                       // 2017-01-10T09:30:00

time("10:30:00") - duration("PT1H")                                           // 09:30:00
```

### Multiplication

_Supported types:_ number, day-time-duration, year-month-duration

```
5 * 3                                            // 15

3 * duration("P2Y")                              // P6Y 

3 * duration("P1D")                              // P3D
```

### Division 

_Supported types:_ number, day-time-duration, year-month-duration

```
6 / 2                                            // 3

duration("P1Y") / 2                              // P6M

duration("P1D") / 4                              // PT6H
```

### Exponentiation 

_Supported types:_ number

```
2 ** 3                                           // 8
```

### Comparison

| operator | supported types | example |
|----------|-----------------|---------|
| equal to | _all_ | x = 2 |
| not equal to  | _all_ | x != "foo" |
| less than | number, date, time, date-time, year-month-duration, day-time-duration  | x < 2017-01-01 |
| less than or equal | number, date, time, date-time, year-month-duration, day-time-duration | x <= P2Y |
| greater than | number, date, time, date-time, year-month-duration, day-time-duration |x > 10:00 |
| greater than or equal | number, date, time, date-time, year-month-duration, day-time-duration | x >= PT3H |
| between | number, date, time, date-time, year-month-duration, day-time-duration | x between 3 and 9 |

### Disjunction and Conjunction

Combine boolean values.

```
true and true                                  // true
true and false                                 // false

true or false                                  // true
false or false                                 // false
```

### If Expression

```
if (x < 5) then "low" else "high"
```

### List Expressions

Special operators for lists: for, some, every, filter

```
for x in [1,2] return x * 2                    // [2,4]
for x in [1,2], y in [3,4] return x * y        // [3,4,6,8]
for x in 1..3 return x * 2                     // [2,4,6]
for x in 3..1 return x * 2                     // [6,4,2]
for x in 1..5 return x + sum(partial)          // [1,3,7,15,31]

some x in [1,2,3] satisfies x > 2              // true
some x in [1,2,3] satisfies x > 3              // false
some x in [1,2], y in [2,3] satisfies x < y    // true

every x in [1,2,3] satisfies x >= 1            // true
every x in [1,2,3] satisfies x >= 2            // false
every x in [1,2], y in [2,3] satisfies x < y   // false

[1,2,3,4][item > 2]                            // [3,4]

[1,2,3,4][1]                                   // 1
[1,2,3,4][4]                                   // 4
[1,2,3,4][-1]                                  // 4
[1,2,3,4][-2]                                  // 3
[1,2,3,4][5]                                   // null
```

### Context Expressions

A value of a context can be accessed by key via '.' operator (i.e. path express).

```
{ a : 1 }.a                                              // 1

{ a : { b : "foo" } }.a                                  // { b : "foo" }

{ a : { b : "bar" } }.a.b                                // "bar"

{ a : 1, b : 2, c: (a+b) }.c                             // 3
```

Special path and filter expression for a list of contextes.

```
[ { a : "foo", b : 5 }, { a : "bar", b : 10} ].a         // ["foo", "bar"]

[ { a : "foo", b : 5},  { a : "bar", b : 10} ][b > 7]    // { a : "bar", b : 10 }
```

### Evaluate Unary Test

Check if a variable satisfies a given unary test.

```
x in (2..4)

x in < 3
```

### Instance-Of

```
"foo" instance of number                            // false
"bar" instance of string                            // true
```


### Functions

You can define a new function by

```
{
    add : function(x,y) x + y
}
```

This function can be invoked with positional or named parameters

```
add(1,2)

// or

add(x:1, y:2)
```

It is also possible to define an external function which calls a Java method.

```
function(x,y) external { 
    java: { 
        class: "java.lang.Math", 
        method signature: "max(int, int)" 
    } 
}
```

### Special Properties

Variables of type date, time, date-time and duration have special properties to access the individual parts.

```
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
