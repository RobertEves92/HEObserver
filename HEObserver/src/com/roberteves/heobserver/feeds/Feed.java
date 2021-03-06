package com.roberteves.heobserver.feeds;

public class Feed {
    private final String link;
    private final Category category;

    public Feed(String link, Category category) {
        this.link = link;
        this.category = category;
    }

    public static Category getCategoryFromString(String category) {
        switch (category) {
            default:
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
