package com.roberteves.heobserver.feeds;


import android.app.Activity;

import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.Lists;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FeedManager {
    public static void LoadFeeds(Activity activity) {
        BufferedReader in = new BufferedReader(new InputStreamReader(activity.getApplicationContext()
                .getResources().openRawResource(R.raw.feeds)));
        String line;
        Category c = Category.News;
        while ((line = in.readLine()) != null) {
            if (line.startsWith("#")) {
                c = Feed.getCategoryFromString(line.toLowerCase().replace('#', '\0').trim());
            } else {
                Lists.FeedList.add(new Feed(line, c));
            }
        }
    }
}
