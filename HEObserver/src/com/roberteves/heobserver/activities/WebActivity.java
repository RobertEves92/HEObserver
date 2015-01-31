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
import android.webkit.WebView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class WebActivity extends Activity {
	private static WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		Util.setupThreadPolicy();
		Intent intent = getIntent();

		if (intent
				.getDataString()
				.matches(
						"http:\\/\\/((www.)?)hertsandessexobserver.co.uk\\/.*story.html")) {
			// is article - open in article activity
			try {
				Article article = new Article(intent.getDataString());
				// TODO Filter photo/video articles (Implement Article.getType)
				Intent i = new Intent(WebActivity.this, ArticleActivity.class);

				i.putExtra("article", article);
				startActivity(i);
			} catch (IOException e) {
				Crashlytics.logException(e); // Send caught exception to
												// crashlytics
				Toast.makeText(getApplicationContext(),
						R.string.error_retreive_article_source,
						Toast.LENGTH_SHORT).show();
			}
		} else {
			// is not article - open in web view
			setContentView(R.layout.activity_web);

			webView = (WebView) findViewById(R.id.webView);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.loadUrl(intent.getDataString());
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		finish(); // close when resumed
	}
}
