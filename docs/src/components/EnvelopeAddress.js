import React from "react";
import axios from "axios";
import Editor from "@site/src/components/Editor";
import Envelope from "@site/src/components/Envelope";

const friend = {
  firstName: '?',
  middleName: '?',
  lastName: '?',
  address: {
    company: 'Camunda Inc.',
    country: 'USA',
    building: 'INDUSTRY Denver',
    street: '3001 Brighton Blvd, Suite 450',
    cityStateZip: 'Denver, CO 80216'
  }
};

class EnvelopeAddress extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      expression: "firstName + \" \" + lastName",
      context: JSON.stringify(friend, null, 2),
      result: {
        addressName: ""
      },
    }

    this.debounce = this.debounce.bind(this);
    this.evaluate = this.evaluate.bind(this);
    this.setContext = this.setContext.bind(this);
    this.setExpression = this.setExpression.bind(this);
    this.setResult = this.setResult.bind(this);
  }

  debounce(func, timeout = 700){
    let timer;
    return (...args) => {
      clearTimeout(timer);
      timer = setTimeout(() => { func.apply(this, args); }, timeout);
    };
  }

  evaluate(expression, context, resultVarName) {
    axios
      .post(
        "https://feel.upgradingdave.com/process/start",
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
        let result = this.state.result;

        if (!response.data) {
          return;
        }
        if (response.data.result) {
          result[resultVarName] = JSON.stringify(response.data.result);
          this.setResult(result);
        } else if (response.data.error) {
          // the error could be displayed nicer
          result[resultVarName] = JSON.stringify(response.data.error);
          this.setResult(result);
        }
      });
  }

  setContext() {
    return this.debounce((text) => {
      this.setState({context: text});
      this.evaluate(this.state.expression, text, "addressName");
    });
  }

  setExpression() {
    return this.debounce((text) => {
      this.setState({expression: text});
      this.evaluate(text, this.state.context, "addressName");
    });
  }

  setResult(result) {
    this.setState({result: result});
  }

  componentDidMount() {
    this.evaluate(this.state.expression, this.state.context, "addressName");
  }

  render() {
    return (
      <div className={"container"}>
        <div className={"row"}>
          <p>
            Before he left on on his quest, Zee promised to keep in touch with friends back home and send them each a letter.
            Luckily, FEEL String functions are here to Help and save time.
          </p>
        </div>
        <div className={"row"}>
          <div className={"col col--6"}>
            <h5 style={{textAlign: "left"}}>Help Zee to send a letter by entering the first and last name of someone you know:</h5>
            <Editor onChange={this.setContext()} language="json">{this.state.context}</Editor>
          </div>
          <div className="col col--6">
            <h5>In FEEL, you can concatenate strings easily. Feel free to experiment:</h5>
            <Editor onChange={this.setExpression()} language="js">{this.state.expression}</Editor>
          </div>
        </div>
        <div className={"row"}>
          <Envelope addressName={this.state.result.addressName.replaceAll('"', '')}/>
        </div>
      </div>
    );
  }

}

export default EnvelopeAddress;
