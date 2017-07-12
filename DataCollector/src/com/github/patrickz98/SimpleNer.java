package com.github.patrickz98;

import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// PERSON, ORGANIZATION, LOCATION, DATE, MONEY, PERCENT, TIME
public class SimpleNer
{
    private AbstractSequenceClassifier classifier;

    // this take a while
    private SimpleNer()
    {
        classifier = CRFClassifier.getClassifierNoExceptions("edu/stanford/nlp/models/ner/german.conll.hgc_175m_600.crf.ser.gz");
    }

    private static SimpleNer instance;

    public static SimpleNer getInstance()
    {
        if (SimpleNer.instance == null)
        {
            SimpleNer.instance = new SimpleNer();
        }

        return SimpleNer.instance;
    }

    // different language labels
    private String normalise(String category)
    {
        if (category.equals("I-ORG"))  return "organization";
        if (category.equals("I-MISC")) return "misc";
        if (category.equals("I-PER"))  return "person";
        if (category.equals("I-LOC"))  return "location";
        if (category.equals("B-ORG"))  return "organization";
        if (category.equals("B-MISC")) return "misc";
        if (category.equals("B-PER"))  return "person";
        if (category.equals("B-LOC"))  return "location";

        if (category.equals("PERS"))   return "person";
        if (category.equals("ORG"))    return "organization";
        if (category.equals("OTROS"))  return "misc";
        if (category.equals("LUG"))    return "location";

        if (category.equals("LOC"))    return "location";

        // chinese --> GPE???
        // if (category.equals("GPE")) return ????;

        return category;
    }

    private String deMoronize(String str)
    {
        str = str.replace("\"",  "");
        str = str.replace("'",  "");
        str = str.replace("(",  "");
        str = str.replace(")",  "");

        return str;

    }

    private boolean checkUpperCase(String str)
    {
        char fist  = str.charAt(0);
        char upper = Character.toUpperCase(fist);

        return fist == upper;
    }

    public JSONObject parse(String str)
    {
        if (str == null) return null;

        String   result = classifier.classifyWithInlineXML(str);
        Object[] labels = classifier.labels().toArray();

        JSONObject json = new JSONObject();

        for (Object label: labels)
        {
            String category = label.toString();
            String regex = String.format("<%s>(.*?)</%s>", category, category);

            Matcher matcher = Pattern.compile(regex).matcher(result);

            ArrayList<String> tmp = new ArrayList<>();

            while (matcher.find())
            {
                String find = deMoronize(matcher.group(1));

                if (! checkUpperCase(find)) break;

                if (! tmp.contains(find)) tmp.add(find);
            }

            if (tmp.size() > 0) json.put(normalise(category), tmp);
        }

        return json;
    }
}
