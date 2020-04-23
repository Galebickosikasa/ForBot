package com.physphile.forbot.Feed;

public class NewsFirebaseItem {
    public String title;
    public String uri;
    public String text;
    public String author;
    public String date;
    public Integer number;

    public NewsFirebaseItem() {
    }

    public NewsFirebaseItem(String title, String uri, String text, String author, String date, Integer number) {
        this.title = title;
        this.uri = uri;
        this.text = text;
        this.author = author;
        this.date = date;
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
