package com.github.patrickz98;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Wikipedia
{
    private final static String baseUrlDe = "https://de.wikipedia.org";
    private final static String baseUrlEn = "https://en.wikipedia.org";
    private final static int thrumbWidth = 600;

//    {
//        "batchcomplete": "",
//            "query": {"pages": {"-1": {
//        "ns": 0,
//                "missing": "",
//                "title": "Pupslala"
//    }}}
//    }

    private static int getId(Set<String> set)
    {
        for (String item: set)
        {
            return Integer.parseInt(item);
        }

        return -1;
    }

    private static String cleanSpaces(String str)
    {
        return str.replace(" ", "_");
    }

    private static String wikiString(String str)
    {
        String wikiString = cleanSpaces(str);
        wikiString = wikiString.replace("\"", "");
        wikiString = wikiString.replace("'", "");

        return wikiString;
    }

    private static JSONObject imgInfo(String baseUrl, JSONArray array)
    {
        if (array == null) return null;

        ArrayList<String> badFiles = new ArrayList<>();
        badFiles.add("Datei:Commons-logo.svg");
        badFiles.add("Datei:Disambig-dark.svg");
        badFiles.add("Datei:Ambox current red.svg");
        badFiles.add("Datei:Disambig-dark.svg");

        badFiles.add("File:Commons-logo.svg");
        badFiles.add("File:Disambig-dark.svg");
        badFiles.add("File:Ambox current red.svg");
        badFiles.add("File:Disambig-dark.svg");

        String url = baseUrl + "/w/api.php?" +
                "action=query&" +
                "prop=imageinfo&" +
                "iiprop=url&" +
                "iiurlwidth=" + thrumbWidth + "&" +
                "format=json&";

        for (int inx = 0; inx < array.length(); inx++)
        {
            String file = array.getString(inx);

            if (badFiles.contains(file)) continue;

            String requestUrl = url + "titles=" + cleanSpaces(file);
            String jsonString = Simple.open_url(requestUrl);

            if (jsonString == null) return null;

            return new JSONObject(jsonString);
        }

        return null;
    }

    private static JSONObject convertImageInfo(JSONObject wikiJson)
    {
        if (wikiJson == null) return null;

        JSONObject base = wikiJson.getJSONObject("query").getJSONObject("pages");

        int pageId = getId(base.keySet());

        JSONObject baseBranch = base.getJSONObject("" + pageId);

        if (! baseBranch.has("imageinfo"))
        {
            System.out.println(wikiJson.toString(2));

            return null;
        }

        JSONObject imgInfo = baseBranch.getJSONArray("imageinfo").getJSONObject(0);
//        System.out.println(imgInfo.toString(2));

        JSONObject json = new JSONObject();

        json.put("width", imgInfo.getInt("thumbwidth"));
        json.put("height", imgInfo.getInt("thumbheight"));
        json.put("thumburl", imgInfo.getString("thumburl"));
        json.put("url", imgInfo.getString("url"));

        return json;
    }

    private static JSONArray getWikiFiles(String baseUrl, String tag)
    {
        String wikitag = wikiString(tag);

        // "https://de.wikipedia.org/w/api.php?action=query&prop=images&format=json&titles=Donald_Trump"
        // "https://de.wikipedia.org/w/api.php?action=query&titles=Datei:Anti-Trump_protest_announcement,_Mission_District,_San_Francisco.jpg&prop=imageinfo&iiprop=url&iiurlwidth=220&format=json"

        String link = baseUrl + "/w/api.php?action=query&prop=images&format=json&titles=" + wikitag;

        String jsonString = Simple.open_url(link);

        if (jsonString == null) return null;

        System.out.println(link);

        JSONObject tmp = new JSONObject(jsonString);

        JSONObject base = tmp.getJSONObject("query").getJSONObject("pages");

        int pageId = getId(base.keySet());

        if (pageId < 0) return null;

        JSONObject pageBranch = base.getJSONObject("" + pageId);

        if (! pageBranch.has("images")) return null;

        JSONArray images = pageBranch.getJSONArray("images");

        JSONArray files = new JSONArray();

        for (int inx = 0; inx < images.length(); inx++)
        {
            JSONObject part = images.getJSONObject(inx);
            files.put(part.getString("title"));
        }

        if (files.length() <= 0) return null;

        return files;
    }

    private static JSONObject findThumb(String baseUrl, String term)
    {
        // "https://en.wikipedia.org/w/api.php?action=query&formatversion=2&generator=prefixsearch&gpssearch=Merkel&gpslimit=10&prop=pageimages|pageterms&piprop=thumbnail&pithumbsize=400&pilimit=10&redirects=&wbptterms=description&format=json"

        String searchTerm = cleanSpaces(term);
        String url = baseUrl + "/w/api.php?" +
                "action=query&" +
                "formatversion=2&" +
                "generator=prefixsearch&" +
                "gpssearch=" + searchTerm + "&" +
                "gpslimit=1&" +
                "prop=pageimages|pageterms&" +
                "piprop=thumbnail&" +
                "pithumbsize=" + thrumbWidth + "&" +
                "pilimit=1&" +
                "redirects=&" +
                "wbptterms=description&" +
                "format=json";

        String jsonString = Simple.open_url(url);

        if (jsonString == null) return null;

        JSONObject wikiResponse = new JSONObject(jsonString);

        // System.out.println(wikiResponse.toString(2));

        if (! wikiResponse.has("query")) return null;

        JSONArray base = wikiResponse.getJSONObject("query").getJSONArray("pages");

        JSONObject pageBranch = base.getJSONObject(0);

        JSONObject wiki = new JSONObject();

        String title = pageBranch.getString("title");
        wiki.put("title", title);

        String link = baseUrl + "/wiki/" + cleanSpaces(title);
        wiki.put("link", link);

        if (pageBranch.has("thumbnail"))
        {
            wiki.put("thumbnail", pageBranch.getJSONObject("thumbnail"));
        }

        if (pageBranch.has("terms"))
        {
            JSONArray descriptions = pageBranch.getJSONObject("terms").getJSONArray("description");

            if (descriptions.getString(0).equals("Wikimedia-Begriffsklärungsseite")) return null;
            if (descriptions.getString(0).equals("Wikipedia disambiguation page"))   return null;

            wiki.put("description", descriptions);
        }

        return wiki;
    }

    public static JSONObject find(String tag)
    {
        JSONObject imgJson = findThumb(baseUrlDe, tag);

//        JSONArray images = getWikiFiles(baseUrlDe, tag);
//        JSONObject wikiImgJson = imgInfo(baseUrlDe, images);
//        JSONObject imgJson = convertImageInfo(wikiImgJson);

        if (imgJson != null) return imgJson;

        imgJson = findThumb(baseUrlEn, tag);

//        images = getWikiFiles(baseUrlEn, tag);
//        wikiImgJson = imgInfo(baseUrlEn, images);
//        imgJson = convertImageInfo(wikiImgJson);

        // System.out.println(imgJson.toString(2));

        return imgJson;
    }

    public static JSONObject find(JSONArray tags)
    {
        JSONObject json = new JSONObject();

        for (int inx = 0; inx < tags.length(); inx++)
        {
            String tag = tags.getString(inx);
            JSONObject fund = find(tag);

            if (fund == null) continue;

            json.put(tag, fund);
        }

        return json;
    }
}

//
// upload.wikimedia.org/wikipedia/commons/thumb/1/1d/DIE_LINKE_Bundesparteitag_Mai_2014_Kipping%2C_Katja.jpg/330px-DIE_LINKE_Bundesparteitag_Mai_2014_Kipping%2C_Katja.jpg 1.5x,
// upload.wikimedia.org/wikipedia/commons/thumb/1/1d/DIE_LINKE_Bundesparteitag_Mai_2014_Kipping%2C_Katja.jpg/440px-DIE_LINKE_Bundesparteitag_Mai_2014_Kipping%2C_Katja.jpg 2x
