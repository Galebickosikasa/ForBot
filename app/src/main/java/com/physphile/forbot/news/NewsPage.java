package com.physphile.forbot.news;

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
import com.google.firebase.storage.FirebaseStorage;
import com.physphile.forbot.BaseSwipeActivity;
import com.physphile.forbot.R;

import static com.physphile.forbot.Constants.ARTEM_ADMIN_UID;
import static com.physphile.forbot.Constants.DATABASE_NEWS_PATH;
import static com.physphile.forbot.Constants.GLEB_ADMIN_ID;
import static com.physphile.forbot.Constants.PAVEL_ST_ADMIN_ID;
import static com.physphile.forbot.Constants.STORAGE_NEWS_IMAGE_PATH;

public class NewsPage extends BaseSwipeActivity {
    private FirebaseUser user;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private Intent intent;
    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.editNews:
                    break;
                case R.id.removeNews:
                    String num = intent.getStringExtra("newsNumber");
                    database.getReference(DATABASE_NEWS_PATH + num).removeValue();
                    storage.getReference(STORAGE_NEWS_IMAGE_PATH + num).delete();
                    NewsPage.super.finish();
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //инициализация активити
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_page);

        //инициализация переменных
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        intent = getIntent();

        //инициализация View-элементов
        Toolbar toolbar = findViewById(R.id.newsToolbar);
        TextView newsText = findViewById(R.id.newsText);
        TextView newsTitle = findViewById(R.id.newsTitle);
        TextView newsDate = findViewById(R.id.newsDate);
        TextView newsAuthor = findViewById(R.id.newsAuthor);
        ImageView newsTitleImage = findViewById(R.id.newsTitleImage);
        AppBarLayout appBarLayout = findViewById(R.id.main_appbar);

        //заполнение View-элементов
        int width = getWindowManager().getDefaultDisplay().getWidth();
        appBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(width,
                width * 10 / 16));
        if (user != null && (user.getUid().equals(ARTEM_ADMIN_UID) || user.getUid().equals(GLEB_ADMIN_ID) || user.getUid().equals(PAVEL_ST_ADMIN_ID))) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.admin_news_page_menu);
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
