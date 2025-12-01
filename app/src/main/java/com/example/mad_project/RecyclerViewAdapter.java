
package com.example.mad_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
//import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecommendedViewHolder> {

    private final List<Integer> item;

    public RecyclerViewAdapter(List<Integer> recommendedItemList) {
        this.item = recommendedItemList;
    }

    @NonNull
    @Override
    public RecommendedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_layout, parent, false);
        return new RecommendedViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecommendedViewHolder holder, int position) {
        holder.imageView.setImageResource(item.get(position));
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public static class RecommendedViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
//        TextView nameView;
//        TextView priceView;

        public RecommendedViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
