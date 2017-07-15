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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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

    public JSONObject getData(String key, String value)
    {
        BsonDocument test = new BsonDocument(key, new BsonString(value));
        FindIterable result = mcollection.find(test);

        Document data = (Document) result.first();

        if (data == null) return null;

        JSONObject json = new JSONObject(data.toJson());
        json.remove("_id");

        return json;
    }

    public JSONObject getData(String value)
    {
        return getData("key", value);
    }

    public void insert(JSONObject json)
    {
        mcollection.insertOne(Document.parse(json.toString()));
    }

//    public void insert(JSONArray json)
//    {
//        ArrayList<Document> list = new ArrayList<>();
//
//        for (int inx = 0; inx < json.length(); inx++)
//        {
//            JSONObject meta = json.getJSONObject(inx);
//            list.add(Document.parse(meta.toString()));
//        }
//
//        System.out.println("(Mongo) insert list: " + list.size());
//        mcollection.insertMany(list);
//    }

    public void insertNotReplace(JSONArray json)
    {
        // ArrayList<Document> list = new ArrayList<>();

        System.out.println("(Mongo) insert list: " + json.length());

        for (int inx = 0; inx < json.length(); inx++)
        {
            JSONObject meta = json.getJSONObject(inx);
            meta.put("_id", Simple.md5(meta.getString("link")));

            Document doc = Document.parse(meta.toString());

            try
            {
                mcollection.insertOne(doc);
            }
            catch (Exception exc)
            {

            }
            // list.add(Document.parse(meta.toString()));
        }

        // mcollection.insertMany(list);
    }

    public void insertOrReplace(JSONObject json)
    {
        try
        {
            // System.out.println("(Mongo) insert: " + json.toString());
            mcollection.insertOne(Document.parse(json.toString()));
        }
        catch (Exception exc)
        {
            String id = json.getString("_id");

            mcollection.deleteOne(Document.parse("{\"_id\":\"" + id + "\"}"));
            mcollection.insertOne(Document.parse(json.toString()));
        }
    }

    public ArrayList<String> find(String str)
    {
        FindIterable resultMongo = mcollection.find(Document.parse(str));

        ArrayList<String> result = new ArrayList<>();

        Iterator<Document> flavoursIter = resultMongo.iterator();
        while (flavoursIter.hasNext())
        {
            Document doc = flavoursIter.next();

            JSONObject json = new JSONObject(doc.toJson());

            result.add(json.getString("key"));
        }

        return result;
    }

    public JSONObject findOne(String str)
    {
        FindIterable resultMongo = mcollection.find(Document.parse(str));
        Document doc = (Document) resultMongo.first();

        if (doc == null) return null;

        String jsonString = doc.toJson();

        return new JSONObject(jsonString);
    }

    public void close()
    {
        mongoClient.close();
    }
}
