import React from "react";
import axios from "axios";
import Editor from "@site/src/components/Editor";
import CodeBlock from '@theme/CodeBlock';

const LiveFeel = ({ children, feelContext }) => {
  const [expression, setExpression] = React.useState(children);
  const [context, setContext] = React.useState(
    JSON.stringify(JSON.parse(feelContext), null, 2)
  );
  const [result, setResult] = React.useState("");

  function evaluate() {
    axios
      .post(
        "http://34.138.73.115/process/start",
        {
          expression: expression,
          context: JSON.parse(context),
          metadata: {
            user: "foo",
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
        setResult(JSON.stringify(response.data));
      });
  }

  return (
    <div>
      <h2>Expression</h2>
      <Editor onChange={setExpression} language="js">{expression}</Editor>

      <h2>Context</h2>
      <Editor onChange={setContext} language="json">{context}</Editor>

      <button
        onClick={evaluate}
        class="button button--primary button--lg"
      >
        Evaluate
      </button>

      <br/><br/>
      <h2>Result</h2>
      <CodeBlock language="json">
        {result}
      </CodeBlock>
    </div>
  );
};

export default LiveFeel;
