package com.roberteves.heobserver.core;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import unbescape.html.HtmlEscape;

@SuppressWarnings("serial")
public class Article implements Serializable {
    private static final String[] mediaTags = new String[]{"PHOTOS:", "PHOTO:",
            "VIDEO:", "VIDEOS:", "PICTURES:", "POLL:", " - SLIDESHOW", "PICTURE GALLERY:", "PHOTO GALLERY:"};
    private static final String[] urlTags = new String[]{"UNDEFINED-HEADLINE", "PICTURES.HTML"};
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
    private static final String regexLinkOpen = "<a.*?>";
    private static final String regexLinkClose = "</a.*?>";
    private String title, body, publishedDate, link, source;
    private ArrayList<Comment> comments;

    public Article(String link) throws IOException {
        source = Util.getWebSource(link);
        // Set Title
        String t = selectStringFromRegex(source, regexTitle);
        t = t.replaceAll(regexTitleStart, "");
        t = t.replaceAll(regexTitleEnd, "");
        setTitle(HtmlEscape.unescapeHtml(t));

        // Set Body
        String b = selectStringFromRegex(source, regexArticle);
        b = selectStringFromRegex(b, regexArticleBody);
        b = b.replaceAll(regexArticleRelated, "");
        b = b.replaceAll(regexXmlComment, "");
        b = b.replaceAll(regexExcessWhitespace, " ");
        setBody(b);


        String date = selectStringFromRegex(source, regexDate);
        String time = selectStringFromRegex(source, regexTime);
        try {
            date = date.substring(0, 10);
            time = time.substring(0, 5);

            try {
                date = Date.FormatDate(Date.ParseDate(date, "yyyy-MM-dd"), "dd/MM/yyyy");
                setPublishedDate(date + " " + time);
            } catch (Exception e) {
                Util.LogException("parse article date", date + " " + time, e);
                setPublishedDate("");
            }
        }catch(Exception ee) {
            Util.LogException("get date/time substring", date + " " + time + " " + link, ee);
            setPublishedDate("");
        }

        // Set Link
        setLink(link);
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

    public static boolean checkTitle(String title) {
        for (String s : mediaTags) {
            if (title.toUpperCase().contains(s.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkLink(String link) {
        for (String s : urlTags) {
            if (link.toUpperCase().contains(s.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public void processComments() {
        try {
            List<String> authors, comments;
            authors = selectStringListFromRegex(source, "<span class=\"author\">.*<\\/span>");
            comments = selectStringListFromRegex(source, "<div class=\"comment-text\">([^]]*?)<\\/div>");

            authors.remove(0);

            this.comments = new ArrayList<>();

            for (int i = 0; i < authors.size(); i++) {
                this.comments.add(new Comment(authors.get(i), comments.get(i)));
            }

            for (Comment c : this.comments) {
                //Format author name
                c.setAuthor(c.getAuthor().replaceAll("<span class=\"author\"><a class=\"\" target=\"\" h.*?>", ""));
                c.setAuthor(c.getAuthor().replaceAll("</a></span>", ""));
                c.setAuthor(HtmlEscape.unescapeHtml(c.getAuthor()));

                //Format comment body
                c.setContent(c.getContent().replaceAll("<div class=\"comment-text\">\n\t\t\t<p class=\"discussion-thread-comments-quotation\">", ""));
                c.setContent(c.getContent().replaceAll("</p>\n\t\t\t</div>", ""));
                c.setContent(HtmlEscape.unescapeHtml(c.getContent()));
                c.setContent(c.getContent().replaceAll(regexLinkOpen, ""));
                c.setContent(c.getContent().replaceAll(regexLinkClose, ""));
            }
        } catch (Exception e) {
            Util.LogException("process comments", getLink(), e);
        }
    }

    public boolean isReadable() {
        //Check for media tags in title
        if (checkTitle(getTitle()))
            return false;

        //Check for body length
        return getBody().length() != 0;

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

    public ArrayList<Comment> getComments() {
        return comments;
    }
}
