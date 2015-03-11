package com.roberteves.heobserver.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.Article;
import com.roberteves.heobserver.core.Util;

public class ArticleActivity extends Activity {
Article article;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        new DownloadArticleTask().execute(getIntent().getStringExtra("link"));
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.article_activity_menu, menu);
        menu.findItem(R.id.action_bar_comment).setVisible(article.hasComments());
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
                        getString(R.string.share_via)));
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
    }*/

    private class DownloadArticleTask extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(ArticleActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Fetching Article");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String url = getIntent().getStringExtra("link");
            try {
                article = new Article(url);
                return true;
            }
            catch (Exception e)
            {
                Util.LogException("load article", url, e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(result)
            {
                TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
                TextView txtBody = (TextView) findViewById(R.id.txtBody);
                TextView txtPubDate = (TextView) findViewById(R.id.txtPubDate);

                //Error handling for return from comments removed - may bug with new async??

                txtTitle.setText(article != null ? article.getTitle() : null);
                txtBody.setText(Html.fromHtml(article.getBody()));

                if (article.getPublishedDate() != null) {
                    txtPubDate.setText(getString(R.string.published) + article.getPublishedDate());
                } else {
                    txtPubDate.setText("");
                }

                article.processComments();
            }
            else
            {
                //TODO display error toast and go back to main activity
            }
        }
    }
}
