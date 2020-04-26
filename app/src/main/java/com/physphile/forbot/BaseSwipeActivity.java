package com.physphile.forbot;

import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;

import com.example.swipebacklib.BaseSwipeBackActivity;
import com.example.swipebacklib.SwipeBackLayout;

public abstract class BaseSwipeActivity extends BaseSwipeBackActivity implements CompoundButton.OnCheckedChangeListener {
    public boolean isMain = false;
    protected SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mSwipeBackLayout = getSwipeBackLayout();
        if (isMain) {
            return;
        }
    }

    protected abstract int getLayoutId();
}

