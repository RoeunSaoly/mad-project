package com.example.mad_project.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView; // Import TextView

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.Adapter.ProductAdapter;
import com.example.mad_project.Product;
import com.example.mad_project.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HeartFragment extends Fragment {
    private static final String TAG = "HeartFragment";

    private RecyclerView productsRecyclerView;
    private ProgressBar progressBar;
    private TextView emptyFavoritesMessage; // TextView for the "empty" message
    private ProductAdapter productAdapter;
    private final List<Product> productList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_heart, container, false);

        // Initialize views
        progressBar = view.findViewById(R.id.progressBar);
        productsRecyclerView = view.findViewById(R.id.products_recycler_view);
        emptyFavoritesMessage = view.findViewById(R.id.empty_favorites_message); // Make sure you add this TextView to your XML layout
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        setupRecyclerView();
        loadFavoriteProductIds(); // Start the loading process

        return view;
    }

    private void setupRecyclerView() {
        // Use your existing ProductAdapter
        productAdapter = new ProductAdapter(getContext(), productList);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        productsRecyclerView.setLayoutManager(layoutManager);
        productsRecyclerView.setAdapter(productAdapter);
    }

    /**
     * Step 1: Fetch the list of favorite product IDs from the user's sub-collection.
     */
    private void loadFavoriteProductIds() {
        if (currentUser == null) {
            Log.w(TAG, "No user logged in. Cannot load favorites.");
            setInProgress(false);
            showEmptyMessage(true); // Show the empty message if no user
            return;
        }

        setInProgress(true);
        showEmptyMessage(false); // Hide message while loading

        db.collection("users").document(currentUser.getUid()).collection("favorites").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Set<String> favoriteProductIds = new HashSet<>();
                        for (DocumentSnapshot doc : task.getResult()) {
                            favoriteProductIds.add(doc.getId());
                        }

                        // If the user has favorites, load the product details for each one.
                        if (!favoriteProductIds.isEmpty()) {
                            loadFavoriteProducts(new ArrayList<>(favoriteProductIds));
                        } else {
                            // If the list of favorites is empty, stop loading and show the message.
                            setInProgress(false);
                            showEmptyMessage(true);
                        }
                    } else {
                        Log.w(TAG, "Error getting favorite IDs.", task.getException());
                        setInProgress(false);
                    }
                });
    }

    /**
     * Step 2: Fetch the full product details for each favorite product ID.
     */
    private void loadFavoriteProducts(List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            setInProgress(false);
            return;
        }

        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (String id : productIds) {
            // For each ID, create a task to fetch the corresponding document from the "products" collection.
            tasks.add(db.collection("products").document(id).get());
        }

        // Use Tasks.whenAllSuccess() to wait for all product fetches to complete.
        Tasks.whenAllSuccess(tasks).addOnCompleteListener(task -> {
            productList.clear(); // Clear the list before adding new items
            if (task.isSuccessful() && task.getResult() != null) {
                for (Object snapshotObject : task.getResult()) {
                    DocumentSnapshot snapshot = (DocumentSnapshot) snapshotObject;
                    if (snapshot.exists()) {
                        Product product = snapshot.toObject(Product.class);
                        if (product != null) {
                            product.setId(snapshot.getId());
                            // We already know this is a favorite, so we can force it to be true.
                            product.setFavorited(true);
                            productList.add(product);
                        }
                    }
                }
            } else {
                Log.w(TAG, "Error fetching one or more favorite products.", task.getException());
            }

            // Update the UI
            setInProgress(false);
            productAdapter.notifyDataSetChanged();
            showEmptyMessage(productList.isEmpty()); // Show message only if the final list is empty
        });
    }

    private void setInProgress(boolean inProgress) {
        if (progressBar != null) {
            progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        }
    }

    // Helper method to show or hide the empty message and the RecyclerView
    private void showEmptyMessage(boolean show) {
        if (emptyFavoritesMessage != null && productsRecyclerView != null) {
            emptyFavoritesMessage.setVisibility(show ? View.VISIBLE : View.GONE);
            productsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
