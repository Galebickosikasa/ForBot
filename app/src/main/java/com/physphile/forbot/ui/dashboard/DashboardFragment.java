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
import com.physphile.forbot.MainActivity;
import com.physphile.forbot.OlympAdapter;
import com.physphile.forbot.R;

import java.util.ArrayList;
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