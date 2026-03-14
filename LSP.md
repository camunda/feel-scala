# FEEL-Scala LSP Server (experimental)

This repository includes an experimental Language Server Protocol (LSP) server for FEEL-Scala over stdio.

## What it gives you in an editor

The FEEL-Scala LSP server helps you catch FEEL issues while editing and explore expressions faster, with:

- diagnostics from the FEEL parser and interpreter
- completions for FEEL code
- hover information
- semantic token highlighting (`keyword`, `function`, `variable`, `string`, `number`)

Current `initialize` capabilities include:

- `textDocumentSync: 1` (full document sync)
- `hoverProvider: true`
- `completionProvider: {}`
- `semanticTokensProvider` with:
  - token types: `keyword`, `function`, `variable`, `string`, `number`
  - `range: false`
  - `full: true`
  - `id: feel-semantic-tokens`
- `experimental.feelLanguageVersion: 1.3`

## Diagnostic example (5 problems)

Use this FEEL expression:

```feel
{
  a: unknownVar + 1,
  b: "abc" - 2,
  c: substring(123, 1, 2),
  d: 1 / 0,
  e: date("2026-99-99")
}
```

In a working setup, this expression can report these 5 problems:

- `FUNCTION_INVOCATION_FAILURE`: Failed to invoke function `substring`: Illegal arguments: `123`, `1`, `2`
- `INVALID_TYPE`: Can't add `1` to `null`
- `INVALID_TYPE`: Can't divide `1` by `0`
- `INVALID_TYPE`: Can't subtract `2` from `"abc"`
- `NO_VARIABLE_FOUND`: No variable found with name `unknownVar`

## IntelliJ setup (LSP4IJ plugin)

Install the [LSP4IJ plugin](https://plugins.jetbrains.com/plugin/23257-lsp4ij) and configure a new LSP server for FEEL.

- Open **Settings**
- Go to **Languages & Frameworks > Language Server Protocol > Servers**
- Click **+** to add a new server
- Set **Name** to `FEEL-Scala LSP`
- Set **Command** to `java -jar /path/to/feel-engine-<version>.jar`
- Go to the **Mappings** tab and then the **file name patterns** tab and add a new mapping:
  - Set **File name patterns** to `*.feel`
  - Set **Language ID** to `feel`
- Click **OK** to save the server configuration

## Build and run

Build the LSP server from source:

```bash
./mvnw clean package
```

Run the LSP server as a standalone process:

```bash
java -jar target/feel-engine-<version>.jar
```

The process speaks JSON-RPC over stdin/stdout and stays running while your editor is connected.

Optional: build an additional thin artifact:

```bash
./mvnw -PthinJar clean package
```

This additionally produces `target/feel-engine-<version>-thin.jar` for controlled runtime environments.

## Integrate with any LSP client

To connect FEEL LSP from any editor/tooling that supports external language servers:

1. Start `java -jar target/feel-engine-<version>.jar` as a stdio process.
2. Send standard LSP `initialize` / `initialized` requests.
3. Send text sync notifications for FEEL documents.
4. Read diagnostics and completion/hover responses over JSON-RPC.

No custom transport is required; standard LSP over stdio is enough.

## Developer documentation

This section is for contributors who want to understand and extend the FEEL LSP server implementation.

### Current architecture

- Launcher entry point: `src/main/scala/org/camunda/feel/lsp/FeelLspLauncher.scala`
- Main Scala server: `src/main/scala/org/camunda/feel/lsp/server/FeelLanguageServer.scala`
- Text document handlers: `src/main/scala/org/camunda/feel/lsp/server/FeelTextDocumentService.scala`
- FEEL analysis logic: `src/main/scala/org/camunda/feel/lsp/analysis/FeelAnalyzer.scala`
- Per-document cache/state: `src/main/scala/org/camunda/feel/lsp/model/DocumentStore.scala`
- Java LSP bridge used by launcher: `src/main/java/org/camunda/feel/lsp/server/FeelLanguageServerJava.java`
- Java service forwarders: `src/main/java/org/camunda/feel/lsp/server/TextDocumentServiceForwarder.java`, `src/main/java/org/camunda/feel/lsp/server/WorkspaceServiceForwarder.java`

### Request and analysis flow

1. `didOpen` / `didChange` stores latest text in `DocumentStore`, runs `FeelAnalyzer.analyze`, and publishes diagnostics.
2. `didClose` removes document state and clears diagnostics for that URI.
3. `completion` and `hover` read cached document state and use analyzer helpers.
4. `semanticTokens/full` reads cached state and returns encoded semantic token data.
5. Stale `didChange` versions are ignored (`version < current.version`).

### Diagnostics behavior

- Parser failures are reported as LSP errors with parser range mapping.
- Interpreter suppressed failures are reported as LSP warnings.
- Interpreter warnings currently use a full-document range because no precise per-failure source span is available.

### Semantic token behavior

- Token legend is defined in `FeelAnalyzer.SemanticTokenTypes`.
- Token classification currently covers keywords, built-in function calls, known variables, string literals, and number literals.
- Full document tokens are supported; range tokens are intentionally disabled (`range: false`).
- Capability id is fixed to `feel-semantic-tokens`.

### Logging and troubleshooting

- Logging is configured in `src/main/scala/org/camunda/feel/lsp/server/FeelLspLogging.scala`.
- Set JVM property `feel.lsp.log.level` (for example `TRACE`) to inspect request flow.
- If semantic tokens are advertised but requests fail, verify Java forwarders include `semanticTokensFull` forwarding.

### Tests and change safety

- Protocol behavior tests: `src/test/scala/org/camunda/feel/lsp/FeelLanguageServerProtocolTest.scala`
- This test suite validates initialize capabilities, diagnostics lifecycle, completion, hover, stale version handling, and semantic token payloads.
- When changing token legend or classification, update both analyzer behavior and protocol expectations in the same change.

### How to extend

- Add new FEEL analysis behavior in `FeelAnalyzer` first.
- Expose new LSP functionality from `FeelTextDocumentService` and advertise capability in `FeelLanguageServer.initialize`.
- If the launcher path goes through Java bridge classes, ensure the corresponding Java forwarder delegates the new LSP method.
- Add or update protocol tests before finalizing the change.

