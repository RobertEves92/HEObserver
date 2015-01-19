package com.roberteves.heobserver;

import java.util.Date;

import org.unbescape.html.HtmlEscape;

public class Text {
	public static String unescapeHtml(String title) {
		return HtmlEscape.unescapeHtml(title);
	}
	
	public static String processArticle(String text)
	{
		// TODO 1.0 Un-capitalise first description word
		
		String t = text;
		t = unescapeHtml(t);
		t=t.replaceAll("</?\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)/?>", ""); //remove any remaining html tags
		return t;
	}

	public static String processPubDate(Date pubDate) {
		String p = pubDate.toString();
		p=p.replaceAll("((:00)( GMT\\+)([0-9]+)(\\:)([0-9]+)( )([0-9]+))", ""); //remove seconds, time zone and year
		return p;
	}
}
