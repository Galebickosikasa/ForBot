package com.physphile.forbot.Calendar;

import android.content.Context;
import android.content.Intent;
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
import com.physphile.forbot.R;

import java.util.ArrayList;
import java.util.List;

import static com.physphile.forbot.Constants.OLYMP_PAGE_ACTIVITY_PATH;

public class OlympsAdapter extends RecyclerView.Adapter<OlympsAdapter.OlympsViewHolder> {
    private List<OlympsListItem> olympsList = new ArrayList<>();
    private Context context;

    public OlympsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public OlympsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.olymps_item, parent, false);
        ImageView iv = view.findViewById(R.id.olympTitleImage);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(parent.getWidth(),
                parent.getWidth() * 10 / 16);
        iv.setLayoutParams(lp);
        return new OlympsViewHolder(view);
    }

    public void addItems(OlympsListItem item) {
        Log.e ("kek", "start");
        olympsList.add(0, item);
        notifyItemChanged(getItemCount() - 1);
    }

    public void clearItems() {
        olympsList.clear();
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

    void olympClick (int pos) {
        Intent intent = new Intent(OLYMP_PAGE_ACTIVITY_PATH);
        intent.putExtra("olympName", olympsList.get(pos).getName());
        intent.putExtra("olympDate", olympsList.get(pos).getDate());
        intent.putExtra("olympLevel", olympsList.get(pos).getLevel());
        intent.putExtra("olympText", olympsList.get(pos).getText());
        intent.putExtra("olympUri", olympsList.get(pos).getUri());
        context.startActivity(intent);
    }

    class OlympsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView olympName;
        private TextView olympLevel;
        private TextView olympDate;
        private TextView olympText;
        private ImageView olympImage;

        public OlympsViewHolder(@NonNull View itemView) {
            super(itemView);
            olympName = itemView.findViewById(R.id.olympName);
            olympLevel = itemView.findViewById(R.id.olympLevel);
//            olympDate = itemView.findViewById(R.id.olympDate);
//            olympText = itemView.findViewById(R.id.olympText);
//            olympImage = itemView.findViewById(R.id.olympTitleImage);
            itemView.setOnClickListener(this);
        }

        public void bind(OlympsListItem item) {
            olympName.setText(item.getName());
            olympLevel.setText(item.getLevel());
//            olympDate.setText(item.getDate());
//            olympText.setText(item.getText());
//            Glide.with(itemView.getContext())
//                    .load(item.getUri())
//                    .into(olympImage);
//            olympImage.setVisibility(item.getUri() != null ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                olympClick (pos);
            }
        }
    }
}
