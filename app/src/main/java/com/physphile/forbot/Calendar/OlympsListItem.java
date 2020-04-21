package com.physphile.forbot.Calendar;

class OlympsListItem {
    public String name;
    public String level;
    public String uri;
    public String date;
    public String text;

    public OlympsListItem() {
    }

    public OlympsListItem(String name, String level, String uri, String date, String text) {
        this.name = name;
        this.level = level;
        this.uri = uri;
        this.date = date;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

}