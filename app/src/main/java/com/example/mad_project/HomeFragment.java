package com.example.mad_project;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {

    private static final long CAROUSEL_DELAY_MS = 3000;

    // UI
    private TextView greetingText, seeAllButton;
    private ViewPager2 carouselViewPager;
    private LinearLayout dotsIndicator;
    private RecyclerView popularRecyclerView, moreProductsRecyclerView, allProductsGridRecyclerView;

    // Adapters
    private ProductAdapter popularProductAdapter, moreProductsAdapter, allProductsGridAdapter;
    private CarouselAdapter carouselAdapter;

    // Data lists
    private final List<Product> popularProductList = new ArrayList<>();
    private final List<Product> moreProductsList = new ArrayList<>();
    private final List<Product> allProductsGridList = new ArrayList<>();
    private final List<CarouselItem> carouselItems = new ArrayList<>();
    private final Set<String> favoriteProductIds = new HashSet<>();

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    // Carousel
    private final Handler carouselHandler = new Handler(Looper.getMainLooper());
    private Runnable carouselRunnable;

    // Pagination
    private DocumentSnapshot popularLastVisible;
    private DocumentSnapshot moreLastVisible;

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
        loadInitialData();
    }

    @Override
    public void onResume() {
        super.onResume();
        startCarouselAutoScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCarouselAutoScroll();
    }

    // -------------------------------
    // INIT
    // -------------------------------
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    private void initViews(View view) {
        greetingText = view.findViewById(R.id.greeting_text);
        seeAllButton = view.findViewById(R.id.see_all_button);
        carouselViewPager = view.findViewById(R.id.carousel_view_pager);
        dotsIndicator = view.findViewById(R.id.dots_indicator);

        popularRecyclerView = view.findViewById(R.id.popular_recycler_view);
        moreProductsRecyclerView = view.findViewById(R.id.more_products_recycler_view);
        // Temporarily comment out the line causing the error
        // allProductsGridRecyclerView = view.findViewById(R.id.all_products_grid_recycler_view);
    }

    // -------------------------------
    // RECYCLERS
    // -------------------------------
    private void setupRecyclerViews() {
        popularProductAdapter = new ProductAdapter(getContext(), popularProductList);
        popularRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularRecyclerView.setAdapter(popularProductAdapter);

        moreProductsAdapter = new ProductAdapter(getContext(), moreProductsList);
        moreProductsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        moreProductsRecyclerView.setAdapter(moreProductsAdapter);

        // Temporarily comment out the setup for the grid
        /*
        allProductsGridAdapter = new ProductAdapter(getContext(), allProductsGridList);
        if (allProductsGridRecyclerView != null) {
            allProductsGridRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            allProductsGridRecyclerView.setAdapter(allProductsGridAdapter);
        }
        */
    }

    // ... (The rest of your code remains the same, but the methods that use the grid will do nothing now)

    // -------------------------------
    // CAROUSEL
    // -------------------------------
    private void setupCarousel() {
        carouselItems.clear();
        carouselItems.add(new CarouselItem("Fresh Fits for the Heat", "Lightweight, breathable styles.", "https://plus.unsplash.com/premium_photo-1678216999335-5153b5a452c2"));
        carouselItems.add(new CarouselItem("New Collection Drop", "Discover the latest trends in fashion.", "https://images.unsplash.com/photo-1445205170230-053b83016050"));
        carouselItems.add(new CarouselItem("Summer Sale is Live!", "Up to 50% off.", "https://images.unsplash.com/photo-1483985988355-763728e1935b"));

        carouselAdapter = new CarouselAdapter(carouselItems);
        carouselViewPager.setAdapter(carouselAdapter);

        setupDotsIndicator();

        carouselRunnable = () -> {
            int next = (carouselViewPager.getCurrentItem() + 1) % carouselAdapter.getItemCount();
            carouselViewPager.setCurrentItem(next, true);
        };

        carouselViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDotsIndicator(position);
                carouselHandler.removeCallbacks(carouselRunnable);
                carouselHandler.postDelayed(carouselRunnable, CAROUSEL_DELAY_MS);
            }
        });
    }

    private void startCarouselAutoScroll() {
        if (carouselRunnable != null) {
            carouselHandler.postDelayed(carouselRunnable, CAROUSEL_DELAY_MS);
        }
    }

    private void stopCarouselAutoScroll() {
        if (carouselRunnable != null) {
            carouselHandler.removeCallbacks(carouselRunnable);
        }
    }

    private void setupDotsIndicator() {
        if (getContext() == null) return;
        dotsIndicator.removeAllViews();
        for (int i = 0; i < carouselItems.size(); i++) {
            ImageView dot = new ImageView(getContext());
            dot.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dot_inactive));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dotsIndicator.addView(dot, params);
        }
        updateDotsIndicator(0);
    }

    private void updateDotsIndicator(int position) {
        if (getContext() == null) return;
        for (int i = 0; i < dotsIndicator.getChildCount(); i++) {
            ImageView dot = (ImageView) dotsIndicator.getChildAt(i);
            dot.setImageDrawable(ContextCompat.getDrawable(
                    requireContext(),
                    i == position ? R.drawable.dot_active : R.drawable.dot_inactive
            ));
        }
    }

    // -------------------------------
    // LISTENERS
    // -------------------------------
    private void setupListeners() {
        seeAllButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                BottomNavigationView nav = getActivity().findViewById(R.id.bottom_nav);
                nav.setSelectedItemId(R.id.nav_search);
            }
        });
    }

    // -------------------------------
    // LOADING DATA
    // -------------------------------
    private void loadInitialData() {
        loadUserData();

        if (currentUser == null) {
            loadPopularProducts();
            return;
        }

        db.collection("users").document(currentUser.getUid())
                .collection("favorites")
                .get()
                .addOnSuccessListener(q -> {
                    favoriteProductIds.clear();
                    for (QueryDocumentSnapshot d : q) favoriteProductIds.add(d.getId());
                    loadPopularProducts();
                });
    }

    private void loadPopularProducts() {
        popularProductList.clear();

        db.collection("products")
                .orderBy("name")
                .limit(10)
                .get()
                .addOnSuccessListener(task -> {
                    if (task.isEmpty()) return;

                    for (DocumentSnapshot doc : task) {
                        Product p = doc.toObject(Product.class);
                        if (p != null) {
                            p.setId(doc.getId());
                            p.setFavorited(favoriteProductIds.contains(p.getId()));
                            popularProductList.add(p);
                        }
                    }

                    popularProductAdapter.notifyDataSetChanged();
                    popularLastVisible = task.getDocuments().get(task.size() - 1);
                    loadMoreProducts();
                });
    }

    private void loadMoreProducts() {
        if (popularLastVisible == null) return;
        moreProductsList.clear();

        db.collection("products")
                .orderBy("name")
                .startAfter(popularLastVisible)
                .limit(10)
                .get()
                .addOnSuccessListener(task -> {
                    if (task.isEmpty()) return;

                    for (DocumentSnapshot doc : task) {
                        Product p = doc.toObject(Product.class);
                        if (p != null) {
                            p.setId(doc.getId());
                            p.setFavorited(favoriteProductIds.contains(p.getId()));
                            moreProductsList.add(p);
                        }
                    }

                    moreProductsAdapter.notifyDataSetChanged();
                    moreLastVisible = task.getDocuments().get(task.size() - 1);
                    // Do not load the grid for now to avoid the error
                    // loadAllProductsGrid(); 
                });
    }

    private void loadAllProductsGrid() {
        // This method is temporarily disabled
    }

    private void loadUserData() {
        if (currentUser == null) return;

        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        greetingText.setText(getGreeting() + ", " + name);
                    }
                });
    }

    private String getGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) return "Good Morning";
        if (hour < 17) return "Good Afternoon";
        return "Good Evening";
    }
}
