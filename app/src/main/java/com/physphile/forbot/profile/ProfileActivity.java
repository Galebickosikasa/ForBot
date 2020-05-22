package com.physphile.forbot.profile;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.physphile.forbot.BaseSwipeActivity;
import com.physphile.forbot.ClassHelper;
import com.physphile.forbot.R;

import java.io.FileNotFoundException;

import static com.physphile.forbot.Constants.FILE_IMAGE_AVATAR;

public class ProfileActivity extends BaseSwipeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ImageView avatar = findViewById(R.id.Avatar);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageReference = storage.getReference("avatars/" + user.getUid());
        try {
            avatar.setImageBitmap(new ClassHelper(this).readFile(FILE_IMAGE_AVATAR));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}
