package com.physphile.forbot.Feed;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.physphile.forbot.ClassHelper;
import com.physphile.forbot.R;

import static android.app.Activity.RESULT_OK;
import static com.physphile.forbot.Constants.ARTEM_ADMIN_UID;
import static com.physphile.forbot.Constants.DATABASE_NEWS_PATH;
import static com.physphile.forbot.Constants.GLEB_ADMIN_ID;
import static com.physphile.forbot.Constants.NEWS_CREATE_ACTIVITY_CODE;
import static com.physphile.forbot.Constants.PAVEL_ST_ADMIN_ID;

public class FeedFragment extends Fragment {
    private NewsAdapter adapter;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private RecyclerView newsList;
    private View v;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.feed_fragment_backdrop, container, false);
        initRecyclerView();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Toolbar toolbar = v.findViewById(R.id.feedToolbar);
        if (user != null) {
            if (user.getUid().equals(ARTEM_ADMIN_UID) || user.getUid().equals(GLEB_ADMIN_ID) || user.getUid().equals(PAVEL_ST_ADMIN_ID)) {
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.admin_toolbar_menu);
//                toolbar.getMenu().getItem(1).setIcon(R.drawable.common_google_signin_btn_icon_dark);
            }
        }

        mSwipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clearItems();
                getNews();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        toolbar.setOnMenuItemClickListener(new ClassHelper(getActivity(), getChildFragmentManager()).onMenuItemClickListener);
        adapter = new NewsAdapter(getContext());
        newsList.setAdapter(adapter);
        getNews();
//        Log.e(LOG_NAME, "getNews()");
        return v;
    }

    private void initRecyclerView() {
        newsList = v.findViewById(R.id.NewsList);
        newsList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEWS_CREATE_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {

            }
        }
    }

    private void getNews() {
        database.getReference(DATABASE_NEWS_PATH)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        NewsFirebaseItem item = dataSnapshot.getValue(NewsFirebaseItem.class);
                        adapter.addItem(item);
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