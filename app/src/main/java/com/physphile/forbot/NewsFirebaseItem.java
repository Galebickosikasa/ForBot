package com.physphile.forbot;

import android.net.Uri;

import java.io.Serializable;

public class NewsFirebaseItem implements Serializable {
    String title;
    String uri;
    NewsFirebaseItem(){

    }
    NewsFirebaseItem(String _title, String _uri){
        this.uri = _uri;
        this.title = _title;
    }

    public String getTitle() {
        return title;
    }

    public String getUri() {
        return uri;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
