package com.physphile.forbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {

    private EditText MailField;
    private EditText PwdField;
    private Button RegBtn;
    private Button SignBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        MailField = findViewById(R.id.MailField);
        PwdField = findViewById(R.id.PwdField);
        RegBtn = findViewById(R.id.RegBtn);
        SignBtn = findViewById(R.id.SignBtn);
        RegBtn.setOnClickListener(OnRegBtnClick);
        SignBtn.setOnClickListener(OnSignBtnClick);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    View.OnClickListener OnSignBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            signing(MailField.getText().toString(), PwdField.getText().toString());
        }
    };
    View.OnClickListener OnRegBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            register(MailField.getText().toString(), PwdField.getText().toString());
            signing(MailField.getText().toString(), PwdField.getText().toString());
        }
    };


    public void signing (String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AuthActivity.this, "Authentication success.", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        } else { Toast.makeText(AuthActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show(); }
                    }
                });
    }
    public void register (String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(AuthActivity.this, "Register success.", Toast.LENGTH_SHORT).show();


                        } else { Toast.makeText(AuthActivity.this, "Register failed.", Toast.LENGTH_SHORT).show(); }
                    }
                });
    }
}
