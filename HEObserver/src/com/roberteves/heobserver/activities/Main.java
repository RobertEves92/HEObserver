package com.roberteves.heobserver.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.matshofman.saxrssreader.RssItem;

import com.roberteves.heobserver.Global;
import com.roberteves.heobserver.Dialogs;
import com.roberteves.heobserver.Lists;
import com.roberteves.heobserver.Text;
import com.roberteves.heobserver.R;
import com.roberteves.heobserver.WebPage;
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

public class Main extends ActionBarActivity {
	private static ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
						Text.unescapeHtml(item.getTitle())));
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

				try {
					String body = Text.processArticle(WebPage
							.getWebSource(Lists.RssItems.get(position)
									.getLink()));
					String title = Lists.RssItems.get(position).getTitle();
					String date = Text.processPubDate(Lists.RssItems.get(
							position).getPubDate());

					Intent i = new Intent(Main.this, Article.class);
					Bundle b = new Bundle();
					b.putString("TITLE", title);
					b.putString("BODY", body);
					b.putString("DATE", date);
					i.putExtras(b);
					startActivity(i);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Global.APP_CONTEXT = getApplicationContext();

				Dialogs.DisplayInfoAlert(
						"Article Summary",
						Text.processArticlePreview(Lists.RssItems.get(position)
								.getDescription())
								+ "\r\n(Published: "
								+ Text.processPubDate(Lists.RssItems.get(
										position).getPubDate()) + ")",
						Main.this);
				return true;
			}
		});
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