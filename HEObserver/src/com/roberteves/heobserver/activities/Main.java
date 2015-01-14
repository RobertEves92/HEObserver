package com.roberteves.heobserver.activities;

import nl.matshofman.saxrssreader.RssItem;

import com.roberteves.heobserver.Global;
import com.roberteves.heobserver.R;
import com.roberteves.heobserver.rss.RSSHandler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Main extends Activity {
	TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Global.APP_CONTEXT = getApplicationContext();

		tv = (TextView) findViewById(R.id.textView1);
		try {
			String s = "";

			for (RssItem item : RSSHandler.GetFeedItems()) {
				s = s + item.getTitle() + "\r\n";
			}

			tv.setText(s);
		} catch (Exception e) {
			Log.e("MAIN", e.getMessage());
		}
	}
}
