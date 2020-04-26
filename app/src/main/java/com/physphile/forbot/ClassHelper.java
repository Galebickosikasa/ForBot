package com.physphile.forbot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MenuItem;
import android.widget.CalendarView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.physphile.forbot.olympiads.OlympsAdapter;
import com.physphile.forbot.profile.ProfileMenuDialog;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

import static com.physphile.forbot.Constants.AUTH_ACTIVITY_PATH;
import static com.physphile.forbot.Constants.FRAGMENT_DIALOG_PROFILE_TAG;
import static com.physphile.forbot.Constants.NEWS_CREATE_ACTIVITY_PATH;

public class ClassHelper {
    private Activity activity;
    private OlympsAdapter olympsAdapter;
    private FragmentManager fragmentManager;
    private CalendarView calendarView;
    public Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.profile:
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        DialogFragment profileDialog = new ProfileMenuDialog();
                        profileDialog.show(fragmentManager, FRAGMENT_DIALOG_PROFILE_TAG);
                    } else {
                        activity.startActivity(new Intent(AUTH_ACTIVITY_PATH));
                    }
                    break;
                case R.id.createNews:
                    activity.startActivity(new Intent(NEWS_CREATE_ACTIVITY_PATH));
                    break;
                case R.id.set_today:
                    olympsAdapter.clearItems();
                    calendarView.setDate(Calendar.getInstance().getTime().getTime());
                    break;
            }
            return false;
        }
    };

    public ClassHelper(Activity _activity) {
        this.activity = _activity;
    }

    public ClassHelper(Activity _activity, FragmentManager _fragmentManager) {
        this.activity = _activity;
        this.fragmentManager = _fragmentManager;
    }

    public ClassHelper(Activity _activity, FragmentManager _fragmentManager, OlympsAdapter _olympsAdapter, CalendarView _calendarView) {
        this.activity = _activity;
        this.olympsAdapter = _olympsAdapter;
        this.fragmentManager = _fragmentManager;
        this.calendarView = _calendarView;
    }

    public void saveFile(Bitmap bitmap, String name) {
        try {
            FileOutputStream out = activity.openFileOutput(name, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//            Log.e(LOG_NAME, "файл сохранен");
            out.close();
        } catch (Exception ignored) {
//            Log.e(LOG_NAME, "файл не сохранен");
        }
    }

    public Bitmap readFile(String name) throws FileNotFoundException {
        FileInputStream is = activity.openFileInput(name);
//        Log.e(LOG_NAME, "файл прочитан");
        return BitmapFactory.decodeStream(is);
    }

    public int dpToPx(final float dp) {
        return (int) (dp * activity.getResources().getDisplayMetrics().density);
    }


}
