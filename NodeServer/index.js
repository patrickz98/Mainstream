const http = require("http");
const url = require("url");

var count = 0;
var data = {};

const dataCallback = function(udata)
{
    data = udata;
};

const Mongo = require("./Mongo");
Mongo.getData(dataCallback);

const setHeaders = function(response)
{
    // Website you wish to allow to connect
    // response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
    // response.setHeader("Access-Control-Allow-Origin", "http://patrick-macbook.local:3000");
    // response.setHeader("Access-Control-Allow-Origin", "http://192.168.0.31:3000");
    // response.setHeader("Access-Control-Allow-Origin", "http://192.168.0.29:3000");
    response.setHeader("Access-Control-Allow-Origin", "*");

    // Request methods you wish to allow
    response.setHeader("Access-Control-Allow-Methods", "GET");

    // Request headers you wish to allow
    response.setHeader("Access-Control-Allow-Headers", "X-Requested-With,content-type");

    // Set to true if you need the website to include cookies in the requests sent
    // to the API (e.g. in case you use sessions)
    response.setHeader("Access-Control-Allow-Credentials", false);

    // response.writeHead(200, {"Content-Type": "application/javascript; charset=utf-8"});
    response.setHeader("Content-Type", "application/json; charset=utf-8");
};

const request = function(request, response)
{
    console.log("Connection #" + count);
    count++;

    setHeaders(response);

    var query = url.parse(request.url, true).query;

    if (query.q)
    {
        console.log("--> search: " + query.q);
        response.end("query.q: " + query.q);

        return;
    }

    if (query.wiki)
    {
        console.log("--> wiki: " + query.wiki);
        response.end("query.q: " + query.wiki);

        return;
    }

    if (query.update !== undefined)
    {
        console.log("--> Update Mongo");
        response.end("{\"Update\": \"OK\"}");

        Mongo.update();
        return;
    }

    response.end(JSON.stringify(data));
};

const server = http.createServer(request);

server.on("clientError", (err, socket) => {
    socket.end("HTTP/1.1 400 Bad Request\r\n\r\n");
});

server.listen(8080);
