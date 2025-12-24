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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mad_project.ExplorePage;
import com.example.mad_project.Product;
import com.example.mad_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecommendedItemAdapter extends RecyclerView.Adapter<RecommendedItemAdapter.ProductViewHolder> {

    private static final String TAG = "ProductAdapter";

    private final Context context;
    private final List<Product> productList;
    private final FirebaseFirestore db;
    private final FirebaseUser currentUser;

    public RecommendedItemAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.db = FirebaseFirestore.getInstance();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recommended_item_layout, parent, false);
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

        // Set initial favorite state
        updateFavoriteIcon(holder.favoriteButton, product.isFavorited());

        holder.favoriteButton.setOnClickListener(v -> {
            if (currentUser == null) return; // Should not happen

            boolean isCurrentlyFavorited = product.isFavorited();
            product.setFavorited(!isCurrentlyFavorited);
            updateFavoriteIcon(holder.favoriteButton, !isCurrentlyFavorited);

            if (!isCurrentlyFavorited) {
                // Add to favorites
                Map<String, Object> favoriteData = new HashMap<>();
                favoriteData.put("productId", product.getId());
                db.collection("users").document(currentUser.getUid())
                        .collection("favorites").document(product.getId())
                        .set(favoriteData)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Added to favorites: " + product.getId()))
                        .addOnFailureListener(e -> Log.w(TAG, "Error adding to favorites", e));
            } else {
                // Remove from favorites
                db.collection("users").document(currentUser.getUid())
                        .collection("favorites").document(product.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Removed from favorites: " + product.getId()))
                        .addOnFailureListener(e -> Log.w(TAG, "Error removing from favorites", e));
            }
        });


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExplorePage.class);
            intent.putExtra("productId", product.getId());
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
            productImage = itemView.findViewById(R.id.imageView);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
            productName = itemView.findViewById(R.id.name);
            productDescription = itemView.findViewById(R.id.description);
            productPrice = itemView.findViewById(R.id.price);
        }
    }
}
