package com.roberteves.heobserver.feeds;


import android.app.Activity;

import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.Lists;
import com.roberteves.heobserver.core.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FeedManager {
    public static void LoadFeeds(Activity activity) {
        ArrayList<Feed> feedList = new ArrayList<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(activity.getApplicationContext()
                .getResources().openRawResource(R.raw.feeds)));
        String line;
        Category c = Category.LocalNews;
        try {
            while ((line = in.readLine()) != null) {
                if (line.startsWith("#")) {
                    c = Feed.getCategoryFromString(line.toLowerCase().replace('#', '\0').trim());
                } else {
                    feedList.add(new Feed(line, c));
                }
            }
            Lists.FeedList = feedList;
        } catch (IOException e) {
            Util.LogException("load feeds", "none", e);
        }
    }
}
