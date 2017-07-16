const http = require('http');

const server = http.createServer((request, response) => {
    console.log("New");

    response.writeHead(200, {'Content-Type': 'application/javascript; charset=utf-8'});

    getData(response);
});

server.on('clientError', (err, socket) => {
    socket.end('HTTP/1.1 400 Bad Request\r\n\r\n');
});

server.listen(8080);

var MongoClient = require('mongodb').MongoClient;
const data = "MainStream";
var url = "mongodb://localhost:27017/" + data;

var parse = function(response, json)
{
    var str = {};

    for (var index in json)
    {
        var tag = json[ index ];
        str[ tag[ "key" ] ] = tag[ "count" ];
    }

    response.write(JSON.stringify(str));
    response.end();
}

var getData = function(response)
{
    MongoClient.connect(url, function(err, db)
    {
        if (err) throw err;

        // db.tagsCount.find({date:20170716}).sort({count:-1}).limit(20).pretty()

        var query = {date:20170716};
        var result = db.collection("tagsCount").find(query).sort({count:-1}).limit(10);

        result.toArray(function(err, result)
        {
            if (err) throw err;

            db.close();

            parse(response, result);
        });
    });
}
