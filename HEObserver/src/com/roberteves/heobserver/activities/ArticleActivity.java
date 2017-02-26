package com.roberteves.heobserver.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.roberteves.heobserver.BuildConfig;
import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.Article;
import com.roberteves.heobserver.core.Util;

import io.fabric.sdk.android.Fabric;

public class ArticleActivity extends Activity {
    private static Article article;
    private static MenuItem comments;
    private static String link;
    private static Activity activity;
    private static Boolean closeOnResume;
    private static ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics.Builder().disabled(BuildConfig.DEBUG).build());
        Util.LogMessage("ArticleActivity", "Activity Started");
        activity = this;
        closeOnResume = false;
        setContentView(R.layout.activity_article);

        if (getIntent().getSerializableExtra("article") != null) {
            article = (Article) getIntent().getSerializableExtra("article");
        } else if (link == null) {
            link = getIntent().getStringExtra("link");
            if (link == null) {
                link = getIntent().getDataString();
            }
        }
    }

    @Override
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
        Util.LogMessage("ArticleActivity", "Activity Ended");
        link = null;
        article = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (closeOnResume) {
            finish(); // close when resumed
        }
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
            if (Util.isNetworkAvailable(this)) {
                new DownloadArticleTask().execute(link);
            } else {
                Util.DisplayToast(this, getString(R.string.error_no_internet));
                activity.finish();
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Util.LogMessage("ArticleActivity", "Option Selected: " + item.getTitle());
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
            case R.id.action_bar_webview:
                Intent intent = new Intent(ArticleActivity.this, WebActivity.class);
                intent.putExtra("link", article.getLink());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void DisplayArticle() {
        Util.LogMessage("ArticleActivity", "Display Article");
        if (articleSupported()) {
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

            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName(article.getTitle())
                    .putContentType("Article")
                    .putContentId("Article"));
        } else {
            Util.DisplayToast(ArticleActivity.this, getString(R.string.error_not_supported));
            openInBrowser();
        }

        if (article.hasImages()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ArticleActivity.this);
            builder.setTitle(getString(R.string.dialog_open_in_browser_title));
            builder.setMessage(getString(R.string.dialog_open_in_browser_message));
            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    openInBrowser();
                }
            });
            builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private boolean articleSupported() {
        return article.isReadable() && (article.getLink().matches("((http://)?)((m.)?)((www.)?)hertsandessexobserver.co.uk/.*story.html") || article.getLink().matches("((http://)?)((m.)?)((www.)?)hertfordshiremercury.co.uk/.*story.html")) && !article.getLink().toUpperCase().contains("UNDEFINED-HEADLINE");
    }

    private void openInBrowser() {
        closeOnResume = true;
        Intent intent = new Intent(ArticleActivity.this, WebActivity.class);
        intent.putExtra("link", article.getLink());
        startActivity(intent);
    }

    private void dismissProgressDialog() {
        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private class DownloadArticleTask extends AsyncTask<String, Void, Boolean> {
        private String toastMessage;

        @Override
        protected void onPreExecute() {

            Util.LogMessage("DownloadArticleAsync", "Pre Execute");
            if (dialog == null) {
                dialog = new ProgressDialog(ArticleActivity.this);
                dialog.setMessage(getString(R.string.dialog_fetching_article));
                dialog.setCancelable(false);
            }
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Util.LogMessage("DownloadArticleAsync", "Execute");
            if (Util.isInternetAvailable()) {
                try {
                    article = new Article(link);

                    return true;
                } catch (Exception e) {
                    Util.LogException("load article", link, e);
                    toastMessage = getString(R.string.error_load_article);
                    return false;
                }
            } else {
                Util.LogMessage("DownloadArticleAsync", "No Internet");
                toastMessage = getString(R.string.error_no_internet);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Util.LogMessage("DownloadArticleAsync", "Post Execute");
            if (result) {
                DisplayArticle();
            } else {
                Util.DisplayToast(ArticleActivity.this, toastMessage);
                activity.finish();
            }

            if (!ArticleActivity.this.isDestroyed())
                dismissProgressDialog();
        }
    }
}
