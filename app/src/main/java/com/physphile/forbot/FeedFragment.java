package com.physphile.forbot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

public class FeedFragment extends Fragment {

    private NewsAdapter adapter;
    private static String ARTEM_ADMIN_UID = "WIdrvU8LHNcltsf6l9dvONruekD2";
    private static String GLEB_ADMIN_ID = "usvB8zPjiCOvLLaZBKwlVVRkI1F2";
    private Toolbar toolbar;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ListView NewsList;

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_feed, container, false);

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        toolbar = root.findViewById(R.id.feedToolbar);
        if ((mAuth.getCurrentUser().getUid()).equals(ARTEM_ADMIN_UID) || (mAuth.getCurrentUser().getUid()).equals(GLEB_ADMIN_ID)) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.admin_toolbar_menu);
        } else {
            toolbar.inflateMenu(R.menu.default_toolbar_menu);
        }
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        adapter = new NewsAdapter(getContext(), R.layout.news_item);
        NewsList = root.findViewById(R.id.NewsList);
        NewsList.setAdapter(adapter);
        getNews();
        return root;
    }

    Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.profile:
                    DialogFragment profileDialog = new ProfileDialogFragment();
                    profileDialog.show(getFragmentManager(), "profileDialog");
                    break;
                case R.id.createNews:
                    startActivityForResult(new Intent("com.physphile.forbot.NewsCreateActivity"), 57);
                    break;

            }
            return false;
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 57:
                if (resultCode == RESULT_OK) {
                    NewsFeedItem nfi = new NewsFeedItem();
                    nfi.setTitle(data.getStringExtra("NewsTitle"));
                    try {
                        nfi.setNewsTitleImage(((MainActivity) getActivity()).readFile("NewsTitleImage"));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    adapter.add(nfi);
                }
                    break;
                }
        }

        private void getNews(){
            DatabaseReference newRef = database.getReference("news/");
            final int[] n = {0};
            Log.e("ARTEM", "Начало");
            newRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.e("ARTEM", "ЗДЕСЬ");
                    Log.e("ARTEM", n[0] + "");
                    final NewsFirebaseItem item = dataSnapshot.getValue(NewsFirebaseItem.class);

                    try {
                        final File localFile = File.createTempFile("Images", "bmp");
                        storageReference = storage.getReference("newsImages/" + n[0]);
                        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener< FileDownloadTask.TaskSnapshot >() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                adapter.insert(new NewsFeedItem(item.getTitle(), bitmap), 0);


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
//    private class DownloadImageTask extends AsyncTask<String, Void, Boolean> {
//        protected Boolean doInBackground(String... strings) {
//            DatabaseReference newRef = database.getReference("news/");
//            newRef.addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                    NewsFirebaseItem item = dataSnapshot.getValue(NewsFirebaseItem.class);
//                    Log.e(item.getTitle(), item.getUri());
//                    try {
//                        InputStream in = new java.net.URL(item.getUri()).openStream();
//                        Bitmap bitmap = BitmapFactory.decodeStream(in);
//                        adapter.add(new NewsFeedItem(item.getTitle(), bitmap));
//                    } catch (Exception e) {
//                        Log.e("Ошибк переaчи изображея", "getMessage");
//                        e.printStackTrace();
//                    }
//                }
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s){}
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {}
//            });
//            return true;
//        }
//    }
}