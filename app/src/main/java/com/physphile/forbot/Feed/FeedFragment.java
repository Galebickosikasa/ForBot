package com.physphile.forbot.Feed;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.physphile.forbot.ProfileDialogFragment;
import com.physphile.forbot.R;
import static android.app.Activity.RESULT_OK;
import static com.physphile.forbot.Constants.ARTEM_ADMIN_UID;
import static com.physphile.forbot.Constants.AUTH_ACTIVITY_CODE;
import static com.physphile.forbot.Constants.AUTH_ACTIVITY_PATH;
import static com.physphile.forbot.Constants.DATABASE_NEWS_PATH;
import static com.physphile.forbot.Constants.FRAGMENT_DIALOG_PROFILE_TAG;
import static com.physphile.forbot.Constants.GLEB_ADMIN_ID;
import static com.physphile.forbot.Constants.LOG_NAME;
import static com.physphile.forbot.Constants.NEWS_CREATE_ACTIVITY_CODE;
import static com.physphile.forbot.Constants.NEWS_CREATE_ACTIVITY_PATH;

public class FeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private NewsAdapter adapter;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private RecyclerView newsList;
    private View v;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getNews();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_feed, container, false);
        initRecyclerView();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Toolbar toolbar = v.findViewById(R.id.feedToolbar);
        if (user != null){
            if ((user.getUid()).equals(ARTEM_ADMIN_UID) || (user.getUid()).equals(GLEB_ADMIN_ID)) {
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.admin_toolbar_menu);
            } else { toolbar.inflateMenu(R.menu.default_toolbar_menu); }
        }
        mSwipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        adapter = new NewsAdapter(getContext());
        newsList.setAdapter(adapter);
        return v;
    }

    private void initRecyclerView() {
        newsList = v.findViewById(R.id.NewsList);
        newsList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public static FeedFragment newInstance() { return new FeedFragment(); }

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.profile:
                    if (FirebaseAuth.getInstance().getCurrentUser() != null){
                        DialogFragment profileDialog = new ProfileDialogFragment();
                        profileDialog.show(getChildFragmentManager(), FRAGMENT_DIALOG_PROFILE_TAG);
                    } else {
                        startActivity(new Intent(AUTH_ACTIVITY_PATH));
                    }
                    break;
                case R.id.createNews:
                    startActivityForResult(new Intent(NEWS_CREATE_ACTIVITY_PATH), NEWS_CREATE_ACTIVITY_CODE);
                    break;
            }
            return false;
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEWS_CREATE_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {

            }
        }
    }

    private void getNews(){
        Log.e(LOG_NAME, DATABASE_NEWS_PATH);
        database.getReference(DATABASE_NEWS_PATH)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        NewsFirebaseItem nfi = dataSnapshot.getValue(NewsFirebaseItem.class);
                        adapter.setItems(nfi);
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}

                });
    }

    @Override
    public void onRefresh() {
        adapter.clearItems();
        getNews();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}