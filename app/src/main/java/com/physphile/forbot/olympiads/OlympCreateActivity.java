package com.physphile.forbot.olympiads;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.physphile.forbot.BaseSwipeActivity;
import com.physphile.forbot.ClassHelper;
import com.physphile.forbot.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import br.com.simplepass.loadingbutton.customViews.CircularProgressImageButton;

import static com.physphile.forbot.Constants.STORAGE_OLYMP_IMAGE_PATH;
import static java.lang.Math.abs;

public class OlympCreateActivity extends BaseSwipeActivity implements DatePickerDialog.OnDateSetListener {
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ImageView olympTitleImage;
    private CircularProgressImageButton btn;
    private EditText olympTitle, olympText;
    private String date = "", level, path;
    private CoordinatorLayout parent;
    private Spinner spinner;
    private Toolbar toolbar;
    private Button dateBtn;
    private TextView visualDate;
    private Integer num, YEAR, MONTH, DAYOFMONTH, baseYear = -1, baseMonth = -1, baseDay = -1;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olymp_create);
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        olympTitleImage = findViewById(R.id.olympTitleImage);
        parent = findViewById(R.id.parent);
        olympTitle = findViewById(R.id.olympTitle);
        olympText = findViewById(R.id.olympText);
        toolbar = findViewById(R.id.olympToolbar);
        spinner = findViewById(R.id.spinner);
        dateBtn = findViewById(R.id.olympDate);
        visualDate = findViewById(R.id.visualOlympDate);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.olymp_level));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show (getSupportFragmentManager(), "date picker");
            }
        });

        num = abs(new Random().nextInt());
        int width = getWindowManager().getDefaultDisplay().getWidth();
        olympTitleImage.setLayoutParams(new CollapsingToolbarLayout.LayoutParams(width, width * 10 / 16));
        olympTitleImage.setOnClickListener(onClickListener);
        btn = getBtn();
        parent.addView(btn);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new ClassHelper(this,
                getSupportFragmentManager()).onMenuItemClickListener);

        boolean flag = getIntent().getBooleanExtra("Edit", false);
        if (flag) {
            olympTitle.setText(getIntent().getStringExtra("Title"));
            olympText.setText(getIntent().getStringExtra("Text"));
            date = getIntent().getStringExtra("Date");
            level = getIntent().getStringExtra("Level");
            num = Integer.parseInt(getIntent().getStringExtra("Num"));
            YEAR = baseYear = getIntent().getIntExtra("Year", 0);
            MONTH = baseMonth = getIntent().getIntExtra("Month", 0);
            DAYOFMONTH = baseDay = getIntent().getIntExtra("Day", 0);
            date = makeDate(YEAR, MONTH + 1, DAYOFMONTH);
            path = makePath(YEAR, MONTH, DAYOFMONTH);
            visualDate.setText(date);
            spinner.setSelection(Integer.parseInt(level) - 1);
            String filename = getIntent().getStringExtra("Bitmap");
            try {
                FileInputStream In = openFileInput(filename);
                Bitmap bmp = BitmapFactory.decodeStream(In);
                In.close ();

                olympTitleImage.setImageBitmap(bmp);
                btn.setImageResource(R.drawable.ic_done_black_24dp);
                btn.setClickable(true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = makeDate(year, month + 1, dayOfMonth);
        path = makePath(year, month, dayOfMonth);
        YEAR = year;
        MONTH = month;
        DAYOFMONTH = dayOfMonth;
        visualDate.setText(date);
    }

    private String makePath (int year, int month, int dayOfMonth) {
        return year + "/" + month + "/" + dayOfMonth + "/";
    }

    private String makeDate (int year, int month, int dayOfMonth) {
        return year + "." + month + "." + dayOfMonth;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.olympTitleImage:
                    CropImage.activity()
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setAspectRatio(16, 10)
                            .start(OlympCreateActivity.this);
                    break;
                case R.id.olympDoneBtn:
                    if (!olympText.getText().toString().equals("") && !olympTitle.getText().toString().equals("") && !date.equals("")) {
                        if (!(YEAR == baseYear && MONTH == baseMonth && DAYOFMONTH == baseDay)) {
                            SharedPreferences sp = getSharedPreferences("Done", Context.MODE_PRIVATE);
                            SharedPreferences.Editor e = sp.edit();
                            e.putBoolean("Done", true);
                            e.apply();
                        }
                        putOlympFirebase();
                        finish();
                    } else {
                        Snackbar.make(v, "Сначала заполните все поля", Snackbar.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(this).getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                olympTitleImage.setImageBitmap(bitmap);
                btn.startAnimation();
                btn.setImageResource(R.drawable.ic_file_download_black_24dp);
                uploadImage(resultUri);
            }
        }
    }

    private void uploadImage(Uri filePath) {
        storageReference = storage.getReference(STORAGE_OLYMP_IMAGE_PATH + num);
        storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                btn.revertAnimation();
                btn.setImageResource(R.drawable.ic_done_black_24dp);
                btn.setClickable(true);
            }
        });
    }

    public void putOlympFirebase() {
        databaseReference = database.getReference(path + num);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                storage.getReference(STORAGE_OLYMP_IMAGE_PATH + num)
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        OlympsListItem item = new OlympsListItem(olympTitle.getText().toString(), level, uri.toString(), olympText.getText().toString(), num, YEAR, MONTH, DAYOFMONTH);
                        databaseReference.setValue(item);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private CircularProgressImageButton getBtn() {
        btn = new CircularProgressImageButton(this);
        ClassHelper classHelper = new ClassHelper(this);
        CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(classHelper.dpToPx(70), classHelper.dpToPx(70));
        lp.setAnchorId(R.id.olympTitleImage);
        lp.anchorGravity = Gravity.BOTTOM | Gravity.END;
        lp.setMarginEnd(classHelper.dpToPx(16));
        btn.setLayoutParams(lp);
        btn.setBackground(getDrawable(R.drawable.circle_shape));
        btn.setId(R.id.olympDoneBtn);
        btn.setOnClickListener(onClickListener);
        btn.setClickable(false);
        btn.setElevation(classHelper.dpToPx(8));
        btn.setImageResource(R.drawable.ic_block_black_24dp);
        btn.setFinalCorner(classHelper.dpToPx(35));
        btn.setInitialCorner(classHelper.dpToPx(35));
        btn.setSpinningBarColor(getResources().getColor(R.color.colorSecond));
        return btn;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_olymp_create;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }


}
