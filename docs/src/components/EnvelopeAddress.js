import React from "react";
import dedent from "dedent";
import Envelope from "@site/src/components/Envelope";
import LiveFeel from "@site/src/components/LiveFeel";

const EnvelopeAddress = () => {

  const defaultName = "< Fill in the name here >";
  
  const [result, setResult] = React.useState(defaultName);

  return (
    <div>
        <LiveFeel
            defaultExpression={dedent`
              // concatenate the first and the last name
              firstName`}
            feelContext='{"firstName":"?", "lastName":"?"}'
            metadata={{ page: "tutorial-2-1" }}
            onResultCallback={ setResult }
            onErrorCallback={ _error => setResult(defaultName) }
        />

        <Envelope addressName={result}/>
    </div>
  );
}

export default EnvelopeAddress;
