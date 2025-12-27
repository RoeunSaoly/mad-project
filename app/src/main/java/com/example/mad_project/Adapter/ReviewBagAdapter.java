package com.example.mad_project.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mad_project.R;
import com.example.mad_project.db.BagItem;
import com.example.mad_project.db.DatabaseClient;

import java.util.List;

public class ReviewBagAdapter extends RecyclerView.Adapter<ReviewBagAdapter.BagViewHolder> {

    private final Context context;
    private final List<BagItem> bagItemList;

    public ReviewBagAdapter(Context context, List<BagItem> bagItemList) {
        this.context = context;
        this.bagItemList = bagItemList;
    }

    @NonNull
    @Override
    public BagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reivew_bag_item, parent, false);
        return new BagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BagViewHolder holder, int position) {
        BagItem bagItem = bagItemList.get(position);

        holder.productName.setText(bagItem.name);
        holder.productPrice.setText(bagItem.price);
        holder.amount.setText(String.valueOf(bagItem.amount));


        if (bagItem.imageUrl != null) {
            Glide.with(context).load(bagItem.imageUrl).into(holder.productImage);
        }

        holder.increase.setOnClickListener(v -> {
            bagItem.amount++;
            holder.amount.setText(String.valueOf(bagItem.amount));
            new Thread(() -> DatabaseClient.getInstance(context).getAppDatabase().bagDao().update(bagItem)).start();
        });

        holder.decrease.setOnClickListener(v -> {
            if (bagItem.amount > 1) {
                bagItem.amount--;
                holder.amount.setText(String.valueOf(bagItem.amount));
                new Thread(() -> DatabaseClient.getInstance(context).getAppDatabase().bagDao().update(bagItem)).start();
            } else {
                bagItemList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, bagItemList.size());
                new Thread(() -> DatabaseClient.getInstance(context).getAppDatabase().bagDao().delete(bagItem)).start();
            }
        });
        holder.removeButton.setOnClickListener(v -> {
            bagItemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, bagItemList.size());
            new Thread(() -> DatabaseClient.getInstance(context).getAppDatabase().bagDao().delete(bagItem)).start();
        });
    }

    @Override
    public int getItemCount() {
        return bagItemList.size();
    }

    public static class BagViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView amount;
        ImageButton decrease;
        ImageButton increase;
        Button removeButton;



        public BagViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.imageView);
            productName = itemView.findViewById(R.id.name);
            productPrice = itemView.findViewById(R.id.price);
            amount = itemView.findViewById(R.id.amount);
            decrease = itemView.findViewById(R.id.decrease);
            increase = itemView.findViewById(R.id.increase);
            removeButton = itemView.findViewById(R.id.Remove);
        }
    }
}
