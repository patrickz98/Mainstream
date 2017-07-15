package com.github.patrickz98;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Simple
{
    public static String readFile(String path)
    {
        String jsonContent = null;

        try
        {
            File file = new File(path);
            jsonContent = new String(Files.readAllBytes(file.toPath()));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return jsonContent;
    }

    public static JSONObject readJSONObjectFile(String path)
    {
        String content = readFile(path);
        if (content == null) return null;
        return new JSONObject(content);
    }

    public static JSONArray readJSONArrayFile(String path)
    {
        String content = readFile(path);
        if (content == null) return null;
        return new JSONArray(content);
    }

    public static String open_url(String url)
    {
        String htmlContent = "";

        try
        {
            URL connection = new URL(url);
            InputStream stream = connection.openConnection().getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String line = reader.readLine();

            while(line != null)
            {
                htmlContent += line + "\n";
                line = reader.readLine();
            }

            reader.close();
            stream.close();

            return htmlContent;
        }
        catch (Exception ex)
        {
//            ex.printStackTrace();
        }

        if (htmlContent.equals("")) return null;

        return htmlContent;
    }

    public static String latin1ToUtf8(String str)
    {
        if (str == null) return null;

        try
        {
            byte[] bytes = str.getBytes("ISO-8859-1");
            String strLatin = new String(bytes, "ISO-8859-1");

            return new String(strLatin.getBytes("UTF-8"), "UTF-8");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return str;
    }

    public static String deMoronize(String str)
    {
        // str = str.replace("\"", "");
        // str = str.replace("'",  "");
        // str = str.replace("\\", "");
        // str = str.replace("/",  "");

        str = str.replaceAll("<.*?>", "");

        str = str.replace("„",  "");
        str = str.replace("“",  "");
        str = str.replace("“",  "");
        str = str.replace("&nbsp;",  " ");
        str = str.replace("&quot;",  "\"");
        str = str.replace("<b>",  "");
        str = str.replace("<br>",  "");
        str = str.replace("</br>",  "\"");
        str = str.replace("&amp;",  "&");

        return str;
    }

    public static int toDayDate()
    {
        Date date = new Date();
        // DateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat form = new SimpleDateFormat("yyyyMMdd");

        return Integer.parseInt(form.format(date));
    }

    public static void saveFile(String filePath, String content)
    {
        File parent = new File(filePath).getParentFile();
        parent.mkdirs();

        File file = new File(filePath);

        try
        {
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            writer.print(content);
            writer.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static JSONObject fixNerArray(ArrayList<JSONObject> array)
    {
        JSONObject json = new JSONObject();

        for (JSONObject part : array)
        {
            for (String key : part.keySet())
            {
                JSONArray partArray;

                if (json.has(key))
                {
                    partArray = (JSONArray) json.get(key);
                }
                else
                {
                    partArray = new JSONArray();
                }

                JSONArray partpartArray = (JSONArray) part.get(key);
                for (int inx = 0; inx < partpartArray.length(); inx++)
                {
                    String value = partpartArray.getString(inx);
                    partArray.put(value);
                }

                json.put(key, partArray);
            }
        }

        return json;
    }

    public static String md5(String str)
    {
        try
        {
            byte[] strBytes = str.getBytes("UTF-8");

            StringBuffer hexString = new StringBuffer();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(strBytes);

            for (int inx = 0; inx < hash.length; inx++)
            {
                if ((0xff & hash[ inx ]) < 0x10)
                {
                    hexString.append("0" + Integer.toHexString((0xFF & hash[ inx ])));
                }
                else
                {
                    hexString.append(Integer.toHexString(0xFF & hash[ inx ]));
                }
            }

            return hexString.toString();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    public static Boolean jsonArrayIncludes(JSONArray array, String item)
    {
        for (int inx = 0; inx < array.length(); inx++)
        {
            if (array.getString(inx).equals(item)) return true;
        }

        return false;
    }

    public static JSONArray getAllNerTags(JSONObject nerJson)
    {
        JSONArray tags = new JSONArray();

        for (String key: nerJson.keySet())
        {
            JSONArray values = (JSONArray) nerJson.get(key);

            for (int inx = 0; inx < values.length(); inx++)
            {
                tags.put(values.getString(inx));
            }
        }

        return tags;
    }

    public static void appendArrayEntriesToArray(JSONArray des, JSONArray source)
    {
        for (int inx = 0; inx < source.length(); inx++)
        {
            des.put(source.getString(inx));
        }
    }

    public static String upperFirst(String str)
    {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
