package com.github.patrickz98;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Numbers
{
    private MongoClient mongoClient;
    private MongoDatabase db;
    private MongoCollection mcollection;
    private String collection;
    private StringBuilder builder = null;

    private Numbers(String collection)
    {
        this.collection = collection;

        this.mongoClient = new MongoClient(Constants.mongoDBServer);
        this.db = mongoClient.getDatabase(Constants.mongoDB);
        mcollection = db.getCollection(collection);

        main();

        mongoClient.close();
    }

    private void main()
    {
        JSONArray top = getTop();

        for (int inx = 0; inx < top.length(); inx++)
        {
            String tag = top.getString(inx);
            addCsvLine(tag);
        }

        // addCsvLine(top.getString(0));

        builder.trimToSize();
        Simple.saveFile(Constants.dumpDir + collection + ".csv", builder.toString());
    }

    private void addCsvLine(String tag)
    {
        Map<Integer, Integer> map = getData(tag);

        List<Integer> list = new ArrayList<>(map.keySet());
        Collections.sort(list);

        if (builder == null)
        {
            builder = new StringBuilder();
            builder.append("Tag,");

            for (int inx: list)
            {
                builder.append(inx);
                builder.append(",");
            }

            builder.append("\n");
        }

        builder.append(tag);
        builder.append(",");

        for (int inx: list)
        {
            builder.append(map.get(inx));
            builder.append(",");
        }

        builder.append("\n");
    }

    private Map<Integer, Integer> getData(String tag)
    {
        Document find = Document.parse("{key:\"" + tag + "\"}");

        FindIterable resultMongo = mcollection.find(find);

        Map<Integer, Integer> result = new HashMap<>();

        Iterator<Document> flavoursIter = resultMongo.iterator();
        while (flavoursIter.hasNext())
        {
            Document doc = flavoursIter.next();
            JSONObject json = new JSONObject(doc.toJson());
            result.put(json.getInt("date"), json.getInt("count"));
        }

        return result;
    }

    private JSONArray getTop()
    {
        Document sort = Document.parse("{count:-1}");
        Document find = Document.parse("{date:" + Simple.toDayDate() + "}");

        FindIterable resultMongo = mcollection.find(find).sort(sort).limit(Constants.limitTop);

        JSONArray result = new JSONArray();

        Iterator<Document> flavoursIter = resultMongo.iterator();

        while (flavoursIter.hasNext())
        {
            Document doc = flavoursIter.next();
            JSONObject json = new JSONObject(doc.toJson());
            result.put(json.getString("key"));
        }

        System.out.println(result.toString(2));

        return result;
    }

    public static void start()
    {
        String[] collections = new String[]
        {
                "tagsCount",
                "tagsCountLocation",
                "tagsCountMisc",
                "tagsCountOrganization",
                "tagsCountPerson",
        };

        for (String collection: collections)
        {
            System.out.println(collection);
            new Numbers(collection);
        }

        System.exit(0);
    }
}
