package com.physphile.forbot;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.physphile.forbot.news.NewsFirebaseItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.physphile.forbot.Constants.DATABASE_NEWS_PATH;
import static com.physphile.forbot.Constants.MIPT_IMAGE_URI;

public class Parser {
    Context context;

    public List<NewsFirebaseItem> parseMIPT () throws IOException {
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
                newsList.add (new NewsFirebaseItem (title, MIPT_IMAGE_URI, text, "", date, num, 0));
                Log.e ("kek", "upd");
                sp.edit().putInt("mx", num + 1).apply();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(uri);
        }
        return newsList;
    }

    public List<NewsFirebaseItem> parseAll () {
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
        return kek;
    }

    public void addToFirebase () throws IOException {
        List<NewsFirebaseItem> kek = parseAll();

    }

    public Parser(Context context) {
        this.context = context;
    }
}


