---
title: Engine Reference: How the Engine works
---

## How the Engine works

![BPMN](how-the-engine-works.png)

1) the parser transform the expression into an internal structure (i.e. the AST)
2) the interpreter processes the stucture with the given variables
  * when a variable is accessed, it is transformed into an internal FEEL data type using the configured Value Mapper   
  * when a function is invoked, it looks for the function
    * in the current scope 
    * in the parent scope
    * in built-in scope (i.e. built-in functions)
    * or request the Function Providers
3) the result is transformed using the configured Value Mapper  
