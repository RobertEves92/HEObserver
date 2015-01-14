package com.roberteves.heobserver.rss;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.xml.sax.SAXException;

import android.util.Log;

import com.roberteves.heobserver.Global;
import com.roberteves.heobserver.R;

import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;

public class RSSHandler {
	public static ArrayList<RssItem> GetFeedItems() {
		URL url = null;
		try {
			url = new URL(Global.APP_CONTEXT.getString(R.string.URL));
		} catch (MalformedURLException e) {
			Log.e("RSS-URL", e.getMessage());
		}
		RssFeed feed = null;
		try {
			feed = RssReader.read(url);
		} catch (SAXException e) {
			Log.e("RSS-SAX", e.getMessage());
		} catch (IOException e) {
			Log.e("RSS-IO", e.getMessage());
		}

		return feed.getRssItems();
	}
}
