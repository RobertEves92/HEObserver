package com.roberteves.heobserver.core;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Map;

public class StorageManager {
    private final static String PREFS_NAME = "HEOStorage";

    public static void SaveData(Activity activity) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = activity.getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();

        editor.putInt("storylist_size", Lists.storyList.size());

        int i = 0;
        for(Map<String,String> m : Lists.storyList)
        {
            editor.putString("storylist_"+i+"_date",m.get("date"));
            editor.putString("storylist_"+i+"_title",m.get("title"));
            i++;
        }
        editor.commit();
    }

    public static Map<?, ?> GetData(Activity activity)
    {
        SharedPreferences settings;
        settings = activity.getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        Map<?,?> map = settings.getAll();
        return map;
    }
}
