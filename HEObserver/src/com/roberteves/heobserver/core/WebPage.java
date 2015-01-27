package com.roberteves.heobserver.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WebPage {
	public static String getWebSource(String Url) throws IOException {
		URL url = new URL(Url);
		URLConnection urlConnection = url.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				urlConnection.getInputStream(), "UTF-8"));
		String inputLine;
		StringBuilder sb = new StringBuilder();
		while ((inputLine = br.readLine()) != null)
			sb.append(inputLine);
		br.close();

		return sb.toString();
	}
}
