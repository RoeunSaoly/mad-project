package com.example.mad_project.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mad_project.Adapter.CarouselAdapter;
import com.example.mad_project.Adapter.RecommendedItemAdapter;
import com.example.mad_project.CarouselItem;
import com.example.mad_project.HomeActivity;
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

    // UI
    private TextView userNameText, setStoreText, seeAllRecommendedButton;
    private ViewPager2 carouselViewPager;
    private LinearLayout dotsIndicator;
    private RecyclerView recommendedProductsRecyclerView, allProductsGridRecyclerView;
    private EditText searchBar;
    private CardView carouselCardView;

    // Adapters
    private ProductAdapter  allProductsGridAdapter;
    private RecommendedItemAdapter recommendedProductsAdapter;
    private CarouselAdapter carouselAdapter;

    // Data lists
    private final List<Product> recommendedProductsList = new ArrayList<>();
    private final List<Product> allProductsGridList = new ArrayList<>();
    private final List<CarouselItem> carouselItems = new ArrayList<>();
    private Set<String> favoriteProductIds = new HashSet<>();

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private static final int PAGE_SIZE = 30;
    private DocumentSnapshot lastVisible;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String selectedCategory = null;
    private ProgressBar homeProgressBar;
    private ProgressBar home_progress_bar2;
    // Carousel
    private final Handler carouselHandler = new Handler(Looper.getMainLooper());
    private Runnable carouselRunnable;

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
    }

    // -------------------------------
    // INITIALIZATION
    // -------------------------------
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    private void initViews(View view) {
        userNameText = view.findViewById(R.id.user_name_text);
        setStoreText = view.findViewById(R.id.set_store_text);
        seeAllRecommendedButton = view.findViewById(R.id.see_all_recommended_button);
        carouselViewPager = view.findViewById(R.id.carousel_view_pager);
        dotsIndicator = view.findViewById(R.id.dots_indicator);
        homeProgressBar = view.findViewById(R.id.home_progress_bar);
        home_progress_bar2 = view.findViewById(R.id.home_progress_bar2);
        searchBar = view.findViewById(R.id.search_bar);
        carouselCardView = view.findViewById(R.id.carousel_card_view);

        recommendedProductsRecyclerView = view.findViewById(R.id.recommended_products_recycler_view);
        allProductsGridRecyclerView = view.findViewById(R.id.all_products_grid_recycler_view);
    }

    private void setupRecyclerViews() {
        if (getContext() == null) return;

        recommendedProductsAdapter = new RecommendedItemAdapter(getContext(), recommendedProductsList);
        recommendedProductsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendedProductsRecyclerView.setAdapter(recommendedProductsAdapter);

        allProductsGridAdapter = new ProductAdapter(getContext(), allProductsGridList);
        allProductsGridRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        allProductsGridRecyclerView.setAdapter(allProductsGridAdapter);
    }

    private void setupListeners() {
        View.OnClickListener searchClickListener = v -> {
            if (getActivity() != null && getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).setExpandSearchOnLoad(true);
                ((BottomNavigationView) getActivity().findViewById(R.id.bottom_nav)).setSelectedItemId(R.id.nav_search);
            }
        };
        seeAllRecommendedButton.setOnClickListener(searchClickListener);
        searchBar.setOnClickListener(searchClickListener);
    }

    // -------------------------------
    // CAROUSEL LOGIC
    // -------------------------------
    private void setupCarousel() {
        if (getContext() == null) return;
        carouselItems.clear();
        carouselItems.add(new CarouselItem("Your Goals. Your Gear.", "Accelerate your fitness journey with these essentials.", R.drawable.carousel1));
        carouselItems.add(new CarouselItem("New Collection Drop", "Discover the latest trends.", R.drawable.carousel2));
        carouselItems.add(new CarouselItem("Summer Sale!", "Up to 50% off.", R.drawable.carousel3));

        carouselAdapter = new CarouselAdapter(carouselItems);
        carouselViewPager.setAdapter(carouselAdapter);
        setupDotsIndicator();
        startCarouselAutoScroll();

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
            int next = (carouselViewPager.getCurrentItem() + 1) % carouselAdapter.getItemCount();
            carouselViewPager.setCurrentItem(next, true);
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
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            dotsIndicator.addView(dot, params);
        }
        updateDotsIndicator(0);
    }

    private void updateDotsIndicator(int position) {
        if (getContext() == null) return;
        for (int i = 0; i < dotsIndicator.getChildCount(); i++) {
            ((ImageView) dotsIndicator.getChildAt(i)).setImageDrawable(ContextCompat.getDrawable(requireContext(), i == position ? R.drawable.dot_active : R.drawable.dot_inactive));
        }
    }

    // -------------------------------
    // DATA LOADING LOGIC
    // -------------------------------
    private void loadInitialData() {
        loadUserData();
        loadFavoritesAndThenProducts();
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

    private void loadProducts() {
        if (isLastPage || isLoading) return;
        setInProgress(true);

        Query query = db.collection("products")
                .orderBy("name")
                .limit(PAGE_SIZE);

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

                // Clear the lists only when it's a fresh load (not pagination)
                if (lastVisible == null) {
                    recommendedProductsList.clear();
                    allProductsGridList.clear();
                }

                for (int i = 0; i < documents.size(); i++) {
                    DocumentSnapshot document = documents.get(i);
                    Product product = document.toObject(Product.class);
                    if (product != null) {
                        product.setId(document.getId());
                        if (favoriteProductIds.contains(product.getId())) {
                            product.setFavorited(true);
                        }

                        if (lastVisible == null && i < 10) {
                            recommendedProductsList.add(product);
                        } else {
                            allProductsGridList.add(product);
                        }
                    }
                }

                // Notify both adapters that their data has changed.
                recommendedProductsAdapter.notifyDataSetChanged();
                allProductsGridAdapter.notifyDataSetChanged();

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

    private void showSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }
    private void loadUserData() {
        if (currentUser == null) return;
        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        userNameText.setText(name);
                    }
                });
    }

    private void setInProgress(boolean inProgress) {
        isLoading = inProgress;
        if (homeProgressBar != null) {
            homeProgressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        }
        if (home_progress_bar2 != null) {
            home_progress_bar2.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        }
    }
}
