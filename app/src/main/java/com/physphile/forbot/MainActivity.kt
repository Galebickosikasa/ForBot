package com.physphile.forbot

import android.os.Bundle
import android.util.SparseArray
import android.view.WindowManager
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.physphile.forbot.news.FeedFragment
import com.physphile.forbot.news.FeedFragment.Companion.newInstance
import com.physphile.forbot.news.NewsAdapter.OnNewsClick
import com.physphile.forbot.olympiads.CalendarFragment
import com.physphile.forbot.olympiads.OlympsAdapter.OnOlympsClick

class MainActivity : BaseSwipeActivity(), OnNewsClick, OnOlympsClick {
    private lateinit var feedFragment: FeedFragment
    private val savedStateSparseArray: SparseArray<*>? = null
    private lateinit var calendarFragment: CalendarFragment
    private lateinit var bottomBarAdapter: BottomBarAdapter
    private lateinit var viewPager: ViewPager

    //    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //        SmoothBottomBar navView = findViewById(R.id.nav_view);
        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        //        if (user == null || !user.isEmailVerified()) {
//            startActivity(new Intent(this, AuthActivity.class));
//            finish();
//        }
        viewPager = findViewById(R.id.nav_host_fragment)
        feedFragment = newInstance()
        calendarFragment = CalendarFragment.newInstance()
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
        bottomBarAdapter = BottomBarAdapter(supportFragmentManager)
        bottomBarAdapter.addFragments(feedFragment)
        bottomBarAdapter.addFragments(calendarFragment)
        viewPager.adapter = bottomBarAdapter
        val w = window
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override val layoutId: Int
        protected get() = R.layout.activity_main

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
    private fun replaceFragment(f: Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.nav_host_fragment, f)
        ft.commit()
    }

    override fun onStart() {
        super.onStart()
        val parser = Parser(this)
        parser.addToFirebase()
    }

    override fun isSupportSwipeBack(): Boolean {
        return false
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {}
    override fun onNewsClick(position: Int) {
        feedFragment.onNewsClick(position)
    }

    override fun onOlympsClick(position: Int) {
        calendarFragment.onOlympsClick(position)
    }
}