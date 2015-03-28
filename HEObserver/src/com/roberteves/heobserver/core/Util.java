package com.roberteves.heobserver.core;

import android.os.StrictMode;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class Util {
    public static void setupThreadPolicy() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitNetwork().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static String getWebSource(String Url) throws IOException {
        int timeout = 5; //timeout in seconds
        HttpClient httpclient = new DefaultHttpClient(); // Create HTTP Client

        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), timeout * 1000); //Connection Timeout
        HttpConnectionParams.setSoTimeout(httpclient.getParams(), timeout * 1000); //Socket Timeout

        HttpGet httpget = new HttpGet(Url); // Set the action you want to do
        HttpResponse response = httpclient.execute(httpget); // Execute it
        HttpEntity entity = response.getEntity();

        InputStream is = entity.getContent();
        Header contentEncoding = response.getFirstHeader("Content-Encoding");

        BufferedReader reader;
        if ((contentEncoding != null) && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
            InputStream gzipIs = new GZIPInputStream(is);
            reader = new BufferedReader(new InputStreamReader(gzipIs), 8);
        } else {
            reader = new BufferedReader(new InputStreamReader(is), 8);
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) // Read line by line
            sb.append(line).append("\n");

        String resString = sb.toString(); // Result is here

        is.close(); // Close the stream

        resString = resString.replaceAll("'", "`"); //prevents some errors in rss feed parsing

        return resString;
    }

    public static void LogException(String action, String data, Exception e) {
        Crashlytics.setString("action", action);
        Crashlytics.setString("data", data);
        Crashlytics.logException(e);
        Crashlytics.log(Log.WARN, "Fabric", "Caught Exception: Action: " + action + "; Data: " + data + "; Exception: " + e.toString());
    }

    public static void LogMessage(int priority, String tag, String message) {
        Crashlytics.log(priority, tag, message);
    }
}