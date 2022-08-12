import React from "react";
import Envelope from "@site/src/components/Envelope";
import LiveFeel from "@site/src/components/LiveFeel";

const EnvelopeAddress = ({ defaultExpression, feelContext, metadata }) => {
  const defaultName = "< Fill in the name here >";

  const [result, setResult] = React.useState(defaultName);

  return (
    <div>
      <LiveFeel
        defaultExpression={defaultExpression}
        feelContext={feelContext}
        metadata={metadata}
        onResultCallback={(result) => setResult(result.replaceAll('"', ""))}
        onErrorCallback={(_error) => setResult(defaultName)}
      />

      <Envelope addressName={result} />
    </div>
  );
};

export default EnvelopeAddress;
