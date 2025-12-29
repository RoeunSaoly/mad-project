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
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.example.mad_project.R;
import com.example.mad_project.db.AppDatabase;
import com.example.mad_project.db.BagDao;
import com.example.mad_project.db.BagItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BagAdapter extends RecyclerView.Adapter<BagAdapter.BagViewHolder> {

    private final Context context;
    private final List<BagItem> bagItemList;
    private final AppDatabase appDb;
    private final BagDao bagDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public BagAdapter(Context context, List<BagItem> bagItemList) {
        this.context = context;
        this.bagItemList = bagItemList;
        this.appDb = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "mad-project-db")
                .fallbackToDestructiveMigration()
                .build();
        this.bagDao = appDb.bagDao();
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
        holder.amount.setText(String.valueOf(bagItem.amount));

        if (bagItem.imageUrl != null) {
            Glide.with(context).load(bagItem.imageUrl).into(holder.productImage);
        }

        holder.increase.setOnClickListener(v -> {
            bagItem.amount++;
            holder.amount.setText(String.valueOf(bagItem.amount));
            executor.execute(() -> bagDao.update(bagItem));
        });

        holder.decrease.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            BagItem currentBagItem = bagItemList.get(currentPosition);
            if (currentBagItem.amount > 1) {
                currentBagItem.amount--;
                holder.amount.setText(String.valueOf(currentBagItem.amount));
                executor.execute(() -> bagDao.update(currentBagItem));
            } else {
                bagItemList.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                notifyItemRangeChanged(currentPosition, bagItemList.size());
                executor.execute(() -> bagDao.delete(currentBagItem));
            }
        });

        holder.removeButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            BagItem itemToRemove = bagItemList.get(currentPosition);
            bagItemList.remove(currentPosition);
            notifyItemRemoved(currentPosition);
            notifyItemRangeChanged(currentPosition, bagItemList.size());
            executor.execute(() -> bagDao.delete(itemToRemove));
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
