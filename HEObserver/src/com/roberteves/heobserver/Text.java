package com.roberteves.heobserver;

import org.unbescape.html.HtmlEscape;

public class Text {
	public static String unescapeHtml(String title) {
		return HtmlEscape.unescapeHtml(title);
	}
	
	public static String processArticle(String text)
	{
		String t = text;
		t = unescapeHtml(t);
		t=t.replaceAll("</?\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)/?>", ""); //remove any remaining html tags
		return t;
	}
}
