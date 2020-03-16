package com.physphile.forbot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import static android.app.Activity.RESULT_OK;
import static com.physphile.forbot.Constants.ACCOUNT_SETTINGS_ACTIVITY_PATH;
import static com.physphile.forbot.Constants.AUTH_ACTIVITY_CODE;
import static com.physphile.forbot.Constants.AUTH_ACTIVITY_PATH;
import static com.physphile.forbot.Constants.FILE_IMAGE_AVATAR;
import static com.physphile.forbot.Constants.FILE_PREFIX;
import static com.physphile.forbot.Constants.LOG_NAME;
import static com.physphile.forbot.Constants.STORAGE_AVATARS_PATH;

public class ProfileDialogFragment extends DialogFragment {
    private View v;
    private FirebaseStorage storage;
    private ImageView Avatar;
    private TextView AccountField;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_profile_dialog, container, false);
        AccountField = v.findViewById(R.id.AccountField);
        mAuth = FirebaseAuth.getInstance();
        Avatar = v.findViewById(R.id.Avatar);
        user = mAuth.getCurrentUser();
        Button dynamicBtn = v.findViewById(R.id.dynamicBtn);
        if(mAuth.getCurrentUser() !=  null){ Avatar.setOnClickListener(onClickListener); }
        dynamicBtn.setOnClickListener(onClickListener);
        storage = FirebaseStorage.getInstance();
        mAuth.addAuthStateListener(mAuthListener);
        if(user != null){ setAvatar(); }
        return v;
    }

    private FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            user = firebaseAuth.getCurrentUser();
            if (user != null) {
                updateProfile(true);
                Log.e(LOG_NAME, "onAuthStateChanged true");
            } else {
                updateProfile(false);
                Log.e(LOG_NAME, "onAuthStateChanged false");
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.Avatar:
                    CropImage.activity()
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setAspectRatio(1, 1)
                            .start(getContext(), ProfileDialogFragment.this);
                    break;
                case R.id.dynamicBtn:
                    startActivityForResult(new Intent(AUTH_ACTIVITY_PATH), AUTH_ACTIVITY_CODE);
                    break;
                case R.id.AccountSettingsBtn:
                    startActivity(new Intent(ACCOUNT_SETTINGS_ACTIVITY_PATH));
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), result.getUri());
                    } catch (IOException e) {
                        Log.e(LOG_NAME, "cropActivity: изображение не найдено");
                    }
                    Avatar.setImageBitmap(bitmap);
                    uploadImage(result.getUri());
                    ((MainActivity) getActivity()).saveFile(bitmap, FILE_IMAGE_AVATAR);
                }
                break;

            case AUTH_ACTIVITY_CODE:
                if (resultCode == RESULT_OK){
                    try {
                        setAvatarFirebase();
                        Log.e(LOG_NAME, "автар установлен");
                    } catch (IOException e) { Log.e(LOG_NAME, "аватар не установлен"); }

                }
                break;
        }
    }

    private void uploadImage(Uri filePath) {
        storage.getReference(STORAGE_AVATARS_PATH + user.getUid())
                .putFile(filePath);
    }

    private void setAvatarFirebase() throws IOException {
        final File localFile = File.createTempFile(FILE_PREFIX, FILE_IMAGE_AVATAR);
        Log.e(LOG_NAME, STORAGE_AVATARS_PATH + user.getUid());
        storage.getReference(STORAGE_AVATARS_PATH + user.getUid())
                .getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bmp = BitmapFactory.decodeFile(localFile.getPath());
                        Avatar.setImageBitmap(bmp);
                        ((MainActivity) getActivity()).saveFile(bmp, FILE_IMAGE_AVATAR);
                        Log.e(LOG_NAME, "download image done");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(LOG_NAME, "download image failure");
                    }
                });
    }

    private void updateProfile(boolean isUser){
        MaterialButton dynamicBtn = v.findViewById(R.id.dynamicBtn);
        if (isUser){
            AccountField.setText(mAuth.getCurrentUser().getEmail());
            dynamicBtn.setText("Настройки аккаунта");
            dynamicBtn.setId(R.id.AccountSettingsBtn);
        } else {
            AccountField.setText("Вы еще не авторизованы");
            dynamicBtn.setText("Авторизация");
        }
    }

    private void setAvatar(){
        try {
            Bitmap b = ((MainActivity) getActivity()).readFile(FILE_IMAGE_AVATAR);
            Avatar.setImageBitmap(b);
            Log.e(LOG_NAME, "Аватар установлен");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(LOG_NAME, "Аватар не установлен");
        }
    }
}