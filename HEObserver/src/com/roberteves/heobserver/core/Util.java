package com.roberteves.heobserver.core;

import android.os.StrictMode;

public class Util {
    public static void setupThreadPolicy() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitNetwork().build();
        StrictMode.setThreadPolicy(policy);
    }
}