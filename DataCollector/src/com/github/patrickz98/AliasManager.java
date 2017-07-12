package com.github.patrickz98;

import org.json.JSONArray;
import org.json.JSONObject;

public class AliasManager
{
    private JSONObject alias;

    AliasManager()
    {
        JSONObject content = Simple.readJSONObjectFile(Constants.aliasPath);

        if (content == null) alias = new JSONObject();

        alias  = content;
    }

    public void cleanNerBranch(JSONArray json)
    {
        for (int inx = 0; inx < json.length(); inx++)
        {
            String tag = json.getString(inx);

            if (! alias.has(tag)) continue;

            String newTag = alias.getString(tag);
            json.put(inx, newTag);

            // System.err.println("Change: " + tag + " --> " + newTag);
        }
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
}
