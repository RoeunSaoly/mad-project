package com.example.mad_project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private RecyclerView productsRecyclerView;
    private ProgressBar progressBar;
    private ProductAdapter productAdapter;
    private final List<Product> productList = new ArrayList<>();

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        db = FirebaseFirestore.getInstance();
        productsRecyclerView = view.findViewById(R.id.products_recycler_view);
        progressBar = view.findViewById(R.id.progressBar);

        setupRecyclerView();
        loadProducts();

        return view;
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(getContext(), productList);
        productsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns
        productsRecyclerView.setAdapter(productAdapter);
    }

    private void loadProducts() {
        setInProgress(true);
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        productList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Product product = document.toObject(Product.class);
                                productList.add(product);
                            } catch (Exception e) {
                                Log.e(TAG, "Error converting document to Product object", e);
                            }
                        }
                        productAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Products loaded: " + productList.size());
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        showSnackbar("Failed to load products.");
                    }
                });
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }
}
