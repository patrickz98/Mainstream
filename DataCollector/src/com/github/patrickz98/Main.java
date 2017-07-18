package com.github.patrickz98;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.logging.Level;

public class Main
{
    private static AliasManager aliasManager;
    private static Mongo mongo;

    private static JSONArray collectData()
    {
        System.out.println("Downloading data...");

        JSONArray allArticles = new JSONArray();

        Faz faz = new Faz(mongo, allArticles);
        faz.scan();

        Spon spon = new Spon(mongo, allArticles);
        spon.scan();

        Suddeutsche suddeutsche = new Suddeutsche(mongo, allArticles);
        suddeutsche.scan();

        System.out.println("Done");

        return allArticles;
    }

    private static void cleanData(JSONArray data)
    {
        for (int inx = 0; inx < data.length(); inx++)
        {
            // JSONObject json = data.getJSONObject(inx);
            // aliasManager.cleanNerBranch(json.getJSONArray("allTags"));
            // aliasManager.cleanNer(json);

            aliasManager.cleanNerBranch(data.getJSONObject(inx).getJSONArray("allTags"));
            aliasManager.cleanNer(data.getJSONObject(inx));
        }
    }

    private static JSONArray getAllTags(JSONArray data)
    {
        JSONArray allTags = new JSONArray();

        for (int inx = 0; inx < data.length(); inx++)
        {
            JSONObject json = data.getJSONObject(inx);
            Simple.appendArrayEntriesToArray(allTags, json.getJSONArray("allTags"));
        }

        return allTags;
    }

    private static JSONArray dumpAllTags(JSONArray data)
    {
        JSONArray allTags = getAllTags(data);

        aliasManager.autoMatch(allTags);
        aliasManager.cleanNerBranch(allTags);

        Simple.saveFile(Constants.dumpDir + "dump-tags.json", allTags.toString(2));

        return allTags;
    }

    private static JSONObject countTags(JSONArray tags, String label)
    {
        JSONObject countData = new JSONObject();

        for (int inx = 0; inx < tags.length(); inx++)
        {
            String tag = tags.getString(inx);
            int count = countData.optInt(tag, 0) + 1;
            countData.put(tag, count);
        }

        Simple.saveFile(Constants.dumpDir + "count-" + label + ".json", countData.toString(2));

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

        Simple.saveFile(Constants.dumpDir + "count-" + label + ".csv", csv.toString());

        return countData;
    }

    private static void addMetaDataMongo(JSONArray metaData)
    {
        Mongo mongo = new Mongo(Constants.collectionMetaData);

        for (int inx = 0; inx < metaData.length(); inx++)
        {
            JSONObject meta = metaData.getJSONObject(inx);

            if (meta.has("_id")) continue;

            System.out.println("(Mongo) insert: " + meta.getString("source") + " - " + meta.getString("title"));

            meta.put("_id", Simple.md5(meta.getString("link")));
            mongo.insert(meta);
        }

        mongo.close();
    }

    private static void addTagCountMongo(JSONObject count, String label)
    {
        Mongo mongo = new Mongo(label);

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
    }

    private static void generateNerTop(JSONArray data, String nerComp)
    {
        JSONArray tags = new JSONArray();

        for (int inx = 0; inx < data.length(); inx++)
        {
            JSONObject json = data.getJSONObject(inx);

            if (! json.has("tags")) continue;
            if (! json.getJSONObject("tags").has(nerComp)) continue;

            Simple.appendArrayEntriesToArray(tags, json.getJSONObject("tags").getJSONArray(nerComp));
        }

        JSONObject count = countTags(tags, nerComp);
        addTagCountMongo(count, "tagsCount" + Simple.upperFirst(nerComp));
    }

    private static void generateNerTop(JSONArray data)
    {
        String[] nerTags = new String[]{"organization", "misc", "person", "location"};

        for (String nerTag: nerTags)
        {
            generateNerTop(data, nerTag);
        }
    }

    private static void generateTop(JSONArray data)
    {
        JSONObject count = countTags(getAllTags(data), "all");
        addTagCountMongo(count, "tagsCount");

    }

    private static void processArgs(String[] args)
    {
        if ((args.length == 1) && args[ 0 ].equals("top"))
        {
            Numbers.start();
            return;
        }

        if (args.length <= 0) return;

//        System.out.println("arg 0: " + args[ 0 ] + " 1: " + args[ 1 ]);
        aliasManager.addManual(args[ 0 ], args[ 1 ]);

        System.exit(0);
    }

    public static void main(String[] args)
    {
        aliasManager = new AliasManager();

        processArgs(args);

        SimpleNer.getInstance();

        java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);

        mongo = new Mongo(Constants.collectionMetaData);

        JSONArray data = collectData();
        Simple.saveFile(Constants.dumpDir + "dump.json", data.toString(2));

        JSONArray allTags = dumpAllTags(data);
        cleanData(data);

        addMetaDataMongo(data);

        generateTop(data);
        generateNerTop(data);

        wiki(allTags);
    }
}
