package com.github.patrickz98;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Elastic
{
    private final static String charset = "UTF-8";
    private final static int port = 9200;

    protected static JSONObject openConection(String url, JSONObject data)
    {
        JSONObject response = new JSONObject();

        try
        {
            HttpURLConnection httpcon = (HttpURLConnection) (new URL(url).openConnection());
            httpcon.setDoOutput(true);
            httpcon.setDoInput(true);
            httpcon.setRequestProperty("Accept-Charset", charset);
            httpcon.setRequestProperty("Content-Type", "application/json");
            // httpcon.setRequestMethod("POST");

            if (data != null)
            {
                httpcon.setRequestMethod("PUT");

                byte[] outputBytes = data.toString().getBytes();

                OutputStream os = httpcon.getOutputStream();
                os.write(outputBytes);
                os.flush();
                os.close();
            }
            else
            {
                httpcon.setRequestMethod("GET");
            }

            String responseMessage = httpcon.getResponseMessage();
            int responseCode = httpcon.getResponseCode();
            // System.out.println("responseMessage: " + responseMessage + " responseCode: " + responseCode);

            response.put("responseMessage", responseMessage);
            response.put("responseCode",    responseCode);
        }
        catch (Exception ex)
        {
//            ex.printStackTrace();
        }

        return response;
    }

    public static JSONObject post(String server, String index, String type, JSONObject data)
    {
        String url = "http://" + server + ":" + port + "/" + index + "/" + type;
        JSONObject response = openConection(url, data);

        return response;
    }

    public static JSONObject post(String server, String index, String type, String id, JSONObject data)
    {
        String url = "http://" + server + ":" + port + "/" + index + "/" + type + "/" + id;
        JSONObject response = openConection(url, data);

        return response;
    }

    public static boolean idExist(String server, String index, String type, String id)
    {
        String url = "http://" + server + ":" + port + "/" + index + "/" + type + "/" + id;
        JSONObject response = openConection(url, null);

        // System.out.println(response.toString(2));

        // curl -i -XHEAD http://localhost:9200/website/blog/123

        if (response.getInt("responseCode") == 404) return false;

        return true;
    }
}
