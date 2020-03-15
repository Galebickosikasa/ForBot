package com.physphile.forbot.Feed;

import android.graphics.Bitmap;

class NewsFeedItem {
    private String Title;
    private Bitmap NewsTitleImage;

    NewsFeedItem(){}

    NewsFeedItem(String _title, Bitmap _image){
        this.Title = _title;
        this.NewsTitleImage = _image;
    }

    Bitmap getNewsTitleImage() { return NewsTitleImage; }

    String getTitle() { return Title; }

    void setNewsTitleImage(Bitmap newsTitleImage) { this.NewsTitleImage = newsTitleImage; }

    void setTitle(String title) { this.Title = title; }
}
