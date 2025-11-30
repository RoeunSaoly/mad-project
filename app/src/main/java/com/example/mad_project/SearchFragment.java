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

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private static final int PAGE_SIZE = 10;

    private RecyclerView productsRecyclerView;
    private ProgressBar progressBar;
    private ChipGroup categoryChipGroup;

    private ProductAdapter productAdapter;
    private final List<Product> productList = new ArrayList<>();
    private final Set<String> favoriteProductIds = new HashSet<>();

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private DocumentSnapshot lastVisible;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String selectedCategory = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        productsRecyclerView = view.findViewById(R.id.products_recycler_view);
        progressBar = view.findViewById(R.id.progressBar);
        categoryChipGroup = view.findViewById(R.id.category_chip_group);

        setupRecyclerView();
        setupChipListener();

        loadFavoritesAndThenProducts();

        return view;
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(getContext(), productList);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        productsRecyclerView.setLayoutManager(layoutManager);
        productsRecyclerView.setAdapter(productAdapter);

        productsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        loadProducts();
                    }
                }
            }
        });
    }

    private void setupChipListener() {
        categoryChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_all) {
                selectedCategory = null;
            } else if (checkedId == R.id.chip_t_shirts) {
                selectedCategory = "T-Shirts";
            } else if (checkedId == R.id.chip_hoodies) {
                selectedCategory = "Hoodies";
            } else if (checkedId == R.id.chip_bottoms) {
                selectedCategory = "Bottoms";
            }
            resetAndLoadProducts();
        });
    }

    private void loadFavoritesAndThenProducts() {
        if (currentUser == null) {
            loadProducts(); // Load products without favorite status
            return;
        }

        db.collection("users").document(currentUser.getUid()).collection("favorites").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        favoriteProductIds.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            favoriteProductIds.add(document.getId());
                        }
                    }
                    // Proceed to load products regardless of favorite loading success
                    loadProducts();
                });
    }

    private void resetAndLoadProducts() {
        productList.clear();
        lastVisible = null;
        isLastPage = false;
        productAdapter.notifyDataSetChanged();
        loadFavoritesAndThenProducts();
    }

    private void loadProducts() {
        if (isLastPage || isLoading) return;

        setInProgress(true);

        Query query = db.collection("products").orderBy("name").limit(PAGE_SIZE);

        if (selectedCategory != null) {
            query = query.whereEqualTo("category", selectedCategory);
        }

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query.get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for (DocumentSnapshot document : documents) {
                    try {
                        Product product = document.toObject(Product.class);
                        product.setId(document.getId()); // Set the document ID
                        if (favoriteProductIds.contains(product.getId())) {
                            product.setFavorited(true);
                        }
                        productList.add(product);
                    } catch (Exception e) {
                        Log.e(TAG, "Error converting document to Product object", e);
                    }
                }
                productAdapter.notifyDataSetChanged();

                if (!documents.isEmpty()) {
                    lastVisible = documents.get(documents.size() - 1);
                }

                if (documents.size() < PAGE_SIZE) {
                    isLastPage = true;
                }

            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
                showSnackbar("Failed to load products.");
            }
        });
    }

    private void setInProgress(boolean inProgress) {
        isLoading = inProgress;
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
