package com.github.patrickz98;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class LocationManager
{
    private static final String worldcitiespop = "/Users/patrick/Sodalitas/Data/worldcitiespop.txt.gz";
    private BufferedReader inputStream = null;
    private String[] keys = null;


    public LocationManager()
    {
        try
        {
            setStreams();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void setStreams() throws Exception
    {
        InputStream inStream = new FileInputStream(worldcitiespop);
        GZIPInputStream gzipInputStream = new GZIPInputStream(inStream);
        InputStreamReader reader = new InputStreamReader(gzipInputStream, StandardCharsets.ISO_8859_1);

        inputStream = new BufferedReader(reader);

        keys = inputStream.readLine().split(",");
    }

    private boolean checkTag(String line, String tag)
    {
        Pattern pattern = Pattern.compile("^.*," + tag + ",");
        Matcher matcher = pattern.matcher(line);

        return matcher.find();
    }

    private void checkLine(String line, JSONArray array)
    {
        for (int inx = 0; inx < array.length(); inx++)
        {
            String tag = array.getString(inx);

            if (tag.contains("/"))  continue;
            if (tag.contains("\\")) continue;
            if (tag.contains("\"")) continue;
            if (tag.contains("("))  continue;
            if (tag.contains(")"))  continue;
            if (tag.contains("["))  continue;
            if (tag.contains("]"))  continue;
            if (tag.contains(","))  continue;

            if (checkTag(line, tag)) nicePrint(line);
        }
    }

    private void nicePrint(String line)
    {
        String[] values = line.split(",");

        JSONObject json = new JSONObject();

        for (int inx = 0; inx < keys.length; inx++)
        {
            json.put(keys[ inx ], values[ inx ]);
        }

        System.out.println(json.toString(2));
    }

    public JSONObject findLocations(JSONArray array) throws Exception
    {
        String readed;
        while ((readed = inputStream.readLine()) != null)
        {
            checkLine(readed, array);
        }

        return null;
    }
}
