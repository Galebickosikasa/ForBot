package com.physphile.forbot.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.physphile.forbot.R;

import java.util.Calendar;

public class DashboardFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, null);
//        CalendarView cal = v.findViewById(R.id.calendar);
//        cal.setDate(Calendar.getInstance().getTime().getTime());
        return v;
    }
}