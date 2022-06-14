package com.haritha.harithaagriofficer;

public class News {

    private String news_id;
    private String news_heading;
    private String news_content;
    private String news_published_date;
    private String news_publish_to;
    private String news_published_by;

    public News() {}

    public News(String news_id, String news_heading, String news_content, String news_published_date, String news_publish_to, String news_published_by) {
        this.news_id = news_id;
        this.news_heading = news_heading;
        this.news_content = news_content;
        this.news_published_date = news_published_date;
        this.news_publish_to = news_publish_to;
        this.news_published_by = news_published_by;
    }

    public String getNews_id() {
        return news_id;
    }

    public String getNews_heading() {
        return news_heading;
    }

    public String getNews_content() {
        return news_content;
    }

    public String getNews_published_date() {
        return news_published_date;
    }

    public String getNews_publish_to() {
        return news_publish_to;
    }

    public String getNews_published_by() {
        return news_published_by;
    }
}
