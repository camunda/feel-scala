import React from "react";
import axios from "axios";
import Editor from "@site/src/components/Editor";

const LiveFeel = () => {
  const [expression, setExpression] = React.useState("3 + x");
  const [context, setContext] = React.useState(
    JSON.stringify({ x: 5 }, null, 2)
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
      <Editor onChange={setExpression}>{expression}</Editor>

      <h2>Context</h2>
      <Editor onChange={setContext}>{context}</Editor>

      <button onClick={evaluate}>Evaluate</button>

      <h2>{result}</h2>
    </div>
  );
};

export default LiveFeel;
