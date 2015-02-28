package com.roberteves.heobserver.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class CommentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_list);
        ListView lv = (ListView) findViewById(R.id.listView);

        ArrayList<Comment> comments = (ArrayList<Comment>) getIntent().getSerializableExtra("comments");
        List<Map<String, String>> commentList;

        //creat map list
        commentList = new ArrayList<>();
        for (Comment c : comments) {
            HashMap<String, String> comment = new HashMap<>();
            comment.put("comment", c.getContent());
            comment.put("author", c.getAuthor());
            commentList.add(comment);
        }

        //Create ListView Adapter
        SimpleAdapter simpleAdpt = new SimpleAdapter(this,
                commentList, android.R.layout.simple_list_item_2,
                new String[]{"author", "comment"},
                new int[]{android.R.id.text1, android.R.id.text2});

        //Set ListView from Adapter
        lv.setAdapter(simpleAdpt);
    }
}
