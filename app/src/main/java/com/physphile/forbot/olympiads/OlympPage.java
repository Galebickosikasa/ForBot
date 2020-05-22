package com.physphile.forbot.olympiads;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.physphile.forbot.BaseSwipeActivity;
import com.physphile.forbot.R;

import java.util.HashMap;

import static com.physphile.forbot.Constants.ARTEM_ADMIN_UID;
import static com.physphile.forbot.Constants.GLEB_ADMIN_ID;
import static com.physphile.forbot.Constants.PAVEL_ST_ADMIN_ID;
import static com.physphile.forbot.Constants.STORAGE_OLYMP_IMAGE_PATH;

public class OlympPage extends BaseSwipeActivity {
    private FirebaseUser user;
    private Intent intent;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    private HashMap<String, String> admins;

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.editOlymp:
                    break;
                case R.id.removeOlymp:
                    String num = intent.getStringExtra("olympNum");
                    String path = intent.getStringExtra("olympPath");
                    database.getReference(path).removeValue();
                    storage.getReference(STORAGE_OLYMP_IMAGE_PATH + num).delete();
                    OlympPage.super.finish();
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
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        final Toolbar toolbar = findViewById(R.id.olympToolbar);
        TextView olympText = findViewById(R.id.olympText);
        ImageView olympTitleImage = findViewById(R.id.olympTitleImage);
        TextView olympName = findViewById(R.id.olympName);
        TextView olympDate = findViewById(R.id.olympDate);
        TextView olympLevel = findViewById(R.id.olympLevel);

        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = width * 10 / 16;
        AppBarLayout appBarLayout = findViewById(R.id.olympAppbar);
        appBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(width, height));
        intent = getIntent();

//        auth = FirebaseAuth.getInstance();
//        FirebaseDatabase.getInstance().getReference("/admins").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                admins = (HashMap<String, String>) dataSnapshot.getValue();
//                auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
//                    @Override
//                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                        FirebaseUser user = firebaseAuth.getCurrentUser();
//                        if (user != null && admins.containsValue(user.getUid())) {
//                            toolbar.getMenu().clear();
//                            toolbar.inflateMenu(R.menu.admin_olymp_page_menu);
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        if (user != null) {
            if (user.getUid().equals(ARTEM_ADMIN_UID) || user.getUid().equals(GLEB_ADMIN_ID) || user.getUid().equals(PAVEL_ST_ADMIN_ID)) {
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.admin_olymp_page_menu);
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
