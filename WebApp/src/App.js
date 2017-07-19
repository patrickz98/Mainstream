import React, { Component } from "react";

import { Line, Bar } from "react-chartjs-2";

// import Utils from "./Utils";
import TopList from "./TopList";

const collection = "tagsCount";
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
            labels: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
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
                    data: [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]
                }
            ]
        };

        this.state = {
            topBarData: topBarData,
            metaData: null
        };
    }

    updateData(responseJson)
    {
        var index = 0;
        var labels = [];
        var data = [];

        for (var tag in responseJson[ collection ][ "data" ])
        {
            labels[ index ] = tag;
            data[ index ] = responseJson[ collection ][ "data" ][ tag ][ Utils.getDate() ];

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
        this.setState({metaData: responseJson[ collection ]});
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

        return (
            <div>
                <center>
                    <div style={{padding: 0, width: 1200, height:450}}>
                        <Bar
                            data={this.state.topBarData}
                            options={options}/>
                        <TopList metaData={this.state.metaData} />
                    </div>
                </center>
            </div>
        );
    }
}

export default App;
