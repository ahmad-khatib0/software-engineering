import React from "react";
import axios from "axios";
import i18next from "i18next";

class App extends React.Component {
  state = {
    text: "",
  };
  lang = "en";
  componentDidMount = async () => {
    await i18next.changeLanguage(this.lang);
    axios.defaults.headers["Accept-Language"] = this.lang;
    const response = await axios.get("http://localhost:8000/text");
    this.setState({
      text: response.data.text,
    });
  };

  change = async (lang) => {
    this.lang = lang;
    await this.componentDidMount();
  };
  click = async () => {
    const response = await axios.get("http://localhost:8000/like");
    const text = await i18next.t(response.data.message);
    alert(text);
  };

  render() {
    return (
      <main className="container">
        <div className="row py-3">
          <div className="col-10"></div>
          <div className="col-2">
            <select
              className="form-control"
              onChange={(e) => this.change(e.target.value)}
            >
              <option value="en">English</option>
              <option value="de">Dutesch</option>
            </select>
          </div>
        </div>

        <div className="row">
          <div className="col-md 12">
            <h2>{i18next.t("title")}</h2>
            <p> {this.state.text}</p>
            <button className="btn btn-outline-primary" onClick={this.click}>
              {i18next.t("like")}
            </button>
          </div>
        </div>
      </main>
    );
  }
}

export default App;
