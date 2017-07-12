package com.github.patrickz98;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.*;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class Mongo
{
    private MongoClient mongoClient;
    private MongoCollection mcollection;

    Mongo(String collection)
    {
        mongoClient = new MongoClient(Constants.mongoDBServer);
        MongoDatabase db = mongoClient.getDatabase(Constants.mongoDB);
        mcollection = db.getCollection(collection);
    }

    public JSONObject getData(String key)
    {
        BsonDocument test = new BsonDocument("key", new BsonString(key));
        FindIterable result = mcollection.find(test);

        Document data = (Document) result.first();

        if (data == null) return null;

        JSONObject json = new JSONObject(data.toJson());
        json.remove("_id");

        return json;
    }

    public void insert(JSONObject json)
    {
        System.out.println("(Mongo) insert: " + json.getString("key"));
        mcollection.insertOne(Document.parse(json.toString()));
    }

    public void close()
    {
        mongoClient.close();
    }
}
