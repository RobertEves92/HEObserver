package com.roberteves.heobserver.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;
import sheetrock.panda.changelog.ChangeLog;
import unbescape.html.HtmlEscape;

public class MainActivity extends Activity {
    private static ListView lv;
    private static SettingsManager settingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics.Builder().disabled(BuildConfig.DEBUG).build()); //dont log in debug mode
        //Fabric.with(this, new Crashlytics()); //do log in debug mode

        settingsManager = new SettingsManager(this);

        Util.LogMessage("MainActivity", "Activity Started");


        setContentView(R.layout.activity_scroll_list);
        lv = (ListView) findViewById(R.id.listView);

        //Display saved feeds if available or update and display if not or 1hr since last update
        if (StorageManager.LoadLists(MainActivity.this)) {
            UpdateView();
            if (CheckUpdates()) {
                updateList();
            }
        } else {
            updateList();
        }

        // Display changelog / whats new if appropriate
        ChangeLog cl = new ChangeLog(this);
        if(cl.firstRunEver())
            cl.getFullLogDialog().show();
        else if(cl.firstRun())
            cl.getLogDialog().show();

        Answers.getInstance().logLogin(new LoginEvent());
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
        Util.LogMessage("MainActivity", "Option Selected: " + item.getTitle());
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_bar_refresh:
                updateList();
                return true;
            case R.id.action_bar_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Util.LogMessage("MainActivity", "Activity Ended");
    }

    private void updateList() {
        Util.LogMessage("MainActivity", "Update List");
        if (Util.isNetworkAvailable(this)) {
            UpdateListViewTask updateListViewTask = new UpdateListViewTask();
            updateListViewTask.execute(getFeeds());
        } else {
            Util.DisplayToast(this, getString(R.string.error_no_internet));
        }
    }

    private String[] getFeeds() {
        Util.LogMessage("MainActivity", "Get Feeds");
        FeedManager.LoadFeeds(this);
        ArrayList<String> feeds = new ArrayList<>();

        for (Feed f : Lists.FeedList) {
            //Only add the feed if the setting is enabled
            if (settingsManager.isEnabled(f.getCategory())) {
                feeds.add(f.getLink());
            }
        }

        return feeds.toArray(new String[feeds.size()]);
    }

    private void UpdateView() {
        Util.LogMessage("MainActivity", "Update View");
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

    private Boolean CheckUpdates() {
        long diff = Date.GetTimeDifference(new java.util.Date(), StorageManager.LastUpdated(this));
        diff = diff / 1000;//seconds
        diff = diff / 60;//mins
        diff = diff / 60;//hours

        Boolean b = diff >= 1;
        Util.LogMessage("MainActivity", "Check Updates: " + b);
        return b;
    }

    private class UpdateListViewTask extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        private ArrayList<RssItem> rssItems = new ArrayList<>();


        @Override
        protected void onPreExecute() {
            Util.LogMessage("UpdateAsync", "Pre Execute");

            this.dialog.setMessage(getString(R.string.dialog_fetching_articles));
            this.dialog.show();
            this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                    Util.LogMessage("UpdateAsync", "Cancelled");
                    Util.DisplayToast(MainActivity.this, "Update Cancelled");
                }
            });
        }

        @Override
        protected Boolean doInBackground(String... feeds) {
            if (Util.isInternetAvailable()) {
                Util.LogMessage("UpdateAsync", "Execute");
                //region Get Feed Items
                Util.LogMessage("UpdateAsync", "Get Feed Items");
                for (String s : feeds) {
                    if (isCancelled())
                        return false;

                    String source;
                    try {
                        source = Util.getWebSource(s);
                        try {
                            rssItems.addAll(RssReader.read(source).getRssItems());
                        } catch (Exception e) {
                            rssItems.addAll(RssReader.read(source.replaceAll("'", "`")).getRssItems());
                        }
                    } catch (Exception e) {
                        Util.LogException("load feed", s, e);
                    }
                }
                //endregion
                //region Remove Duplicates
                Util.LogMessage("UpdateAsync", "Remove Duplicates");
                ArrayList<RssItem> items = new ArrayList<>();
                for (RssItem x : rssItems) {
                    if (isCancelled())
                        return false;

                    Boolean exists = false;

                    for (RssItem y : items) {
                        if (isCancelled())
                            return false;

                        if (x.getLink().equalsIgnoreCase(y.getLink())) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists)
                        items.add(x);
                }
                rssItems = items;
                //endregion

                return !isCancelled();
            } else {
                Util.LogMessage("UpdateAsync", "No Internet");
                Util.DisplayToast(MainActivity.this, getString(R.string.error_no_internet));
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Util.LogMessage("UpdateAsync", "Post Execute");

            if (result && !isCancelled()) {
                if(rssItems.size() > 0) {
                    //region Generate and Save Lists
                    ArrayList<RssItem> supportedRssItems = new ArrayList<>();
                    List<Map<String, String>> supportedStoryList = new ArrayList<>();
                    Collections.sort(rssItems);
                    Collections.reverse(rssItems);
                    for (RssItem item : rssItems) {
                        //If item has unsupported media, don't add
                        if (!Article.checkLink(item.getLink()) && !Article.checkTitle(item.getTitle())) {
                            HashMap<String, String> story = new HashMap<>();
                            story.put("title", HtmlEscape.unescapeHtml(item.getTitle()));
                            story.put("date", Date.FormatDate(item.getPubDate(), "dd/MM/yyyy HH:mm"));
                            supportedStoryList.add(story);
                            supportedRssItems.add(item);
                        }
                    }

                    //Update with new lists (filtered results)
                    Lists.RssItems = supportedRssItems;
                    Lists.storyList = supportedStoryList;

                    //Save Lists
                    StorageManager.SaveLists(MainActivity.this);
                    //endregion

                    UpdateView();
                }
                else
                {
                    Util.LogMessage("UpdateAsync", "No Items");
                    Util.DisplayToast(MainActivity.this, getString(R.string.error_no_items));
                }
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    }
}