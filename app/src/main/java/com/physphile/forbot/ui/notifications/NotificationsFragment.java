package com.physphile.forbot.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.physphile.forbot.R;

public class NotificationsFragment extends Fragment {

    View v;
    TextView AccoutField;

    private FirebaseAuth mAuth;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        v = inflater.inflate(R.layout.fragment_notifications, null);

        AccoutField = v.findViewById(R.id.AccountField);

        ConstraintLayout ConLay = v.findViewById(R.id.ConLay);
        Button btn = new Button(getContext());
        if (mAuth.getCurrentUser() != null){
            AccoutField.setText("Вы вошли как: " + '\n' + mAuth.getCurrentUser().getEmail());
            MaterialButton AccoutSettingsBtn = new MaterialButton(getContext());
            AccoutSettingsBtn.setText("Настройки аккаунта");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            params.gravity = Gravity.CENTER;
            params.bottomMargin = 30;
            params.topMargin = 30;
            AccoutSettingsBtn.setLayoutParams(params);
            AccoutSettingsBtn.setOnClickListener(OnAccountSettingsBtnClick);
            LinearLayout LL = v.findViewById(R.id.LL);
            LL.addView(AccoutSettingsBtn);

        }
        else{
            AccoutField.setText("Вы еще не авторизованы");
            MaterialButton NotAuthBtn = new MaterialButton(getContext());
            NotAuthBtn.setText("Авторизация");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            params.gravity = Gravity.CENTER;
            params.bottomMargin = 30;
            params.topMargin = 30;
            NotAuthBtn.setLayoutParams(params);
            NotAuthBtn.setOnClickListener(OnNotAuthBtnClick);
            LinearLayout LL = v.findViewById(R.id.LL);
            LL.addView(NotAuthBtn);

        }

        TextView tv = v.findViewById(R.id.CheckField);
        if(this.getActivity().getSharedPreferences("physics", Context.MODE_PRIVATE).getBoolean("physics", false)){
            tv.setText(tv.getText().toString() + " physics");
        }
        if(this.getActivity().getSharedPreferences("math", Context.MODE_PRIVATE).getBoolean("math", false)){
            tv.setText(tv.getText().toString() + " math");
        }

        return v;
    }


    View.OnClickListener OnNotAuthBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent("com.physphile.forbot.AuthActivity"));
        }
    };
    View.OnClickListener OnAccountSettingsBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent("com.physphile.forbot.AccountSettingsActivity"));
        }
    };
}