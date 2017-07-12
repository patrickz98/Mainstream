package com.github.patrickz98;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainOld
{
//    public static final String dataPath        = "/Users/patrick/Sodalitas/Java/Sodalitas/data/";
//    public static final String aliasPath       = "/Users/patrick/Sodalitas/Data/alias.json";
//    public static final String wrongAliasPath  = "/Users/patrick/Sodalitas/Data/wrongs.json";
//    public static final String CountryJsonPath = "/Users/patrick/Sodalitas/Data/countries.json";
//
//    public static JSONObject wiki = new JSONObject();
//    public static ArrayList<String> allTags = new ArrayList<>();
//
//    private static void wiki(JSONArray allTags)
//    {
//        ArrayList<String> pups = new ArrayList<>();
//
//        for (int inx = 0; inx < allTags.length(); inx++)
//        {
//            String tag = allTags.getString(inx);
//
//            if (pups.contains(tag)) continue;
//
//            pups.add(tag);
//
//            JSONObject wikiJson = Wikipedia.find(tag);
//
//            if (wiki == null) continue;
//
//            wiki.put(tag, wikiJson);
//            System.out.println("(Wiki) " + tag);
//        }
//
//        System.out.println("Done.");
//
//        String jsfile = "./js/wiki-" + Simple.toDayDate() + ".js";
//        String content = "wikiData = " + wiki.toString(2) + ";";
//
//        Simple.saveFile(jsfile, content);
//    }
//
//    private static void addManual(String[] args)
//    {
////        System.out.println("arg 0: " + args[ 0 ] + " 1: " + args[ 1 ]);
//        AliasManager.addManual(args[ 0 ], args[ 1 ]);
//    }
//
//    //    public static void main(String[] args)
//    public static void asdf(String[] args)
//    {
//        // b7315fa2643749cef5b0bab7888d66df
//        Elastic.idExist("localhost", "sodalitas", "articles", "4f873c9aed28b7e8e4922c76189b33b7");
//        Elastic.idExist("localhost", "sodalitas", "articles", "asdfasdfasdfasdf");
//    }
//
//    //    public static void asdf(String[] args)
//    public static void main(String[] args)
//    {
//        if (args.length == 2)
//        {
//            addManual(args);
//            return;
//        }
//
//        long startTime = System.currentTimeMillis();
//
//        AliasManager.setAliasJson();
//
//        JSONArray spon = Spon.scan();
//
//        if (spon == null)
//        {
//            System.out.println("Spiegel Error.");
//            return;
//        }
//
//        JSONArray suddeutsche = Suddeutsche.scan();
//
//        if (suddeutsche == null)
//        {
//            System.out.println("Suddeutsche Error.");
//            return;
//        }
//
//        JSONArray all = new JSONArray();
//
//        for (int inx = 0; inx < spon.length(); inx++)
//        {
//            all.put(spon.getJSONObject(inx));
//        }
//
//        for (int inx = 0; inx < suddeutsche.length(); inx++)
//        {
//            all.put(suddeutsche.getJSONObject(inx));
//        }
//
//        Simple.saveFile(dataPath + "dump.json", all.toString(2));
//
//        JSONArray allTags = new JSONArray();
//
//        for (int inx = 0; inx < all.length(); inx++)
//        {
//            JSONObject json = all.getJSONObject(inx);
//
//            Simple.appendArrayEntrysToArray(allTags, json.getJSONArray("TAGS"));
//
//            AliasManager.cleanNerBrach(json.getJSONArray("TAGS"));
//
//            if (json.has("LOCATION"))     AliasManager.cleanNerBrach(json.getJSONArray("LOCATION"));
//            if (json.has("MISC"))         AliasManager.cleanNerBrach(json.getJSONArray("MISC"));
//            if (json.has("ORGANIZATION")) AliasManager.cleanNerBrach(json.getJSONArray("ORGANIZATION"));
//            if (json.has("PERSON"))       AliasManager.cleanNerBrach(json.getJSONArray("PERSON"));
//
//            // String id = Simple.md5(json.getString("link"));
//            // JSONObject response = Elastic.post("localhost", "sodalitas", "articles", id, json);
//
//            // System.out.println("--> " + json.getString("title") + " (" + response.get("responseCode") + ")");
//            System.out.println("--> " + json.getString("title"));
//
//            if (json.has("COUNTRIES"))
//            {
//                System.out.println(json.getJSONArray("COUNTRIES").toString(2));
//            }
//        }
//
//        System.out.println("Done.");
//
//        AliasManager.autoMatch(allTags);
////        wiki(allTags);
//
//        long endTime   = System.currentTimeMillis();
//        long totalTime = endTime - startTime;
//        System.out.println("Done: " + (totalTime / 1000) + "s");
//    }
}
