package com.physphile.forbot;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.swipebacklib.BaseSwipeBackActivity;
import com.example.swipebacklib.SwipeBackLayout;

/**
 * Created by GongWen on 17/8/25.
 */

public abstract class BaseSwipeActivity extends BaseSwipeBackActivity implements CompoundButton.OnCheckedChangeListener {
    protected SwipeBackLayout mSwipeBackLayout;

    public boolean isMain = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mSwipeBackLayout = getSwipeBackLayout();
        if(isMain){
            return;
        }
    }

    protected abstract int getLayoutId();
}

