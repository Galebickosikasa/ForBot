package com.physphile.forbot;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import static com.physphile.forbot.Constants.ARTEM_ADMIN_UID;
import static com.physphile.forbot.Constants.DATABASE_NEWS_PATH;
import static com.physphile.forbot.Constants.GLEB_ADMIN_ID;
import static com.physphile.forbot.Constants.PAVEL_ST_ADMIN_ID;

public class NewsPage extends BaseSwipeActivity {
    private FirebaseUser user;
    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.editNews:
                    break;
                case R.id.removeNews:
//                    SharedPreferences sp = getSharedPreferences("newsNums", Context.MODE_PRIVATE);
//                    String s = sp.getString("news#" + getIntent().getIntExtra("newsNumber", -1), "kek");
                    FirebaseDatabase.getInstance().getReference(DATABASE_NEWS_PATH + getIntent().getExtras().getLong("newsNumber")).removeValue();
                    NewsPage.super.finish();
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_news_page);
        Toolbar toolbar = findViewById(R.id.newsToolbar);
        TextView newsText = findViewById(R.id.newsText);
        ImageView newsTitleImage = findViewById(R.id.newsTitleImage);
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = width * 10 / 16;
        AppBarLayout appBarLayout = findViewById(R.id.main_appbar);
        appBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(width, height));
        Intent intent = getIntent();
        TextView newsTitle = findViewById(R.id.newsTitle);
        TextView newsDate = findViewById(R.id.newsDate);
        TextView newsAuthor = findViewById(R.id.newsAuthor);
        if (user != null) {
            if (user.getUid().equals(ARTEM_ADMIN_UID) || user.getUid().equals(GLEB_ADMIN_ID) || user.getUid().equals(PAVEL_ST_ADMIN_ID)) {
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.admin_news_page_menu);
//                toolbar.getMenu().getItem(1).setIcon(R.drawable.common_google_signin_btn_icon_dark); lol
            }
        }
        newsTitle.setText(intent.getStringExtra("newsTitle"));
        newsAuthor.setText(intent.getStringExtra("newsAuthor"));
        newsDate.setText(intent.getStringExtra("newsDate"));
        newsText.setText(intent.getStringExtra("newsText"));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        Glide.with(this).load(intent.getStringExtra("newsTitleImageUri")).into(newsTitleImage);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_news_page;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}
