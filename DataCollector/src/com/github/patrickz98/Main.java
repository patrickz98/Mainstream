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

        return allArticles;
    }

    private static void cleanData(JSONArray data)
    {
        for (int inx = 0; inx < data.length(); inx++)
        {
            JSONObject json = data.getJSONObject(inx);
            aliasManager.cleanNer(json);
        }
    }

    private static void dumpAllTags(JSONArray data)
    {
        JSONArray allTags = new JSONArray();

        for (int inx = 0; inx < data.length(); inx++)
        {
            JSONObject json = data.getJSONObject(inx);

            Simple.appendArrayEntriesToArray(allTags, json.getJSONArray("allTags"));
        }

        aliasManager.autoMatch(allTags);
        aliasManager.cleanNerBranch(allTags);

        JSONArray wikiJson = Wikipedia.find(allTags);

        Simple.saveFile("/Users/patrick/Desktop/Projects/Mainstream/DataCollector/dump/dump-wiki.json", wikiJson.toString(2));
        Simple.saveFile("/Users/patrick/Desktop/Projects/Mainstream/DataCollector/dump/dump-tags.json", allTags.toString(2));
    }

    public static void main(String[] args)
    {
        new Mongo("asdf");

        aliasManager = new AliasManager();

        JSONArray data = collectData();
        Simple.saveFile("/Users/patrick/Desktop/Projects/Mainstream/DataCollector/dump/dump.json", data.toString(2));

        dumpAllTags(data);
        cleanData(data);

//        JSONArray spon = Spon.scan();
//
//        if (spon == null)
//        {
//            System.out.println("Spiegel Error.");
//            return;
//        }

//        JSONArray suddeutsche = Suddeutsche.scan();
//
//        if (suddeutsche == null)
//        {
//            System.out.println("Suddeutsche Error.");
//            return;
//        }
//
//        Simple.saveFile("/Users/patrick/Desktop/Projects/Mainstream/DataCollector/dump/dump.json", suddeutsche.toString(2));
//
//        AliasManager aliasManager = new AliasManager();
//
//        JSONArray allTags = new JSONArray();
//
//        for (int inx = 0; inx < suddeutsche.length(); inx++)
//        {
//            JSONObject json = suddeutsche.getJSONObject(inx);
//
//            Simple.appendArrayEntrysToArray(allTags, json.getJSONArray("TAGS"));
//
//            aliasManager.cleanNerBranch(json.getJSONArray("TAGS"));
//
//            if (json.has("LOCATION"))     aliasManager.cleanNerBranch(json.getJSONArray("LOCATION"));
//            if (json.has("MISC"))         aliasManager.cleanNerBranch(json.getJSONArray("MISC"));
//            if (json.has("ORGANIZATION")) aliasManager.cleanNerBranch(json.getJSONArray("ORGANIZATION"));
//            if (json.has("PERSON"))       aliasManager.cleanNerBranch(json.getJSONArray("PERSON"));
//
//            System.out.println("--> " + json.getString("title"));
//
//            if (json.has("COUNTRIES"))
//            {
//                System.out.println(json.getJSONArray("COUNTRIES").toString(2));
//            }
//        }
//
//        aliasManager.autoMatch(allTags);
//
//        Simple.saveFile("/Users/patrick/Desktop/Projects/Mainstream/DataCollector/dump/dump-tags.json", allTags.toString(2));
//
//        System.out.println("Done");
    }
}
