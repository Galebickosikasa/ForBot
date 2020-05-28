package com.physphile.forbot;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.physphile.forbot.news.NewsFirebaseItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.physphile.forbot.Constants.DATABASE_NEWS_PATH;
import static com.physphile.forbot.Constants.MIPT_IMAGE_URI;

public class Parser {
    Context context;
    HashMap<String, Long> secret_keys;

    private List<NewsFirebaseItem> parseMIPT () throws IOException {
        Document doc1 = Jsoup.connect("https://olymp.mipt.ru").get();
        Elements href = doc1.getElementsByAttributeValue("class", "news-item");
        final List<NewsFirebaseItem> newsList = new ArrayList<>();

        for (Element  hrefElem: href) {
            Element element = hrefElem.child(0);
            String uri = element.attr("href");
            try {
                Document doc = Jsoup.connect("https://olymp.mipt.ru" + uri).get ();
                Elements Date = doc.getElementsByAttributeValue("class", "news-date");
                String date = Date.first().getElementsByTag("span").text();
                Elements Title = doc.getElementsByAttributeValue("class", "news-title");
                String title = Title.first().text();
                Elements Content = doc.getElementsByAttributeValue("class", "news-content");
                String text = Content.first().text();
                SharedPreferences sp = context.getSharedPreferences("MxValue", Context.MODE_PRIVATE);
                int num = sp.getInt("mx", 0) + 1;
//                Log.e ("kek", "get");
                NewsFirebaseItem item = new NewsFirebaseItem(title, MIPT_IMAGE_URI, text, "", date, num, 0);
                if (secret_keys.containsKey("MIPT") && item.getCoolDate() == secret_keys.get("MIPT")) break;
                else if (!secret_keys.containsKey("MIPT") || secret_keys.get("MIPT") > item.getCoolDate()) {
                    FirebaseDatabase.getInstance().getReference("/Secret_keys/MIPT").setValue(item.getCoolDate());
                    secret_keys.put ("MIPT", item.getCoolDate());
                }
                newsList.add (item);
//                Log.e ("kek", "upd");
                sp.edit().putInt("mx", num).apply();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return newsList;
    }

    private void parseAll () {
        final List<NewsFirebaseItem> kek = new ArrayList<>();
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                List<NewsFirebaseItem> a = null;
                try {
                    a = parseMIPT();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (NewsFirebaseItem x: a) {
                    FirebaseDatabase.getInstance().getReference(DATABASE_NEWS_PATH + x.getNumber()).setValue(x);
                }
                kek.addAll(a);
            }
        });
        t.start();
    }

    public void addToFirebase () {
        FirebaseDatabase.getInstance().getReference("/Secret_keys").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                secret_keys = (HashMap<String, Long>) dataSnapshot.getValue();
                parseAll();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public Parser(Context context) {
        this.context = context;
    }
}


