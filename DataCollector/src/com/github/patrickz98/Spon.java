package com.github.patrickz98;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spon
{
    private SimpleNer ner;
    private JSONArray dataPool;
    private Mongo mongo;

    Spon(Mongo mongo, JSONArray dataPool)
    {
        ner = SimpleNer.getInstance();
        this.dataPool = dataPool;
        this.mongo = mongo;
    }

    private void match(String html, String pattern, StringBuilder head)
    {
        Matcher matcher = Pattern.compile(pattern).matcher(html);

        while (matcher.find())
        {
            head.append(matcher.group(1));
            head.append("\n");
        }
    }

    private String head(String html)
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
    private String getArticle(String link)
    {
        String metaData = Simple.latin1ToUtf8(Simple.open_url(link));

        String head = head(metaData);

        String pattern = "<p>(.*?)</p>";
        Matcher matcher = Pattern.compile(pattern).matcher(metaData);

        StringBuilder articleString = new StringBuilder();
        articleString.append(head);
        articleString.append("\n\n");

        while (matcher.find())
        {
            articleString.append(matcher.group(1));
            articleString.append("\n\n");
        }

        String content = Simple.deMoronize(articleString.toString());
        content = content.replaceAll("<.*?>(.*?)</.*?>", "$1");

        StringEscapeUtils.unescapeHtml4(content);

        return content;
    }

    private JSONObject processArticle(String title, String link)
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

        json.put("allTags", tags);
        json.put("tags", nerResult);
        // json.put("COUNTRIES", CountryManager.getCountrys(tags));

        return json;
    }

    private JSONObject preExisting(String link)
    {
        JSONObject find = new JSONObject();
        find.put("_id", Simple.md5(link));

        return mongo.findOne(find.toString());
    }

    public void scan()
    {
        // System.out.println("Downloading: http://www.spiegel.de/schlagzeilen/");

        String spon = Simple.open_url("http://www.spiegel.de/schlagzeilen/");
        spon = Simple.latin1ToUtf8(spon);

        if (spon == null)
        {
            System.err.println("Error: spiegel.de");
            return;
        }

        String pattern = "<a href=\"(.*?)\".*?title=\"(.*?)\">";
        Matcher matcher = Pattern.compile(pattern).matcher(spon);

        int count = 1;

        while (matcher.find())
        {
            String title = matcher.group(2);
            String link  = "http://www.spiegel.de" + matcher.group(1);

            if (matcher.group(1).startsWith("http")) continue;

            JSONObject preExisting = preExisting(link);

            if (preExisting == null)
            {
                JSONObject article = processArticle(title, link);

                if (article == null) continue;

                dataPool.put(article);

                System.out.println("(Spiegel Online) #" + count + " - " + article.getString("title"));
                count++;

                continue;
            }

            if (preExisting.getInt("date") == Simple.toDayDate())
            {
                dataPool.put(preExisting);
                // System.out.println("(Suddeutsche) preExisting: " + title);
            }
            else
            {
                // System.out.println("(Suddeutsche) preExisting-past: " + title);
            }

            // System.out.print("Spiegel Online: " + count + "\r");
            // System.out.flush();
        }
    }
}
