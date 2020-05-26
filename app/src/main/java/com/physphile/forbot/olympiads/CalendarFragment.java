package com.physphile.forbot.olympiads;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.physphile.forbot.ClassHelper;
import com.physphile.forbot.R;
import com.physphile.forbot.profile.ProfileMenuDialog;

import java.util.Calendar;
import java.util.HashMap;

import static com.physphile.forbot.Constants.AUTH_ACTIVITY_PATH;
import static com.physphile.forbot.Constants.FRAGMENT_DIALOG_PROFILE_TAG;
import static com.physphile.forbot.Constants.OLYMPS_CREATE_ACTIVITY_PATH;
import static com.physphile.forbot.Constants.OLYMP_PAGE_ACTIVITY_PATH;

public class CalendarFragment extends Fragment {
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private OlympsAdapter olympsAdapter;
    private View v;
    private RecyclerView OlympsList;
    private FirebaseAuth auth;
    private HashMap<String, String> admins;
    private CalendarView calendarView;
    private Calendar calendar;
    SharedPreferences sp;
    private int YEAR;
    private int MONTH;
    private int DAYOFMONTH;
    private CalendarView.OnDateChangeListener onDateChangeListener = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
            YEAR = year;
            MONTH = month;
            DAYOFMONTH = dayOfMonth;
            getOlymps ();
        }
    };

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_calendar, container, false);
        OlympsList = v.findViewById(R.id.OlympsList);
        calendarView = v.findViewById(R.id.calendar);
        final Toolbar toolbar = v.findViewById(R.id.calendarToolbar);

        auth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().getReference("/admins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                admins = (HashMap<String, String>) dataSnapshot.getValue();
                auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null && admins.containsValue(user.getUid())) {
                            toolbar.getMenu().clear();
                            toolbar.inflateMenu(R.menu.admin_menu_calendar_fragment);
                        } else {
                            toolbar.getMenu().clear();
                            toolbar.inflateMenu(R.menu.toolbar_menu_calendar_fragment);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.editOlymp:
                        startActivityForResult(new Intent(OLYMPS_CREATE_ACTIVITY_PATH), 1);
                        break;
                    case R.id.set_today:
                        olympsAdapter.clearItems();
                        calendarView.setDate(Calendar.getInstance().getTime().getTime());
                        break;
                    case R.id.profile:
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            DialogFragment profileDialog = new ProfileMenuDialog();
                            profileDialog.show(getChildFragmentManager (), FRAGMENT_DIALOG_PROFILE_TAG);
                        } else {
                            startActivity(new Intent(AUTH_ACTIVITY_PATH));
                        }
                        break;
                }
                return false;
            }
        });

        OlympsList.setLayoutManager(new LinearLayoutManager(getContext()));
        olympsAdapter = new OlympsAdapter(getContext());
        OlympsList.setAdapter(olympsAdapter);
        sp = getActivity().getSharedPreferences("FlagToRemove", Context.MODE_PRIVATE);

        calendar = Calendar.getInstance();
        calendarView.setOnDateChangeListener(onDateChangeListener);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        YEAR = calendar.get(Calendar.YEAR);
        MONTH = calendar.get(Calendar.MONTH);
        DAYOFMONTH = calendar.get(Calendar.DATE);
        OlympsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
        });

        getOlymps();
        return v;
    }

    public void onOlympsClick (int pos) {
        Intent intent = new Intent(OLYMP_PAGE_ACTIVITY_PATH);
        intent.putExtra("olympName", olympsAdapter.olympsList.get(pos).getName());
        intent.putExtra("olympDate", olympsAdapter.olympsList.get(pos).getDate());
        intent.putExtra("olympLevel", olympsAdapter.olympsList.get(pos).getLevel());
        intent.putExtra("olympText", olympsAdapter.olympsList.get(pos).getText());
        intent.putExtra("olympUri", olympsAdapter.olympsList.get(pos).getUri());
        intent.putExtra("olympPath", olympsAdapter.olympsList.get(pos).getPath());
        intent.putExtra("olympNum", olympsAdapter.olympsList.get(pos).getNum().toString());
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            boolean f = sp.getBoolean("RemovedOlymp", false);
            if (f) getOlymps();
            Log.e ("kek", "date " + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.DATE));
            SharedPreferences.Editor e = sp.edit();
            e.putBoolean("RemovedOlymp", false);
            e.apply();
        }
    }

    private String makePath(int year, int month, int dayOfMonth) {
        return year + "/" + month + "/" + dayOfMonth;
    }

    private void getOlymps() {
        olympsAdapter.clearItems();
        database.getReference(makePath(YEAR, MONTH, DAYOFMONTH))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        OlympsListItem item = dataSnapshot.getValue(OlympsListItem.class);
                        if (item.getYear() == YEAR && item.getMonth() == MONTH && item.getDayOfMonth() == DAYOFMONTH) olympsAdapter.addItems(item);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }
}