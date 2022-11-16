import React from 'react';

export const MarkerCamundaExtension = () => {
  return (
      <p>
        <span style={{
          backgroundColor: '#FC5D0D',
          borderRadius: '7px',
          color: '#fff',
          padding: '0.2rem',
          marginRight: '0.5rem'
        }}
              title={"This feature is not part of the official DMN standard. It is an extension from Camunda's implementation."}>Camunda Extension</span>
      </p>
  );
}

export default MarkerCamundaExtension;