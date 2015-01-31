package com.roberteves.heobserver.activities;

import io.fabric.sdk.android.Fabric;

import java.io.IOException;

import com.crashlytics.android.Crashlytics;
import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.Article;
import com.roberteves.heobserver.core.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class WebActivity extends Activity {
	private static WebView webView;
	private boolean finishOnResume = false;
	private String dataString;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		Util.setupThreadPolicy();

		setContentView(R.layout.activity_web);
		webView = (WebView) findViewById(R.id.webView);

		Intent intent = getIntent();
		dataString = formatDataString(intent.getDataString());

		if (dataString
				.matches("http:\\/\\/((www.)?)hertsandessexobserver.co.uk\\/.*story.html")) {
			// is article - open in article activity
			try {
				Article article = new Article(dataString);
				if (article.hasMedia()) // load in web view
				{
					loadWebView(intent);
				} else { // load in article activity
					Intent i = new Intent(WebActivity.this,
							ArticleActivity.class);

					i.putExtra("article", article);
					Log.i("WebActivity", "Loading Article Activity from "
							+ dataString);
					finishOnResume = true;
					startActivity(i);
				}
			} catch (IOException e) {
				Crashlytics.logException(e); // Send caught exception to
												// crashlytics
				Toast.makeText(getApplicationContext(),
						R.string.error_retreive_article_source,
						Toast.LENGTH_SHORT).show();
			}
		} else {
			loadWebView(intent);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (finishOnResume) {
			Log.i("WebActivity", "Finishing Activity - onResume");
			finish(); // close when resumed
		}
	}

	private void loadWebView(Intent intent) {
		Log.i("WebActivity", "Loading Web Activity from " + dataString);
		Toast.makeText(getApplicationContext(),
				"Article not supported, opening in web view",
				Toast.LENGTH_SHORT).show();
		// webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(dataString);
	}

	private String formatDataString(String dataString) {
		return dataString.replaceAll("\\/story.html#.*", "/story.html");
	}
}
