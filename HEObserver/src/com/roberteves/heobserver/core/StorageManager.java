package com.roberteves.heobserver.core;


import android.app.Activity;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageManager {
    private final static String PREFS_NAME = "HEOStorage";

    public static void SaveData(Activity activity) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = activity.getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();

        //region StoryList
        editor.putInt("storylist_size", Lists.storyList.size());

        int i = 0;
        for (Map<String, String> m : Lists.storyList) {
            editor.putString("storylist_" + i + "_date", m.get("date"));
            editor.putString("storylist_" + i + "_title", m.get("title"));
            i++;
        }
        //endregion

        editor.apply();
    }

    public static void GetData(Activity activity) {
        SharedPreferences settings;
        settings = activity.getApplicationContext().getSharedPreferences(PREFS_NAME, 0);

        //region StoryList
        int storyListSize = settings.getInt("storylist_size", 0);
        List<Map<String, String>> storyList = new ArrayList<>();

        int i = 0;
        do {
            HashMap<String, String> story = new HashMap<>();
            story.put("title", settings.getString("storylist_" + i + "_title", ""));
            story.put("date", settings.getString("storylist_" + i + "_date", ""));
            storyList.add(story);
            i++;
        } while (i != storyListSize);
        //endregion

        //TODO add final processing here
    }
}
