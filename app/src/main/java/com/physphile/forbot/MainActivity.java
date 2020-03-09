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
        toolbar = findViewById(R.id.Toolbar);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        feedFragment = FeedFragment.newInstance();
        calendarFragment = CalendarFragment.newInstance();


        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);

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
    Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.profile:
                    DialogFragment profileDialog = new ProfileDialogFragment();
                    profileDialog.show(getSupportFragmentManager(), "profileDialog");
                    break;
            }
            return false;
        }
    };

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
    public void saveAvatar(Bitmap bitmap) {
        try {
            Toast.makeText(getBaseContext(), "аватар сохранен", Toast.LENGTH_SHORT).show();
            FileOutputStream out = openFileOutput("avatar", MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // ставить 85 бесполезно, PNG - это формат сжатия без потерь
            out.close();

        } catch (Exception ignored) {
            Toast.makeText(getBaseContext(), "ошибка сохраненя", Toast.LENGTH_SHORT).show();
        }
    }
    public Bitmap readAvatar() throws FileNotFoundException {
        FileInputStream is = openFileInput("avatar");
        return BitmapFactory.decodeStream(is);
    }
}

