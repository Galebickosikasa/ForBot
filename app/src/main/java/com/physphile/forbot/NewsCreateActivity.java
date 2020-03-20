package com.physphile.forbot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.physphile.forbot.Feed.NewsFirebaseItem;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import br.com.simplepass.loadingbutton.customViews.CircularProgressImageButton;
import br.com.simplepass.loadingbutton.customViews.OnAnimationEndListener;

import static com.physphile.forbot.Constants.DATABASE_NEWS_PATH;
import static com.physphile.forbot.Constants.INTENT_EXTRA_NEWS_TITLE;
import static com.physphile.forbot.Constants.INTENT_EXTRA_NEWS_TITLE_IMAGE;
import static com.physphile.forbot.Constants.LOG_NAME;
import static com.physphile.forbot.Constants.STORAGE_NEWS_IMAGE_PATH;

public class NewsCreateActivity extends BaseSwipeActivity {
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ImageView NewsTitleImage;
    private EditText NewsTitle;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private EditText NewsNumber;
    private EditText newsText;
    private CircularProgressImageButton btn;
    private CoordinatorLayout parent;
//    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_create);
        NewsTitleImage = findViewById(R.id.newsTitleImage);
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = width * 10 /16;
        NewsTitleImage.setLayoutParams(new CollapsingToolbarLayout.LayoutParams(width, height));
        NewsTitle = findViewById(R.id.newsTitle);
        NewsTitleImage.setOnClickListener(onClickListener);
        parent = findViewById(R.id.parent);
        btn = getBtn();
        parent.addView(btn);
        TextInputLayout newsNumberField = findViewById(R.id.newsNumberField);
//        toolbar = findViewById(R.id.newsToolbar);
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        NewsNumber = findViewById(R.id.newsNumber);
        CoordinatorLayout.LayoutParams numberParams = (CoordinatorLayout.LayoutParams) newsNumberField.getLayoutParams();
        numberParams.setAnchorId(R.id.newsTitleImage);
        newsNumberField.setLayoutParams(numberParams);
        newsText = findViewById(R.id.newsText);
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_news_create;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.newsTitleImage:
                    CropImage.activity()
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setAspectRatio(16, 10)
                            .start(NewsCreateActivity.this);

                    break;
                case R.id.newsDoneBtn:
                    putNewsFirebase(Integer.parseInt(NewsNumber.getText().toString()), NewsTitle.getText().toString(), newsText.getText().toString());
                    Intent intent = new Intent();
                    intent.putExtra(INTENT_EXTRA_NEWS_TITLE, NewsTitle.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    };

    private CircularProgressImageButton getBtn(){
        btn = new CircularProgressImageButton(this);
        CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(dpToPx(this, 70), dpToPx(this, 70));
        lp.setAnchorId(R.id.newsTitleImage);
        lp.anchorGravity = Gravity.BOTTOM | Gravity.END;
        lp.setMarginEnd(dpToPx(this, 16));
        btn.setLayoutParams(lp);
        btn.setBackground(getDrawable(R.drawable.circle_shape));
        btn.setId(R.id.newsDoneBtn);
        btn.setOnClickListener(onClickListener);
        btn.setElevation(dpToPx(this, 8));
        btn.setImageResource(R.drawable.ic_block_black_24dp);
        btn.setFinalCorner(dpToPx(this, 35));
        btn.setInitialCorner(dpToPx(this, 35));
        btn.setSpinningBarColor(getResources().getColor(R.color.colorSecond));
        return btn;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(this).getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                NewsTitleImage.setImageBitmap(bitmap);
                btn.startAnimation();
                btn.setImageResource(R.drawable.ic_file_download_black_24dp);
                saveFile(bitmap, INTENT_EXTRA_NEWS_TITLE_IMAGE);
                uploadImage(resultUri);
            }
        }
    }

    public void saveFile(Bitmap bitmap, String name) {
        try {
            FileOutputStream out = openFileOutput(name, MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Log.e(LOG_NAME, "файл сохранен");
            out.close();
        } catch (Exception ignored) {
            Log.e(LOG_NAME, "файл не сохранен");
        }
    }

    private void uploadImage(Uri filePath) {
        storageReference = storage.getReference(STORAGE_NEWS_IMAGE_PATH + NewsNumber.getText().toString());
        storageReference.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                btn.revertAnimation();
                btn.setImageResource(R.drawable.ic_done_black_24dp);
            }
        });
    }

    private static int dpToPx(final Context context, final float dp) {
        return (int)(dp * context.getResources().getDisplayMetrics().density);
    }

    public void putNewsFirebase(int num, final String title, final String text) {
        storageReference = storage.getReference(STORAGE_NEWS_IMAGE_PATH + num);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                databaseReference = database.getReference(DATABASE_NEWS_PATH);
                Calendar calendar = Calendar.getInstance();
                NewsFirebaseItem nfi = new NewsFirebaseItem(title,
                        uri.toString(),
                        text,
                        FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                        calendar.get(Calendar.DATE) + "." + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR)
                );
                databaseReference.push().setValue(nfi);
                Log.e(LOG_NAME, "Новости подгружены");
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}
