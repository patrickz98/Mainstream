package com.github.patrickz98;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main
{
    private static AliasManager aliasManager;

    private static JSONArray collectData()
    {
        JSONArray allArticles = new JSONArray();

        Suddeutsche suddeutsche = new Suddeutsche(allArticles);
        suddeutsche.scan();

        Spon spon = new Spon(allArticles);
        spon.scan();

        return allArticles;
    }

    private static void cleanData(JSONArray data)
    {
        for (int inx = 0; inx < data.length(); inx++)
        {
            JSONObject json = data.getJSONObject(inx);
            aliasManager.cleanNerBranch(json.getJSONArray("allTags"));
            aliasManager.cleanNer(json);
        }
    }

    private static JSONArray dumpAllTags(JSONArray data)
    {
        JSONArray allTags = new JSONArray();

        for (int inx = 0; inx < data.length(); inx++)
        {
            JSONObject json = data.getJSONObject(inx);
            Simple.appendArrayEntriesToArray(allTags, json.getJSONArray("allTags"));
        }

        aliasManager.autoMatch(allTags);
        aliasManager.cleanNerBranch(allTags);

        Simple.saveFile("/Users/patrick/Desktop/Projects/Mainstream/DataCollector/dump/dump-tags.json", allTags.toString(2));

        return allTags;
    }

    private static JSONObject countTags(JSONArray data)
    {
        JSONObject countData = new JSONObject();

        for (int inx = 0; inx < data.length(); inx++)
        {
            JSONObject json = data.getJSONObject(inx);
            JSONArray allTags = json.getJSONArray("allTags");

            for (int iny = 0; iny < allTags.length(); iny++)
            {
                String tag = allTags.getString(iny);
                int count = countData.optInt(tag, 0) + 1;
                countData.put(tag, count);
            }
        }

        Simple.saveFile("/Users/patrick/Desktop/Projects/Mainstream/DataCollector/dump/count.json", countData.toString(2));

        StringBuilder csv = new StringBuilder();
        csv.append("Tag,Count\n");

        for (String tag: countData.keySet())
        {
            csv.append(tag);
            csv.append(",");
            csv.append(countData.getInt(tag));
            csv.append("\n");
        }

        csv.trimToSize();

        Simple.saveFile("/Users/patrick/Desktop/Projects/Mainstream/DataCollector/dump/count.csv", csv.toString());

        return countData;
    }

    private static void addMetaDataMongo(JSONArray metaData)
    {
        Mongo mongo = new Mongo(Constants.collectionMetaData);
        mongo.insertNotReplace(metaData);
        mongo.close();
    }

    private static void addTagCountMongo(JSONObject count)
    {
        Mongo mongo = new Mongo("tagsCount");

        // String date = Simple.toDayDate();
        int date = Simple.toDayDate();

        for (String key: count.keySet())
        {
            JSONObject entry = new JSONObject();

            String id = Simple.md5(key + date);

            entry.put("_id", id);
            entry.put("key", key);
            entry.put("count", count.getInt(key));
            entry.put("date", date);

            mongo.insertOrReplace(entry);
        }

        mongo.close();
    }

    private static void wiki(JSONArray allTags)
    {
        Wikipedia wiki = new Wikipedia();
        wiki.findMongo(allTags);
        wiki.close();

        // JSONArray wikiJson = Wikipedia.find(allTags);
        // Simple.saveFile("/Users/patrick/Desktop/Projects/Mainstream/DataCollector/dump/dump-wiki.json", wikiJson.toString(2));
    }

    public static void main(String[] args)
    {
        aliasManager = new AliasManager();

        JSONArray data = collectData();
        Simple.saveFile("/Users/patrick/Desktop/Projects/Mainstream/DataCollector/dump/dump.json", data.toString(2));

        JSONArray allTags = dumpAllTags(data);
        cleanData(data);

        addMetaDataMongo(data);

        JSONObject count = countTags(data);
        addTagCountMongo(count);

        wiki(allTags);
    }
}
