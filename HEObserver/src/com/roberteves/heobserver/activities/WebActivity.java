package com.roberteves.heobserver.activities;

import com.roberteves.heobserver.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

@SuppressLint("SetJavaScriptEnabled")
public class WebActivity extends Activity {
	private static WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();

		if (intent
				.getDataString()
				.matches(
						"http:\\/\\/((www.)?)hertsandessexobserver.co.uk\\/.*story.html")) {
			// is article - open in article activity
			// TODO Open in app
		} else {
			// is not article - open in web view
			setContentView(R.layout.activity_web);

			webView = (WebView) findViewById(R.id.webView);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.loadUrl(intent.getDataString());
		}
	}
}
