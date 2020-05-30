package com.physphile.forbot;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.physphile.forbot.Constants.DATABASE_NEWS_PATH;
import static com.physphile.forbot.Constants.MIPT_IMAGE_URI;
import static com.physphile.forbot.Constants.MOSH_INF_IMAGE_URI;
import static com.physphile.forbot.Constants.MOSH_PHYS_IMAGE_URI;

public class Parser {
    private Context context;
    private HashMap<String, Long> secret_keys;
    private HashMap<String, Integer> months = new HashMap<String, Integer>() {
        {
            put ("янв", 1);
            put ("фев", 2);
            put ("мар", 3);
            put ("апр", 4);
            put ("мая", 5);
            put ("июн", 6);
            put ("июл", 7);
            put ("авг", 8);
            put ("сен", 9);
            put ("окт", 10);
            put ("ноя", 11);
            put ("дек", 12);
            put ("января", 1);
            put ("февраля", 2);
            put ("марта", 3);
            put ("апреля", 4);
            put ("июня", 6);
            put ("июля", 7);
            put ("августа", 8);
            put ("сентября", 9);
            put ("октября", 10);
            put ("ноября", 11);
            put ("декабря", 12);
        }
    };
    private Long ns = 97L;
    private Long mod = 1148822869L;

    private List<NewsFirebaseItem> parseMIPT () throws IOException {
        Document doc1 = Jsoup.connect("https://olymp.mipt.ru").get();
        Elements href = doc1.getElementsByAttributeValue("class", "news-item");
        final List<NewsFirebaseItem> newsList = new ArrayList<>();
        boolean flag = true;

        for (Element hrefElem: href) {
            Element element = hrefElem.child(0);
            String uri = element.attr("href");
            try {
                Document doc = Jsoup.connect("https://olymp.mipt.ru" + uri).get();

                Elements Date = doc.getElementsByAttributeValue("class", "news-date");
                String date = Date.first().getElementsByTag("span").text();

                Elements Title = doc.getElementsByAttributeValue("class", "news-title");
                String title = Title.first().text();

                Elements Content = doc.getElementsByAttributeValue("class", "news-content");
                String text = Content.first().text();

                SharedPreferences sp = context.getSharedPreferences("MxValue", Context.MODE_PRIVATE);
                int num = sp.getInt("mx", 0) + 1;

                NewsFirebaseItem item = new NewsFirebaseItem(title, MIPT_IMAGE_URI, text, "", date, num, 3);

                if (secret_keys.containsKey("MIPT")){
                    if (getKey(item) == secret_keys.get("MIPT")) break;
                }
                else if (!secret_keys.containsKey("MIPT") || flag) {
                    FirebaseDatabase.getInstance().getReference("/Secret_keys/MIPT").setValue(getKey(item));
                    secret_keys.put ("MIPT", getKey(item));
                    flag = false;
                }
                newsList.add (item);

                sp.edit().putInt("mx", num).apply();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newsList;
    }

    private List<NewsFirebaseItem> parseMOSH_PHYS () throws IOException {
        Document doc1 = Jsoup.connect("http://mosphys.olimpiada.ru/").get ();
        Elements href = doc1.getElementsByAttributeValue("class", "data_news");
        final List<NewsFirebaseItem> newsList = new ArrayList<>();

        Element element = href.first().child(1);
        String uri = element.attr("href");
//        Log.e("LOG_TAG", uri);

        int N = getNumFromUri(uri);

        uri = "news/";

        for (int k = 0; k < 50; ++k) {
            Document doc = Jsoup.connect("http://mosphys.olimpiada.ru/" + uri + (N - k)).get();
            Elements Title = doc.getElementsByClass("headlineM");

            String title = Title.first().ownText();
//            Log.e("LOG_TAG", title);
            String date = doc.getElementsByAttributeValue("class", "data_news").first().text();
            date = getDateFromElement(date);
//            Log.e("LOG_TAG", date);
            Elements next = doc.select("div.mainblock");
            String[] t = next.text().split(" ");

            int i = 0;
            for (int j = 0; j < t.length; ++j) {
                String x = t[j];
                if (x.equals(date)) {
                    i = j + 1;
                    break;
                }
            }
            String text = "";
            for (; i < t.length; ++i) {
                text = text.concat(t[i] + " ");
            }

            if (title.isEmpty() || text.isEmpty()) continue;

            SharedPreferences sp = context.getSharedPreferences("MxValue", Context.MODE_PRIVATE);
            int num = sp.getInt("mx", 0) + 1;

            NewsFirebaseItem item = new NewsFirebaseItem(title, MOSH_PHYS_IMAGE_URI, text, "", date, num, 1);
            if (secret_keys.containsKey("MOSH_PHYS") && item.getCoolDate() == secret_keys.get("MOSH_PHYS")) break;
            else if (!secret_keys.containsKey("MOSH_PHYS") || secret_keys.get("MOSH_PHYS") > item.getCoolDate()) {
                FirebaseDatabase.getInstance().getReference("/Secret_keys/MOSH_PHYS").setValue(item.getCoolDate());
                secret_keys.put("MOSH_PHYS", item.getCoolDate());
            }
            newsList.add(item);

            sp.edit().putInt("mx", num).apply();
        }
        return newsList;

    }

    private Long getKey (NewsFirebaseItem item) {
        Long ans = 0L;
        String[] goo = item.getText().split(" ");
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < goo[i].length(); ++j) {
                ans += (goo[i].charAt(j));
                ans *= ns;
                ans %= mod;
            }
        }
        for (int i = goo.length - 3; i < goo.length; ++i) {
            for (int j = 0; j < goo[i].length(); ++j) {
                ans += (goo[i].charAt(j));
                ans *= ns;
                ans %= mod;
            }
        }
        return ans;
    }

    private String getDateFromElement(String s) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == ' ') {
                break;
            } else {
                ret.append(s.charAt(i));
            }
        }
        return ret.toString();
    }

    private int getNumFromUri(String s) {
        StringBuilder ans = new StringBuilder();
        while (s.charAt(s.length() - 1) != '/') {
            ans.append(s.charAt(s.length() - 1));
            s = s.substring(0, s.length() - 1);
        }
        ans = new StringBuilder(new StringBuilder(ans.toString()).reverse().toString());
        return Integer.parseInt(ans.toString());
    }

    private List<NewsFirebaseItem> parseMOSH_INF() throws IOException {
        Document doc1 = Jsoup.connect("http://mos-inf.olimpiada.ru").get ();
        Elements href = doc1.getElementsByAttributeValue("class", "data_news");
        final List<NewsFirebaseItem> newsList = new ArrayList<>();

        Element element = href.first().child(1);
        String uri = element.attr("href");

        int N = getNumFromUri(uri);

        uri = "news/";

        for (int k = 0; k < 50; ++k) {
            Document doc = Jsoup.connect("http://mos-inf.olimpiada.ru/" + uri + (N - k)).get ();
            Elements Title = doc.getElementsByClass("headlineM");

            String title = Title.first().ownText();

            String date = doc.getElementsByAttributeValue("class", "data_news").last().text();
            Elements next = doc.select("div.mainblock");
            String[] t = next.text().split(" ");

            int i = 0;
            for (int j = 0; j < t.length; ++j) {
                String x = t[j];
                if (x.equals(date)) {
                    i = j + 1;
                    break;
                }
            }
            String text = "";
            for (; i < t.length; ++i) {
                text = text.concat(t[i] + " ");
            }

            if (title.isEmpty() || text.isEmpty()) continue;

            SharedPreferences sp = context.getSharedPreferences("MxValue", Context.MODE_PRIVATE);
            int num = sp.getInt("mx", 0) + 1;

            NewsFirebaseItem item = new NewsFirebaseItem(title, MOSH_INF_IMAGE_URI, text, "", date, num, 16);
            if (secret_keys.containsKey("MOSH_INF") && item.getCoolDate() == secret_keys.get("MOSH_INF")) break;
            else if (!secret_keys.containsKey("MOSH_INF") || secret_keys.get("MOSH_INF") > item.getCoolDate()) {
                FirebaseDatabase.getInstance().getReference("/Secret_keys/MOSH_INF").setValue(item.getCoolDate());
                secret_keys.put ("MOSH_INF", item.getCoolDate());
            }
            newsList.add (item);

            sp.edit().putInt("mx", num).apply();
        }
        return newsList;
    }

    private void parseVP () throws IOException {
        String URI = "https://olymp.hse.ru/mmo/news/";

        for (int k = 1; k <= 10; ++k) {
            Document doc1 = Jsoup.connect(URI + "page" + k + ".html").get();
            Elements href = doc1.getElementsByAttributeValue("class", "first_child");
            for (Element element: href) {
                String uri = element.child(0).attr("href");
                String title = "", date = "", text = "";
                if (uri.charAt(0) == '/') {
                    uri = "https:" + uri;
                    Document doc = Jsoup.connect(uri).get();
                    title = doc.getElementsByAttributeValue("class", "post-title").text();
                    date = doc.getElementsByAttributeValue("class", "post-meta__day").first().text() + "." +
                            months.get(doc.getElementsByAttributeValue("class", "post-meta__month").first().text()) + "." +
                            doc.getElementsByAttributeValue("class", "post-meta__year").first().text();
                    text = doc.getElementsByAttributeValue("class", "post__text").text();
                } else {
                    Document doc = Jsoup.connect(uri).get ();
                    title = doc.getElementsByAttributeValue("class", "post_single").text();
                    text = doc.getElementsByAttributeValue("class", "post__text").text();
                    String[] Date = doc.getElementsByAttributeValue("class", "small fa-grey").text().split(" ");
                    date = Date[0] + "." + months.get(Date[1]) + ".";
                    if (Date.length == 3) date += Date[2];
                    else date += "" + Calendar.getInstance().get(Calendar.YEAR);

                }


                Log.e ("kek", uri);
                Log.e("kek", date);
            }
        }

    }

    private void parseAll () {
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
               List<NewsFirebaseItem> list = new ArrayList<>();
                try {
                    list.addAll(parseMIPT());
                    list.addAll(parseMOSH_INF());
                    list.addAll(parseMOSH_PHYS());
                    parseVP();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e ("kek", e.toString());
                }
                for (NewsFirebaseItem x: list){
                    FirebaseDatabase.getInstance().getReference(DATABASE_NEWS_PATH + x.getNumber()).setValue(x);
                }
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


