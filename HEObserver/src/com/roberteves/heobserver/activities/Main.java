package com.roberteves.heobserver.activities;

import com.roberteves.heobserver.Global;
import com.roberteves.heobserver.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ListView;

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
	}
}