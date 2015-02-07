package com.roberteves.heobserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.Article;
import com.roberteves.heobserver.core.Util;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;

public class WebActivity extends Activity {
    private static WebView webView;
    private boolean finishOnResume = false;
    private String dataString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setupThreadPolicy();
        Fabric.with(this, new Crashlytics());

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
                    finishOnResume = true;
                    startActivity(i);
                }
            } catch (IOException e) {
                Crashlytics.logException(e); // Send caught exception to
                // crashlytics
                Toast.makeText(getApplicationContext(),
                        R.string.error_retrieve_article_source,
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
            finish(); // close when resumed
        }
    }

    private void loadWebView(Intent intent) {
        Toast.makeText(getApplicationContext(),
                "Article not supported, opening in web view",
                Toast.LENGTH_SHORT).show();
        // TODO prompt to enable javascript - warn about ads
        // webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(dataString);
    }

    private String formatDataString(String dataString) {
        return dataString.replaceAll("\\/story.html#.*", "/story.html");
    }
}
