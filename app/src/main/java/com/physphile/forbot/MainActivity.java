package com.physphile.forbot;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity {

    FeedFragment feedFragment;
    Toolbar toolbar;
    CalendarFragment calendarFragment;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        feedFragment = FeedFragment.newInstance();
        calendarFragment = CalendarFragment.newInstance();
        replaceFragment(feedFragment);

        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);


        updateTheme();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateTheme(){
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:

                getWindow().setNavigationBarColor(getResources().getColor(R.color.gray));

                break;
            case Configuration.UI_MODE_NIGHT_NO:

                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                break;
        }
    }
    private void replaceFragment(Fragment f){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nav_host_fragment, f);
        ft.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_feed:
                    replaceFragment(feedFragment);
                    item.setChecked(true);
                    break;
                case R.id.nav_cal:
                    replaceFragment(calendarFragment);
                    item.setChecked(true);
                    break;
            }
            return false;
        }
    };
    public void saveFile(Bitmap bitmap, String name) {
        try {
            FileOutputStream out = openFileOutput(name, MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Log.e("ARTEM", "файл сохранен");
            out.close();
        } catch (Exception ignored) {
            Log.e("ARTEM", "файл не сохранен");
        }
    }
    public Bitmap readFile(String name) throws FileNotFoundException {
        FileInputStream is = openFileInput(name);
        Log.e("ARTEM", "файл прочитан");
        return BitmapFactory.decodeStream(is);
    }
}

