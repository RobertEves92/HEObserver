package com.roberteves.heobserver.feeds;

public class Feed {
    private String link;
    private Category category;

    public Feed(String link, Category category) {
        this.link = link;
        this.category = category;
    }

    public static Category getCategoryFromString(String category) {
        switch (category) {
            case "localnews":
                return Category.LocalNews;
            case "sport":
                return Category.Sport;
            case "music":
                return Category.Music;
            case "lifestyle":
                return Category.Lifestyle;
            case "weather":
                return Category.Weather;
            case "news":
            default:
                return Category.News;
        }
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
