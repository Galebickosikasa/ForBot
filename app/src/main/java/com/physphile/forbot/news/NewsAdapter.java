package com.physphile.forbot.news;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.physphile.forbot.MainActivity;
import com.physphile.forbot.R;

import java.util.ArrayList;
import java.util.List;

import static com.physphile.forbot.Constants.NEWS_PAGE_ACTIVITY_PATH;
import static java.lang.Math.max;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    public static int mx;
    public List<NewsFirebaseItem> newsList = new ArrayList<>();
    private Context context;

    public NewsAdapter(Context _context) {
        this.context = _context;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);
        ImageView iw = view.findViewById(R.id.NewsTitleImage);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(parent.getWidth(),
                parent.getWidth() * 10 / 16);
        iw.setLayoutParams(lp);
        return new NewsViewHolder(view);
    }

    public void addItem(NewsFirebaseItem item) {
        newsList.add(0, item);
        notifyDataSetChanged();
        mx = max(item.getNumber(), mx);
    }

    public void clearItems() {
        newsList.clear();
        notifyDataSetChanged();
    }

    public List<NewsFirebaseItem> getArrayList() {
        return newsList;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.bind(newsList.get(position));
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    private void itemLongClick(int position) {
        NewsLongTapDialog profileDialog = new NewsLongTapDialog();
        profileDialog.show(((MainActivity) context).getSupportFragmentManager(), "NewsLongTapDialog");
    }

    private void itemClick(int position) {
        Intent intent = new Intent(NEWS_PAGE_ACTIVITY_PATH);
        intent.putExtra("newsTitle", newsList.get(position).getTitle());
        intent.putExtra("newsText", newsList.get(position).getText());
        intent.putExtra("newsDate", newsList.get(position).getDate());
        intent.putExtra("newsAuthor", newsList.get(position).getAuthor());
        intent.putExtra("newsTitleImageUri", newsList.get(position).getUri());
        intent.putExtra("newsNumber", newsList.get(position).getNumber().toString());
        context.startActivity(intent);

    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView newsTitle;
        private ImageView newsTitleImage;
        private TextView newsText;
        private TextView newsAuthor;
        private TextView newsDate;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            newsTitleImage = itemView.findViewById(R.id.NewsTitleImage);
            newsAuthor = itemView.findViewById(R.id.newsAuthor);
            newsDate = itemView.findViewById(R.id.newsDate);
            newsText = itemView.findViewById(R.id.newsText);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(NewsFirebaseItem item) {
            newsTitle.setText(item.getTitle());
            newsText.setText(item.getText());
            newsAuthor.setText(item.getAuthor());
            newsDate.setText(item.getDate());
            Glide.with(itemView.getContext())
                    .load(item.getUri())
                    .into(newsTitleImage);
            newsTitleImage.setVisibility(item.getUri() != null ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                itemClick(position);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                itemLongClick(position);
            }
            return true;
        }
    }
}