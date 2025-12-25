package com.example.mad_project.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mad_project.R;
import com.example.mad_project.db.BagItem;

import java.util.List;

public class BagAdapter extends RecyclerView.Adapter<BagAdapter.BagViewHolder> {

    private final Context context;
    private final List<BagItem> bagItemList;

    public BagAdapter(Context context, List<BagItem> bagItemList) {
        this.context = context;
        this.bagItemList = bagItemList;
    }

    @NonNull
    @Override
    public BagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bag_item, parent, false);
        return new BagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BagViewHolder holder, int position) {
        BagItem bagItem = bagItemList.get(position);

        holder.productName.setText(bagItem.name);
        holder.productPrice.setText(bagItem.price);

        if (bagItem.imageUrl != null) {
            Glide.with(context).load(bagItem.imageUrl).into(holder.productImage);
        }
    }

    @Override
    public int getItemCount() {
        return bagItemList.size();
    }

    public static class BagViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;

        public BagViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.imageView);
            productName = itemView.findViewById(R.id.name);
            productPrice = itemView.findViewById(R.id.price);
        }
    }
}
