package com.example.mad_project.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.Product;
import com.example.mad_project.Adapter.ProductAdapter;
import com.example.mad_project.R;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private static final int PAGE_SIZE = 10;
    private static final long SEARCH_DELAY_MS = 500; // 500ms delay

    private RecyclerView productsRecyclerView;
    private ProgressBar progressBar;
    private ChipGroup categoryChipGroup;
    private EditText searchBarEditText;
    private ImageView searchIcon;

    private ProductAdapter productAdapter;
    private final List<Product> productList = new ArrayList<>();
    private final Set<String> favoriteProductIds = new HashSet<>();

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private DocumentSnapshot lastVisible;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String selectedCategory = null;
    private String currentSearchTerm = null;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        productsRecyclerView = view.findViewById(R.id.products_recycler_view);
        progressBar = view.findViewById(R.id.progressBar);
        categoryChipGroup = view.findViewById(R.id.category_chip_group);
        searchBarEditText = view.findViewById(R.id.search_bar_edit_text);
        searchIcon = view.findViewById(R.id.action_search);

        setupRecyclerView();
        setupListeners();

        loadFavoritesAndThenProducts();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Important: Cancel any pending search to prevent crashes
        searchHandler.removeCallbacks(searchRunnable);
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
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        loadProducts();
                    }
                }
            }
        });
    }

    private void setupListeners() {
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

        searchIcon.setOnClickListener(v -> toggleSearchBar());

        searchBarEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentSearchTerm = s.toString().trim();
                searchRunnable = () -> resetAndLoadProducts();
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
            }
        });
    }

    private void toggleSearchBar() {
        if (searchBarEditText.getVisibility() == View.GONE) {
            searchBarEditText.setVisibility(View.VISIBLE);
            searchBarEditText.requestFocus();
            showKeyboard();
        } else {
            searchBarEditText.setVisibility(View.GONE);
            searchBarEditText.setText("");
            currentSearchTerm = null;
            hideKeyboard();
            resetAndLoadProducts();
        }
    }

    private void showKeyboard() {
        if (getContext() == null) return;
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchBarEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        if (getContext() == null) return;
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchBarEditText.getWindowToken(), 0);
        }
    }

    private void loadFavoritesAndThenProducts() {
        if (currentUser == null) {
            loadProducts();
            return;
        }

        db.collection("users").document(currentUser.getUid()).collection("favorites").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        favoriteProductIds.clear();
                        for (DocumentSnapshot doc : task.getResult()) {
                            favoriteProductIds.add(doc.getId());
                        }
                    }
                    loadProducts();
                });
    }

    private void resetAndLoadProducts() {
        productList.clear();
        lastVisible = null;
        isLastPage = false;
        productAdapter.notifyDataSetChanged();
        loadProducts();
    }

    private void loadProducts() {
        if (isLastPage || isLoading) return;

        setInProgress(true);

        Query query = db.collection("products").orderBy("name").limit(PAGE_SIZE);

        if (selectedCategory != null) {
            query = query.whereEqualTo("category", selectedCategory);
        }

        if (currentSearchTerm != null && !currentSearchTerm.isEmpty()) {
            query = query.whereGreaterThanOrEqualTo("name", currentSearchTerm)
                         .whereLessThanOrEqualTo("name", currentSearchTerm + "\uf8ff");
        }

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query.get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for (DocumentSnapshot document : documents) {
                    Product product = document.toObject(Product.class);
                    if (product != null) {
                        product.setId(document.getId());
                        if (favoriteProductIds.contains(product.getId())) {
                            product.setFavorited(true);
                        }
                        productList.add(product);
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
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
    }

    private void showSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }
}
