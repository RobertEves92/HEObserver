package com.roberteves.heobserver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.unbescape.html.HtmlEscape;

public class Text {
	private static String regexArticle = "<!-- Article Start -->([\\s\\S]*?)<!-- Article End -->";
	private static String regexHtml = "</?\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)/?>";

	public static String unescapeHtml(String title) {
		return HtmlEscape.unescapeHtml(title);
	}

	public static String processArticlePreview(String text) {
		String t = text;
		t = unescapeHtml(t);
		t = t.replaceAll(regexHtml, ""); // remove any remaining html tags
		return t;
	}

	public static String processArticle(String text) {
		String t = selectStringFromRegex(text, regexArticle);
		return t;
	}

	public static String processPubDate(Date pubDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(pubDate);
		return sdf.format(calendar.getTime());
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
}
