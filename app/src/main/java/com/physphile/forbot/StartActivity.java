package com.physphile.forbot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Query;
import com.google.firebase.database.annotations.NotNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class StartActivity extends AppCompatActivity {


    //SharedPreferences это штука, которая сохраняет значения нужных переменных в файлике
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChooseActivity();
    }

    protected void ChooseActivity() {
        boolean isClick;
        sp = getSharedPreferences("isClick", Context.MODE_PRIVATE);

        /*
        создаём атрибут isClick, Context.MODE_PRIVATE значит,
        что доступ к этому файлику есть только у этого приложения
        */

        isClick = sp.getBoolean("isClick", false); // устанавливаем значение этого атрибута - false

        if (isClick) {

            setContentView(R.layout.activity_main);
            BottomNavigationView navView = findViewById(R.id.nav_view);
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                    .build();
            NavController navController = Navigation.findNavController (this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController (this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController (navView, navController);

        }
        else {
            setContentView(R.layout.activity_start);
            ImageButton StartContinueBtn = (ImageButton) findViewById(R.id.StartContinueBtn);
            SeekBar ChooseFoamBar = (SeekBar)findViewById(R.id.ChooseFoamBar);

            StartContinueBtn.setOnClickListener (OnStartContinueBtnListener);
            ChooseFoamBar.setOnSeekBarChangeListener (ChooseFoamBarListener);
        }
    }

    //просто листенер для кнопки
    View.OnClickListener OnStartContinueBtnListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            startActivity(new Intent("com.physphile.forbot.MainActivity"));
            SharedPreferences.Editor e = sp.edit(); // чтобы менять этот файлик надо создать эдитор
            e.putBoolean("isClick", true); // устанавливаем значение этого атрибута - true
            e.apply(); // закрываем эдитор
        }

    };

    //листенер для сикбара
    SeekBar.OnSeekBarChangeListener ChooseFoamBarListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            TextView ChooseFoamTxt = findViewById(R.id.ChooseFoamTxt);
            String s = progress + 5 + " класс";
            ChooseFoamTxt.setText(s);
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };



}
