package com.physphile.forbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StartActivity extends AppCompatActivity {


    //SharedPreferences это штука, которая сохраняет значения нужных переменных в файлике
    SharedPreferences sp;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChooseActivity();
    }


    //метод, который запускает нужные активити
    protected void ChooseActivity() {
        boolean isClick;
        sp = getSharedPreferences("isClick", Context.MODE_PRIVATE); //создаём атрибут isClick, Context.MODE_PRIVATE значит,
        // что доступ к этому файлику есть только у этого приложения
        isClick = sp.getBoolean("isClick", false);//устанавливаем значение этого атрибута - false

        if (isClick) {
            setContentView(R.layout.activity_main);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            BottomNavigationView navView = findViewById(R.id.nav_view);
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
        } else {
            setContentView(R.layout.activity_start);
            ImageButton StartContinueBtn = (ImageButton) findViewById(R.id.StartContinueBtn);
            SeekBar ChooseFoamBar = (SeekBar)findViewById(R.id.ChooseFoamBar);

            StartContinueBtn.setOnClickListener(OnStartContinueBtnListener);
            ChooseFoamBar.setOnSeekBarChangeListener(ChooseFoamBarListener);

        }
    }

    //просто листенер для кнопки
    View.OnClickListener OnStartContinueBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent("com.physphile.forbot.MainActivity"));
            SharedPreferences.Editor e = sp.edit();//чтобы менять этот файлик надо создать эдитор
            e.putBoolean("isClick", true);//устанавливаем значение этого атрибута - true
            e.apply();//закрываем эдитор
        }
    };

    //листенер для сикбара
    SeekBar.OnSeekBarChangeListener ChooseFoamBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            TextView ChooseFoamTxt = (TextView) findViewById(R.id.ChooseFoamTxt);
            ChooseFoamTxt.setText(String.valueOf(progress + 5) + " класс");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
