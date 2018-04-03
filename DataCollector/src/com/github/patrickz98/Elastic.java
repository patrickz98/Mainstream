package com.github.patrickz98;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Date;

public class Elastic
{
    private static void putExeption(JSONObject json) throws Exception
    {
        URL myURL = new URL("http://localhost:9200/test/test/");
        HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
        myURLConnection.setRequestMethod("POST");
        myURLConnection.setRequestProperty("Content-Type", "application/json");
        myURLConnection.setUseCaches(false);
        myURLConnection.setDoInput(true);
        myURLConnection.setDoOutput(true);
        myURLConnection.connect();

        OutputStream os = myURLConnection.getOutputStream();
        // os.write(URLEncoder.encode(jsonParam.toString(),"UTF-8").getBytes(Charset.forName("UTF-8")));
        os.write(json.toString().getBytes(Charset.forName("UTF-8")));
        os.close();

        StringBuilder builder = new StringBuilder();
        builder.append(myURLConnection.getResponseCode())
                .append(" ")
                .append(myURLConnection.getResponseMessage())
                .append("\n");

        System.out.println("Elastic: " + builder);
    }

    public static void put(JSONObject json)
    {
        JSONObject cleanJSON = new JSONObject();

        for (String key: json.keySet())
        {
            if (key.equals("date"))
            {
                cleanJSON.put(key, json.getInt(key) + "");
                continue;
            }

            cleanJSON.put(key, json.get(key));
        }

        cleanJSON.remove("_id");

        if (cleanJSON.has("allTags"))
        {
            JSONArray all_tags = json.getJSONArray("allTags");
            cleanJSON.put("countries", CountryManager.getCountries(all_tags));
        }

        try
        {
            putExeption(cleanJSON);
            System.out.println("Json: " + cleanJSON.toString());
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }
}
