package com.physphile.forbot;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /*
    Очень важная фигня!
    На данный момент все нормально работает только если переустанавливать каждый раз приложение
    После второго захода в приложение появляется нижний бар и все становится очень плохо ))
    Также в DashboardFragment я закоментил календарь, из-за него все падало,
    хотя я хз, вроде и так норм без того кода

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // из-за этой штуки все вылетает

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

//        Log.e ("kek", "start");

        //что-то с базами данных
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();

        final ArrayList<Item> items = new ArrayList<>();

        // Именно тут добавление новых данных, сейчас уже добавлять не надо, но код пусть останется

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

        final ListView OlympsList = findViewById(R.id.OlympsList);
        final OlympAdapter adapter = new OlympAdapter(MainActivity.this, R.layout.olympitem, items);
        OlympsList.setAdapter(adapter);

        databaseReference.child("SomDir").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Item item = dataSnapshot.getValue(Item.class);
                items.add (item);
                adapter.add(item);
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

