package com.roberteves.heobserver.core;


import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nl.matshofman.saxrssreader.RssItem;

public class StorageManager {
    private final static String PREFS_NAME = "HEOStorage";

    public static void SaveData(Activity activity) {
        try {
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

            //region RssItem List
            editor.putInt("rssitems_size", Lists.RssItems.size());

            int ii = 0;
            for (RssItem r : Lists.RssItems) {
                editor.putString("rssitem_" + ii + "_title", r.getTitle());
                editor.putString("rssitem_" + ii + "_link", r.getLink());
                editor.putString("rssitem_" + ii + "_description", r.getDescription());
                editor.putString("rssitem_" + ii + "_date", r.getPubDate().toString());
                editor.putString("rssitem_" + ii + "_content", r.getContent());
                ii++;
            }
            //endregion
            editor.apply();
        } catch (Exception e) {
            Util.LogException("save data", "none", e);
        }
    }

    public static Boolean GetData(Activity activity) {
        try {
            SharedPreferences settings;
            settings = activity.getApplicationContext().getSharedPreferences(PREFS_NAME, 0);

            //region StoryList
            int storyListSize = settings.getInt("storylist_size", 0);
            List<Map<String, String>> storyList = new ArrayList<>();
            if (storyListSize != 0) {
                int i = 0;
                do {
                    HashMap<String, String> story = new HashMap<>();
                    story.put("title", settings.getString("storylist_" + i + "_title", ""));
                    story.put("date", settings.getString("storylist_" + i + "_date", ""));
                    storyList.add(story);
                    i++;
                } while (i != storyListSize);
            }
            //endregion
            //region RssItem List
            int rssItemsSize = settings.getInt("rssitems_size", 0);
            ArrayList<RssItem> RssItems = new ArrayList<>();
            if (rssItemsSize != 0) {
                DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzzzzzzz yyyy", Locale.getDefault());

                int ii = 0;
                do {
                    RssItem r = new RssItem();
                    r.setTitle(settings.getString("rssitem_" + ii + "_title", ""));
                    r.setLink(settings.getString("rssitem_" + ii + "_link", ""));
                    r.setDescription(settings.getString("rssitem_" + ii + "_description", ""));
                    try {
                        String d = settings.getString("rssitem_" + ii + "_date", "");
                        r.setPubDate(df.parse(d));
                    } catch (Exception e) {
                        Log.e("ERROR", e.getMessage());
                    }
                    r.setContent(settings.getString("rssitem_" + ii + "_content", ""));
                    RssItems.add(r);
                    ii++;
                } while (ii != rssItemsSize);
            }
            //endregion
            if (storyList.size() != 0 && RssItems.size() != 0) {
                Lists.storyList = storyList;
                Lists.RssItems = RssItems;

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Util.LogException("load data", "none", e);
            return false;
        }
    }
}
