package com.physphile.forbot.news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.physphile.forbot.BaseSwipeActivity;
import com.physphile.forbot.ClassHelper;
import com.physphile.forbot.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

import br.com.simplepass.loadingbutton.customViews.CircularProgressImageButton;

import static com.physphile.forbot.Constants.DATABASE_NEWS_PATH;
import static com.physphile.forbot.Constants.STORAGE_NEWS_IMAGE_PATH;

public class NewsCreateActivity extends BaseSwipeActivity {
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ImageView NewsTitleImage;
    private EditText NewsTitle;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private EditText newsText;
    private CircularProgressImageButton btn;
    private CoordinatorLayout parent;
    private Toolbar toolbar;
    private int num, mask = 0;
    private CheckBox phis, math, rus, lit, inf, chem, his, ast;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.newsTitleImage:
                    CropImage.activity()
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setAspectRatio(16, 10)
                            .start(NewsCreateActivity.this);
                    break;
                case R.id.newsDoneBtn:
                    if (!newsText.getText().toString().equals("") && !NewsTitle.getText().toString().equals("")) {
                        putNewsFirebase(NewsTitle.getText().toString(), newsText.getText().toString());
                        finish();
                    } else {
                        Snackbar.make(v, "Сначала заполните все поля", Snackbar.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //инициализация активити
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_create);

        // инициализация предметов
        phis = findViewById(R.id.physicsCheck); // 0
        math = findViewById(R.id.mathCheck); // 1
        rus = findViewById(R.id.russianCheck); // 2
        lit = findViewById(R.id.literatureCheck); // 3
        inf = findViewById(R.id.informaticsCheck); // 4
        chem = findViewById(R.id.chemistryCheck); // 5
        his = findViewById(R.id.historyCheck); // 6
        ast = findViewById(R.id.astronomyCheck); // 7

        phis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mask ^= (1<<0);
            }
        });

        math.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mask ^= (1<<1);
            }
        });

        rus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mask ^= (1<<2);
            }
        });

        lit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mask ^= (1<<3);
            }
        });

        inf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mask ^= (1<<4);
            }
        });

        chem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mask ^= (1<<5);
            }
        });

        his.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mask ^= (1<<6);
            }
        });

        ast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mask ^= (1<<7);
            }
        });

        //инициализация переменных
        num = NewsAdapter.mx + 1;
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        //инициализация View-элементов
        NewsTitleImage = findViewById(R.id.newsTitleImage);
        parent = findViewById(R.id.parent);
        toolbar = findViewById(R.id.newsToolbar);
        newsText = findViewById(R.id.newsText);
        NewsTitle = findViewById(R.id.newsTitle);

        //заполнение View-элементов
        int width = getWindowManager().getDefaultDisplay().getWidth();
        NewsTitleImage.setLayoutParams(new CollapsingToolbarLayout.LayoutParams(width, width * 10 / 16));
        NewsTitleImage.setOnClickListener(onClickListener);
        btn = getBtn();
        parent.addView(btn);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new ClassHelper(this, getSupportFragmentManager()).onMenuItemClickListener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_news_create;
    }

    private CircularProgressImageButton getBtn() {
        btn = new CircularProgressImageButton(this);
        ClassHelper classHelper = new ClassHelper(this);
        CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(classHelper.dpToPx(70), classHelper.dpToPx(70));
        lp.setAnchorId(R.id.newsTitleImage);
        lp.anchorGravity = Gravity.BOTTOM | Gravity.END;
        lp.setMarginEnd(classHelper.dpToPx(16));
        btn.setLayoutParams(lp);
        btn.setBackground(getDrawable(R.drawable.circle_shape));
        btn.setId(R.id.newsDoneBtn);
        btn.setOnClickListener(onClickListener);
        btn.setClickable(false);
        btn.setElevation(classHelper.dpToPx(8));
        btn.setImageResource(R.drawable.ic_block_black_24dp);
        btn.setFinalCorner(classHelper.dpToPx(35));
        btn.setInitialCorner(classHelper.dpToPx(35));
        btn.setSpinningBarColor(getResources().getColor(R.color.colorSecond));
        return btn;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(this).getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                NewsTitleImage.setImageBitmap(bitmap);
                btn.startAnimation();
                btn.setImageResource(R.drawable.ic_file_download_black_24dp);
                uploadImage(resultUri, String.valueOf(num));
            }
        }
    }

    private void uploadImage(Uri filePath, String path) {
        storageReference = storage.getReference(STORAGE_NEWS_IMAGE_PATH + path);
        storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                btn.revertAnimation();
                btn.setImageResource(R.drawable.ic_done_black_24dp);
                btn.setClickable(true);
            }
        });
    }

    public void putNewsFirebase(final String title, final String text) {
        databaseReference = database.getReference(DATABASE_NEWS_PATH + num);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                storage.getReference(STORAGE_NEWS_IMAGE_PATH + num)
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Calendar calendar = Calendar.getInstance();
                        NewsFirebaseItem nfi = new NewsFirebaseItem(title,
                                uri.toString(),
                                text,
                                FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                calendar.get(Calendar.DATE) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR),
                                num, mask
                        );
                        databaseReference.setValue(nfi);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    }
}