package com.roberteves.heobserver.activities;

import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.Article;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class ArticleActivity extends Activity {
	private static Article article;
	TextView txtTitle, txtBody, txtPubDate;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);

		article = (Article) getIntent().getSerializableExtra("article");

		txtTitle = (TextView) findViewById(R.id.txtTitle);
		txtBody = (TextView) findViewById(R.id.txtBody);
		txtPubDate = (TextView) findViewById(R.id.txtPubDate);

		txtTitle.setText(article.getTitle());
		txtBody.setText(Html.fromHtml(article.getBody()));
		txtPubDate.setText(String.format(getString(R.string.published),
				article.getPublishedDate()));
	}

}
