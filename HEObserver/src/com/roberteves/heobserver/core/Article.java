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
    private static final String regexArticleBody = "(<p>|<ul>)(.*)(</p>|</ul>)";
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
    private static final String regexBulletList = "<ul>|</ul>";
    private static final String regexBulletStart = "<li>";
    private static final String regexBulletEnd = "</li>";
    private static final String regexImage = "<img([\\w\\W]+?)\\/>";
    private static final String regexScript = "";
    private static final String regexStyle = "(<style)([\\s\\S]*?)<\\/style>";
    private static final String regexBody = "<body([\\s\\S]*?)<\\/body>";
    private static final String regexStartOfBody = "<body([\\s\\S]*?)<div class=\"single-image image-wrap\">";
    private String title, body, publishedDate, link, source, image, imageText;

    private ArrayList<Comment> comments;

    public Article(String link) throws IOException {

        source = Util.getWebSource(link.replace("m.", ""));

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
        b = b.replaceAll(regexBulletList, "");
        b = b.replaceAll(regexBulletStart, "- ");
        b = b.replaceAll(regexBulletEnd, "<br />");
        b = b.replaceAll(regexScript, "");
        b = b.replaceAll(regexStyle, "");
        setBody(b);

        //remove any images in the article body
        if (!selectStringFromRegex(b, regexImage).contentEquals("")) {
            b = b.replaceAll(regexImage, "");
            setBody(b);
        }

        //find the main image for the article if it has one
        image = selectStringFromRegex(source, regexBody);
        image = image.replaceAll(regexStartOfBody, "");
        image = selectSingleStringFromRegex(image, regexImage);
        image = selectStringFromRegex(image, "src=\"([\\s\\S]*?)\"");
        image = image.replaceAll("src=", "");
        image = image.replaceAll("\"", "");

        imageText = selectStringFromRegex(source, "<p class=\"caption\">\\s*.*?<\\/p>");
        imageText = imageText.replaceAll("<p class=\"caption\">", "");
        imageText = imageText.replaceAll("<\\/p>", "");
        imageText = imageText.replaceAll("\\s+", " ");
        imageText = imageText.trim();

        String date = selectStringFromRegex(source, regexDate);
        String time = selectStringFromRegex(source, regexTime);
        if (date.length() == 0 || time.length() == 0) {
            Util.LogMessage("Article", "Date or time blank");
            setPublishedDate("");
        } else {
            try {
                date = date.substring(0, 10);
                time = time.substring(0, 5);

                try {
                    date = Date.FormatDate(Date.ParseDate(date, "yyyy-MM-dd"), "dd/MM/yyyy");
                    setPublishedDate(date + " " + time);
                } catch (Exception e) {
                    Util.LogException("parse article date", "date: '" + date + "'; time: '" + time + "'", e);
                    setPublishedDate("");
                }
            } catch (Exception ee) {
                Util.LogException("get date/time substring", "date: '" + date + "'; time: '" + time + "'; link: " + link, ee);
                setPublishedDate("");
            }
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


    private static String selectSingleStringFromRegex(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        matcher.find();
        return matcher.group();
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
            String commentsSection = selectStringFromRegex(source, "<section class=\"section section__comments\">.*<\\/section>");
            authors = selectStringListFromRegex(commentsSection, "<span itemprop=\"name\">\\s*.*?<\\/span>");
            comments = selectStringListFromRegex(commentsSection, "<p class=\"discussion-thread-comments-quotation\">\\s*.*?<\\/p>");

            this.comments = new ArrayList<>();

            for (int i = 0; i < authors.size(); i++) {
                this.comments.add(new Comment(authors.get(i), comments.get(i)));
            }

            for (Comment c : this.comments) {
                //Format author name
                c.setAuthor(c.getAuthor().replaceAll("<span itemprop=\"name\">", ""));
                c.setAuthor(c.getAuthor().replaceAll("<\\/span>", ""));
                c.setAuthor(HtmlEscape.unescapeHtml(c.getAuthor()));

                //Format comment body
                c.setContent(c.getContent().replaceAll("<p class=\"discussion-thread-comments-quotation\">", ""));
                c.setContent(c.getContent().replaceAll("<\\/p>", ""));
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

    private void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    private void setBody(String body) {
        this.body = body;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    private void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getLink() {
        return link;
    }

    private void setLink(String link) {
        this.link = link;
    }

    public Boolean hasComments() {
        return comments.size() > 0;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public String getImage() {
        return image;
    }

    public String getImageText() {
        return imageText;
    }
}
