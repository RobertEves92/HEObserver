package com.roberteves.heobserver.activities;

import android.app.Activity;
import android.os.Bundle;

import com.roberteves.heobserver.core.Util;

import us.feras.mdv.MarkdownView;

public class MarkdownActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.LogMessage("MarkdownActivity", "Activity Ended");
        MarkdownView markdownView = new MarkdownView(this);
        setContentView(markdownView);
        setTitle(getIntent().getStringExtra("title"));
        markdownView.loadMarkdown(getIntent().getStringExtra("text"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Util.LogMessage("MarkdownActivity", "Activity Ended");
    }
}
