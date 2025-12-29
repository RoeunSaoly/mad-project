package com.example.mad_project.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.example.mad_project.ExplorePage;
import com.example.mad_project.Product;
import com.example.mad_project.R;
import com.example.mad_project.db.AppDatabase;
import com.example.mad_project.db.FavoriteDao;
import com.example.mad_project.db.FavoriteItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private static final String TAG = "ProductAdapter";

    private final Context context;
    private final List<Product> productList;
    private final AppDatabase appDb;
    private final FavoriteDao favoriteDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.appDb = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "mad-project-db")
                .fallbackToDestructiveMigration()
                .build();
        this.favoriteDao = appDb.favoriteDao();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getName());
        holder.productDescription.setText(product.getDescription());
        holder.productPrice.setText(String.format(Locale.US, "$%.2f USD", product.getPrice()));

        String imageUrl = product.getFirstImageUrl();
        if (imageUrl != null) {
            Glide.with(context).load(imageUrl).into(holder.productImage);
        }

        executor.execute(() -> {
            boolean isFavorited = favoriteDao.getFavoriteById(product.getId()) != null;
            product.setFavorited(isFavorited);
            holder.itemView.post(() -> updateFavoriteIcon(holder.favoriteButton, isFavorited));
        });

        holder.favoriteButton.setOnClickListener(v -> {
            executor.execute(() -> {
                boolean isCurrentlyFavorited = product.isFavorited();
                if (isCurrentlyFavorited) {
                    favoriteDao.delete(new FavoriteItem(product.getId()));
                    Log.d(TAG, "Removed from favorites: " + product.getId());
                } else {
                    favoriteDao.insert(new FavoriteItem(product.getId()));
                    Log.d(TAG, "Added to favorites: " + product.getId());
                }
                product.setFavorited(!isCurrentlyFavorited);
                holder.itemView.post(() -> updateFavoriteIcon(holder.favoriteButton, !isCurrentlyFavorited));
            });
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExplorePage.class);
            intent.putExtra("Name", product.getName());
            intent.putExtra("Description", product.getDescription());
            intent.putExtra("Price", String.format(Locale.US, "$%.2f USD", product.getPrice()));
            intent.putExtra("img", product.getFirstImageUrl());

            context.startActivity(intent);
        });
    }

    private void updateFavoriteIcon(ImageView imageView, boolean isFavorited) {
        if (isFavorited) {
            imageView.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            imageView.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        ImageView favoriteButton;
        TextView productName;
        TextView productDescription;
        TextView productPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
            productName = itemView.findViewById(R.id.product_name);
            productDescription = itemView.findViewById(R.id.product_description);
            productPrice = itemView.findViewById(R.id.product_price);
        }
    }
}
