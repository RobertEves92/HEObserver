package com.roberteves.heobserver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.unbescape.html.HtmlEscape;

public class Text {
	private static String regexArticle = "(<!-- Article Start -->)([\\w\\d\\s\\W\\D\\S]+)(<!-- Article End -->)";
	private static String regexHtml = "</?\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)/?>";
	
	public static String unescapeHtml(String title) {
		return HtmlEscape.unescapeHtml(title);
	}
	
	public static String processArticlePreview(String text)
	{
		// TODO 1.0 Un-capitalise first description word
		
		String t = text;
		t = unescapeHtml(t);
		t=t.replaceAll(regexHtml, ""); //remove any remaining html tags
		return t;
	}

	public static String processPubDate(Date pubDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");	
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(pubDate);
		return sdf.format(calendar.getTime());
	}
}
