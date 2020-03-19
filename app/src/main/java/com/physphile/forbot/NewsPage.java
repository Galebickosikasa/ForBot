package com.physphile.forbot;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class NewsPage extends BaseSwipeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_page);
        Toolbar toolbar = findViewById(R.id.newsToolbar);
        TextView newsText = findViewById(R.id.newsText);
        ImageView newsTitleImage = findViewById(R.id.newsTitleImage);
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = width * 10 /16;
        AppBarLayout appBarLayout = findViewById(R.id.main_appbar);
        appBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(width, height));
        Intent intent = getIntent();
        TextView newsTitle = findViewById(R.id.newsTitle);
        TextView newsDate = findViewById(R.id.newsDate);
        TextView newsAuthor = findViewById(R.id.newsAuthor);
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
