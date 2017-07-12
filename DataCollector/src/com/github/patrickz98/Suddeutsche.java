package com.github.patrickz98;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Suddeutsche
{
    private SimpleNer ner;
    private JSONArray dataPool;

    Suddeutsche(JSONArray dataPool)
    {
        ner = SimpleNer.getInstance();
        this.dataPool = dataPool;
    }

    private String getSummary(String html)
    {
        //<p class="article entry-summary resized"></p>

        String  patternString = "text: \"(.*?)\",";

        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) return matcher.group(1);

        return null;
    }

    private String secondTry(String html)
    {
        //<p class="resized"></p>

        if (! html.contains("<span id=\"sharingbaranchor\"></span>")) return null;

        String summary = getSummary(html);

        String[] parts = html.split("<span id=\"sharingbaranchor\"></span>");

        String  patternString = "<(p|h3)>(.*?)</(p|h3)>";

        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(parts[ 1 ]);

        StringBuilder content = new StringBuilder();
        if (summary != null)
        {
            content.append(summary);
            content.append("\n\n\n");
        }

        while (matcher.find())
        {
            String part = matcher.group(2);
            content.append(part);
            content.append("\n\n");
        }

        String article = Simple.deMoronize(content.toString());
        article = article.replaceAll("<.*?>(.*?)</.*?>", "$1");

        if (article.length() < 20) return null;

        return article;
    }

    public String getArticle(String link)
    {
        String html = Simple.open_url(link);

        if (html == null) return null;

        String  patternString = "<section class=\"body\">(.*?)</section>";
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(html);

        if (! matcher.find()) return secondTry(html);

        String section = matcher.group(1);

        patternString = "<p>(.*?)</p>";
        pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
        matcher = pattern.matcher(section);

        StringBuilder articleString = new StringBuilder();

        while (matcher.find())
        {
            String p = matcher.group(1);
            articleString.append(p);
            articleString.append("\n");
        }

        return Simple.deMoronize(articleString.toString());
    }

    private JSONObject processArticle(String title, String link)
    {
        if (link.contains("anzeige"))        return null;
        if (title.contains("Kalenderblatt")) return null;

        String content = getArticle(link);

        if (content == null) return null;

        JSONObject nerResult = ner.parse(content);

        if ((nerResult == null) || (nerResult.length() <= 0)) return null;

        JSONObject json = new JSONObject();

        json.put("source", "Süddeutsche Zeitung");

        json.put("title",    title);
        json.put("link",     link);
        json.put("date",     Simple.toDayDate());
        json.put("content",  content);

        json.put("allTags", Simple.getAllNerTags(nerResult));
        // json.put("COUNTRIES", CountryManager.getCountries(tags));

        json.put("tags", nerResult);

        return json;
    }

    public void scan()
    {
        System.out.println("Downloading: http://www.sueddeutsche.de");

        String htmlJson = Simple.open_url("http://www.sueddeutsche.de/news/teasers?from=0&size=9999&search=&sort=date&all[]=dep&all[]=typ&all[]=sys&time=P1D");

        if (htmlJson == null)
        {
            System.err.println("Error");
            return;
        }

        JSONObject sourceJson = new JSONObject(htmlJson);

        String html = sourceJson.getString("listitems");

        String  patternString = "<a class=\"entrylist__link\" href=\"(.*?)\">.*?<em class=\"entrylist__title\">(.*?)</em>";
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(html);

        int count = 1;

        while (matcher.find())
        {
            String title = matcher.group(2);
            String link  = matcher.group(1);

            JSONObject article = processArticle(title, link);

            if (article == null) continue;

            dataPool.put(article);

            System.out.println("(" + count + ") Suddeutsche: " + article.getString("title"));

            count++;

            // if (count >= 50) break;
        }
    }
}
