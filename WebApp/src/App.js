import React, { Component } from "react";
import logo from "./logo.svg";
import "./App.css";

import { Line, Bar } from "react-chartjs-2";

// const collection = "tagsCount";
// const collection = "tagsCountLocation";
// const collection = "tagsCountMisc";
// const collection = "tagsCountOrganization";
const collection = "tagsCountPerson";

class App extends Component
{
    constructor()
    {
        super();

        this.state = {
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
    }

    getdate()
    {
        const date = new Date();
        var day   = "" + date.getDate();
        var month = "" + (date.getMonth() + 1);
        var year  = date.getFullYear();

        if (day.length   == 1) day   = "0" + day;
        if (month.length == 1) month = "0" + month;

        return parseInt(year + month + day);
    }

    updateData(responseJson)
    {
        console.log("Done");
        console.log(JSON.stringify(responseJson));

        var index = 0;
        var labels = [];
        var data = [];

        for (var tag in responseJson[ collection ][ "data" ])
        {
            labels[ index ] = tag;
            data[ index ] = responseJson[ collection ][ "data" ][ tag ][ this.getdate() ];

            index++;
        }

        // {
        //     label: "Count",
        //     backgroundColor: "rgba(151,187,205,0.2)",
        //     borderColor: "rgba(151,187,205,1)",
        //     pointBackgroundColor: "rgba(151,187,205,1)",
        //     pointBorderColor: "rgba(255, 255, 255, 1)",
        //     pointStrokeColor: "#ffffff",
        //     pointHighlightFill: "#ffffff",
        //     pointHighlightStroke: "rgba(151,187,205,1)",
        //     data: data
        // }

        this.setState({
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
        });
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
        console.log("componentDidMount");
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

        var labels = this.state[ "labels" ];

        const graphClickEvent = function(event, array)
        {
            console.log("graphClickEvent");

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
            },
            // tooltips: {
            //     enabled: false
            // },
            // scales: {
            //     yAxes: [
            //         {
            //             stacked: true,
            //             display: true,
            //             gridLines: {
            //                 display: false
            //             }
            //         }
            //     ],
            //     xAxes: [
            //         {
            //             stacked: true,
            //             gridLines: {
            //                 display: false
            //             },
            //             ticks: {
            //                 beginAtZero: true
            //             }
            //         }
            //     ]
            // }
        };

        return (
            <div>
                <center>
                    <div style={{padding: 20, width: 1200, height:450}}>
                        <Bar
                            data={this.state}
                            options={options}/>
                    </div>
                </center>
            </div>
        );
    }
}

export default App;
