package com.roberteves.heobserver.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.unbescape.html.HtmlEscape;

import nl.matshofman.saxrssreader.RssItem;

import com.roberteves.heobserver.Global;
import com.roberteves.heobserver.Dialogs;
import com.roberteves.heobserver.Lists;
import com.roberteves.heobserver.R;
import com.roberteves.heobserver.rss.RSSHandler;

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

//TODO 1.1 convert to activity and add pull to refresh
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
		// TODO 1.0 Add please wait dialog
		// TODO 1.2 Removed and setup async feed methods
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Stores all Rss Items from news feed
		Lists.RssItems = RSSHandler.GetFeedItems();
		Lists.storyList = new ArrayList<Map<String, String>>();

		// Add all story items to hashmap array
		for (RssItem item : Lists.RssItems) {
			// TODO 2.0 Add support for slideshows
			// If the article is a picture slideshow, dont add it to the list
			if (!item.getTitle().toUpperCase().contains("PICTURES:")) {
				Lists.storyList.add(createStory("story",
						formatTitle(item.getTitle())));
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
				// TextView clickedView = (TextView) view;
				//
				// Toast.makeText(
				// Main.this,
				// "Clicked Item with id [" + id + "] - Position ["
				// + position + "] - Planet ["
				// + clickedView.getText() + "]",
				// Toast.LENGTH_SHORT).show();

				// TODO 1.0 Open Article
			}
		});

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TextView clickedView = (TextView) view;
				//
				// Toast.makeText(
				// Main.this,
				// "Long Clicked Item with id [" + id + "] - Position ["
				// + position + "] - Planet ["
				// + clickedView.getText() + "]",
				// Toast.LENGTH_SHORT).show();

				// TODO 1.0 Open published date/description
				Global.APP_CONTEXT = getApplicationContext();

				// TODO 1.0 Un-capitalise first description word
				Dialogs.DisplayInfoAlert(
						"Article Info",
						"Published: "
								+ Lists.RssItems.get(position).getPubDate()
								+ "\r\nPreview:\r\n"
								+ HtmlEscape.unescapeHtml(Lists.RssItems.get(
										position).getDescription()), Main.this);
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

	private String formatTitle(String title) {
		return HtmlEscape.unescapeHtml(title);
	}
}