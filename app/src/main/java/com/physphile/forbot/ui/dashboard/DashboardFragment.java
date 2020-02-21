package com.physphile.forbot.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DashboardFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private OlympAdapter adapter;
    private CalendarView calendarView;
    private HashMap<Integer, Integer> cnt;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, null);

        calendarView = v.findViewById(R.id.calendar);
        calendarView.setDate(Calendar.getInstance().getTime().getTime());
        calendarView.setOnDateChangeListener(onDateChangeListener);
        ListView OlympsList = v.findViewById(R.id.OlympsList);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        cnt = new HashMap<>();
        adapter = new OlympAdapter(getContext(), R.layout.olympitem);
        OlympsList.setAdapter(adapter);

        // Пример добавления элементов

        /*
        Item it1 = new Item("kek1", "1");
        Item it2 = new Item("kek2", "2");
        Item it3 = new Item("kek3", "3");
        Item it4 = new Item("kek4", "1");
        Item it5 = new Item("kek5", "2");
        Item it6 = new Item("kek6", "3");
        Item it7 = new Item("kek7", "3");
        Item it8 = new Item("kek8", "3");
        Item it9 = new Item("kek9", "3");

        addItemToDay(2020, 1, 22, it1);
        addItemToDay(2020, 1, 22, it2);
        addItemToDay(2020, 1, 22, it3);
        addItemToDay(2020, 1, 21, it4);
        */

//        databaseReference.child("SomDir").push().setValue(it1);
//        databaseReference.child("SomDir").push().setValue(it2);
//        databaseReference.child("SomDir").push().setValue(it3);
//        databaseReference.child("SomDir").push().setValue(it4);
//        databaseReference.child("SomDir").push().setValue(it5);
//        databaseReference.child("SomDir").push().setValue(it6);
//        databaseReference.child("SomDir").push().setValue(it7);
//        databaseReference.child("SomDir").push().setValue(it8);
//        databaseReference.child("SomDir").push().setValue(it9);

        Date tmp = new Date(calendarView.getDate());
        setItemByDate(tmp.getYear(), tmp.getMonth(), tmp.getDay());

        return v;

    }

    private String makePath (int year, int month, int dayOfMonth) {
        String ans = Integer.toString(year);
        ans += '/';
        ans += Integer.toString(month);
        ans += '/';
        ans += Integer.toString(dayOfMonth);
        return ans;
    }

    private int countChildrenOnDirectory (DatabaseReference ref) {
        int hash = ref.hashCode();
        int ans;
        if (!cnt.containsKey(hash)) ans = 0;
        else ans = cnt.get(hash);
        return ans;
    }

    private void incrementChildrenOnDirectory (DatabaseReference ref) {
        int hash = ref.hashCode();
        if (!cnt.containsKey(hash)) cnt.put(hash, 1);
        else {
            int t = cnt.get (hash);
            cnt.put (hash, t + 1);
        }
    }

    private void addItemToDay (int year, int month, int dayOfMonth, Item item) {
        String curDate = makePath(year, month, dayOfMonth);
        DatabaseReference newRef = databaseReference.child(curDate);
        int c = countChildrenOnDirectory(newRef);
        newRef = newRef.child("" + c);
        newRef.setValue(item);
        incrementChildrenOnDirectory(databaseReference.child(curDate));
    }

    private void setItemByDate (int year, int month, int dayOfMonth) {
        adapter.clear();
        String curDate = makePath(year, month, dayOfMonth);
        DatabaseReference newRef = databaseReference.child(curDate);
        int c = countChildrenOnDirectory(newRef);
        if (c == 0) {
            Toast.makeText(getContext(), "На этот день нет олимпиад", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < c; ++i) {
                DatabaseReference tmpRef = newRef.child(""+i);
                tmpRef.addChildEventListener(new ChildEventListener() {
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
        }
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