import React, { Component } from "react";

import { Line, Bar } from "react-chartjs-2";

// import Utils from "./Utils";
import TopList from "./TopList";
import Menu from "./Menu";

// const collection = "tagsCount";
// const collection = "tagsCountLocation";
// const collection = "tagsCountMisc";
// const collection = "tagsCountOrganization";
// const collection = "tagsCountPerson";

const Utils = require("./Utils");

class App extends Component
{
    constructor()
    {
        super();

        const topBarData = {
            labels: [],
            datasets: [
                {
                    label: "Count",
                    backgroundColor: "rgba(151,187,205,0.2)",
                    borderColor: "rgba(151,187,205,1)",
                    pointBackgroundColor: "rgba(151,187,205,1)",
                    pointBorderColor: "rgba(255, 255, 255, 1)",
                    pointStrokeColor: "#ffffff",
                    pointHighlightFill: "#ffffff",
                    pointHighlightStroke: "rgba(151,187,205,1)",
                    data: []
                }
            ]
        };

        this.state = {
            topBarData: topBarData,
            metaData: null,
            tag: "tagsCount"
        };

        this.handler = this.handler.bind(this)
    }

    updateData(responseJson)
    {
        var index = 0;
        var labels = [];
        var data = [];

        for (var tag in responseJson[ this.state.tag ][ "data" ])
        {
            labels[ index ] = tag;
            data[ index ] = responseJson[ this.state.tag ][ "data" ][ tag ][ Utils.getDate() ];

            index++;
        }

        const topBarData = {
            labels: labels,
            datasets: [
                {
                    label: "Count",
                    backgroundColor: "rgba(0, 170, 255, 1)",
                    borderColor: "rgba(0, 170, 255, 1)",
                    pointBackgroundColor: "rgba(0, 170, 255, 1)",
                    pointBorderColor: "rgba(255, 255, 255, 1)",
                    pointStrokeColor: "#ffffff",
                    pointHighlightFill: "#ffffff",
                    pointHighlightStroke: "rgba(151,187,205,1)",
                    data: data
                }
            ]
        };

        this.setState({topBarData: topBarData});
        this.setState({metaData: responseJson[ this.state.tag ]});
    }

    update()
    {
        // fetch("https://facebook.github.io/react-native/movies.json")
        // fetch("http://localhost:8080/?q=SPD")
        // fetch("https://codepen.io/jobs.json")
        return fetch("http://localhost:8080")
        .then((response) => response.json())
        .then((responseJson) => {
            this.updateData(responseJson);
            return responseJson;
        })
        .catch((error) => {
            console.error(error);
        });
    }

    componentDidMount()
    {
        this.update();
    }

    handler(str)
    {
        console.log("######## " + str);
        this.setState({tag: str});
        console.log(this.state);

        this.state.tag = str;
        console.log(this.state);
    }

    render()
    {
        //   <div className="App">
        //     <div className="App-header">
        //       <img src={logo} className="App-logo" alt="logo" />
        //       <h2>Welcome to React</h2>
        //     </div>
        //     <p className="App-intro">
        //       To get started, edit <code>src/App.js</code> and save to reload.
        //     </p>
        //   </div>

        var labels = this.state.topBarData[ "labels" ];

        const graphClickEvent = function(event, array)
        {
            if(! array[ 0 ]) return;

            var clicked = labels[ array[ 0 ][ "_index" ] ];

            console.log("clicked: " + clicked);

            window.open("http://odroid-ubuntu.local:28017/MainStream/wiki/?filter_key=" + clicked);
        };

        const options = {
            maintainAspectRatio: false,
            onClick: graphClickEvent,
            legend: {
                display: false
            }
        };

        const menuHeight = 60;

        const menuStyle = {
            width: "100%",
            height: menuHeight,
            backgroundColor: "#24292e",
            position: "absolute"
        };

        const bodyStyle = {
            left: 0,
            top: menuHeight,
            right: 0,
            bottom: 0,
            paddingTop: 20,
            overflow: "auto",
            backgroundColor: "#ffffff",
            position: "absolute"
        };

        const topDivStyle = {
            left: 0,
            top: 0,
            right: 0,
            bottom: 0,
            margin: 0,
            padding: 0,
            backgroundColor: "#82eb80",
            position: "absolute"
        };

        const test = {
            padding: 0,
            margin: 0,
            // width: "100%",
            left: 100,
            right: 100,
            height: 450,
            position: "absolute"
        };

        return (
            <div style={topDivStyle}>
                <Menu title={this.state.tag} handler={this.handler.bind(this)}/>

                <div style={bodyStyle}>
                    <center>
                        <div style={test}>
                            <Bar
                                data={this.state.topBarData}
                                options={options}/>
                            <TopList metaData={this.state.metaData} />
                        </div>
                    </center>
                </div>
            </div>
        );
    }
}

export default App;
