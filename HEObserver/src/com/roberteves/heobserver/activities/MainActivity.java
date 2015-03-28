package com.roberteves.heobserver.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.roberteves.heobserver.BuildConfig;
import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.Article;
import com.roberteves.heobserver.core.Date;
import com.roberteves.heobserver.core.Lists;
import com.roberteves.heobserver.core.SettingsManager;
import com.roberteves.heobserver.core.StorageManager;
import com.roberteves.heobserver.core.Util;
import com.roberteves.heobserver.feeds.Feed;
import com.roberteves.heobserver.feeds.FeedManager;

import java.net.SocketTimeoutException;
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
        Fabric.with(this, new Crashlytics.Builder().disabled(BuildConfig.DEBUG).build()); //dont log in debug mode
        //Fabric.with(this, new Crashlytics()); //do log in debug mode
        setTitle(getString(R.string.app_name_long));
        setContentView(R.layout.activity_scroll_list);
        lv = (ListView) findViewById(R.id.listView);

        //Display saved feeds if available or update and display if not or 1hr since last update
        if (StorageManager.LoadLists(MainActivity.this)) {
            if (CheckUpdates()) {
                updateList();
            } else {
                UpdateView();
            }
        } else {
            updateList();
        }
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
            case R.id.action_bar_save:
                StorageManager.SaveLists(this);
                return true;
            case R.id.action_bar_load:
                StorageManager.LoadLists(this);
                UpdateView();
                return true;
            case R.id.action_bar_refresh:
                updateList();
                return true;
            case R.id.action_bar_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateList() {
        UpdateListViewTask updateListViewTask = new UpdateListViewTask();
        updateListViewTask.execute(getFeeds());
    }

    private String[] getFeeds() {
        FeedManager.LoadFeeds(this);
        ArrayList<String> feeds = new ArrayList<>();
        SettingsManager settingsManager = new SettingsManager(this);

        for (Feed f : Lists.FeedList) {
            //Only add the feed if the setting is enabled
            if (settingsManager.isEnabled(f.getCategory())) {
                feeds.add(f.getLink());
            }
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
                Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                intent.putExtra("link", Lists.RssItems.get(position).getLink());
                startActivity(intent);
            }
        });
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private Boolean CheckUpdates() {
        long diff = Date.GetTimeDifference(StorageManager.LastUpdated(this), new java.util.Date());
        diff = diff / 1000;//seconds
        diff = diff / 60;//mins
        diff = diff / 60;//hours

        if (diff >= 1) {
            return true;
        } else {
            return false;
        }
    }

    private class UpdateListViewTask extends AsyncTask<String, Integer, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        private int completedFeeds = 0;

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getString(R.string.dialog_fetching_articles));
            this.dialog.setCancelable(false);
            this.dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... feeds) {
            if (isOnline()) {

                ArrayList<RssItem> rssItems = new ArrayList<>();

                getFeeds(rssItems, feeds);
                processFeeds(rssItems);

                return true;
            } else {
                Handler handler = new Handler(getApplicationContext().getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.error_no_internet,
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
        }

        private void processFeeds(ArrayList<RssItem> rssItems) {
            Collections.sort(rssItems);// sorts into reverse date order
            Collections.reverse(rssItems);// flip to correct order
            Lists.RssItems = rssItems;

            // Add Story Items to HashMap Array
            Lists.storyList = new ArrayList<>();
            rssItems = new ArrayList<>();

            for (RssItem item : Lists.RssItems) {
                //If item has unsupported media, don't add
                if (!Article.checkLink(item.getLink()) && !Article.checkTitle(item.getTitle())) {
                    HashMap<String, String> story = new HashMap<>();
                    story.put("title", HtmlEscape.unescapeHtml(item.getTitle()));
                    story.put("date",Date.FormatDate(item.getPubDate(),"dd/MM/yyyy HH:mm"));
                    Lists.storyList.add(story);
                    rssItems.add(item);
                }
            }

            //Update with new lists (filtered results)
            Lists.RssItems = rssItems;
        }

        private void getFeeds(ArrayList<RssItem> rssItems, String[] feeds) {
            ArrayList<RssItem> feedItems;
            for (String s : feeds) {
                try {
                    feedItems = RssReader.read(Util.getWebSource(s, false)).getRssItems();
                    processDuplicates(rssItems, feedItems);
                } catch (Exception e) {
                    if (!(e instanceof SocketTimeoutException)) { //Don't log or try again if timeout exception
                        Util.LogException("load feed without processing", s, e);
                    } else {
                        Util.LogMessage(Log.INFO, "SocketTimeout", "Feed: " + s);
                    }


                    //Try with processing if it doesnt work
                    try {
                        feedItems = RssReader.read(Util.getWebSource(s, true)).getRssItems();
                        processDuplicates(rssItems, feedItems);
                    } catch (Exception ee) {
                        if (!(ee instanceof SocketTimeoutException)) {
                            Util.LogException("load feed with processing", s, ee);
                        } else {
                            Util.LogMessage(Log.INFO, "SocketTimeout", "Feed: " + s);
                        }
                    }
                } finally {
                    completedFeeds++;
                    publishProgress((100 / feeds.length) * completedFeeds);
                }
            }
        }

        private void processDuplicates(ArrayList<RssItem> rssItems, ArrayList<RssItem> feedItems) {
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

        protected void onProgressUpdate(Integer... progress) {
            this.dialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (result) {
                UpdateView();
                StorageManager.SaveLists(MainActivity.this);
            }
        }
    }
}