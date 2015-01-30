package com.roberteves.heobserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class WebActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();

		if (intent
				.getDataString()
				.matches(
						"http:\\/\\/((www.)?)hertsandessexobserver.co.uk\\/.*story.html")) {
			// is article - open
			// TODO Open in app
		} else {
			// is not article - close app
			//TODO Open in browser
		}
	}
}
