package com.roberteves.heobserver.core;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.roberteves.heobserver.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class Util {
    private static final int timeout = 5000;

    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            LogMessage("Network", "Connected to " + activeNetwork.getTypeName() + ": " + activeNetwork.getExtraInfo() + " " + activeNetwork.getSubtypeName());
            return true;
        } else {
            LogMessage("Network", "Not Connected");
            return false;
        }
    }

    public static Boolean isInternetAvailable() {
        try {
            URL url = new URL("http://www.hertsandessexobserver.co.uk/");
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setRequestProperty("User-Agent", "test");
            urlc.setRequestProperty("Connection", "close");

            urlc.setConnectTimeout(timeout); //timeout if cant connect within 1s
            urlc.setReadTimeout(timeout); //timeout if cant read within 1s

            urlc.connect();
            if (urlc.getResponseCode() == 200) {
                LogMessage("Internet", "Connected");
                return true;
            } else {
                LogMessage("Internet", "Not Connected (" + urlc.getResponseCode() + ")");
                return false;
            }
        } catch (Exception e) {
            LogException("Internet", "Check Internet Access Error", e);
            return false;
        }
    }

    public static String getWebSource(String Url) throws IOException {
        // Build and set timeout values for the request.
        URLConnection connection = (new URL(Url)).openConnection();
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        connection.connect();

        // Read and store the result line by line then return the entire string.
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder html = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) {
            html.append(line);
        }
        in.close();

        return html.toString();
    }

    public static void LogException(String action, String data, Exception e) {
        if (BuildConfig.DEBUG) {
            //only log in logcat if in debug
            Log.w("Fabric", "Caught Exception: Action: " + action + "; Data: " + data + "; Exception: " + e.toString());
        }

        if (e instanceof SocketTimeoutException || e instanceof UnknownHostException || e.getMessage().contains("junk after document element")) {
            LogMessage("Exception", e.toString());
        } else {
            Crashlytics.setString("action", action);
            Crashlytics.setString("data", data);
            Crashlytics.logException(e);
        }
    }

    public static void LogMessage(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message); //only log in logcat if in debug
        }

        Crashlytics.log("[" + tag + "] " + message);
    }

    public static void DisplayToast(final Activity activity, final String message) {
        new Thread() {
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }
}