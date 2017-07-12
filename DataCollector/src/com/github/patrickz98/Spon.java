package com.github.patrickz98;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spon
{
    private static SimpleNer ner = SimpleNer.getInstance();

    private static void match(String html, String pattern, StringBuilder head)
    {
        Matcher matcher = Pattern.compile(pattern).matcher(html);

        while (matcher.find())
        {
            head.append(matcher.group(1));
            head.append("\n");
        }
    }

    private static String head(String html)
    {
        StringBuilder head = new StringBuilder();
        String pattern;

        // headline-intro
        pattern = "<span class=\"headline-intro\">(.*?)</span>";
        match(html, pattern, head);

        // headline
        pattern = "<span class=\"headline\">(.*?)</span>";
        match(html, pattern, head);

        // article-intro
        pattern = "<p class=\"article-intro\"><strong>(.*?)</strong></p>";
        match(html, pattern, head);

        return head.toString();
    }

    // Bug --> two kinds of articles (Authors and Agency Articles)
    private static String getArticle(String link)
    {
        String article = Simple.latin1ToUtf8(Simple.open_url(link));

        String head = head(article);

        String[] parts = article.split("<div class=\"author-contacts\">");

        if (parts.length < 2) return null;

        String pattern = "<p>(.*?)</p>";
        Matcher matcher = Pattern.compile(pattern).matcher(parts[ 1 ]);

        StringBuilder articleString = new StringBuilder();
        articleString.append(head);
        articleString.append("\n\n\n");

        while (matcher.find())
        {
            articleString.append(matcher.group(1));
            articleString.append("\n\n");
        }

        String content = Simple.deMoronize(articleString.toString());
        content = content.replaceAll("<.*?>(.*?)</.*?>", "$1");

        return content;
    }

    private static JSONObject processArticle(String title, String link)
    {
        if (! link.endsWith(".html")) return null;
        if (title.equals(""))         return null;
        if (title.contains("="))      return null;

        title = Simple.deMoronize(title);

        String content = getArticle(link);

        JSONObject nerResult = ner.parse(content);

        if ((nerResult == null) || (nerResult.length() <= 0)) return null;

        JSONObject json = new JSONObject();

        json.put("source",  "Spiegel Online");

        json.put("title",    title);
        json.put("link",     link);
        json.put("date",     Simple.toDayDate());
        json.put("content",  content);

        JSONArray tags = Simple.getAllNerTags(nerResult);

        json.put("TAGS",  tags);
        // json.put("COUNTRIES", CountryManager.getCountrys(tags));

        if (nerResult.has("LOCATION"))     json.put("LOCATION",     nerResult.getJSONArray("LOCATION"));
        if (nerResult.has("MISC"))         json.put("MISC",         nerResult.getJSONArray("MISC"));
        if (nerResult.has("ORGANIZATION")) json.put("ORGANIZATION", nerResult.getJSONArray("ORGANIZATION"));
        if (nerResult.has("PERSON"))       json.put("PERSON",       nerResult.getJSONArray("PERSON"));

        return json;
    }

    public static JSONArray scan()
    {
        System.out.println("Downloading: http://www.spiegel.de/schlagzeilen/");

        String spon = Simple.open_url("http://www.spiegel.de/schlagzeilen/");
        spon = Simple.latin1ToUtf8(spon);

        if (spon == null)
        {
            System.err.println("SPON Error: spon == null");
            return null;
        }

        String pattern = "<a href=\"(.*?)\".*?title=\"(.*?)\">";
        Matcher matcher = Pattern.compile(pattern).matcher(spon);

        JSONArray json = new JSONArray();

        int count = 1;

        System.out.print("Spiegel Online: 0\n");

        while (matcher.find())
        {
            String title = matcher.group(2);
            String link  = "http://www.spiegel.de" + matcher.group(1);

            if (matcher.group(1).startsWith("http")) continue;

            // performance
//            String id = Simple.md5(link);
//            Boolean idExist = Elastic.idExist("localhost", "sodalitas", "articles", id);
//            if (idExist) continue;

            // System.out.println("--> " + link);

            JSONObject article = processArticle(title, link);

            if (article == null) continue;

            json.put(article);

            // System.out.print("Spiegel Online: " + count + "\r");
            // System.out.flush();

            System.out.println("Spiegel Online: " + count);

            count++;
        }

        System.out.println();

        return json;
    }
}
