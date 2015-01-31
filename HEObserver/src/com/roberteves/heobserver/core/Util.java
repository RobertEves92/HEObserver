package com.roberteves.heobserver.core;

import android.os.StrictMode;

public class Util {
	public static void setupThreadPolicy() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}
}
