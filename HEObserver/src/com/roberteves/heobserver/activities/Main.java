package com.roberteves.heobserver.activities;

import nl.matshofman.saxrssreader.RssItem;

import com.roberteves.heobserver.Global;
import com.roberteves.heobserver.R;
import com.roberteves.heobserver.rss.RSSHandler;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;

public class Main extends Activity {
	TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Global.APP_CONTEXT = getApplicationContext();
		
		//TODO Removed and setup async feed methods
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 

		tv = (TextView) findViewById(R.id.textView1);
		
		String s = "";

		for (RssItem item : RSSHandler.GetFeedItems()) {
			s = s + item.getTitle() + "\r\n";
		}

		tv.setText(s);
	}
}