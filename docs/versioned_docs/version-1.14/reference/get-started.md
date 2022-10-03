---
id: get-started
title: Get Started
slug: /reference/
---

Welcome to the documentation of the FEEL-Scala engine!

If you're **new** and don't know anything about FEEL then have a look
at [What is FEEL](./what-is-feel.md) first.

If you want to know how to **write** FEEL expressions then have a look at
the [Language Guide](./language-guide/language-guide-introduction.md).

If you want to **integrate** the FEEL engine in your application then have a look at
the [Developer Guide](./developer-guide/developer-guide-introduction.md).

## FEEL REPL

The easiest way to try out your FEEL expressions in development is to use the REPL
(Read-Eval-Print-Loop) of the FEEL engine. It is a simple script based
on [Ammonite](http://ammonite.io/) (aka Scala Scripting) that downloads the dependency to the FEEL
engine and initialize it for you.

![the-feel-repl](./assets/feel-repl.png)

### Install

* Download Ammonite: http://ammonite.io/#Ammonite-REPL
  * On Linux:
  ```
  sudo sh -c '(echo "#!/usr/bin/env sh" && curl -L https://github.com/com-lihaoyi/Ammonite/releases/download/2.4.0/2.13-2.4.0) > /usr/local/bin/amm && chmod +x /usr/local/bin/amm' && amm
  ```
  * On Mac:  
  ```
  brew install ammonite-repl
  ```
* Download the script [feel-repl.sc](https://raw.githubusercontent.com/camunda/feel-scala/master/feel-repl.sc) or clone the [Git repository](https://github.com/camunda/feel-scala)

### Usage

Run the following script to start the REPL:
```
amm --predef feel-repl.sc
```

In the REPL, use one of the following functions to evaluate a FEEL expression:

```scala
feel("1 + 3")
// evaluate an expression without any context

val context = Map("x" -> 3)
feel("1 + x", context)
// evaluate an expression with a map-based context

feel("1 + x", "{ \"x\": 3}")
// evaluate an expression with a JSON context

feel(""" date("2020-04-06") + duration("P3D") """)
// evaluate an expression ignoring any quotes in the expression

//----------------------------------------------------------------------

unaryTests("> 3", 5)
// evaluate a unary-tests with a given input value

unaryTests("> 3", 5)
// evaluate a unary-tests with a given input value

val context = Map("x" -> 3)
unaryTests("> x", 5, context)
// evaluate a unary-tests with a given input value and map-based context

unaryTests("> x", "5", "{ \"x\": 3}")
// evaluate a unary-tests with a given JSON input value and JSON context
```
