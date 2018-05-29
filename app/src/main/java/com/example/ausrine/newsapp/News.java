package com.example.ausrine.newsapp;

public class News {

    // Title of the News
    private String newsTitle;

    // Section of the News
    private String newsSection;

    // Date when the News was published
    private String newsDate;

    // URL of the news
    private String newsUrl;

    public News(String title, String section, String date, String url) {
        newsTitle = title;
        newsSection = section;
        newsDate = date;
        newsUrl = url;
    }

    /**
     * Returns the title of the news.
     */
    public String getTitle() {
        return newsTitle;
    }

    /**
     * Returns the section of the news.
     */
    public String getSection() {
        return newsSection;
    }

    /**
     * Returns the date of the news.
     */
    public String getDate() {
        return newsDate;
    }

    /**
     * Returns the website URL to find more information about the news.
     */
    public String getUrl() {
        return newsUrl;
    }
}