package com.roberteves.heobserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.Util;

public class WebActivity extends Activity {
    private static WebView webView;
    private String dataString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.LogMessage("WebActivity", "Activity Started");
        Util.enableNetworkOnMainThread();

        setContentView(R.layout.activity_web);
        webView = (WebView) findViewById(R.id.webView);
        if (Util.isNetworkAvailable(this)) {
            if (Util.isInternetAvailable()) {
                Intent intent = getIntent();
                dataString = formatDataString(intent.getStringExtra("link"));
                loadWebView();
            } else {
                Util.DisplayToast(this, getString(R.string.error_no_internet));
                this.finish();
            }
        } else {
            Util.DisplayToast(this, getString(R.string.error_no_internet));
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
