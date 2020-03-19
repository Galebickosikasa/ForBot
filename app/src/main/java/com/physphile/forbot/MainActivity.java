package com.physphile.forbot;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.physphile.forbot.Calendar.CalendarFragment;
import com.physphile.forbot.Feed.FeedFragment;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import static com.physphile.forbot.Constants.LOG_NAME;

public class MainActivity extends BaseSwipeActivity {
    FeedFragment feedFragment;
    CalendarFragment calendarFragment;

//    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        feedFragment = FeedFragment.newInstance();
        calendarFragment = CalendarFragment.newInstance();
        replaceFragment(feedFragment);
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
//        updateTheme();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    //    @RequiresApi(api = Build.VERSION_CODES.M)
//    public void updateTheme(){
//        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
//            case Configuration.UI_MODE_NIGHT_YES:
//
//                getWindow().setNavigationBarColor(getResources().getColor(R.color.gray));
//
//                break;
//            case Configuration.UI_MODE_NIGHT_NO:
//
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
////                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
//                break;
//        }
//    }
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
            out.close();
            Log.e(LOG_NAME, "файл сохранен");
        } catch (Exception ignored) {
            Log.e(LOG_NAME, "файл не сохранен");
        }
    }

    public Bitmap readFile(String name) throws FileNotFoundException {
        FileInputStream is = openFileInput(name);
        Log.e(LOG_NAME, "файл прочитан");
        return BitmapFactory.decodeStream(is);
    }

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}

