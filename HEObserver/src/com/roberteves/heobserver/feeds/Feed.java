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
            case "news":
            default:
                return Category.News;
            case "localnews":
                return Category.LocalNews;
            case "sport":
                return Category.Sport;
            case "lifestyle":
                return Category.Lifestyle;
            case "retail":
                return Category.Retail;
            case "weather":
                return Category.Weather;
            case "family":
                return Category.Family;
            case "misc":
                return Category.Misc;
        }
    }

    public String getLink() {
        return link;
    }

    public Category getCategory() {
        return category;
    }

}
