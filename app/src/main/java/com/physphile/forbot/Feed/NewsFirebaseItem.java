package com.physphile.forbot.Feed;

public class NewsFirebaseItem {
    public String title;
    public String uri;
    public String text;
    public String author;
    public String date;

    public NewsFirebaseItem(){}

    public NewsFirebaseItem(String _title, String _uri, String _text, String _author, String _date){
        this.uri = _uri;
        this.title = _title;
        this.text = _text;
        this.author = _author;
        this.date = _date;
    }

    public String getTitle() { return title; }
    public String getUri() { return uri; }
    public String getAuthor() { return author; }
    public String getDate() { return date; }
    public String getText() { return text; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setDate(String date) { this.date = date; }
    public void setText(String text) { this.text = text; }
    public void setUri(String uri) { this.uri = uri; }
}
