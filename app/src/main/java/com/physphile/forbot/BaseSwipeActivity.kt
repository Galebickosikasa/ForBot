package com.physphile.forbot

import android.os.Bundle
import android.widget.CompoundButton
import com.example.swipebacklib.BaseSwipeBackActivity
import com.example.swipebacklib.SwipeBackLayout

abstract class BaseSwipeActivity : BaseSwipeBackActivity(), CompoundButton.OnCheckedChangeListener {
    var isMain = false
    protected var mSwipeBackLayout: SwipeBackLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        mSwipeBackLayout = swipeBackLayout
        if (isMain) {
            return
        }
    }

    protected abstract val layoutId: Int
}