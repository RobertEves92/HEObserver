package com.roberteves.heobserver.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.unbescape.html.HtmlEscape;

public class Article {
	private String title, body, description, publishedDate;

	private static String regexArticleBody = "<p>.*</p>";
	private static String regexArticleRelated = "<div.*?<\\/div>";
	private static String regexXmlComment = "<!--.*?-->";
	private static String regexExcessWhitespace = "\\s+";
	private static String regexArticle = "<!-- Article Start -->([\\s\\S]*?)<!-- Article End -->";
	private static String regexHtml = "</?\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)/?>";
	private static String regexTitle = "<title>.*?<\\/title>";
	private static String regexTitleStart = "<title>\\s+";
	private static String regexTitleEnd = "\\s\\|.*";

	public Article(String link, String summary, Date published) throws IOException {
		String source = getWebSource(link);
		// Set Title
		String t = selectStringFromRegex(source, regexTitle);
		t = t.replaceAll(regexTitleStart, "");
		t = t.replaceAll(regexTitleEnd, "");
		setTitle(t);

		// Set Body
		String b = selectStringFromRegex(source, regexArticle);
		b = selectStringFromRegex(b, regexArticleBody);
		b = b.replaceAll(regexArticleRelated, "");
		b = b.replaceAll(regexXmlComment, "");
		b = b.replaceAll(regexExcessWhitespace, " ");
		setBody(b);

		// Set Summary/Description
		setDescription(processArticlePreview(summary));

		// Set Date
		setPublishedDate(processPubDate(published));
	}

	private static String selectStringFromRegex(String text, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		List<String> listMatches = new ArrayList<String>();
		while (matcher.find()) {
			listMatches.add(matcher.group());
		}
		String t = "";
		for (String s : listMatches) {
			t = t + s;
		}
		return t;
	}

	private static String processPubDate(Date pubDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(pubDate);
		return sdf.format(calendar.getTime());
	}

	private static String processArticlePreview(String text) {
		String t = text;
		t = HtmlEscape.unescapeHtml(t);
		t = t.replaceAll(regexHtml, ""); // remove any remaining html tags
		return t;
	}
	
	private static String getWebSource(String Url) throws IOException {
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

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
