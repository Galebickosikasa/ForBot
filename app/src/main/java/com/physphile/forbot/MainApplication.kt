package com.physphile.forbot

import android.app.Application
import com.example.swipebacklib.SlideFinishManager

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SlideFinishManager.getInstance().init(this)
    }
}