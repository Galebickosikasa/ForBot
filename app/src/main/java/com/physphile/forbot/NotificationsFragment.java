package com.physphile.forbot;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.util.Objects;
import static android.app.Activity.RESULT_OK;

public class NotificationsFragment extends Fragment {
    private View v;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri filePath;
    private ImageView Avatar;
    private final int PICK_IMAGE_REQUEST = 71;
    private TextView AccountField;
    private FirebaseAuth mAuth;
    @SuppressLint("InflateParams")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_notifications, null);
        AccountField = v.findViewById(R.id.AccountField);
        mAuth = FirebaseAuth.getInstance();
        Avatar = v.findViewById(R.id.Avatar);
        Avatar.setOnClickListener(OnAvatarClick);
        storage = FirebaseStorage.getInstance();
        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
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
        if (mAuth.getCurrentUser() !=  null) {
            storageReference = storage.getReference("images/" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String url = uri.toString();
                    Glide.with(Objects.requireNonNull(getContext())).load(url).into(Avatar);
                }
            });
        }
        mAuth.addAuthStateListener(mAuthListener);
        return v;
    }

    private View.OnClickListener OnNotAuthBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent("com.physphile.forbot.AuthActivity"));
        }
    };
    private View.OnClickListener OnAccountSettingsBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent("com.physphile.forbot.AccountSettingsActivity"));
        }
    };
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    private View.OnClickListener OnAvatarClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            chooseImage();
        }
    };
    @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), filePath);
                Avatar.setImageBitmap(bitmap);
                uploadImage();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storage.getReference("images/"+ Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateProfile(boolean isUser){
        LinearLayout LL = v.findViewById(R.id.LL);
        LL.removeAllViews();
        final MaterialButton dynamicBtn = new MaterialButton(Objects.requireNonNull(getContext()));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1.0f;
        params.gravity = Gravity.CENTER;
        params.bottomMargin = 30;
        params.topMargin = 30;
        dynamicBtn.setLayoutParams(params);
        LL.addView(dynamicBtn);
        if (isUser){
            AccountField.setText("Вы вошли как: " + '\n' + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
            dynamicBtn.setText("Настройки аккаунта");
            storageReference = storage.getReference("images/" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String url = uri.toString();
                    Glide.with(Objects.requireNonNull(getContext())).load(url).into(Avatar);
                    dynamicBtn.setOnClickListener(OnAccountSettingsBtnClick);
                }
            });
        } else {
            AccountField.setText("Вы еще не авторизованы");
            dynamicBtn.setText("Авторизация");
            dynamicBtn.setOnClickListener(OnNotAuthBtnClick);
        }

    }
}