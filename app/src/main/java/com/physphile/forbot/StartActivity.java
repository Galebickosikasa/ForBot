package com.physphile.forbot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Query;
import com.google.firebase.database.annotations.NotNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class StartActivity extends AppCompatActivity {

    //SharedPreferences это штука, которая сохраняет значения нужных переменных в файлике
    SharedPreferences sp;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChooseActivity();

        //что-то с базами данных
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("physics");

        final TextView ChooseFoamTxt = (TextView) findViewById(R.id.ChooseFoamTxt);
        final ListView OlympsList = (ListView)findViewById(R.id.OlympsList);
        final OlympsAdapter adapter = new OlympsAdapter();
        OlympsList.setAdapter(adapter);


        //реализуем возможность читать БД
        Query myQuery = myRef;
        myQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Item i = dataSnapshot.getValue(Item.class);//берем элементик из БД
                adapter.add(i);

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


    // определяем штуки баз данных
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    // метод, который запускает нужные активити
    protected void ChooseActivity() {
        boolean isClick;
        sp = getSharedPreferences("isClick", Context.MODE_PRIVATE);

        /*
        создаём атрибут isClick, Context.MODE_PRIVATE значит,
        что доступ к этому файлику есть только у этого приложения
        */

        isClick = sp.getBoolean("isClick", false); //устанавливаем значение этого атрибута - false

        if (isClick) {
            setContentView(R.layout.activity_main);

            /*
            Passing each menu ID as a set of Ids because each
            menu should be considered as top level destinations.
            */

            BottomNavigationView navView = findViewById(R.id.nav_view);
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
        }
        else {
            setContentView(R.layout.activity_start);
            ImageButton StartContinueBtn = (ImageButton) findViewById(R.id.StartContinueBtn);
            SeekBar ChooseFoamBar = (SeekBar)findViewById(R.id.ChooseFoamBar);

            StartContinueBtn.setOnClickListener(OnStartContinueBtnListener);
            ChooseFoamBar.setOnSeekBarChangeListener(ChooseFoamBarListener);

        }
    }

    //просто листенер для кнопки
    View.OnClickListener OnStartContinueBtnListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            startActivity(new Intent("com.physphile.forbot.MainActivity"));
            SharedPreferences.Editor e = sp.edit(); // чтобы менять этот файлик надо создать эдитор
            e.putBoolean("isClick", true); // устанавливаем значение этого атрибута - true
            e.apply(); // закрываем эдитор
        }

    };

    //листенер для сикбара
    SeekBar.OnSeekBarChangeListener ChooseFoamBarListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            TextView ChooseFoamTxt = (TextView) findViewById(R.id.ChooseFoamTxt);
            ChooseFoamTxt.setText(String.valueOf(progress + 5) + " класс");

//            myRef.push().setValue(new Item("student", ChooseFoamTxt.getText().toString())); //создаём элемент в бд
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private class OlympsAdapter extends ArrayAdapter<Item> {

        public OlympsAdapter() {
            super(StartActivity.this, R.layout.olympitem);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final View OlympItemInflate = getLayoutInflater().inflate(R.layout.olympitem, null);
            final Item item = getItem(position);
            ((TextView)OlympItemInflate.findViewById(R.id.olympName)).setText(item.name);
            ((TextView)OlympItemInflate.findViewById(R.id.olympLevel)).setText(item.level);
            return OlympItemInflate;
        }
    }

}
