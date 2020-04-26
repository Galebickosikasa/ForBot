package com.physphile.forbot.news;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.physphile.forbot.R;
import com.physphile.forbot.profile.ProfileMenuDialog;

import static com.physphile.forbot.Constants.ARTEM_ADMIN_UID;
import static com.physphile.forbot.Constants.AUTH_ACTIVITY_PATH;
import static com.physphile.forbot.Constants.DATABASE_NEWS_PATH;
import static com.physphile.forbot.Constants.FRAGMENT_DIALOG_PROFILE_TAG;
import static com.physphile.forbot.Constants.GLEB_ADMIN_ID;
import static com.physphile.forbot.Constants.NEWS_CREATE_ACTIVITY_PATH;
import static com.physphile.forbot.Constants.PAVEL_ST_ADMIN_ID;

public class FeedFragment extends Fragment {
    private NewsAdapter adapter;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private RecyclerView newsList;
    private View v;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.profile:
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        DialogFragment profileDialog = new ProfileMenuDialog();
                        profileDialog.show(getParentFragmentManager(), FRAGMENT_DIALOG_PROFILE_TAG);
                    } else {
                        startActivity(new Intent(AUTH_ACTIVITY_PATH));
                    }
                    break;
                case R.id.createNews:
                    startActivity(new Intent(NEWS_CREATE_ACTIVITY_PATH));
                    break;
            }
            return false;
        }
    };

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //инициализация фрагмента
        v = inflater.inflate(R.layout.feed_fragment_backdrop, container, false);

        //инициализация переменных
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //инициализация View-элементов
        initRecyclerView();
        Toolbar toolbar = v.findViewById(R.id.feedToolbar);
        mSwipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);

        //заполнение View-элементов
        if (user != null && (user.getUid().equals(ARTEM_ADMIN_UID) || user.getUid().equals(GLEB_ADMIN_ID) || user.getUid().equals(PAVEL_ST_ADMIN_ID))) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.admin_toolbar_menu);
        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clearItems();
                getNews();
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);

        //остальные методы
        adapter = new NewsAdapter(getContext());
        newsList.setAdapter(adapter);
        adapter.clearItems();
        getNews();

        return v;
    }

    private void initRecyclerView() {
        newsList = v.findViewById(R.id.NewsList);
        newsList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getNews() {
        final DatabaseReference ref = database.getReference(DATABASE_NEWS_PATH);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 1; i < dataSnapshot.getChildrenCount() + 1; ++i) {
                    ref.child(i + "").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            NewsFirebaseItem item = dataSnapshot.getValue(NewsFirebaseItem.class);
                            adapter.addItem(item);
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}