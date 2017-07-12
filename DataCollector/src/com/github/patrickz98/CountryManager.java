package com.github.patrickz98;

import org.json.JSONArray;
import org.json.JSONObject;

public class CountryManager
{
    private static JSONObject json = null;

    CountryManager()
    {
        json = Simple.readJSONObjectFile(Constants.countriesJsonPath);
    }

    public static JSONArray getCountries(JSONArray tags)
    {
        if (json == null) return null;

        JSONArray array = new JSONArray();

        for (int inx = 0; inx < tags.length(); inx++)
        {
            String tag = tags.getString(inx);

            if (! json.has(tag)) continue;
            if (Simple.jsonArrayIncludes(array, tag)) continue;

            array.put(json.getString(tag));
        }

        if (array.length() <= 0) return null;

        return array;
    }
}
