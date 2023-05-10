import React, { useEffect } from "react";
import axios from "axios";

const FeelPlaygroundVersion = ({

}) => {
  const [version, setVersion] = React.useState("?");

  function loadVersion() {
    axios
        .get(
            "https://feel.upgradingdave.com/api/v1/version",
            {
              headers: {
                accept: "*/*",
                "content-type": "application/json",
              },
            }
        )
        .then((response) => {

          console.log(response?.data)

          if (response?.data?.feelEngineVersion) {
            const feelEngineVersion = response.data.feelEngineVersion;

            setVersion(feelEngineVersion);
          }
        });
  }

  // This function will called only once on first load
  useEffect(() => {
    loadVersion();
  }, [])

  return (
      <code>{version}</code>
  );
};

export default FeelPlaygroundVersion;
