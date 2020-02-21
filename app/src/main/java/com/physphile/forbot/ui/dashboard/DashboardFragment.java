package com.physphile.forbot.ui.dashboard;

import android.os.Bundle;
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

public class DashboardFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, null);

//        CalendarView cal = v.findViewById(R.id.calendar);
//        cal.setDate(Calendar.getInstance().getTime().getTime());
        ListView OlympsList = v.findViewById(R.id.OlympsList);

        //что-то с базами данных
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();

        final OlympAdapter adapter = new OlympAdapter(getContext(), R.layout.olympitem);
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

        databaseReference.child("SomDir").push().setValue(it1);
        databaseReference.child("SomDir").push().setValue(it2);
        databaseReference.child("SomDir").push().setValue(it3);
        databaseReference.child("SomDir").push().setValue(it4);
        databaseReference.child("SomDir").push().setValue(it5);
        databaseReference.child("SomDir").push().setValue(it6);
        databaseReference.child("SomDir").push().setValue(it7);
        databaseReference.child("SomDir").push().setValue(it8);
        databaseReference.child("SomDir").push().setValue(it9);
         */

        databaseReference.child("SomDir").addChildEventListener(new ChildEventListener() {
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

        return v;

    }
}