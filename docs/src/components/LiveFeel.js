import React from "react";
import axios from "axios";
import Editor from "@site/src/components/Editor";
import CodeBlock from "@theme/CodeBlock";

const LiveFeel = ({ defaultExpression, feelContext, metadata }) => {
  if (feelContext) {
    // format the context
    feelContext = JSON.stringify(JSON.parse(feelContext), null, 2);
  }

  const [expression, setExpression] = React.useState(defaultExpression);
  const [context, setContext] = React.useState(feelContext);
  const [result, setResult] = React.useState("<click 'Evaluate' to see the result of the expression>");
  const [error, setError] = React.useState(null);

  function evaluate() {
    const parsedContext = feelContext ? JSON.parse(context) : {};
    axios
      .post(
        "https://feel.upgradingdave.com/process/start",
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
        if (!response.data) {
          return;
        }
        if (response.data.result) {
          setError(null);
          setResult(JSON.stringify(response.data.result));
        } else if (response.data.error) {
          const errorMessage = JSON.stringify(response.data.error);
          const match = /^.+(?<line>\d+):(?<position>\d+).+$/gm.exec(
            errorMessage
          );
          setResult(null);
          setError({
            message: errorMessage,
            line: match?.groups?.line,
            position: match?.groups?.position,
          });
        }
      });
  }

  return (
    <div>
      <h2>Expression</h2>
      <Editor onChange={setExpression} language="js">
        {expression}
      </Editor>

      {feelContext && (
        <div>
          <h2>Context</h2>
          <i>A JSON document that is used to resolve <strong>variables</strong> in the expression.</i>
          <Editor onChange={setContext} language="json">
            {context}
          </Editor>
        </div>
      )}

      <button onClick={evaluate} className="button button--primary button--lg">
        Evaluate
      </button>

      <br />
      <br />
      <h2>Result</h2>
      <CodeBlock
        title={
          error && `Error on line ${error.line} at position ${error.position}`
        }
        language="json"
      >
        {result || error?.message}
      </CodeBlock>
    </div>
  );
};

export default LiveFeel;
