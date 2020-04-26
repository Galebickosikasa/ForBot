package com.physphile.forbot;

import android.app.Application;

import com.example.swipebacklib.SlideFinishManager;


public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SlideFinishManager.getInstance().init(this);
    }
}
