import React from "react";
import axios from "axios";
import Editor from "@site/src/components/Editor";
import CodeBlock from "@theme/CodeBlock";
import BrowserOnly from '@docusaurus/BrowserOnly';


const LiveFeel = ({
  defaultExpression,
  feelContext,
  metadata,
  onResultCallback,
  onErrorCallback,
}) => {
  const fromUrl = decodeFromUrl();

  let expressionContext = fromUrl.context ?? feelContext;
  if (expressionContext) {
    // format the context
    expressionContext = JSON.stringify(JSON.parse(expressionContext), null, 2);
  }

  const [expression, setExpression] = React.useState(fromUrl.expression ?? defaultExpression);
  const [context, setContext] = React.useState(expressionContext);
  const [result, setResult] = React.useState(
    "<click 'Evaluate' to see the result of the expression>"
  );
  const [error, setError] = React.useState(null);
  const [warnings, setWarnings] = React.useState(null);

  // https://regex101.com/r/WnWTtz/1
  const errorPattern = /^.+(?<line>\d+):(?<position>\d+).+$/gm;

  // https://regex101.com/r/jus80g/1
  const contextErrorPattern = /^.+at position (?<position>\d+)$/gm;

  const parseContext = () => {
    if (!feelContext  || context.trim().length === 0) {
      return {};
    }
    return JSON.parse(context);
  };

  function tryEvaluate() {
    try {
      // to indicate the progress
      setResult("<evaluating the expression...>");
      setWarnings(null)

      const parsedContext = parseContext();
      evaluate(parsedContext);
    } catch (err) {
      const match = contextErrorPattern.exec(err.message);
      onError({
        message: `failed to parse context: ${err.message}`,
        position: match?.groups?.position,
      });
    }
  }

  function evaluate(parsedContext) {
    axios
      .post(
          "https://feel.upgradingdave.com/api/v1/feel/evaluate",
        {
          expression: expression,
          context: parsedContext,
          metadata: {
            ...metadata,
          },
        },
        {
          headers: {
            accept: "*/*",
            "content-type": "application/json",
          },
        }
      )
      .then((response) => {
        if (response?.data?.error) {
          const errorMessage = response.data.error;
          const match = errorPattern.exec(errorMessage);
          onError({
            message: errorMessage,
            line: match?.groups?.line,
            position: match?.groups?.position,
          }, response.data.warnings);
        } else {
          onResult(response.data);
        }
      });
  }

  function onResult(data) {
    setError(null);

    const result = JSON.stringify(data.result);
    setResult(result);

    if (data.warnings.length >= 1) {
      setWarnings(data.warnings);
    }

    if (onResultCallback) {
      onResultCallback(result);
    }
  }

  function onError(error, warnings) {
    setResult(null);
    setError(error);
    setWarnings(warnings);

    if (onErrorCallback) {
      onErrorCallback(error);
    }
  }

  const resultTitle = () => {
    const onLine = error?.line ? ` on line ${error.line}` : "";
    const atPosition = error?.position ? ` at position ${error.position}` : "";
    return error && `Error${onLine}${atPosition}`;
  };

  function decodeFromUrl() {
    let decoded = {};

    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);

    if (urlParams.has("expression")) {
      decoded["expression"] = decodeUrlParameter(urlParams.get("expression"));
    }
    if (urlParams.has("context")) {
      decoded["context"] = decodeUrlParameter(urlParams.get("context"));
    }

    return decoded;
  }

  function encodeToUrl() {
    const path = window.location.href.split('?')[0];
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);

    urlParams.set("expression", encodeUrlParameter(expression));
    if (context) {
      urlParams.set("context", encodeUrlParameter(context));
    }
    urlParams.set("expression-type", "expression");

    return path + "?" + urlParams;
  }

  function encodeUrlParameter(parameter) {
    // parameter.toString("base64");
    return btoa(parameter);
  }

  function decodeUrlParameter(parameter) {
    // Buffer.from(parameter, "base64") - But no Buffer module available!?
    return atob(parameter);
  }

  function copyToClipboard() {
    navigator.clipboard.writeText(encodeToUrl());
  }

  return (
      <BrowserOnly>
        { () =>
          <div>
            <h2>Expression</h2>
            <Editor onChange={setExpression} language="js">
              {expression}
            </Editor>

            {feelContext && (
                <div>
                  <h2>Context</h2>
                  <i>
                    A JSON document that is used to
                    resolve <strong>variables</strong>{" "}
                    in the expression.
                  </i>
                  <Editor onChange={setContext} language="json">
                    {context}
                  </Editor>
                </div>
            )}

            <button
                onClick={tryEvaluate}
                className="button button--primary button--lg"
            >
              Evaluate
            </button>

            <button
                onClick={copyToClipboard}
                className="button button--secondary button--lg"
                title="Copy an URL to the clipboard for sharing the expression"
                style={{ "margin-left": "10px"}}
            >
              Share
            </button>

            <br/>
            <br/>
            <h2>Result</h2>
            <CodeBlock title={resultTitle()} language="json">
              {result || error?.message}
            </CodeBlock>
            <br/>
            <h2>Warnings</h2>
            <CodeBlock>
              {warnings?.map((item, i) =>
                  <li key={i}>[{item.type}] {item.message}</li>) || "<none>"}
            </CodeBlock>

          </div>
        }
      </BrowserOnly>
  );
};

export default LiveFeel;
