package com.example.mad_project.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.R;
import com.example.mad_project.Shirt;

import java.util.ArrayList;
import java.util.List;

public class ShirtAdapter extends RecyclerView.Adapter<ShirtAdapter.ShirtViewHolder> {

    private final List<Shirt> originalList; // full list for reference
    private final List<Shirt> displayList;  // filtered list to show

    public ShirtAdapter(List<Shirt> shirtList) {
        this.originalList = new ArrayList<>(shirtList); // store original
        this.displayList = new ArrayList<>(shirtList);  // current displayed
    }

    @NonNull
    @Override
    public ShirtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shirt, parent, false);
        return new ShirtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShirtViewHolder holder, int position) {
        Shirt shirt = displayList.get(position);
        holder.shirtName.setText(shirt.getName());
        holder.shirtPrice.setText("$" + shirt.getPrice());
        holder.shirtImage.setImageResource(shirt.getImageResId());
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    // Update filtered list
    public void updateList(List<Shirt> newList) {
        displayList.clear();
        displayList.addAll(newList);
        notifyDataSetChanged();
    }

    static class ShirtViewHolder extends RecyclerView.ViewHolder {
        ImageView shirtImage;
        TextView shirtName, shirtPrice;

        public ShirtViewHolder(@NonNull View itemView) {
            super(itemView);
            shirtImage = itemView.findViewById(R.id.shirtImage);
            shirtName = itemView.findViewById(R.id.shirtName);
            shirtPrice = itemView.findViewById(R.id.shirtPrice);
        }
    }
}
