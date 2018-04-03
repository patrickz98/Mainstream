import React, { Component } from "react";

class Menu extends Component
{
    constructor(props)
    {
        super(props);

        console.log("title: " + props.title);
    }

    onClick(event)
    {
        event.preventDefault();

        console.log("id: " + event.target.id);
        this.props.handler(event.target.id);
    }

    createButtons()
    {
        var opt = {
            "tagsCount": "All",
            "tagsCountPerson": "Person",
            "tagsCountOrganization": "Organization",
            "tagsCountLocation": "Location",
            "tagsCountMisc": "Misc"
        };

        var array = [];

        var inx = 0;

        for (var key in opt)
        {
            const style = {
                position: "absolute",
                left: (20 * inx) + "%",
                width: "20%",
                top: 0,
                bottom: 0,
                backgroundColor: "#707175",
                onclick: this.onClick
            };

            array[ inx ] = <span id={key} onClick={this.onClick.bind(this)} style={style}>{opt[ key ]}</span>;

            inx++;
        }

        return array;
    }

    render()
    {
        const menuStyle = {
            width: "100%",
            height: 60,
            backgroundColor: "#24292e",
            position: "absolute",
            fontFamily: "Arial"
        };

        const buttonBar = {
            padding: 0,
            left: 100,
            top: 0,
            right: 100,
            bottom: 0,
            position: "absolute",
            color: "#ffffff",
            lineHeight: "60px",
            backgroundColor: "#a0be4c"
        };

        const buttons = this.createButtons();

        return (
            <div style={menuStyle}>
                <center>
                    <div style={buttonBar}>
                        {this.props.title}
                        {buttons}
                    </div>
                </center>
            </div>
        );
    }
}

export default Menu;
