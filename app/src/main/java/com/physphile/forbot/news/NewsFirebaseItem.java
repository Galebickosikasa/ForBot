package com.physphile.forbot.news;

public class NewsFirebaseItem {
    private String title;
    private String uri;
    private String text;
    private String author;
    private String date;
    private Integer number;
    private Integer mask;

    public NewsFirebaseItem() {
    }

    public NewsFirebaseItem(String title, String uri, String text, String author, String date, Integer number, Integer mask) {
        this.title = title;
        this.uri = uri;
        this.text = text;
        this.author = author;
        this.date = date;
        this.number = number;
        this.mask = mask;
    }

    public Integer getMask() {
        return mask;
    }

    public void setMask(Integer mask) {
        this.mask = mask;
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
