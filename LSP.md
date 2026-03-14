# FEEL-Scala LSP Server (experimental)

This repository includes an experimental Language Server Protocol (LSP) server for FEEL-Scala over stdio.

## What it gives you in an editor

The FEEL-Scala LSP server helps you catch FEEL issues while editing and explore expressions faster, with:

- diagnostics from the FEEL parser and interpreter
- completions for FEEL code
- hover information
- semantic token highlighting (`keyword`, `function`, `variable`, `string`, `number`)

During `initialize`, the offered FEEL version is exposed via `capabilities.experimental.feelLanguageVersion`.

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
