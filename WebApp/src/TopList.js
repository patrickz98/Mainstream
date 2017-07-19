import React, { Component } from "react";
import TopTag from "./TopTag";

const Utils = require("./Utils");

class TopList extends Component
{
    constructor()
    {
        super();
    }

    update()
    {
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
    }

    getTop()
    {
        const meta = this.props.metaData;

        if (meta == null) return;

        var html = [];

        for (var index in meta[ "top" ])
        {
            const str = meta[ "top" ][ index ];
            const dataSet = meta[ "data" ][ str ];

            html[ index ] = <TopTag title={str} data={dataSet} />;
        }

        return html;
    }

    render()
    {
        const divStyle = {
            // backgroundColor: "#5f9dde",
            fontFamily: "Arial"
        };

        const json = JSON.stringify(this.props.metaData);
        const topTags = this.getTop();

        return (
            <div style={divStyle}>
                <h1>Top List:</h1>
                {topTags}
            </div>
        );
    }
}

export default TopList;
