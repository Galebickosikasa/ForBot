package com.physphile.forbot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class FeedFragment extends Fragment {


    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_feed, container, false);



        return root;
    }
    Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.profile:
                    DialogFragment profileDialog = new ProfileDialogFragment();
                    profileDialog.show(getFragmentManager(), "profileDialog");
                    break;
            }
            return false;
        }
    };
}