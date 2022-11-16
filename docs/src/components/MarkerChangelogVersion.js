import React from 'react';

export const MarkerChangelogVersion = ({versionZeebe, versionC7}) => {
  return (
      <p>
        <span style={{
          backgroundColor: '#26D07C',
          borderRadius: '7px',
          color: '#fff',
          padding: '0.2rem',
          marginRight: '0.5rem'
        }}
              title={"Available since the given Camunda Platform 8 (Zeebe) version."}>Zeebe: {versionZeebe}</span>
        <span style={{
          backgroundColor: '#0072CE',
          borderRadius: '7px',
          color: '#fff',
          padding: '0.2rem',
        }}
              title={"Available since the given Camunda Platform 7 version."}>Camunda Platform: {versionC7}</span>
      </p>
  );
}

export default MarkerChangelogVersion;