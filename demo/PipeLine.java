package com.example.demo;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.util.Properties;


public class PipeLine {
    private static Properties properties;
    private static String propertiesName = "tokenize, ssplit, pos, lemma, ner";
    private static StanfordCoreNLP stanfordCoreNLP;
    static {
        properties = new Properties();
        properties.setProperty("annotators", propertiesName);
        properties.setProperty("ner.useSUTime", "false");
    }

    public static StanfordCoreNLP getPipeLine()
    {
        if (stanfordCoreNLP==null)
        {
            stanfordCoreNLP = new StanfordCoreNLP(properties);
        }
        return stanfordCoreNLP;
    }

}
