package com.roberteves.heobserver.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.Article;
import com.roberteves.heobserver.core.Util;

import java.net.SocketTimeoutException;

public class ArticleActivity extends Activity {
    private static Article article;
    private static MenuItem comments;
    private static String link;
    private static Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_article);

        if (getIntent().getSerializableExtra("article") != null) {
            article = (Article) getIntent().getSerializableExtra("article");
        } else if (link == null) {
            link = getIntent().getStringExtra("link");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        link = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.article_activity_menu, menu);
        comments = menu.findItem(R.id.action_bar_comment);
        if (article != null) {
            DisplayArticle();
        } else {
            new DownloadArticleTask().execute(link);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_share:
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");

                // Add data to the intent, the receiving app will decide
                // what to do with it.
                share.putExtra(Intent.EXTRA_SUBJECT, article.getTitle());
                share.putExtra(Intent.EXTRA_TEXT, article.getLink());

                startActivity(Intent.createChooser(share,
                        getString(R.string.action_share_via)));
                return true;
            case R.id.action_bar_comment:
                Intent i = new Intent(ArticleActivity.this,
                        CommentActivity.class);

                i.putExtra("comments", article.getComments());
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void DisplayArticle() {
        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        TextView txtBody = (TextView) findViewById(R.id.txtBody);
        TextView txtPubDate = (TextView) findViewById(R.id.txtPubDate);

        txtTitle.setText(article != null ? article.getTitle() : null);
        txtBody.setText(Html.fromHtml(article.getBody()));

        if (article.getPublishedDate() != null) {
            txtPubDate.setText(getString(R.string.published) + article.getPublishedDate());
        } else {
            txtPubDate.setText("");
        }

        article.processComments();

        comments.setVisible(article.hasComments());
    }

    private class DownloadArticleTask extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(ArticleActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getString(R.string.dialog_fetching_article));
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                article = new Article(link);
                return true;
            } catch (Exception e) {
                if (!(e instanceof SocketTimeoutException)) { //Don't log if timeout exception
                    Util.LogException("load article", link, e);
                } else {
                    Util.LogMessage(Log.INFO, "SocketTimeout", "Article: " + link);
                }
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (result) {
                DisplayArticle();
            } else {
                Handler handler = new Handler(getApplicationContext().getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Failed to load article",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                activity.finish();
            }
        }
    }
}
