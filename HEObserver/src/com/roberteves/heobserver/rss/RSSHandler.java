package com.roberteves.heobserver.rss;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.xml.sax.SAXException;

import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.Global;

import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;

public class RSSHandler {
	public static ArrayList<RssItem> GetFeedItems() {
		URL url = null;
		try {
			url = new URL(Global.APP_CONTEXT.getString(R.string.URL));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RssFeed feed = null;
		try {
			feed = RssReader.read(url);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return feed.getRssItems();
	}
}
