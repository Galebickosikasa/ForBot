package com.physphile.forbot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.Objects;
import static android.app.Activity.RESULT_OK;

public class ProfileDialogFragment extends DialogFragment {
    private View v;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ImageView Avatar;
    private TextView AccountField;
    private FirebaseAuth mAuth;


    @SuppressLint("InflateParams")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_profile_dialog, null);
        AccountField = v.findViewById(R.id.AccountField);
        mAuth = FirebaseAuth.getInstance();
        Avatar = v.findViewById(R.id.Avatar);
        if(mAuth.getCurrentUser() !=  null){
            Avatar.setOnClickListener(OnAvatarClick);
            setAvatar();
        }
        storage = FirebaseStorage.getInstance();
        mAuth.addAuthStateListener(mAuthListener);

        return v;
    }
    private FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                updateProfile(true);

            } else {
                updateProfile(false);
            }
        }
    };

    private View.OnClickListener OnNotAuthBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent("com.physphile.forbot.AuthActivity"), 50);
        }
    };

    private View.OnClickListener OnAccountSettingsBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent("com.physphile.forbot.AccountSettingsActivity"));
        }
    };

    private View.OnClickListener OnAvatarClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CropImage.activity()
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1, 1)
                    .start(getContext(), ProfileDialogFragment.this);
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    Bitmap bitmap;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), resultUri);
                        uploadImage(resultUri);
                        ((MainActivity) getActivity()).saveAvatar(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            case 50:
                if (resultCode == RESULT_OK){
                    getFirebaseBitmap();
                }
        }
    }

    private void uploadImage(Uri filePath) {
        storageReference = storage.getReference("images/" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        storageReference.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void updateProfile(boolean isUser){
        final MaterialButton dynamicBtn = v.findViewById(R.id.dynamicBtn);
        if (isUser){
            AccountField.setText("Вы вошли как: " + '\n' + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
            dynamicBtn.setText("Настройки аккаунта");
            dynamicBtn.setOnClickListener(OnAccountSettingsBtnClick);

        } else {
            AccountField.setText("Вы еще не авторизованы");
            dynamicBtn.setText("Авторизация");
            dynamicBtn.setOnClickListener(OnNotAuthBtnClick);
        }

    }
    private void setAvatarFirebase(){
        storageReference = storage.getReference("images/" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                Glide.with(Objects.requireNonNull(getContext())).load(url).into(Avatar);
            }
        });
    }
    private void setAvatar(){
        try {
            Bitmap b = ((MainActivity) getActivity()).readAvatar();
            Avatar.setImageBitmap(b);
            Toast.makeText(getContext(), "аватар установлен", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "ошибка установки аватара", Toast.LENGTH_SHORT).show();
        }
    }
    private void getFirebaseBitmap(){
        StorageReference ref = storage.getReference().child("images/" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        try {
            final File localFile = File.createTempFile("Images", "bmp");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener< FileDownloadTask.TaskSnapshot >() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                   Bitmap my_image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    ((MainActivity) getActivity()).saveAvatar(my_image);
                    setAvatar();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}