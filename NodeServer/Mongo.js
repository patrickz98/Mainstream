var MongoClient = require('mongodb').MongoClient;
const dataBase = "MainStream";
const mongodb = "mongodb://odroid-ubuntu.local:27017/" + dataBase;

var data = {};

const analyse = [
    "tagsCount",
    "tagsCountPerson",
    "tagsCountLocation",
    "tagsCountMisc",
    "tagsCountOrganization"
];

var analyseCount = 0;
var dataCount = 0;

var dataCallBack;

function final(db)
{
    var keysLength = 0;
    for (var collection in data)
    {
        keysLength += data[ collection ][ "top" ].length;
    }

    if (keysLength != dataCount) return;

    db.close();

    dataCallBack(data);
    console.log("--> Data update done");
}

function collectStatistics(db, collection)
{
    data[ collection ][ "data" ] = {};

    for (var inx in data[ collection ][ "top" ])
    {
        const key = data[ collection ][ "top" ][ inx ];

        data[ collection ][ "data" ][ key ] = {};

        var cursor = db.collection(collection).find({key: key});
        cursor.toArray(function(err, array)
            {
                if (err) throw err;

                for (const inx in array)
                {
                    const doc = array[ inx ];
                    const date = doc[ "date" ];
                    const count = doc[ "count" ];

                    data[ collection ][ "data" ][ key ][ date ] = count;
                }

                dataCount++;
                final(db);
            }
        );
    }
}

function done(db)
{
    // console.log("data: " + JSON.stringify(data));
    // console.log("analyseCount: " + analyseCount);
    // console.log("analyse.length: " + analyse.length);

    if (analyseCount != analyse.length) return;

    console.log("--> Collect statisics");

    for (var collection in data)
    {
        collectStatistics(db, collection);
    }

}

function getTop(db, collection)
{
    data[ collection ] = {};
    data[ collection ][ "top" ] = [];

    var query = {date: getdate()};
    var cursor = db.collection(collection).find(query).sort({count:-1}).limit(20);

    cursor.toArray(function(err, array){
        if (err) throw err;

        for (const inx in array)
        {
            const doc = array[ inx ];
            data[ collection ][ "top" ][ inx ] = doc[ "key" ];
        }

        analyseCount++;

        done(db);
    });
}

function connected(err, db)
{
    if (err) throw err;

    for (var variable in analyse)
    {
        getTop(db, analyse[ variable ]);
    }
}

function getdate()
{
    const date = new Date();
    var day   = "" + date.getDate();
    var month = "" + (date.getMonth() + 1);
    var year  = date.getFullYear();

    if (day.length   == 1) day   = "0" + day;
    if (month.length == 1) month = "0" + month;

    return parseInt(year + month + day);
}

function update()
{
    console.log("--> Start data update");

    data = {};
    analyseCount = 0;
    dataCount = 0;

    MongoClient.connect(mongodb, connected);
}

function main(callBack)
{
    dataCallBack = callBack;
    update();
}

module.exports = {
    getData: main,
    update: update
};
