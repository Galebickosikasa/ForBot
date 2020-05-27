package com.physphile.forbot.olympiads;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import static com.physphile.forbot.Constants.OLYMPS_CREATE_ACTIVITY_PATH;
import static com.physphile.forbot.Constants.STORAGE_OLYMP_IMAGE_PATH;

public class OlympPage extends BaseSwipeActivity {
    private FirebaseUser user;
    private Intent intent;
    private FirebaseAuth auth;
    private SharedPreferences sp;
    private ImageView olympTitleImage;
    private Toolbar toolbar;
    FirebaseDatabase database;
    FirebaseStorage storage;
    private HashMap<String, String> admins;

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.editOlymp:
                    Intent intent = new Intent(OLYMPS_CREATE_ACTIVITY_PATH);
                    intent.putExtra("Edit", true);
                    intent.putExtra("Title", getIntent().getStringExtra("olympName"));
                    intent.putExtra("Text", getIntent().getStringExtra("olympText"));
                    intent.putExtra("Date", getIntent().getStringExtra("olympDate"));
                    intent.putExtra("Level", getIntent().getStringExtra("olympLevel"));
                    intent.putExtra("Num", getIntent().getStringExtra("olympNum"));
                    intent.putExtra("Path", getIntent().getStringExtra("olympPath"));
                    intent.putExtra("Year", getIntent().getIntExtra("olympYear", 0));
                    intent.putExtra("Month", getIntent().getIntExtra("olympMonth", 0));
                    intent.putExtra("Day", getIntent().getIntExtra("olympDay", 0));
                    Bitmap bmp = ((BitmapDrawable) olympTitleImage.getDrawable()).getBitmap();
                    String filename = "bitmap.png";
                    try {
                        FileOutputStream stream = openFileOutput(filename, Context.MODE_PRIVATE);
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        stream.close ();
                        intent.putExtra("Bitmap", filename);
                        startActivityForResult(intent, 2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.removeOlymp:
                    SharedPreferences.Editor e = sp.edit();
                    e.putBoolean("RemovedOlymp", true);
                    e.apply();
                    String num = getIntent().getStringExtra("olympNum");
                    String path = getIntent().getStringExtra("olympPath");
                    database.getReference(path).removeValue();
                    storage.getReference(STORAGE_OLYMP_IMAGE_PATH + num).delete();
                    OlympPage.super.finish();
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            SharedPreferences sp1 = getSharedPreferences("Done", Context.MODE_PRIVATE);
            boolean f = sp1.getBoolean("Done", false);
            if (f) {
                SharedPreferences.Editor e = sp.edit();
                e.putBoolean("RemovedOlymp", true);
                e.apply();
                String num = getIntent().getStringExtra("olympNum");
                String path = getIntent().getStringExtra("olympPath");
                database.getReference(path).removeValue();
                OlympPage.super.finish();
            }
            SharedPreferences.Editor e = sp1.edit();
            e.putBoolean("Done", false);
            e.apply();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olymp_page);
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        sp = getSharedPreferences("FlagToRemove", Context.MODE_PRIVATE);

        toolbar = findViewById(R.id.olympToolbar);
        TextView olympText = findViewById(R.id.olympText);
        olympTitleImage = findViewById(R.id.olympTitleImage);
        TextView olympName = findViewById(R.id.olympName);
        TextView olympDate = findViewById(R.id.olympDate);
        TextView olympLevel = findViewById(R.id.olympLevel);

        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = width * 10 / 16;
        AppBarLayout appBarLayout = findViewById(R.id.olympAppbar);
        appBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(width, height));
        intent = getIntent();

        auth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().getReference("/admins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                admins = (HashMap<String, String>) dataSnapshot.getValue();
                auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null && admins.containsValue(user.getUid())) {
                            toolbar.getMenu().clear();
                            toolbar.inflateMenu(R.menu.admin_olymp_page_menu);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
