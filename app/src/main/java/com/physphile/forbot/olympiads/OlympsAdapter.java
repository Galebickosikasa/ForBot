package com.physphile.forbot.olympiads;

import android.content.Context;
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
import java.util.HashMap;
import java.util.List;

public class OlympsAdapter extends RecyclerView.Adapter<OlympsAdapter.OlympsViewHolder> {
    List<OlympsListItem> olympsList = new ArrayList<>();
    HashMap<Integer, Integer> olymps = new HashMap<>();
    private Context context;
    private OnOlympsClick onOlympsClick;

    public interface OnOlympsClick {
        void onOlympsClick (int position);
    }

    public OlympsAdapter(Context context) {
        this.context = context;
        this.onOlympsClick = (OnOlympsClick) context;
    }

    @NonNull
    @Override
    public OlympsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.olymps_item, parent, false);
        return new OlympsViewHolder(view);
    }

    public void addItems(OlympsListItem item) {
        if (olymps.containsKey(item.getNum())) return;
        olympsList.add(item);
        olymps.put (item.getNum(), 1);
        notifyItemChanged(getItemCount() - 1);
    }

    public void clearItems() {
        olympsList.clear ();
        olymps.clear ();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull OlympsViewHolder holder, int position) {
        holder.bind(olympsList.get(position));
    }

    @Override
    public int getItemCount() {
        return olympsList.size();
    }

    class OlympsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView olympName;
        private TextView olympLevel;
        private ImageView olympImage;

        public OlympsViewHolder(@NonNull View itemView) {
            super(itemView);
            olympName = itemView.findViewById(R.id.olympName);
            olympLevel = itemView.findViewById(R.id.olympLevel);
            olympImage = itemView.findViewById(R.id.olympImage);
            itemView.setOnClickListener(this);
        }

        public void bind(OlympsListItem item) {
            olympName.setText(item.getName());
            olympLevel.setText(item.getLevel());
            Glide.with(itemView.getContext())
                    .load(item.getUri())
                    .into(olympImage);
            olympImage.setVisibility(item.getUri() != null ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                onOlympsClick.onOlympsClick (position);
            }
        }
    }
}
