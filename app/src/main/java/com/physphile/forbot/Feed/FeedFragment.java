package com.physphile.forbot.Feed;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.physphile.forbot.MainActivity;
import com.physphile.forbot.Feed.NewsFirebaseItem;
import com.physphile.forbot.ProfileDialogFragment;
import com.physphile.forbot.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import static android.app.Activity.RESULT_OK;
import static com.physphile.forbot.Constants.ARTEM_ADMIN_UID;
import static com.physphile.forbot.Constants.DATABASE_NEWS_PATH;
import static com.physphile.forbot.Constants.FILE_PREFIX;
import static com.physphile.forbot.Constants.FRAGMENT_DIALOG_PROFILE_TAG;
import static com.physphile.forbot.Constants.GLEB_ADMIN_ID;
import static com.physphile.forbot.Constants.INTENT_EXTRA_NEWS_TITLE;
import static com.physphile.forbot.Constants.INTENT_EXTRA_NEWS_TITLE_IMAGE;
import static com.physphile.forbot.Constants.NEWS_CREATE_ACTIVITY_CODE;
import static com.physphile.forbot.Constants.NEWS_CREATE_ACTIVITY_PATH;
import static com.physphile.forbot.Constants.STORAGE_NEWS_IMAGE_PATH;
import static com.physphile.forbot.Constants.TEMP_FILE;

public class FeedFragment extends Fragment {
    private NewsAdapter adapter;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase database;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_feed, container, false);
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Toolbar toolbar = root.findViewById(R.id.feedToolbar);
        if (user != null){
            if ((user.getUid()).equals(ARTEM_ADMIN_UID) || (user.getUid()).equals(GLEB_ADMIN_ID)) {
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.admin_toolbar_menu);
            } else { toolbar.inflateMenu(R.menu.default_toolbar_menu); }
            getNews();
        }
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        adapter = new NewsAdapter(getContext(), R.layout.news_item);
        ListView newsList = root.findViewById(R.id.NewsList);
        newsList.setAdapter(adapter);
        return root;
    }

    static FeedFragment newInstance() { return new FeedFragment(); }

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.profile:
                    DialogFragment profileDialog = new ProfileDialogFragment();
                    profileDialog.show(getChildFragmentManager(), FRAGMENT_DIALOG_PROFILE_TAG);
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
                NewsFeedItem nfi = new NewsFeedItem();
                nfi.setTitle(data.getStringExtra(INTENT_EXTRA_NEWS_TITLE));
                try {
                    nfi.setNewsTitleImage(((MainActivity) getActivity()).readFile(INTENT_EXTRA_NEWS_TITLE_IMAGE));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getNews(){
        DatabaseReference newRef = database.getReference(DATABASE_NEWS_PATH);
        final int[] n = {0};
        newRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final NewsFirebaseItem item = dataSnapshot.getValue(NewsFirebaseItem.class);
                try {
                    final File localFile = File.createTempFile(FILE_PREFIX, TEMP_FILE);
                    storageReference = storage.getReference(STORAGE_NEWS_IMAGE_PATH + n[0]);
                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener< FileDownloadTask.TaskSnapshot >() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            adapter.insert(new NewsFeedItem(item.getTitle(), bitmap), 0);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ++n[0];
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
}