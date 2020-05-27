package com.physphile.forbot.profile;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
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

        try {
            avatar.setImageBitmap(new ClassHelper(this).readFile(FILE_IMAGE_AVATAR));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        TextView emailProfile = findViewById(R.id.emailProfile);
        emailProfile.setText(user.getEmail());

        EditText et = findViewById(R.id.aboutEdit);
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    LinearLayout parentLayout = findViewById(R.id.aboutParent);
                    ImageView aboutDone = new ImageView(ProfileActivity.this);
                    aboutDone.setId(View.generateViewId());
                    aboutDone.setImageDrawable(getDrawable(R.drawable.ic_done_black_24dp));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.weight = 1;
                    lp.setMargins(0, 5, 10, 0);
                    aboutDone.setLayoutParams(lp);
                    parentLayout.addView(aboutDone);


                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}
