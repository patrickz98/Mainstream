package com.github.patrickz98;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class AliasManager
{
    private JSONObject alias;

    AliasManager()
    {
        JSONObject content = Simple.readJSONObjectFile(Constants.aliasPath);

        if (content == null) alias = new JSONObject();

        alias = content;
    }

    public void cleanNerBranch(JSONArray json)
    {
        //System.out.println("before: " + json.toString(2));
        //System.out.println("before: " + json.length());

        ArrayList<String> array = new ArrayList<>();

        for (int inx = 0; inx < json.length(); inx++)
        {
            String tag = json.getString(inx);

            if (! alias.has(tag))
            {
                array.add(tag);
                continue;
            }

            array.add(alias.getString(tag));
        }

//        int length = json.length();
//        for (int inx = 0; inx < length; inx++)
//        {
//            json.remove(0);
//        }

        while (! json.isNull(0))
        {
            json.remove(0);
        }


        //System.out.println("empty:  " + json.toString(2));

        ArrayList<String> done = new ArrayList<>();
        for (String str: array)
        {
            if (done.contains(str)) continue;

            json.put(str);
            done.add(str);
        }

        //System.out.println("after:  " + json.toString(2));
    }

    public void cleanNer(JSONObject json)
    {
        JSONObject tags = json.getJSONObject("tags");

        for (String nerTag: tags.keySet())
        {
            JSONArray nerTags = tags.getJSONArray(nerTag);
            cleanNerBranch(nerTags);
        }
    }

    private void cleanJsonAliases(JSONObject json)
    {
        System.out.println("Cleaning Aliases");

        for (String key: json.keySet())
        {
            String value = json.getString(key);

            if (! json.has(value)) continue;

            json.put(key, json.getString(value));
            System.out.println(key + " --> " + json.getString(value));
        }
    }

    private void auto(String str1, String str2)
    {
        if (str1.equals(str2)) return;

        if (str2.startsWith(str1) &&
           (str1.length() == str2.length()  - 1) &&
           (str2.charAt(str2.length() - 1) == 's'))
        {
            if (alias.has(str2)) return;

            System.out.println("Auto-matching: " + str2 + " --> " + str1);
            alias.put(str2, str1);

            return;
        }

        if (str2.startsWith(str1) &&
           (str1.length() == str2.length()  - 2) &&
           (str2.endsWith("er")))
        {
            if (alias.has(str2)) return;
            if (str2.equals("Bayer")) return;

            System.out.println("Auto-matching: " + str2 + " --> " + str1);
            alias.put(str2, str1);
        }
    }

    public void autoMatch(JSONArray allTags)
    {
        System.out.println("Auto-matching");

        for (Object str1: allTags.toList())
        {
            for (Object str2: allTags.toList())
            {
                auto((String) str1, (String) str2);
            }
        }

        cleanJsonAliases(alias);

        Simple.saveFile(Constants.aliasPath, alias.toString(2));
    }

    public void addManual(String wrong, String right)
    {
        alias.put(wrong, right);

        cleanJsonAliases(alias);
        Simple.saveFile(Constants.aliasPath, alias.toString(2));
    }
}
