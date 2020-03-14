package com.physphile.forbot;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NewsAdapter extends ArrayAdapter<NewsFeedItem> {

    private Context mContext;
    private int mResource;

    NewsAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String title = getItem(position).getTitle();
        Bitmap image = getItem(position).getNewsTitleImage();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate (mResource, parent, false);

        TextView tvTitle = convertView.findViewById(R.id.NewsTitle);
        ImageView iwImage = convertView.findViewById(R.id.NewsTitleImage);

        tvTitle.setText(title);
        iwImage.setImageBitmap(image);

        return convertView;
    }
}