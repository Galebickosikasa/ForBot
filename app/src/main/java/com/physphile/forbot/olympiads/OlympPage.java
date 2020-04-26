package com.physphile.forbot.olympiads;

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
import com.physphile.forbot.BaseSwipeActivity;
import com.physphile.forbot.R;

import static com.physphile.forbot.Constants.ARTEM_ADMIN_UID;
import static com.physphile.forbot.Constants.GLEB_ADMIN_ID;
import static com.physphile.forbot.Constants.PAVEL_ST_ADMIN_ID;

public class OlympPage extends BaseSwipeActivity {
    private FirebaseUser user;
    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.editOlymp:
                    break;
                case R.id.removeOlymp:
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olymp_page);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Toolbar toolbar = findViewById(R.id.olympToolbar);
        TextView olympText = findViewById(R.id.olympText);
        ImageView olympTitleImage = findViewById(R.id.olympTitleImage);
        TextView olympName = findViewById(R.id.olympName);
        TextView olympDate = findViewById(R.id.olympDate);
        TextView olympLevel = findViewById(R.id.olympLevel);
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = width * 10 / 16;
        AppBarLayout appBarLayout = findViewById(R.id.olympAppbar);
        appBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(width, height));
        Intent intent = getIntent();
        if (user != null) {
            if (user.getUid().equals(ARTEM_ADMIN_UID) || user.getUid().equals(GLEB_ADMIN_ID) || user.getUid().equals(PAVEL_ST_ADMIN_ID)) {
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.admin_olymp_page_menu);
//                toolbar.getMenu().getItem(1).setIcon(R.drawable.common_google_signin_btn_icon_dark); lol
            }
        }
        olympName.setText(intent.getStringExtra("olympName"));
        olympDate.setText(intent.getStringExtra("olympDate"));
        olympText.setText(intent.getStringExtra("olympText"));
        olympLevel.setText(intent.getStringExtra("olympLevel"));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        Glide.with(this).load(intent.getStringExtra("olympUri")).into(olympTitleImage);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_olymp_page;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}
