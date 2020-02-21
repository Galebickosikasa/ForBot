package com.physphile.forbot.ui.dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.physphile.forbot.Item;
import com.physphile.forbot.OlympAdapter;
import com.physphile.forbot.R;


import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DashboardFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private OlympAdapter adapter;
    private CalendarView calendarView;
    private HashMap<Integer, Integer> cnt;
    private SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, null);

        calendarView = v.findViewById(R.id.calendar);
        calendarView.setDate(Calendar.getInstance().getTime().getTime());
        calendarView.setOnDateChangeListener(onDateChangeListener);
        ListView OlympsList = v.findViewById(R.id.OlympsList);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

//        cnt = new HashMap<>();
        adapter = new OlympAdapter(getContext(), R.layout.olympitem);
        OlympsList.setAdapter(adapter);

        Date tmp = new Date(calendarView.getDate());
        setItemByDate(tmp.getYear(), tmp.getMonth(), tmp.getDay());

        return v;

    }

    private String makePath (int year, int month, int dayOfMonth) {
        return year + "/" + month + "/" + dayOfMonth;
    }

//    private int countChildrenOnDirectory (DatabaseReference ref) {
//        int hash = ref.hashCode();
//        int ans = sp.getInt(""+hash, -1);
//        if (ans == -1) return 0;
//        else return ans;
//    }

//    private void incrementChildrenOnDirectory (DatabaseReference ref) {
//        int hash = ref.hashCode();
//        int t = sp.getInt(""+hash, -1);
//        SharedPreferences.Editor e = sp.edit();
//        if (t == -1) e.putInt(""+hash, 1);
//        else e.putInt(""+hash, t + 1);
//        e.apply();
//    }

//    private void addItemToDay (int year, int month, int dayOfMonth, Item item) {
//        String curDate = makePath(year, month, dayOfMonth);
//        DatabaseReference newRef = databaseReference.child(curDate);
//        int c = countChildrenOnDirectory(newRef);
//        newRef = newRef.child("" + c);
//        newRef.setValue(item);
//        incrementChildrenOnDirectory(databaseReference.child(curDate));
//    }

    private void setItemByDate (int year, int month, int dayOfMonth) {
        adapter.clear();
        String curDate = makePath(year, month, dayOfMonth);
        DatabaseReference newRef = database.getReference(curDate);
//        int c = countChildrenOnDirectory(newRef);
//        for (int i = 0; i < c; ++i) {
//            DatabaseReference tmpRef = newRef.child(""+i);
            newRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Item item = dataSnapshot.getValue(Item.class);
                    adapter.add(item);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s){}
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private CalendarView.OnDateChangeListener onDateChangeListener= new CalendarView.OnDateChangeListener() {

        @Override
        public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
            String curDate = makePath(year, month, dayOfMonth);
            setItemByDate(year, month, dayOfMonth);
            Log.e ("kek", curDate);
        }

    };

}