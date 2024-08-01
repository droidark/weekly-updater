package xyz.krakenkat.weeklyupdater.util;

public class Constants {
    private Constants() {}
    public static final String QUERY = "select key, title_id, whakoom_url, publisher_url from \"%s\" where key in (%s)";
}
