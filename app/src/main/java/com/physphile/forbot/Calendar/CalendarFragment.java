package com.physphile.forbot.Calendar;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.physphile.forbot.ClassHelper;
import com.physphile.forbot.R;

import java.util.Calendar;

import static com.physphile.forbot.Constants.STORAGE_OLYMP_IMAGE_PATH;

public class CalendarFragment extends Fragment {
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private OlympsAdapter olympsAdapter;
    private View v;
    private RecyclerView OlympsList;
    private CalendarView.OnDateChangeListener onDateChangeListener = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
            olympsAdapter.clearItems();
            getOlymps(year, month, dayOfMonth);
        }
    };

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_calendar, container, false);
        Toolbar toolbar = v.findViewById(R.id.calendarToolbar);
        initRecyclerView();
        CalendarView calendarView = v.findViewById(R.id.calendar);
        calendarView.setDate(Calendar.getInstance().getTime().getTime());
        calendarView.setOnDateChangeListener(onDateChangeListener);
        OlympsList = v.findViewById(R.id.OlympsList);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        olympsAdapter = new OlympsAdapter(getContext());
        OlympsList.setAdapter(olympsAdapter);
        Calendar calendar = Calendar.getInstance();
        toolbar.setOnMenuItemClickListener(new ClassHelper(getActivity(), getChildFragmentManager(), olympsAdapter, calendarView).onMenuItemClickListener);
        getOlymps(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

        /////////////////////////////////////
        /*
        DatabaseReference databaseReference = database.getReference();
        databaseReference = databaseReference.child(makePath(2020, 3, 24));
        String uri = "https://firebasestorage.googleapis.com/v0/b/forbot-20468.appspot.com/o/newsImages%2F1?alt=media&token=7280c4c6-f8c4-4b5f-9ade-ac7477cc7dcc";
        OlympsListItem oli = new OlympsListItem ("LOL", "1", uri, "2020.4.22", "here we go");
        databaseReference.push().setValue(oli);
        */
        ////////////////////////////////////

        return v;
    }

    private void initRecyclerView() {
        OlympsList = v.findViewById(R.id.OlympsList);
        OlympsList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private String makePath(int year, int month, int dayOfMonth) {
        return year + "/" + month + "/" + dayOfMonth;
    }

    private void getOlymps(int year, int month, int dayOfMonth) {
        database.getReference(makePath(year, month, dayOfMonth))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        OlympsListItem item = dataSnapshot.getValue(OlympsListItem.class);
                        olympsAdapter.addItems(item);
                        Log.e ("kek", "next1");
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