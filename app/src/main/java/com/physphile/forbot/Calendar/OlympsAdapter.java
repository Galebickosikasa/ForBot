package com.physphile.forbot.Calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.physphile.forbot.R;
import java.util.ArrayList;
import java.util.List;

public class OlympsAdapter extends RecyclerView.Adapter<OlympsAdapter.OlympsViewHolder> {
    private List<OlympsListItem> olympsList = new ArrayList<>();

    @NonNull
    @Override
    public OlympsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.olymps_item, parent, false);
        return new OlympsViewHolder(view);
    }

    public void setItems(OlympsListItem item){
        olympsList.add(item);
        notifyDataSetChanged();
    }
    public void clearItems(){
        olympsList.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull OlympsViewHolder holder, int position) {
        holder.bind(olympsList.get(position));
    }

    @Override
    public int getItemCount() {  return olympsList.size(); }

    class OlympsViewHolder extends RecyclerView.ViewHolder {
        private TextView olympName;
        private TextView olympLevel;

        public void bind(OlympsListItem item){
            olympName.setText(item.getName());
            olympLevel.setText(item.getLevel());
        }

        public OlympsViewHolder(@NonNull View itemView) {
            super(itemView);
            olympName = itemView.findViewById(R.id.olympName);
            olympLevel = itemView.findViewById(R.id.olympLevel);
        }
    }
}
