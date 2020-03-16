package com.physphile.forbot.Feed;

public class NewsFirebaseItem {
    public String title;
    public String uri;

    public NewsFirebaseItem(){}

    public NewsFirebaseItem(String _title, String _uri){
        this.uri = _uri;
        this.title = _title;
    }

    public String getTitle() { return title; }

    public String getUri() { return uri; }

    public void setTitle(String title) { this.title = title; }

    public void setUri(String uri) { this.uri = uri; }
}
