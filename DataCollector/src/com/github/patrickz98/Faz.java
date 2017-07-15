package com.github.patrickz98;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Faz
{
    private SimpleNer ner;
    private JSONArray dataPool;
    private Mongo mongo;

    Faz(Mongo mongo, JSONArray dataPool)
    {
        this.ner = SimpleNer.getInstance();
        this.dataPool = dataPool;
        this.mongo = mongo;
    }

    private JSONObject preExisting(String link)
    {
        JSONObject find = new JSONObject();
        find.put("_id", Simple.md5(link));

        return mongo.findOne(find.toString());
    }

    private String getTitle(String metaData)
    {
        String pattern = "<meta property=\"og:title\" content=\"(.*?)\" />";
        Matcher matcher = Pattern.compile(pattern).matcher(metaData);
        matcher.find();

        return matcher.group(1);
    }

    private String getHead(String metaData)
    {
        String pattern = "<meta property=\"og:description\" content=\"(.*?)(\"|\n)";
        Matcher matcher = Pattern.compile(pattern).matcher(metaData);
        matcher.find();

        return matcher.group(1) + "\n\n";
    }

    private String getArticle(String metaData)
    {
        StringBuilder content = new StringBuilder();
        content.append(getHead(metaData));

        String pattern = "(<p|<p class=\"First\".*?)>(.*?)</p>";
        Matcher matcher = Pattern.compile(pattern).matcher(metaData);

        while (matcher.find())
        {
            String pBlock = matcher.group(2);

            if (pBlock.equals("Ein Fehler ist aufgetreten. Bitte überprüfen Sie Ihre Eingaben.")) continue;

            content.append(pBlock);
            content.append("\n\n");
        }

        String result = Simple.deMoronize(content.toString().trim());
        StringEscapeUtils.unescapeHtml4(result);

        return result;
    }

    private JSONObject processArticle(String link)
    {
        String metaData = Simple.open_url(link);

        String title = Simple.deMoronize(getTitle(metaData));
        String content = getArticle(metaData);

        JSONObject nerResult = ner.parse(content);

        if ((nerResult == null) || (nerResult.length() <= 0)) return null;

        JSONObject json = new JSONObject();

        json.put("source",  "FAZ");

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

    // http://www.faz.net/aktuell/rhein-main/region/step-by-step-in-darmstadt-es-dauert-lange-das-schweigen-zu-brechen-15105448.html

    public void scan()
    {
        // System.out.println("Downloading: http://www.faz.net");

        String date = Simple.toDayDate("dd.MM.yyyy");
        String html = Simple.open_url(
                "http://www.faz.net/suche/?" +
                "offset=&cid=&index=&query=&offset=&allboosted=&boostedresultsize=%24boostedresultsize&" +
                "from=" + date + "&to=" + date + "&" +
                "BTyp=redaktionelleInhalte&chkBoxType_2=on&author=&username=&sort=date&" +
                "resultsPerPage=9999");

        if (html == null)
        {
            System.err.println("Error: faz.net");
            return;
        }

        String pattern = "<a.*?href=\"(/aktuell.*?.html)\"";
        Matcher matcher = Pattern.compile(pattern).matcher(html);

        int count = 1;

        ArrayList<String> processedUrls = new ArrayList<>();

        while (matcher.find())
        {
            String link = "http://www.faz.net" + matcher.group(1);

            if (processedUrls.contains(link)) continue;
            processedUrls.add(link);

            // System.out.println(link);

            JSONObject preExisting = preExisting(link);

            if (preExisting == null)
            {
                JSONObject article = processArticle(link);

                if (article == null) continue;

                dataPool.put(article);

                System.out.println("(FAZ) #" + count + " - " + article.getString("title"));
                count++;

                continue;
            }

            if (preExisting.getInt("date") == Simple.toDayDate())
            {
                dataPool.put(preExisting);
                // System.out.println("(Suddeutsche) preExisting: " + title);
            }
        }
    }
}
