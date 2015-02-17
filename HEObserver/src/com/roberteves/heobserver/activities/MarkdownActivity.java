package com.roberteves.heobserver.activities;

import android.app.Activity;
import android.os.Bundle;

import us.feras.mdv.MarkdownView;

public class MarkdownActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MarkdownView markdownView = new MarkdownView(this);
        setContentView(markdownView);
        setTitle(getIntent().getStringExtra("title"));
        markdownView.loadMarkdownFile(getIntent().getStringExtra("url"));
        //markdownView.loadMarkdownFile("https://raw.githubusercontent.com/RobertEves92/HEObserver/master/README.md");
    }
}
