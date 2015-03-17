package com.roberteves.heobserver.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;

import com.roberteves.heobserver.feeds.Category;

public class SettingsManager {
    private final static String PREFS_NAME = "HEOPrefs";
    private final static String KEY_FEED_SPORT = "feed_sport";
    private final static String KEY_FEED_LIFESTYLE = "feed_lifestyle";
    private final static String KEY_FEED_RETAIL = "feed_retail";
    private final static String KEY_FEED_WEATHER = "feed_weather";
    private final static String KEY_FEED_FAMILY = "feed_family";
    private final static String KEY_FEED_MISC = "feed_misc";

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public SettingsManager(Activity activity) {
        settings = activity.getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
    }

    public Boolean isEnabled(Category category) {
        switch (category) {
            default:
            case LocalNews:
                return true;
            case Sport:
                return getFeedSport();
            case Lifestyle:
                return getFeedLifestyle();
            case Retail:
                return getFeedRetail();
            case Weather:
                return getFeedWeather();
            case Family:
                return getFeedFamily();
            case Misc:
                return getFeedMisc();
        }
    }

    public Boolean getFeedLocalNews() {
        return true;
    }

    public Boolean getFeedSport() {
        return settings.getBoolean(KEY_FEED_SPORT, false);
    }

    public void setFeedSport(Boolean b) {
        editor.putBoolean(KEY_FEED_SPORT, b).commit();
    }

    public Boolean getFeedLifestyle() {
        return settings.getBoolean(KEY_FEED_LIFESTYLE, false);
    }

    public void setFeedLifestyle(Boolean b) {
        editor.putBoolean(KEY_FEED_LIFESTYLE, b).commit();
    }

    public Boolean getFeedRetail() {
        return settings.getBoolean(KEY_FEED_RETAIL, false);
    }

    public void setFeedRetail(Boolean b) {
        editor.putBoolean(KEY_FEED_RETAIL, b).commit();
    }

    public Boolean getFeedWeather() {
        return settings.getBoolean(KEY_FEED_WEATHER, false);
    }

    public void setFeedWeather(Boolean b) {
        editor.putBoolean(KEY_FEED_WEATHER, b).commit();
    }

    public Boolean getFeedFamily() {
        return settings.getBoolean(KEY_FEED_FAMILY, false);
    }

    public void setFeedFamily(Boolean b) {
        editor.putBoolean(KEY_FEED_FAMILY, b).commit();
    }

    public Boolean getFeedMisc() {
        return settings.getBoolean(KEY_FEED_MISC, false);
    }

    public void setFeedMisc(Boolean b) {
        editor.putBoolean(KEY_FEED_MISC, b).commit();
    }

    public void resetSettings() {
        setFeedSport(false);
        setFeedLifestyle(false);
        setFeedRetail(false);
        setFeedWeather(false);
        setFeedFamily(false);
        setFeedMisc(false);
    }
}
