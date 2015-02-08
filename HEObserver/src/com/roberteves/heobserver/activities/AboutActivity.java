package com.roberteves.heobserver.activities;

import android.app.Activity;
import android.os.Bundle;

import us.feras.mdv.MarkdownView;

public class AboutActivity extends Activity {
    private static MarkdownView markdownView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markdownView = new MarkdownView(this);
        setContentView(markdownView);
        markdownView.loadMarkdownFile("https://raw.githubusercontent.com/RobertEves92/HEObserver/master/README.md");
    }
}
