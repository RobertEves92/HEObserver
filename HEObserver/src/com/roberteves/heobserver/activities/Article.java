package com.roberteves.heobserver.activities;

import com.roberteves.heobserver.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Article extends Activity {
	TextView txtTitle, txtBody, txtPubDate;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);

		txtTitle = (TextView) findViewById(R.id.txtTitle);
		txtBody = (TextView) findViewById(R.id.txtBody);
		txtPubDate = (TextView) findViewById(R.id.txtPubDate);

		Bundle b = getIntent().getExtras();
		txtTitle.setText(b.getString("TITLE"));
		txtBody.setText(b.getString("BODY"));
		txtPubDate.setText("Published: " + b.getString("DATE"));
	}

}
