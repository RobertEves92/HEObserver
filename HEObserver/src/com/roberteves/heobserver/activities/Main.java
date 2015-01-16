package com.roberteves.heobserver.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.matshofman.saxrssreader.RssItem;

import com.roberteves.heobserver.Global;
import com.roberteves.heobserver.R;
import com.roberteves.heobserver.rss.RSSHandler;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Main extends Activity {
private static ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Global.APP_CONTEXT = getApplicationContext();
		lv = (ListView) findViewById(R.id.listView);

		// TODO Removed and setup async feed methods
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		//Stores all Rss Items from news feed
		ArrayList<RssItem> RssItems = RSSHandler.GetFeedItems();
		List<Map<String,String>> storyList = new ArrayList<Map<String,String>>();
		
		//Add all story items to hashmap array
		for(RssItem item : RssItems)
		{
			storyList.add(createStory("story",item.getTitle()));
		}
		
	    SimpleAdapter simpleAdpt = new SimpleAdapter(this, storyList, android.R.layout.simple_list_item_1, new String[] {"story"}, new int[] {android.R.id.text1});

	    lv.setAdapter(simpleAdpt);
	}
	
	private HashMap<String,String> createStory(String key,String title){
		HashMap<String,String> story = new HashMap<String,String>();
		story.put(key,title);
		
		return story;
	}
}