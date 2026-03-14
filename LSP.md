# FEEL LSP Server (experimental)

This repository includes an experimental Language Server Protocol (LSP) server for FEEL over stdio.

## What it is

The FEEL LSP server provides editor features backed by the FEEL parser and interpreter:

- initialize/lifecycle
- text document sync (`didOpen`, `didChange`, `didClose`)
- diagnostics
- completion
- hover

During `initialize`, the offered FEEL version is exposed via `capabilities.experimental.feelLanguageVersion`.

## Build and run

Build the project:

```bash
./mvnw clean package
```

Run the shaded artifact (recommended):

```bash
java -jar target/feel-engine-<version>-complete.jar
```

The process speaks JSON-RPC over stdin/stdout and stays running while your editor is connected.

Optional: build an additional thin artifact:

```bash
./mvnw -PthinJar clean package
```

This additionally produces `target/feel-engine-<version>-thin.jar` for controlled runtime environments.

## IntelliJ setup (LSP plugin)

If your IntelliJ LSP plugin only supports `Command` and `Environment`, use a small wrapper script.

Create script (example for macOS/zsh):

```bash
mkdir -p "$HOME/bin"
cat > "$HOME/bin/feel-lsp" <<'EOF'
#!/usr/bin/env zsh
exec /usr/bin/java \
  -jar "/absolute/path/to/feel-scala/target/feel-engine-<version>-complete.jar"
EOF
chmod +x "$HOME/bin/feel-lsp"
```

Configure the plugin:

- Command: `$HOME/bin/feel-lsp`
- Environment: optional (can be empty)
- File pattern / language mapping: `*.feel`

## VS Code setup

VS Code does not provide a built-in UI for attaching arbitrary external language servers.
Use one of these options:

- an LSP client extension that lets you configure a command for `*.feel`
- a small VS Code extension using `vscode-languageclient`

Minimal `vscode-languageclient` setup:

```ts
import * as path from "path";
import { ExtensionContext } from "vscode";
import { LanguageClient, ServerOptions, TransportKind } from "vscode-languageclient/node";

let client: LanguageClient;

export function activate(context: ExtensionContext) {
  const jar = path.join(context.extensionPath, "server", "feel-engine-<version>-complete.jar");

  const serverOptions: ServerOptions = {
    command: "java",
    args: ["-jar", jar],
    transport: TransportKind.stdio
  };

  client = new LanguageClient(
    "feel-lsp",
    "FEEL LSP",
    serverOptions,
    { documentSelector: [{ scheme: "file", language: "feel" }] }
  );

  context.subscriptions.push(client.start());
}
```

## Integrate with any LSP client

To connect FEEL LSP from any editor/tooling that supports external language servers:

1. Start `java -jar ...-complete.jar` as a stdio process.
2. Send standard LSP `initialize` / `initialized` requests.
3. Send text sync notifications for FEEL documents.
4. Read diagnostics and completion/hover responses over JSON-RPC.

No custom transport is required; standard LSP over stdio is enough.

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

A working setup is expected to report these 5 problems:

- `FUNCTION_INVOCATION_FAILURE`: Failed to invoke function `substring`: Illegal arguments: `123`, `1`, `2`
- `INVALID_TYPE`: Can't add `1` to `null`
- `INVALID_TYPE`: Can't divide `1` by `0`
- `INVALID_TYPE`: Can't subtract `2` from `"abc"`
- `NO_VARIABLE_FOUND`: No variable found with name `unknownVar`

