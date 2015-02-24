package com.roberteves.heobserver.core;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("serial")
public class Article implements Serializable {
    private static final String[] mediaTags = new String[]{"PHOTOS:", "PHOTO:",
            "VIDEO:", "VIDEOS:", "PICTURES:", "POLL:", " - SLIDESHOW"};
    private static final String regexArticleBody = "<p>.*</p>";
    private static final String regexArticleRelated = "<div.*?</div>";
    private static final String regexXmlComment = "<!--.*?-->";
    private static final String regexExcessWhitespace = "\\s+";
    private static final String regexArticle = "<!-- Article Start -->([\\s\\S]*?)<!-- Article End -->";
    private static final String regexTitle = "<title>\\s*.*?<\\/title>";
    private static final String regexTitleStart = "<title>\\s+";
    private static final String regexTitleEnd = "\\s\\|.*";
    private static final String regexDate = "\\d{4}\\-\\d{2}\\-\\d{2}";
    private static final String regexTime = "\\d{2}\\:\\d{2}\\:\\d{2}";
    private String title, body, publishedDate, link, source;
    private ArrayList<Comment> comments;

    public Article(String link, Date published)
            throws IOException {
        source = Util.getWebSource(link, false);
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

        // Set Date
        setPublishedDate(processPubDate(published));

        // Set Link
        setLink(link);
    }

    public Article(String link) throws IOException {
        source = Util.getWebSource(link, false);
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

        String date = selectStringFromRegex(source, regexDate).substring(0, 10);
        String time = selectStringFromRegex(source, regexTime).substring(0, 8);

        setPublishedDate(processPubDate(date + time));

        // Set Link
        setLink(link);
    }
    
    public void getComments()
    {
        List<String> authors,comments;
        authors = selectStringListFromRegex(source,"<span class=\"author\">.*<\\/span>");
        comments = selectStringListFromRegex(source,"<div class=\"comment-text\">.*\\s*.*\\s*<\\/div>");
        
        authors.remove(0);
        
        this.comments = new ArrayList<>();
        
        for(int i = 0;i<authors.size();i++)
        {
            this.comments.add(new Comment(authors.get(i),comments.get(i)));
        }
        
        for(Comment c : this.comments)
        {
            c.setAuthor(c.getAuthor().replaceAll("<span class=\"author\"><a class=\"\" target=\"\" h.*?\\>",""));
            c.setAuthor(c.getAuthor().replaceAll("</a></span>",""));
            c.setContent(c.getContent().replaceAll("<div class=\"comment-text\">\n\t\t\t<p class=\"discussion-thread-comments-quotation\">",""));
            c.setContent(c.getContent().replaceAll("</p>\n\t\t\t</div>",""));
        }
    }

    private static String selectStringFromRegex(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        List<String> listMatches = new ArrayList<>();
        while (matcher.find()) {
            listMatches.add(matcher.group());
        }
        String t = "";
        for (String s : listMatches) {
            t = t + s;
        }
        return t;
    }
    
    private static List<String> selectStringListFromRegex(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        List<String> listMatches = new ArrayList<>();
        while (matcher.find()) {
            listMatches.add(matcher.group());
        }
        
        return listMatches;
    }
    
    public static String processPubDate(Date pubDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(pubDate);
        return sdf.format(calendar.getTime());
    }

    private static String processPubDate(String date) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
            Date d = df.parse(date);
            return processPubDate(d);
        } catch (Exception e) {
            Crashlytics.logException(e);
            return "";
        }
    }

    public static boolean hasMedia(String title) {
        for (String s : mediaTags) {
            if (title.toUpperCase().contains(s.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasMedia() {
        return hasMedia(getTitle());
    }

    public String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    void setBody(String body) {
        this.body = body;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getLink() {
        return link;
    }

    void setLink(String link) {
        this.link = link;
    }

    public Boolean hasComments() {
        return comments.size() > 0;
    }
}
