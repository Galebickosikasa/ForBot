package com.physphile.forbot;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

public class AccountSettingsActivity extends AppCompatActivity {

    IndicatorSeekBar ChooseFoamSeek;
    CheckBox physicsCheck, mathCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Настройки аккаунта");
        setContentView(R.layout.activity_account_settings);
        ChooseFoamSeek = findViewById(R.id.ChooseFoamSeek);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        ChooseFoamSeek.setOnSeekChangeListener(ChooseFoamSeekListener);

        physicsCheck = findViewById(R.id.physicsCheck);
        mathCheck = findViewById(R.id.mathCheck);
        physicsCheck.setOnCheckedChangeListener(OnCheckBoxClick("physics"));
        mathCheck.setOnCheckedChangeListener(OnCheckBoxClick("math"));

        if(getSharedPreferences("physics", Context.MODE_PRIVATE).getBoolean("physics", false)){
            physicsCheck.setChecked(true);
        }
        if(getSharedPreferences("math", Context.MODE_PRIVATE).getBoolean("math", false)){
            mathCheck.setChecked(true);
        }
        ChooseFoamSeek.setProgress(getSharedPreferences("foam", Context.MODE_PRIVATE).getInt("foam", 0));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //листенер для сикбара
    OnSeekChangeListener ChooseFoamSeekListener = new OnSeekChangeListener() {
        @Override
        public void onSeeking(SeekParams seekParams) {
            TextView ChooseFoamTxt = findViewById(R.id.ChooseFoamTxt);
            String s = ChooseFoamSeek.getProgress() + " класс";
            ChooseFoamTxt.setText(s);
        }
        @Override
        public void onStartTrackingTouch(IndicatorSeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            SharedPreferences sp = getSharedPreferences("foam", Context.MODE_PRIVATE);
            SharedPreferences.Editor e = sp.edit();
            e.putInt("foam", ChooseFoamSeek.getProgress());
            e.apply();
        }
    };

    private CheckBox.OnCheckedChangeListener OnCheckBoxClick (final String name){
        final CheckBox.OnCheckedChangeListener onCheckBoxClick = new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sp = getSharedPreferences(name, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = sp.edit();
                if (isChecked){
                    e.putBoolean(name, true);
                } else {
                    e.putBoolean(name, false);
                }
                e.apply();
            }
        };
        return onCheckBoxClick;
    }
}
