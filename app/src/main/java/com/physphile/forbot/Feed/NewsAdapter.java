package com.physphile.forbot.Feed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.physphile.forbot.R;
import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<NewsFirebaseItem> newsList = new ArrayList<>();

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    public void setItems(NewsFirebaseItem item){
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