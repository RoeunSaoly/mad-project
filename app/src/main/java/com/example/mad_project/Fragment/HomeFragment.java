package com.example.mad_project.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mad_project.Adapter.CarouselAdapter;
import com.example.mad_project.Adapter.RecommendedItemAdapter;
import com.example.mad_project.CarouselItem;
import com.example.mad_project.Category;
import com.example.mad_project.Adapter.CategoryAdapter;
import com.example.mad_project.Product;
import com.example.mad_project.Adapter.ProductAdapter;
import com.example.mad_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final long CAROUSEL_DELAY_MS = 3000;
    private static final int PAGE_SIZE = 30;

    // UI
    private TextView userNameText, allProductsHeader, seeAllCategoriesButton, seeAllRecommendedButton;
    private ViewPager2 carouselViewPager;
    private LinearLayout dotsIndicator, homeContentContainer;
    private RecyclerView categoryRecyclerView, recommendedProductsRecyclerView, allProductsGridRecyclerView;
    private EditText searchBar;
    private ProgressBar homeProgressBar, homeProgressBar2;

    // Adapters
    private CategoryAdapter categoryAdapter;
    private ProductAdapter allProductsGridAdapter;
    private RecommendedItemAdapter recommendedProductsAdapter;
    private CarouselAdapter carouselAdapter;

    // Data lists
    private final List<Category> categoryList = new ArrayList<>();
    private final List<Product> recommendedProductsList = new ArrayList<>();
    private final List<Product> allProductsGridList = new ArrayList<>();
    private final List<CarouselItem> carouselItems = new ArrayList<>();
    private final Set<String> favoriteProductIds = new HashSet<>();

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    
    // Pagination & Search
    private DocumentSnapshot lastVisible;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String currentSearchTerm = "";

    // Carousel
    private final Handler carouselHandler = new Handler(Looper.getMainLooper());
    private Runnable carouselRunnable;

    // Search Debounce
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFirebase();
        initViews(view);
        setupRecyclerViews();
        setupCarousel();
        setupListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        startCarouselAutoScroll();
        loadInitialData();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCarouselAutoScroll();
        searchHandler.removeCallbacks(searchRunnable);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    private void initViews(View view) {
        userNameText = view.findViewById(R.id.user_name_text);
        seeAllCategoriesButton = view.findViewById(R.id.see_all_categories_button);
        seeAllRecommendedButton = view.findViewById(R.id.see_all_recommended_button);
        carouselViewPager = view.findViewById(R.id.carousel_view_pager);
        dotsIndicator = view.findViewById(R.id.dots_indicator);
        homeProgressBar = view.findViewById(R.id.home_progress_bar);
        homeProgressBar2 = view.findViewById(R.id.home_progress_bar2);
        searchBar = view.findViewById(R.id.search_bar);
        homeContentContainer = view.findViewById(R.id.home_content_container);
        allProductsHeader = view.findViewById(R.id.all_products_header);

        categoryRecyclerView = view.findViewById(R.id.category_recycler_view);
        recommendedProductsRecyclerView = view.findViewById(R.id.recommended_products_recycler_view);
        allProductsGridRecyclerView = view.findViewById(R.id.all_products_grid_recycler_view);
    }

    private void setupRecyclerViews() {
        if (getContext() == null) return;
        
        // Category list
        categoryList.clear();
        categoryList.add(new Category("Sports", R.drawable.ic_category_sports));
        categoryList.add(new Category("Shoes", R.drawable.ic_category_shoes));
        categoryList.add(new Category("Women", R.drawable.ic_category_women));
        categoryList.add(new Category("Men", R.drawable.ic_category_men));
        categoryList.add(new Category("Beauty", R.drawable.ic_category_accessories));
        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Recommended
        recommendedProductsAdapter = new RecommendedItemAdapter(getContext(), recommendedProductsList);
        recommendedProductsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendedProductsRecyclerView.setAdapter(recommendedProductsAdapter);

        // All Products
        allProductsGridAdapter = new ProductAdapter(getContext(), allProductsGridList);
        allProductsGridRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        allProductsGridRecyclerView.setAdapter(allProductsGridAdapter);
    }

    private void setupListeners() {
        View.OnClickListener seeAllClickListener = v -> {
            if (getActivity() != null) {
                ((BottomNavigationView) getActivity().findViewById(R.id.bottom_nav)).setSelectedItemId(R.id.nav_search);
            }
        };
        seeAllCategoriesButton.setOnClickListener(seeAllClickListener);
        seeAllRecommendedButton.setOnClickListener(seeAllClickListener);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.equals(currentSearchTerm)) return;
                
                currentSearchTerm = query;

                // UI adjustments for search mode
                if (currentSearchTerm.isEmpty()) {
                    homeContentContainer.setVisibility(View.VISIBLE);
                    allProductsHeader.setText("All Products");
                } else {
                    homeContentContainer.setVisibility(View.GONE);
                    allProductsHeader.setText("Search Results for \"" + currentSearchTerm + "\"");
                }

                // Debounce search
                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> {
                    resetPagination();
                    loadProducts();
                };
                searchHandler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupCarousel() {
        if (getContext() == null) return;
        carouselItems.clear();
        carouselItems.add(new CarouselItem("Your Goals. Your Gear.", "Accelerate your fitness journey.", R.drawable.carousel1));
        carouselItems.add(new CarouselItem("New Collection", "Discover the latest trends.", R.drawable.carousel2));
        carouselItems.add(new CarouselItem("Summer Sale!", "Up to 50% off.", R.drawable.carousel3));

        carouselAdapter = new CarouselAdapter(carouselItems);
        carouselViewPager.setAdapter(carouselAdapter);
        setupDotsIndicator();

        carouselViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDotsIndicator(position);
            }
        });
    }

    private void startCarouselAutoScroll() {
        carouselHandler.removeCallbacks(carouselRunnable);
        carouselRunnable = () -> {
            if (carouselAdapter != null && carouselAdapter.getItemCount() > 0) {
                int next = (carouselViewPager.getCurrentItem() + 1) % carouselAdapter.getItemCount();
                carouselViewPager.setCurrentItem(next, true);
                carouselHandler.postDelayed(carouselRunnable, CAROUSEL_DELAY_MS);
            }
        };
        carouselHandler.postDelayed(carouselRunnable, CAROUSEL_DELAY_MS);
    }

    private void stopCarouselAutoScroll() {
        carouselHandler.removeCallbacks(carouselRunnable);
    }

    private void setupDotsIndicator() {
        if (getContext() == null) return;
        dotsIndicator.removeAllViews();
        for (int i = 0; i < carouselAdapter.getItemCount(); i++) {
            ImageView dot = new ImageView(getContext());
            dot.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dot_inactive));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(16, 16);
            params.setMargins(8, 0, 8, 0);
            dotsIndicator.addView(dot, params);
        }
        updateDotsIndicator(0);
    }

    private void updateDotsIndicator(int position) {
        if (getContext() == null) return;
        for (int i = 0; i < dotsIndicator.getChildCount(); i++) {
            ((ImageView) dotsIndicator.getChildAt(i)).setImageDrawable(ContextCompat.getDrawable(requireContext(), 
                    i == position ? R.drawable.dot_active : R.drawable.dot_inactive));
        }
    }

    private void loadInitialData() {
        loadUserData();
        loadFavoritesAndThenProducts();
    }

    private void loadFavoritesAndThenProducts() {
        if (currentUser == null) {
            resetPagination();
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
                    resetPagination();
                    loadProducts();
                });
    }

    private void resetPagination() {
        lastVisible = null;
        isLastPage = false;
        isLoading = false;
    }

    private void loadProducts() {
        if (isLastPage || isLoading) return;
        setInProgress(true);

        final String searchTermAtStart = currentSearchTerm;

        Query query = db.collection("products").orderBy("name");

        if (!searchTermAtStart.isEmpty()) {
            query = query.whereGreaterThanOrEqualTo("name", searchTermAtStart)
                    .whereLessThanOrEqualTo("name", searchTermAtStart + "\uf8ff");
        }

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query.limit(PAGE_SIZE).get().addOnCompleteListener(task -> {
            if (!searchTermAtStart.equals(currentSearchTerm)) {
                return; // Ignore results from old queries
            }

            setInProgress(false);
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                if (lastVisible == null) {
                    recommendedProductsList.clear();
                    allProductsGridList.clear();
                }

                boolean isSearching = !searchTermAtStart.isEmpty();

                for (int i = 0; i < documents.size(); i++) {
                    DocumentSnapshot document = documents.get(i);
                    Product product = document.toObject(Product.class);
                    if (product != null) {
                        product.setId(document.getId());
                        product.setFavorited(favoriteProductIds.contains(product.getId()));

                        if (!isSearching && lastVisible == null && i < 6) {
                            recommendedProductsList.add(product);
                        } else {
                            allProductsGridList.add(product);
                        }
                    }
                }

                recommendedProductsAdapter.notifyDataSetChanged();
                allProductsGridAdapter.notifyDataSetChanged();

                if (!documents.isEmpty()) {
                    lastVisible = documents.get(documents.size() - 1);
                }
                isLastPage = documents.size() < PAGE_SIZE;
            } else {
                Log.e(TAG, "Error getting products", task.getException());
                showSnackbar("Failed to load products.");
            }
        });
    }

    private void loadUserData() {
        if (currentUser == null) return;
        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && userNameText != null) {
                        userNameText.setText(doc.getString("name"));
                    }
                });
    }

    private void setInProgress(boolean inProgress) {
        isLoading = inProgress;
        if (homeProgressBar != null) homeProgressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        if (homeProgressBar2 != null) homeProgressBar2.setVisibility(inProgress ? View.VISIBLE : View.GONE);
    }

    private void showSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }
}
