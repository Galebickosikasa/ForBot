package com.physphile.forbot.Feed;

class NewsFirebaseItem {
    public String title;
    public String uri;

    NewsFirebaseItem(){}

    NewsFirebaseItem(String _title, String _uri){
        this.uri = _uri;
        this.title = _title;
    }

    String getTitle() { return title; }

    String getUri() { return uri; }

    void setTitle(String title) { this.title = title; }

    void setUri(String uri) { this.uri = uri; }
}
