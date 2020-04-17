package com.physphile.forbot;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

public class AccountSettingsActivity extends AppCompatActivity {
    IndicatorSeekBar ChooseFoamSeek;
    CheckBox physicsCheck, mathCheck;
    OnSeekChangeListener ChooseFoamSeekListener = new OnSeekChangeListener() {
        @Override
        public void onSeeking(SeekParams seekParams) {
            TextView ChooseFoamTxt = findViewById(R.id.ChooseFoamTxt);
            String s = ChooseFoamSeek.getProgress() + " класс";
            ChooseFoamTxt.setText(s);
        }

        @Override
        public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            SharedPreferences sp = getSharedPreferences("foam", Context.MODE_PRIVATE);
            SharedPreferences.Editor e = sp.edit();
            e.putInt("foam", ChooseFoamSeek.getProgress());
            e.apply();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        updateTheme();
        setTitle("Настройки аккаунта");
        setContentView(R.layout.activity_account_settings);
        ChooseFoamSeek = findViewById(R.id.ChooseFoamSeek);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        ChooseFoamSeek.setOnSeekChangeListener(ChooseFoamSeekListener);
        physicsCheck = findViewById(R.id.physicsCheck);
        mathCheck = findViewById(R.id.mathCheck);
        physicsCheck.setOnCheckedChangeListener(OnCheckBoxClick("physics"));
        mathCheck.setOnCheckedChangeListener(OnCheckBoxClick("math"));

        if (getSharedPreferences("physics", Context.MODE_PRIVATE).getBoolean("physics", false)) {
            physicsCheck.setChecked(true);
        }
        if (getSharedPreferences("math", Context.MODE_PRIVATE).getBoolean("math", false)) {
            mathCheck.setChecked(true);
        }
        int foam = getSharedPreferences("foam", Context.MODE_PRIVATE).getInt("foam", -1);

        if (foam != -1) {
            ChooseFoamSeek.setProgress(foam);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private CheckBox.OnCheckedChangeListener OnCheckBoxClick(final String name) {
        return new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sp = getSharedPreferences(name, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = sp.edit();
                if (isChecked) {
                    e.putBoolean(name, true);
                } else {
                    e.putBoolean(name, false);
                }
                e.apply();
            }
        };
    }

//    public void updateTheme() {
//        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
//            case Configuration.UI_MODE_NIGHT_YES:
//
//                break;
//            case Configuration.UI_MODE_NIGHT_NO:
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//                break;
//        }
//    }
}
