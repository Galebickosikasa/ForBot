package com.physphile.forbot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class NewsCreateActivity extends AppCompatActivity {
    private EditText NewsText;
    private Button NewsDoneBtn;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ImageView NewsTitleImage;
    private EditText NewsTitle;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private EditText NewsNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_create);
        NewsTitleImage = findViewById(R.id.newsTitleImage);
        NewsText = findViewById(R.id.NewsText);
        NewsTitle = findViewById(R.id.NewsTitle);
        NewsDoneBtn = findViewById(R.id.NewsDoneBtn);
        NewsTitleImage.setOnClickListener(onClickListener);
        NewsDoneBtn.setOnClickListener(onClickListener);
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        NewsNumber = findViewById(R.id.newsNumber);
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
                    putNewsFirebase(Integer.parseInt(NewsNumber.getText().toString()), NewsTitle.getText().toString());
                    Intent intent = new Intent();
                    intent.putExtra("NewsTitle", NewsTitle.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    Bitmap bitmap;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(this).getContentResolver(), resultUri);
                        saveFile(bitmap, "NewsTitleImage");
                        NewsTitleImage.setImageBitmap(bitmap);
                        uploadImage(resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
                break;
        }
    }
    public void saveFile(Bitmap bitmap, String name) {
        try {
            FileOutputStream out = openFileOutput(name, MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Log.e("ARTEM", "файл сохранен");
            out.close();
        } catch (Exception ignored) {
            Log.e("ARTEM", "файл не сохранен");
        }
    }
    public Bitmap readFile(String name) throws FileNotFoundException {
        FileInputStream is = openFileInput(name);
        Log.e("ARTEM", "файл прочитан");
        return BitmapFactory.decodeStream(is);
    }

    private void uploadImage(Uri filePath) {
        storageReference = storage.getReference("newsImages/" + NewsNumber.getText().toString());
        storageReference.putFile(filePath);
    }

    public void putNewsFirebase(int num, final String title){
        storageReference = storage.getReference("newsImages/" + num);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.e("ARTEM", "SUCCES");
                databaseReference = database.getReference("news/");
                NewsFirebaseItem nfi = new NewsFirebaseItem(title, uri.toString());
                databaseReference.push().setValue(nfi);
            }
        });
    }

}
