package com.physphile.forbot.Calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.physphile.forbot.ProfileDialogFragment;
import com.physphile.forbot.R;

import java.util.Calendar;

import static com.physphile.forbot.Constants.FRAGMENT_DIALOG_PROFILE_TAG;

public class CalendarFragment extends Fragment {
    private FirebaseDatabase database;
    private OlympAdapter adapter;
    private View v;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_calendar, container,false);
        Toolbar toolbar = v.findViewById(R.id.calendarToolbar);
        CalendarView calendarView = v.findViewById(R.id.calendar);
        calendarView.setDate(Calendar.getInstance().getTime().getTime());
        calendarView.setOnDateChangeListener(onDateChangeListener);
        ListView olympsList = v.findViewById(R.id.OlympsList);
        database = FirebaseDatabase.getInstance();
        adapter = new OlympAdapter(getContext(), R.layout.olymps_item);
        olympsList.setAdapter(adapter);
        Calendar calendar = Calendar.getInstance();
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        setItemByDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        return v;
    }

    static CalendarFragment newInstance() { return new CalendarFragment(); }

    private String makePath (int year, int month, int dayOfMonth) {
        return year + "/" + month + "/" + dayOfMonth;
    }

    private OnMenuItemClickListener onMenuItemClickListener = new OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.set_today:
                    CalendarView cal = v.findViewById(R.id.calendar);
                    adapter.clear();
                    cal.setDate(Calendar.getInstance().getTime().getTime());
                    break;
                case R.id.profile:
                    DialogFragment profileDialog = new ProfileDialogFragment();
                    profileDialog.show(getChildFragmentManager(), FRAGMENT_DIALOG_PROFILE_TAG);
                    break;
            }
            return false;
        }
    };

    private void setItemByDate (int year, int month, int dayOfMonth) {
        adapter.clear();
        String curDate = makePath(year, month, dayOfMonth);
        DatabaseReference newRef = database.getReference(curDate);
        newRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                OlympsListItem item = dataSnapshot.getValue(OlympsListItem.class);
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
            setItemByDate(year, month, dayOfMonth);
        }
    };
}