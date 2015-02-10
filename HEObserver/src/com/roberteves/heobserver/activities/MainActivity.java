package com.roberteves.heobserver.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.Article;
import com.roberteves.heobserver.core.Lists;
import com.roberteves.heobserver.core.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import io.fabric.sdk.android.Fabric;
import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;
import unbescape.html.HtmlEscape;

public class MainActivity extends Activity {
    private static ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Util.setupThreadPolicy();
        setTitle(getString(R.string.app_name_long));
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.listView);

        updateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_bar_refresh:
                updateList();
                return true;
            case R.id.action_bar_about:
                Intent i = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateList() {
        UpdateListViewTask updateListViewTask = new UpdateListViewTask();
        try {
            updateListViewTask.execute(getFeeds());
        } catch (IOException e) {
            Crashlytics.logException(e);
            Toast.makeText(getApplicationContext(), "Failed to read feeds list",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String[] getFeeds() throws IOException {
        ArrayList<String> feeds = new ArrayList<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(this
                .getResources().openRawResource(R.raw.feeds)));
        String line;
        while ((line = in.readLine()) != null) {
            feeds.add(line);
        }

        return feeds.toArray(new String[feeds.size()]);
    }

    private void UpdateView() {
        //Create ListView Adapter
        SimpleAdapter simpleAdpt = new SimpleAdapter(this,
                Lists.storyList, android.R.layout.simple_list_item_2,
                new String[]{"title", "date"},
                new int[]{android.R.id.text1, android.R.id.text2});

        //Set ListView from Adapter
        lv.setAdapter(simpleAdpt);

        //Set OnClick Handler
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (isOnline()) {
                    Article article;
                    try {
                        article = new Article(Lists.RssItems.get(position)
                                .getLink(), Lists.RssItems.get(
                                position).getPubDate());

                        Intent i = new Intent(MainActivity.this,
                                ArticleActivity.class);

                        i.putExtra("article", article);
                        startActivity(i);
                    } catch (IOException e) {
                        Crashlytics.logException(e);
                        Toast.makeText(getApplicationContext(),
                                R.string.error_retrieve_article_source,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_no_internet,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private class UpdateListViewTask extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Updating Article List...");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... feeds) {
            if (isOnline()) {
                //Set Lists
                Lists.RssItems = getDataFromFeeds(feeds);
                Lists.storyList = new ArrayList<>();
                ArrayList<RssItem> rssItems = new ArrayList<>();

                // Add Story Items to HashMap Array
                for (RssItem item : Lists.RssItems) {
                    //If item has unsupported media, don't add
                    if (!Article.hasMedia(item.getTitle())) {
                        Lists.storyList.add(createStory(HtmlEscape.unescapeHtml(item.getTitle()), Article.processPubDate(item.getPubDate())));
                        rssItems.add(item);
                    }
                }

                //Update with new lists (filtered results)
                Lists.RssItems = rssItems;

                return true;
            } else {
                Toast.makeText(getApplicationContext(), R.string.error_no_internet,
                        Toast.LENGTH_SHORT).show();

                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (result) {
                UpdateView();
            } else {
                Toast.makeText(getApplicationContext(),
                        R.string.error_update_article_list, Toast.LENGTH_SHORT)
                        .show();
            }
        }

        private HashMap<String, String> createStory(String title, String publishedDate) {
            HashMap<String, String> story = new HashMap<>();
            story.put("title", title);
            story.put("date", publishedDate);

            return story;
        }

        private ArrayList<RssItem> getDataFromFeeds(String[] feeds) {
            ArrayList<RssItem> rssItems = new ArrayList<>();
            ArrayList<RssItem> feedItems;

            for (String s : feeds) {
                try {
                    feedItems = RssReader.read(Util.getWebSource(s, false)).getRssItems();
                    checkDuplicates(rssItems, feedItems);
                } catch (Exception e) {
                    Log.d("Feed Exception", "Feed: " + s + "; Processing: False; " + e.getMessage());
                    Crashlytics.logException(e);

                    //Try with processing if it doesnt work
                    try {
                        feedItems = RssReader.read(Util.getWebSource(s, true)).getRssItems();
                        checkDuplicates(rssItems, feedItems);
                    } catch (Exception ee) {
                        Log.d("Feed Exception", "Feed: " + s + "; Processing: True; " + e.getMessage());
                        Crashlytics.logException(ee);
                    }
                }
            }

            Collections.sort(rssItems);// sorts into reverse date order
            Collections.reverse(rssItems);// flip to correct order
            return rssItems;
        }

        private void checkDuplicates(ArrayList<RssItem> rssItems,
                                     ArrayList<RssItem> feedItems) {
            for (RssItem y : feedItems) {
                Boolean exists = false;
                for (RssItem z : rssItems) {
                    if (z.getTitle().equalsIgnoreCase(y.getTitle())) {
                        exists = true;
                    }
                }

                if (!exists)
                    rssItems.add(y);
            }
        }
    }
}