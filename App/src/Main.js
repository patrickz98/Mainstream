import React from 'react';
import {
    AppRegistry,
    StyleSheet,
    Text,
    View,
    StatusBar,
    Menu,
    WebView,
    Navigator,
    Alert
} from 'react-native';

import NavigationBar from 'react-native-navigation-bar';

const styles = {
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#f29b67',
    },
    welcome: {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
    }
};

export default class Main extends React.Component
{
    constructor()
    {
        super();
        console.log("super()"); // prints out whatever is inside props

        // super(props);

        this.state = {
            container: {
                flex: 1,
                justifyContent: "center",
                alignItems: "center",
                backgroundColor: "#e8e8e8"
            }
        };

        // this.setState({showText: "true"});
        // console.log(this.state);
    }

    // <WebView
    //     style={{backgroundColor: "#30b1c8"}}
    //     source={{html: html}}
    //     scalesPageToFit={true} />

    onBackHandle()
    {

    }

    onForwardHandle()
    {
        Alert.alert(
            'Alert Title', 'My Alert Msg',
            [
                // {
                //     text: 'Ask me later', onPress: () => console.log('Ask me later pressed')
                // },
                {
                    text: 'Cancel', onPress: () => console.log('Cancel Pressed'), style: 'cancel'
                },
                {
                    text: 'OK', onPress: () => console.log('OK Pressed')
                },
            ],
            {
                cancelable: false
            }
        );
    }

    render()
    {
        // <View style={styles.container}>
        //   </View>
        // <View style={this.state.container}>

        const html = "<html><body><h1>Hallo</h1></body></html>";
        // let bla = this.state.showText;
        let bla = "sadf";

        console.log("render()");
        // console.log(this.state);

        return (
            <View style={this.state.container}>
                <StatusBar
                    barStyle="light-content"
                    backgroundColor="#3c3c3c"
                    hidden={false} />

                <NavigationBar
                    title={"Title"}
                    height={44}
                    titleColor={"#ffffff"}
                    backgroundColor={"#585858"}
                    leftButtonTitle={"Back"}
                    leftButtonTitleColor={"#ffffff"}
                    onLeftButtonPress={this.onBackHandle}
                    rightButtonTitle={"Nexts"}
                    rightButtonTitleColor={"#ffffff"}
                    onRightButtonPress={this.onForwardHandle} />

                <Text>{bla}</Text>
            </View>
        );
    }
}

// const styles = StyleSheet.create({
//   container: {
//     flex: 1,
//     backgroundColor: '#F5FCFF',
//   },
//   center: {
//       flex: 1,
//       justifyContent: 'center',
//       alignItems: 'center',
//   },
//   welcome: {
//     fontSize: 20,
//     textAlign: 'center',
//     margin: 10,
//   },
//   instructions: {
//     textAlign: 'center',
//     color: '#333333',
//     marginBottom: 5,
//   },
// });
