package com.roberteves.heobserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.crashlytics.android.Crashlytics;
import com.roberteves.heobserver.BuildConfig;
import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.Article;
import com.roberteves.heobserver.core.Util;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;

public class WebActivity extends Activity {
    private static WebView webView;
    private String dataString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics.Builder().disabled(BuildConfig.DEBUG).build());
        Util.LogMessage("WebActivity","Activity Started");
        Util.setupThreadPolicy();

        setContentView(R.layout.activity_web);
        webView = (WebView) findViewById(R.id.webView);
        if (Util.isInternetAvailable(getApplicationContext())) {
            Intent intent = getIntent();
            dataString = formatDataString(intent.getDataString());

            if (dataString
                    .matches("http://((www.)?)hertsandessexobserver.co.uk/.*story.html") && !dataString.toUpperCase().contains("UNDEFINED-HEADLINE")) {
                // is article - open in article activity
                try {
                    Article article = new Article(dataString);
                    if (!article.isReadable()) // load in web view
                    {
                        Util.DisplayToast(getApplicationContext(), getString(R.string.error_not_supported));
                        loadWebView();
                    } else { // load in article activity
                        Intent i = new Intent(WebActivity.this,
                                ArticleActivity.class);

                        i.putExtra("article", article);
                        startActivity(i);
                    }
                } catch (IOException e) {
                    Util.LogException("load article from link", dataString, e);
                    Util.DisplayToast(getApplicationContext(), getString(R.string.error_retrieve_article_source));
                    loadWebView();
                }
            } else {
                Util.DisplayToast(getApplicationContext(), getString(R.string.error_not_supported));
                loadWebView();
            }
        } else {
            Util.DisplayToast(getApplicationContext(), getString(R.string.error_no_internet));
            this.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        finish(); // close when resumed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Util.LogMessage("WebActivity","Activity Ended");
    }

    private void loadWebView() {
        webView.loadUrl(dataString);
    }

    private String formatDataString(String dataString) {
        return dataString.replaceAll("/story.html#.*", "/story.html");
    }
}
