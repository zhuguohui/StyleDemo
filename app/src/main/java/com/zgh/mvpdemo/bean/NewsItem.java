package com.zgh.mvpdemo.bean;

/**
 * Created by yuelin on 2016/6/29.
 */
public class NewsItem {
    String title;
    String url;

    public NewsItem(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return title;
    }
}
