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
  const [result, setResult] = React.useState("");
  const [error, setError] = React.useState(null);

  function evaluate() {
    const parsedContext = feelContext ? JSON.parse(context) : {};
    axios
      .post(
        "http://34.138.73.115/process/start",
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
          setResult(null);
          setError(JSON.stringify(response.data.error));
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
      <CodeBlock title={error != null && "Error"} language="json">
        {result || error}
      </CodeBlock>
    </div>
  );
};

export default LiveFeel;
