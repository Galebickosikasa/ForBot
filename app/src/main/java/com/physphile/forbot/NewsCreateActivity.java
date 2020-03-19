package com.physphile.forbot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.physphile.forbot.Feed.NewsFirebaseItem;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_create);
        NewsTitleImage = findViewById(R.id.newsTitleImage);
        NewsTitle = findViewById(R.id.NewsTitle);
        Button newsDoneBtn = findViewById(R.id.NewsDoneBtn);
        NewsTitleImage.setOnClickListener(onClickListener);
        newsDoneBtn.setOnClickListener(onClickListener);
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        NewsNumber = findViewById(R.id.newsNumber);
        newsText = findViewById(R.id.newsText);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_news_create;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.newsTitleImage:
                    CropImage.activity()
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setAspectRatio(16, 10)
                            .start(NewsCreateActivity.this);
                    break;
                case R.id.NewsDoneBtn:
                    putNewsFirebase(Integer.parseInt(NewsNumber.getText().toString()), NewsTitle.getText().toString(), newsText.getText().toString());
                    Intent intent = new Intent();
                    intent.putExtra(INTENT_EXTRA_NEWS_TITLE, NewsTitle.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    };

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
        storageReference.putFile(filePath);
    }

    public void putNewsFirebase(int num, final String title, final String text){
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
