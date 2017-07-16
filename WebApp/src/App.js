import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';

import {Line} from 'react-chartjs-2';

class App extends Component
{
    getData()
    {
        
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

        // var Chart = require('chart.js');
        //
        // var myLineChart = new Chart(ctx, {
        //     type: 'line',
        //     data: data,
        //     options: options
        // });

        // <LineChart data={chartData} options={chartOptions} width="600" height="250"/>

        // const data = {
        //     labels: ["Red", "Blue", "Yellow", "Green", "Purple", "Orange1234"],
        //     datasets: [
        //         {
        //             label: '# of Votes',
        //             data: [12, 19, 3, 5, 2, 3],
        //             backgroundColor: [
        //                 'rgba(255, 99, 132, 0.2)',
        //                 'rgba(54, 162, 235, 0.2)',
        //                 'rgba(255, 206, 86, 0.2)',
        //                 'rgba(75, 192, 192, 0.2)',
        //                 'rgba(153, 102, 255, 0.2)',
        //                 'rgba(255, 159, 64, 0.2)'
        //             ],
        //             borderColor: [
        //                 '#ff6384',
        //                 'rgba(54, 162, 235, 1)',
        //                 'rgba(255, 206, 86, 1)',
        //                 'rgba(75, 192, 192, 1)',
        //                 'rgba(153, 102, 255, 1)',
        //                 'rgba(255, 159, 64, 1)'
        //             ],
        //             borderWidth: 2
        //         }
        //     ]
        // };

        var data = {
            labels: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
            datasets: [
                {
                    label: "My Second dataset",
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

        return (
            <div>
                <h1>Hallo Welt</h1>
                <div style={{width: 800, height:250}}>
                    <Line
                        data={data}
                        options={{
                            maintainAspectRatio: false
                        }}/>
                </div>
            </div>
        );
    }
}

export default App;
