package com.physphile.forbot.Feed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
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
import com.physphile.forbot.NewsLongTapDialog;
import com.physphile.forbot.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.physphile.forbot.Constants.LOG_NAME;
import static com.physphile.forbot.Constants.NEWS_PAGE_ACTIVITY_PATH;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<NewsFirebaseItem> newsList = new ArrayList<>();
    private Context context;
    private TextView newsTitle;
    private ImageView newsTitleImage;
    private TextView newsText;
    private TextView newsAuthor;
    private TextView newsDate;

    NewsAdapter(Context _context){
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
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.bind(newsList.get(position));
    }

    @Override
    public int getItemCount() {  return newsList.size(); }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{


        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.NewsTitle);
            newsTitleImage = itemView.findViewById(R.id.NewsTitleImage);
            newsAuthor = itemView.findViewById(R.id.newsAuthor);
            newsDate = itemView.findViewById(R.id.newsDate);
            newsText = itemView.findViewById(R.id.newsText);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        public void bind(NewsFirebaseItem item){
            newsTitle.setText(item.getTitle());
            Glide.with(itemView.getContext())
                    .load(item.getUri())
                    .into(newsTitleImage);
            newsText.setText(item.getText());
            newsAuthor.setText(item.getAuthor());
            newsDate.setText(item.getDate());
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
    private void itemLongClick(int position){
        NewsLongTapDialog profileDialog = new NewsLongTapDialog();
        profileDialog.show(((MainActivity)context).getSupportFragmentManager(), "NewsLongTapDialog");

    }
    private void itemClick(int position){
        Log.e(LOG_NAME, position + "");
        Intent intent = new Intent(NEWS_PAGE_ACTIVITY_PATH);
        intent.putExtra("newsTitle", newsList.get(position).getTitle());
        intent.putExtra("newsText", newsList.get(position).getText());
        intent.putExtra("newsDate", newsDate.getText().toString());
        intent.putExtra("newsAuthor", newsAuthor.getText().toString());
        intent.putExtra("newsTitleImageUri", newsList.get(position).getUri());
        context.startActivity(intent);
    }
}