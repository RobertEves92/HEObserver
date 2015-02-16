package com.roberteves.heobserver.core;

import android.app.Activity;
import android.content.SharedPreferences;

public class SettingsManager {
    private final static String PREFS_NAME = "HEOPrefs";
    private final static String KEY_FEED_NEWS = "feed_news";
    private final static String KEY_FEED_LOCALNEWS = "feed_localnews";
    private final static String KEY_FEED_SPORT = "feed_sport";
    private final static String KEY_FEED_MUSIC = "feed_music";
    private final static String KEY_FEED_LIFESTYLE = "feed_lifestyle";
    private final static String KEY_FEED_WEATHER = "feed_weather";

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    public SettingsManager(Activity activity) {
        settings = activity.getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
    }

    public Boolean getFeedNews() {
        return settings.getBoolean(KEY_FEED_NEWS, true);
    }

    public void setFeedNews(Boolean b) {
        editor.putBoolean(KEY_FEED_NEWS, b).commit();
    }

    public Boolean getFeedLocalNews() {
        return settings.getBoolean(KEY_FEED_LOCALNEWS, true);
    }

    public void setFeedLocalNews(Boolean b) {
        editor.putBoolean(KEY_FEED_LOCALNEWS, b).commit();
    }

    public Boolean getFeedSport() {
        return settings.getBoolean(KEY_FEED_SPORT, true);
    }

    public void setFeedSport(Boolean b) {
        editor.putBoolean(KEY_FEED_SPORT, b).commit();
    }

    public Boolean getFeedMusic() {
        return settings.getBoolean(KEY_FEED_MUSIC, true);
    }

    public void setFeedMusic(Boolean b) {
        editor.putBoolean(KEY_FEED_MUSIC, b).commit();
    }

    public Boolean getFeedLifestyle() {
        return settings.getBoolean(KEY_FEED_LIFESTYLE, true);
    }

    public void setFeedLifestyle(Boolean b) {
        editor.putBoolean(KEY_FEED_LIFESTYLE, b).commit();
    }

    public Boolean getFeedWeather() {
        return settings.getBoolean(KEY_FEED_WEATHER, true);
    }

    public void setFeedWeather(Boolean b) {
        editor.putBoolean(KEY_FEED_WEATHER, b).commit();
    }

    public void resetSettings() {
        setFeedNews(true);
        setFeedLocalNews(true);
        setFeedSport(true);
        setFeedMusic(true);
        setFeedLifestyle(true);
        setFeedWeather(true);
    }
}
