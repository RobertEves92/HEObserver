package com.roberteves.heobserver.activities;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.unbescape.html.HtmlEscape;

import nl.matshofman.saxrssreader.RssItem;

import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.Article;
import com.roberteves.heobserver.core.Dialogs;
import com.roberteves.heobserver.core.Global;
import com.roberteves.heobserver.core.Lists;
import com.roberteves.heobserver.rss.RSSHandler;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends ActionBarActivity {
	private static ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_main);
		Global.APP_CONTEXT = getApplicationContext();
		lv = (ListView) findViewById(R.id.listView);

		updateList();
	}

	private void updateList() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Stores all Rss Items from news feed
		Lists.RssItems = RSSHandler.GetFeedItems();
		Lists.storyList = new ArrayList<Map<String, String>>();

		// Add all story items to hashmap array
		for (RssItem item : Lists.RssItems) {
			// If the article is a picture slideshow, dont add it to the list
			if (!item.getTitle().toUpperCase().contains("PICTURES:")) {
				Lists.storyList.add(createStory("story",
						HtmlEscape.unescapeHtml(item.getTitle())));
			}
		}

		SimpleAdapter simpleAdpt = new SimpleAdapter(this, Lists.storyList,
				android.R.layout.simple_list_item_1, new String[] { "story" },
				new int[] { android.R.id.text1 });

		lv.setAdapter(simpleAdpt);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Global.APP_CONTEXT = getApplicationContext();

				Article article;
				try {
					article = new Article(Lists.RssItems.get(position)
							.getLink(), Lists.RssItems.get(position)
							.getDescription(), Lists.RssItems.get(position)
							.getPubDate());

					Intent i = new Intent(MainActivity.this,
							ArticleActivity.class);
					Bundle b = new Bundle();
					b.putString("TITLE", article.getTitle());
					b.putString("BODY", article.getBody());
					b.putString("DATE", article.getPublishedDate());

					i.putExtras(b);
					startActivity(i);
				} catch (IOException e) {
					Dialogs.DisplayInfoAlert("Failed to get article",
							"Failed to get article from source\r\nTechnical: "
									+ e.getMessage(), MainActivity.this);
				}
			}
		});

		// lv.setOnItemLongClickListener(new
		// AdapterView.OnItemLongClickListener() {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// Global.APP_CONTEXT = getApplicationContext();
		//
		// Dialogs.DisplayInfoAlert(
		// "Article Summary",
		// Text.processArticlePreview(Lists.RssItems.get(position)
		// .getDescription())
		// + "\r\n"
		// + String.format(
		// getString(R.string.published),
		// Text.processPubDate(Lists.RssItems.get(
		// position).getPubDate())),
		// MainActivity.this);
		// return true;
		// }
		// });
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private HashMap<String, String> createStory(String key, String title) {
		HashMap<String, String> story = new HashMap<String, String>();
		story.put(key, title);

		return story;
	}
}