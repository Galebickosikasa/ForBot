package com.physphile.forbot.news;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.physphile.forbot.R;
import com.physphile.forbot.profile.ProfileMenuDialog;

import java.util.HashMap;

import static com.physphile.forbot.Constants.AUTH_ACTIVITY_PATH;
import static com.physphile.forbot.Constants.DATABASE_NEWS_PATH;
import static com.physphile.forbot.Constants.FRAGMENT_DIALOG_PROFILE_TAG;
import static com.physphile.forbot.Constants.NEWS_CREATE_ACTIVITY_PATH;

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
    private HashMap<String, String> admins;
    private ViewPager viewPager;
    private CheckBox phis, math, rus, lit, inf, chem, his, ast;

    private int msk = 0; // маска предметов

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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //инициализация фрагмента
        v = inflater.inflate(R.layout.feed_fragment_backdrop, container, false);

        // инициализация предметов
        phis = v.findViewById(R.id.physicsCheck); // 0
        math = v.findViewById(R.id.mathCheck); // 1
        rus = v.findViewById(R.id.russianCheck); // 2
        lit = v.findViewById(R.id.literatureCheck); // 3
        inf = v.findViewById(R.id.informaticsCheck); // 4
        chem = v.findViewById(R.id.chemistryCheck); // 5
        his = v.findViewById(R.id.historyCheck); // 6
        ast = v.findViewById(R.id.astronomyCheck); // 7

        phis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                msk ^= (1 << 0);
                getNews();
            }
        });

        math.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                msk ^= (1 << 1);
                getNews();
            }
        });

        rus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                msk ^= (1 << 2);
                getNews();
            }
        });

        lit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                msk ^= (1 << 3);
                getNews();
            }
        });

        inf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                msk ^= (1 << 4);
                getNews();
            }
        });

        chem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                msk ^= (1 << 5);
                getNews();
            }
        });

        his.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                msk ^= (1 << 6);
                getNews();
            }
        });

        ast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                msk ^= (1 << 7);
                getNews();
            }
        });

        //инициализация переменных
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //инициализация View-элементов
        initRecyclerView();
        final Toolbar toolbar = v.findViewById(R.id.feedToolbar);
        mSwipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);

        //заполнение View-элементов
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
        // admins initialize

        FirebaseDatabase.getInstance().getReference("/admins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                admins = (HashMap<String, String>) dataSnapshot.getValue();
                mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser _user = firebaseAuth.getCurrentUser();
                        if (_user != null && admins.containsValue(_user.getUid())) {
                            toolbar.getMenu().clear();
                            toolbar.inflateMenu(R.menu.admin_toolbar_menu);
                        } else {
                            toolbar.getMenu().clear();
                            toolbar.inflateMenu(R.menu.default_toolbar_menu);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return v;
    }

    private void initRecyclerView() {
        newsList = v.findViewById(R.id.NewsList);
        newsList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private boolean isAdmin(String uid) {
        for (int i = 0; i < admins.size(); ++i) {
            if (admins.get(i).equals(uid)) {
                return true;
            }
        }
        return false;
    }

    private void getNews() {
        adapter.clearItems();
        final DatabaseReference ref = database.getReference(DATABASE_NEWS_PATH);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NewsFirebaseItem item = dataSnapshot.getValue(NewsFirebaseItem.class);
                if (item != null) {
                    NewsAdapter.setMx(item.getNumber());
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (msk == 0) {
                        adapter.addItem(item);
                    } else if ((item.getMask() & msk) != 0) {
                        adapter.addItem(item);
                    }
                }
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