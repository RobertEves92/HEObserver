package com.roberteves.heobserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.Util;

public class WebActivity extends Activity {
    private static WebView webView;
    private static Activity activity;
    private String dataString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.LogMessage("WebActivity", "Activity Started");
        activity = this;
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_web);

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                activity.setProgress(progress * 1000);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Util.LogMessage("WebViewClient", description);
            }
        });

        if (Util.isNetworkAvailable(this)) {
            WebViewTask webViewTask = new WebViewTask();
            webViewTask.execute();
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
        Util.LogMessage("WebActivity", "Activity Ended");
    }

    private void loadWebView() {
        webView.loadUrl(dataString);
    }

    private String formatDataString(String dataString) {
        return dataString.replaceAll("/story.html#.*", "/story.html");
    }

    private class WebViewTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            Util.LogMessage("WebViewAsync", "Execute");
            return Util.isInternetAvailable();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Util.LogMessage("WebViewAsync", "Post Execute");
            if (result) {
                Intent intent = getIntent();
                dataString = formatDataString(intent.getStringExtra("link"));
                loadWebView();
            } else {
                Util.DisplayToast(WebActivity.this, getString(R.string.error_no_internet));
                activity.finish();
            }
        }
    }
}
