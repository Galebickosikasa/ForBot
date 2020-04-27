package com.physphile.forbot;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.physphile.forbot.news.FeedFragment;
import com.physphile.forbot.olympiads.CalendarFragment;

public class MainActivity extends BaseSwipeActivity {
    private FeedFragment feedFragment;
    private SparseArray savedStateSparseArray;
    private CalendarFragment calendarFragment;
    private BottomBarAdapter bottomBarAdapter;
    private ViewPager viewPager;

    //    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        SmoothBottomBar navView = findViewById(R.id.nav_view);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
//        if (user == null || !user.isEmailVerified()) {
//            startActivity(new Intent(this, AuthActivity.class));
//            finish();
//        }
        viewPager = findViewById(R.id.nav_host_fragment);
        feedFragment = FeedFragment.newInstance();
        calendarFragment = CalendarFragment.newInstance();
//        replaceFragment(feedFragment);
//        navView.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public void onItemSelect(int i) {
//                switch (i) {
//                    case 0:
////                        replaceFragment(feedFragment);
//                        break;
//                    case 1:
////                        replaceFragment(calendarFragment);
//                        break;
//                }
//            }
//        });
//        updateTheme();
        bottomBarAdapter = new BottomBarAdapter(getSupportFragmentManager());
        bottomBarAdapter.addFragments(feedFragment);
        bottomBarAdapter.addFragments(calendarFragment);
        viewPager.setAdapter(bottomBarAdapter);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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

    private void replaceFragment(Fragment f) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nav_host_fragment, f);
        ft.commit();
    }

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    }
}

