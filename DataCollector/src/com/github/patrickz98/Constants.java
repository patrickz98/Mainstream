package com.github.patrickz98;

public class Constants
{
    private static final String basePath          = "/Users/patrick/Desktop/Projects/Mainstream/DataCollector/Data/";
    // private static final String basePath          = "./";
    public  static final String countriesJsonPath = basePath + "countries.json";
    public  static final String aliasPath         = basePath + "alias.json";

//    public static final String mongoDBServer = "localhost";
    public static final String mongoDBServer = "odroid-ubuntu.local";
    public static final String mongoDB = "MainStream";

    public static final String collectionWiki = "wiki";
    public static final String collectionMetaData = "metaData";

    // public static final String dumpDir = "/Users/patrick/Desktop/Projects/Mainstream/DataCollector/dump/";
    public static final String dumpDir = "./dump/";
    public static final int limitTop = 30;
}
