# AGENTS.md

This file provides context for AI coding agents working on this project.

## Project Overview

**FEEL Scala** is a parser and interpreter for FEEL (Friendly Enough Expression Language), the expression language defined in the DMN specification. It is officially maintained by [Camunda](https://camunda.org/) and integrated into:

- Camunda 7 (DMN engine)
- Camunda 8 / Zeebe (expression language)
- DMN-Scala

## Tech Stack

- **Language**: Scala 2.13.18
- **Build Tool**: sbt (with cross-compilation support)
- **Platforms**: JVM, JavaScript (Scala.js), Native (Scala Native)
- **Parser**: [FastParse](https://com-lihaoyi.github.io/fastparse/)
- **Testing**: ScalaTest
- **Code Formatting**: Scalafmt

## Project Structure

```
feel-scala/
├── core/                     # Main FEEL engine
│   ├── shared/               # Cross-platform code (JVM, JS, Native)
│   │   └── src/main/scala/org/camunda/feel/
│   │       ├── api/          # Public API (FeelEngineApi, EvaluationResult, etc.)
│   │       ├── context/      # Context, CustomContext, FunctionProvider
│   │       ├── impl/         # Internal implementation (NOT public API)
│   │       │   ├── builtin/  # Built-in functions by type
│   │       │   ├── interpreter/  # FeelInterpreter, BuiltinFunctions
│   │       │   └── parser/   # FeelParser, ExpressionValidator
│   │       ├── syntaxtree/   # Exp (AST), Val (values), ParsedExpression
│   │       └── valuemapper/  # ValueMapper, CustomValueMapper
│   ├── js/                   # JS-specific code
│   │   └── src/main/scala/org/camunda/feel/
│   │       ├── JSFeelEngine.scala      # JS-exported engine wrapper
│   │       └── valuemapper/JSValueMapper.scala  # JS<->FEEL value conversion
│   ├── jvm/                  # JVM-specific code (script engines, Java interop)
│   └── native/               # Native-specific code
├── cli/                      # Command-line interface
├── docs/                     # Docusaurus documentation site
└── project/                  # sbt configuration
    └── plugins.sbt           # sbt plugins
```

## Build Commands

### sbt Commands

```bash
# Compile all platforms
sbt compile

# Run tests on all platforms
sbt test

# Run tests for a specific platform
sbt coreJVM/test
sbt coreJS/test
sbt coreNative/test

# Format code (auto-runs on compile)
sbt scalafmt
sbt "Test / scalafmt"

# Check formatting without applying
sbt scalafmtCheck

# JS-specific: Compile and copy to feel-playground
sbt coreJS/installJS
```

### Custom sbt Tasks

- **`installJS`**: Compiles JS output (`fastLinkJS`) and copies `.js` and `.js.map` files to `../feel-playground/vendor`

### Documentation

```bash
cd docs
npm install
npm run start    # Development server with hot reload
npm run build    # Production build
```

## Code Style

### Scalafmt Configuration

- Max column: 100
- Alignment: `most` preset
- Dialect: Scala 2.13

Formatting runs automatically on `compile` and `test:compile`.

### Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>: <description>

<optional body>
```

Types: `feat`, `fix`, `refactor`, `test`, `docs`, `style`, `build`, `ci`

Example:
```
feat: add random() function

* add new built-in function random()
* it returns a random number between 0.0 and 1.0
```

## Architecture

```
FeelEngineBuilder --> FeelEngineApi --> FeelEngine
                                            |
                                            v
                    FeelParser --> Exp (AST) --> FeelInterpreter --> Val (result)
                                                        |
                                                        v
                                                 BuiltinFunctions
```

Key classes:

| Class | Location | Purpose |
|-------|----------|---------|
| `FeelEngineApi` | `api/` | Public API entry point |
| `FeelEngine` | `shared/` | Core engine orchestration |
| `FeelParser` | `impl/parser/` | Expression parsing (FastParse) |
| `Exp` | `syntaxtree/` | AST node types |
| `FeelInterpreter` | `impl/interpreter/` | Expression evaluation |
| `Val` | `syntaxtree/` | FEEL value types (ValNumber, ValString, etc.) |
| `ValueMapper` | `valuemapper/` | Convert between FEEL values and host language |

## JS Platform Specifics

### JSFeelEngine

The `JSFeelEngine` class (`core/js/`) is exported for JavaScript usage:

```javascript
const engine = new JSFeelEngine();
const result = engine.evaluate("1 + 2", {});

if (result.isSuccess) {
  console.log(result.result);  // 3
} else {
  console.error(result.failure);
}
```

### JSValueMapper

Handles bidirectional conversion between JavaScript and FEEL values:

**`toVal` (JS -> FEEL)**:
- `null`, `undefined` -> `ValNull`
- `Boolean` -> `ValBoolean`
- `Number` (Int, Long, Float, Double) -> `ValNumber`
- `String` -> `ValString`
- `js.Array` / `Seq` -> `ValList`
- `js.Object` / `Map` -> `ValContext`
- `Option` -> unwrapped or `ValNull`

**`unpackVal` (FEEL -> JS)**:
- `ValNumber` -> `Double`
- `ValDate`, `ValTime`, etc. -> String representations
- `ValContext` -> `js.Dictionary`
- `ValList` -> `js.Array`
- `ValFunction` -> callable `js.Function1`
- `ValError` / `ValFatalError` -> JS object with error info

### Important: `instanceof` Doesn't Work

Scala.js exported classes don't support JavaScript `instanceof`. Use the `isSuccess` boolean field instead:

```javascript
// DON'T do this (won't work):
if (result instanceof SuccessfulEvaluationResult) { ... }

// DO this instead:
if (result.isSuccess) { ... }
```

## Testing

### Test Organization

- **Shared tests**: `core/shared/src/test/scala/` (run on all platforms)
- **JVM-specific**: `core/jvm/src/test/scala/`
- **JS-specific**: `core/js/src/test/scala/`

### Test Patterns

```scala
class BuiltinStringFunctionsTest extends AnyFlatSpec with Matchers {
  
  "A reverse() function" should "return a string in reverse order" in {
    // Test implementation
  }
  
  it should "return null if the argument is not a string" in {
    // Additional test case
  }
}
```

For error verification:
```scala
should (returnNull() and reportFailure(
  FUNCTION_INVOCATION_FAILURE, 
  "Failed to invoke function 'reverse': something went wrong"
))
```

## Common Development Tasks

### Adding a Built-in Function

1. Implement in `core/shared/src/main/scala/org/camunda/feel/impl/builtin/<Type>BuiltinFunctions.scala`
2. Write tests in `core/shared/src/test/scala/org/camunda/feel/impl/builtin/Builtin<Type>FunctionsTest.scala`
3. Run `sbt test` to verify
4. Document in [Camunda docs](https://docs.camunda.io/docs/next/components/modeler/feel/builtin-functions/)

### Modifying the Parser

1. Extend AST in `syntaxtree/Exp.scala`
2. Update parser in `impl/parser/FeelParser.scala`
3. Handle new expression in `impl/interpreter/FeelInterpreter.scala`
4. Add tests

### Modifying the Interpreter

1. Find relevant case in `impl/interpreter/FeelInterpreter.scala` (`def eval(expression: Exp)`)
2. Modify behavior
3. Update/add tests in `impl/interpreter/Interpreter*Test.scala`

## Public API Stability

Classes outside `impl` packages are public API and must remain backward compatible:

- `FeelEngine`
- `FeelEngineApi`, `FeelEngineBuilder`
- `EvaluationResult`, `SuccessfulEvaluationResult`, `FailedEvaluationResult`
- `ValueMapper`, `CustomValueMapper`
- `Context`, `CustomContext`, `FunctionProvider`

**Supported environments**:
- Java 11+ (Camunda 7)
- Java 21 (Camunda 8)

## Dependencies

Key dependencies (defined in `build.sbt`):

- `fastparse` - Parser combinators
- `ujson` - JSON handling
- `scala-java-time` - Cross-platform date/time (JS/Native)
- `scalatest` - Testing framework
- `log4j` - Logging (JVM only)

## Integration with feel-playground

The `installJS` task copies compiled JS to `../feel-playground/vendor`:

```bash
sbt coreJS/installJS
```

This outputs:
- `main.js` - Compiled FEEL engine
- `main.js.map` - Source maps for debugging

## Useful Resources

- [FEEL Documentation](https://docs.camunda.io/docs/next/components/modeler/feel/language-guide/feel-expressions-introduction/)
- [FEEL Playground](https://camunda.github.io/feel-scala/docs/playground/)
- [FastParse Documentation](https://com-lihaoyi.github.io/fastparse/)
- [Scala.js Documentation](https://www.scala-js.org/doc/)
