package com.physphile.forbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OlympAdapter extends ArrayAdapter<Item> {

    private Context mContext;
    private int mResource;

    public OlympAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        String level = getItem(position).getLevel();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate (mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.olympName);
        TextView tvLevel = (TextView) convertView.findViewById(R.id.olympLevel);

        tvName.setText(name);
        tvLevel.setText(level);

        return convertView;
    }
}