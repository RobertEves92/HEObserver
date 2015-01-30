package com.roberteves.heobserver.activities;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.unbescape.html.HtmlEscape;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;

import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.Article;
import com.roberteves.heobserver.core.Lists;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_main);
		lv = (ListView) findViewById(R.id.listView);

		updateList();
	}

	private void updateList() {
		if (isOnline()) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

			// Stores all Rss Items from news feed
			try {
				Lists.RssItems = getFeeds();
				Lists.storyList = new ArrayList<Map<String, String>>();

				// Add all story items to hashmap array
				for (RssItem item : Lists.RssItems) {
					// If the article is a picture slideshow, dont add it to the
					// list
					if (!item.getTitle().toUpperCase().contains("PICTURES:")) {
						Lists.storyList.add(createStory("story",
								HtmlEscape.unescapeHtml(item.getTitle())));
					}
				}

				SimpleAdapter simpleAdpt = new SimpleAdapter(this,
						Lists.storyList, android.R.layout.simple_list_item_1,
						new String[] { "story" },
						new int[] { android.R.id.text1 });

				lv.setAdapter(simpleAdpt);

				lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						Article article;
						try {
							article = new Article(Lists.RssItems.get(position)
									.getLink(), Lists.RssItems.get(position)
									.getDescription(), Lists.RssItems.get(
									position).getPubDate());

							Intent i = new Intent(MainActivity.this,
									ArticleActivity.class);

							i.putExtra("article", article);
							startActivity(i);
						} catch (IOException e) {
							Crashlytics.logException(e); // Send caught
															// exception to
															// crashlytics
							Toast.makeText(getApplicationContext(),
									R.string.error_retreive_article_source,
									Toast.LENGTH_SHORT).show();
						}
					}
				});

				// lv.setOnItemLongClickListener(new
				// AdapterView.OnItemLongClickListener() {
				//
				// @Override
				// public boolean onItemLongClick(AdapterView<?> parent, View
				// view,
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
			} catch (Exception e) {
				Crashlytics.logException(e); // Send caught exception to
												// crashlytics
				Toast.makeText(getApplicationContext(),
						R.string.error_update_article_list, Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			Toast.makeText(getApplicationContext(), R.string.error_no_internet,
					Toast.LENGTH_SHORT).show();
		}
	}

	private ArrayList<RssItem> getFeeds() throws SAXException, IOException,
			MalformedURLException, XmlPullParserException {
		ArrayList<String> feeds = new ArrayList<String>();
		ArrayList<RssItem> rssItems = new ArrayList<RssItem>();
		ArrayList<RssItem> feedItems = new ArrayList<RssItem>();

		BufferedReader in = new BufferedReader(new InputStreamReader(this
				.getResources().openRawResource(R.raw.feeds)));
		String line;
		while ((line = in.readLine()) != null) {
			feeds.add(line);
		}

		for (String s : feeds) {
			feedItems = RssReader.read(new URL(s)).getRssItems();
			checkDuplicates(rssItems, feedItems);
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

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnected();
	}
}