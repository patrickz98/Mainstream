import React, { Component } from "react";
import { Bar, Line } from "react-chartjs-2";

const height = 100;

class TopTag extends Component
{
    constructor(props)
    {
        super(props);

        var meta = props.data;
        var labels = [];
        var data = [];

        console.log(meta);

        var inx = 0;
        for (var key in meta)
        {
            // const tag = meta[ "top" ][ inx ];
            labels[ inx ] = key;
            data[ inx ] = meta[ key ];

            inx++;
        }

        const chartData = {
            labels: labels,
            datasets: [
                {
                    label: props.title,
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

        console.log(chartData);

        this.state = {
            wiki: {},
            chartData: chartData
        };
    }

    getWiki()
    {
        // const wikiCallback = function(responseJson)
        // {
        //     this.setState({wiki: responseJson});
        // };

        // const url = "http://odroid-ubuntu.local:28017/MainStream/wiki/?filter_key=" + this.props.title;
        const url = "http://localhost/?q=" + this.props.title;

        return fetch(url)
            .then((response) => response.json())
            .then((responseJson) => {
                this.setState({wiki: responseJson});
                return responseJson;
            })
            .catch((error) => {
                console.error(error);
            });
    }

    componentDidMount()
    {
        console.log("componentDidMount");
        this.getWiki();
    }

    getCircle()
    {
        const style = {
            position: "absolute",
            // position: "relative",
            display: "inline-block",
            backgroundColor: "#323232",
            borderRadius: 1000,
            width: height,
            height: height,
            overflow: "hidden"
        };

        var imgStyle = {
            width: height + "px",
            height: "auto",
            // position: "absolute"
        };

        var src = "";
        var wiki = this.state.wiki;

        if (("rows" in wiki) && (this.state.wiki.rows.length > 0))
        {
            var row = this.state.wiki.rows[ 0 ];

            if ("thumbnail" in row)
            {
                var tmp = row[ "thumbnail" ];
                src = tmp[ "source" ];

                if (tmp[ "height" ] < tmp[ "width" ])
                {
                    imgStyle.width = "auto";
                    imgStyle.height = height + "px";
                }
            }
        }

        // var row = this.state.wiki.rows[ 0 ];
        // console.log(row);
        //
        // if (this.state.wiki.rows[ 0 ].hasOwnProperty("thumbnail"))
        // {
        //     src = row[ "source" ];
        //
        //     console.log(src);
        // }

        return (
            <div style={style}>
                <center>
                    <img style={imgStyle} src={src}></img>
                </center>
            </div>);
    }

    render()
    {
        const inline = {
            padding: 0,
            paddingRight: 20,
            margin: 0,
            lineHeight: height + "px",
            position: "absolute",
            display: "inline-block",
            width: 400,
            height: "100%",
            backgroundColor: "#ffffff"
        };

        const divStyle = {
            textAlign: "left",
            paddingTop: 20,
            paddingBottom: 20,
            // height: height,
            overflow: "hidden",
        };

        // <h3 style={inline}>{JSON.stringify(this.props.data)}</h3>
        // <h3 style={inline}>{JSON.stringify(this.state.wiki)}</h3>

        const options = {
            bezierCurve: false,
            padding: 20,
            animation: {
                duration: 0
            },
            maintainAspectRatio: false,
            legend: {
                display: false
            },
            scales: {
                yAxes: [{
                    gridLines: {
                        display:false
                    },
                    ticks: {
                        display: false
                    }
                }],
                xAxes: [{
                    gridLines: {
                        display:false
                    }
                }]
            }
        };

        // <Line
        //     data={this.state.chartData}
        //     options={options}/>

        return (
            <div style={divStyle}>
                {this.getCircle()}
                <div style={{left: height+20, display: "inline-block", backgroundColor: "#ffffff", position: "relative", height: height, width: "100%"}}>
                    <h1 style={inline}>
                        <span>{this.props.title}</span>
                    </h1>
                    <div
                        style={{
                            position: "relative",
                            display: "inline-block",
                            backgroundColor: "#ffffff",
                            left: 400,
                            width: "100%",
                            height: height,
                            overflow: "hidden"
                        }}>
                    </div>
                </div>
                    <div
                        style={{
                            // padding: 40,
                            // margin: 40,
                            // paddingLeft: height + 20,
                            position: "relative",
                            backgroundColor: "#ffffff",
                            // left: height + 20,
                            width: "100%",
                            // width: 800,
                            height: 200,
                            overflow: "hidden"
                        }}>
                        <div style={{padding: 20}}>
                            <Line
                                data={this.state.chartData}
                                options={options}/>
                        </div>
                    </div>
            </div>
        );
    }
}

export default TopTag;
