import React from "react";
import EnvelopeImg from '@site/static/img/envelope.png';

const containerStyle = {
  position : 'relative',
  textAlign: 'left',
  color: 'black'
}

const topLeftStyle = {
  position : 'absolute',
  top: '10px',
  left: '16px'
}

const centeredStyle = {
  position : 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)'
}

class Envelope extends React.Component {

  constructor(props) {
    super(props);
  }

  render() {

    return (
        <div style={containerStyle}>
          <img src={EnvelopeImg}></img>
          <div style={topLeftStyle}>
            Camundonaut<br/>
            Camunda Services GmbH<br/>
            Zossener Str. 55<br/>
            10961 Berlin<br/>
            Germany
          </div>
          <div style={centeredStyle}>
            {this.props.addressName}<br/>
            Camunda Inc.<br/>
            INDUSTRY Denver<br/>
            3001 Brighton Blvd, Suite 450<br/>
            Denver, CO 80216<br/>
            USA
          </div>


        </div>
    );
  }

}

export default Envelope;
