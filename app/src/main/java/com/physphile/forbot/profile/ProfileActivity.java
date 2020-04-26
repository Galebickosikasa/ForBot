package com.physphile.forbot.profile;

import android.os.Bundle;
import android.widget.CompoundButton;

import com.physphile.forbot.BaseSwipeActivity;
import com.physphile.forbot.R;

public class ProfileActivity extends BaseSwipeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}
