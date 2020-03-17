package com.physphile.forbot.Feed;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.physphile.forbot.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.physphile.forbot.Constants.LOG_NAME;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<NewsFirebaseItem> newsList = new ArrayList<>();

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);
        ImageView iw = view.findViewById(R.id.NewsTitleImage);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(parent.getWidth(),
                parent.getWidth() * 10 / 16);
        Log.e(LOG_NAME, String.valueOf(parent.getWidth()) + " " + String.valueOf(lp.height));
        iw.setLayoutParams(lp);
        return new NewsViewHolder(view);
    }

    public void setItems(NewsFirebaseItem item){
        Log.e(LOG_NAME, "setItems()");
        newsList.add(item);
        notifyDataSetChanged();
    }
    public void clearItems(){
        newsList.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.bind(newsList.get(position));
    }

    @Override
    public int getItemCount() {  return newsList.size(); }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView newsTitle;
        private ImageView newsTitleImage;

        public void bind(NewsFirebaseItem item){
            newsTitle.setText(item.getTitle());
            Glide.with(itemView.getContext())
                    .load(item.getUri())
                    .into(newsTitleImage);
        }

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.NewsTitle);
            newsTitleImage = itemView.findViewById(R.id.NewsTitleImage);
        }
    }
}