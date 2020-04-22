package com.physphile.forbot.Feed;

public class NewsFirebaseItem {
    public String title;
    public String uri;
    public String text;
    public String author;
    public String date;
    public long number;

    public NewsFirebaseItem() {
    }

    public NewsFirebaseItem(String _title, String _uri, String _text, String _author, String _date, long _number) {
        this.uri = _uri;
        this.title = _title;
        this.text = _text;
        this.author = _author;
        this.date = _date;
        this.number = _number;
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

    public long getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
