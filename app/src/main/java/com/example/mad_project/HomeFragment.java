package com.example.mad_project;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final long CAROUSEL_DELAY_MS = 3000;

    private TextView greetingText, seeAllButton;
    private ViewPager2 carouselViewPager;
    private LinearLayout dotsIndicator;

    private RecyclerView popularRecyclerView;
    private ProductAdapter popularProductAdapter;
    private final List<Product> popularProductList = new ArrayList<>();

    private RecyclerView moreProductsRecyclerView;
    private ProductAdapter moreProductsAdapter;
    private final List<Product> moreProductsList = new ArrayList<>();

    private CarouselAdapter carouselAdapter;
    private final List<CarouselItem> carouselItems = new ArrayList<>();
    private final Set<String> favoriteProductIds = new HashSet<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private final Handler carouselHandler = new Handler(Looper.getMainLooper());
    private Runnable carouselRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initFirebase();
        initViews(view);

        setupPopularRecyclerView();
        setupMoreProductsRecyclerView();
        setupCarousel();
        setupListeners();

        loadInitialData();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCarouselAutoScroll();
    }

    @Override
    public void onResume() {
        super.onResume();
        startCarouselAutoScroll();
    }

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
    }

    private void setupPopularRecyclerView() {
        popularProductAdapter = new ProductAdapter(getContext(), popularProductList);
        popularRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularRecyclerView.setAdapter(popularProductAdapter);
    }

    private void setupMoreProductsRecyclerView() {
        moreProductsAdapter = new ProductAdapter(getContext(), moreProductsList);
        moreProductsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        moreProductsRecyclerView.setAdapter(moreProductsAdapter);
    }

    private void setupCarousel() {
        // Create 5 sample slides
        carouselItems.add(new CarouselItem("Fresh Fits for the Heat", "Lightweight, breathable styles.", "https://plus.unsplash.com/premium_photo-1678216999335-5153b5a452c2"));
        carouselItems.add(new CarouselItem("New Collection Drop", "Discover the latest trends in fashion.", "https://images.unsplash.com/photo-1445205170230-053b83016050"));
        carouselItems.add(new CarouselItem("Summer Sale is Live!", "Up to 50% off on selected items.", "https://images.unsplash.com/photo-1483985988355-763728e1935b"));
        carouselItems.add(new CarouselItem("Accessorize Your Look", "Find the perfect finishing touches.", "https://images.unsplash.com/photo-1590779233252-a5895740b21a"));
        carouselItems.add(new CarouselItem("Explore Outerwear", "Stay warm and stylish this season.", "https://images.unsplash.com/photo-1592878912957-695c5d0a98c7"));

        carouselAdapter = new CarouselAdapter(carouselItems);
        carouselViewPager.setAdapter(carouselAdapter);

        setupDotsIndicator();
        
        carouselViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDotsIndicator(position);
                // Restart auto-scroll timer on manual swipe
                carouselHandler.removeCallbacks(carouselRunnable);
                carouselHandler.postDelayed(carouselRunnable, CAROUSEL_DELAY_MS);
            }
        });
    }

    private void startCarouselAutoScroll() {
        carouselRunnable = () -> {
            int currentItem = carouselViewPager.getCurrentItem();
            int nextItem = (currentItem + 1) % carouselAdapter.getItemCount();
            carouselViewPager.setCurrentItem(nextItem, true);
        };
        carouselHandler.postDelayed(carouselRunnable, CAROUSEL_DELAY_MS);
    }

    private void stopCarouselAutoScroll() {
        carouselHandler.removeCallbacks(carouselRunnable);
    }

    private void setupDotsIndicator() {
        if (getContext() == null || carouselItems.isEmpty()) return;
        dotsIndicator.removeAllViews();
        ImageView[] dots = new ImageView[carouselItems.size()];
        for (int i = 0; i < carouselItems.size(); i++) {
            dots[i] = new ImageView(getContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dot_inactive));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            dotsIndicator.addView(dots[i], params);
        }
        updateDotsIndicator(0); // Select the first dot initially
    }

    private void updateDotsIndicator(int position) {
        if (getContext() == null) return;
        for (int i = 0; i < dotsIndicator.getChildCount(); i++) {
            ImageView dot = (ImageView) dotsIndicator.getChildAt(i);
            dot.setImageDrawable(ContextCompat.getDrawable(getContext(), i == position ? R.drawable.dot_active : R.drawable.dot_inactive));
        }
    }

    private void setupListeners() {
        seeAllButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                ((BottomNavigationView) getActivity().findViewById(R.id.bottom_nav)).setSelectedItemId(R.id.nav_search);
            }
        });
    }

    private void loadInitialData() {
        loadUserData();
        if (currentUser == null) {
            loadPopularProducts(null);
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
                    loadPopularProducts(null);
                });
    }

    private void loadPopularProducts(DocumentSnapshot lastVisible) {
        Query query = db.collection("products").orderBy("name").limit(10);
        if(lastVisible != null) query = query.startAfter(lastVisible);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Product product = document.toObject(Product.class);
                    product.setId(document.getId());
                    if (favoriteProductIds.contains(product.getId())) product.setFavorited(true);
                    popularProductList.add(product);
                }
                popularProductAdapter.notifyDataSetChanged();
                
                DocumentSnapshot newLastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                loadMoreProducts(newLastVisible);
            }
        });
    }

    private void loadMoreProducts(DocumentSnapshot lastVisible) {
        if (lastVisible == null) return;
        
        db.collection("products").orderBy("name").startAfter(lastVisible).limit(10).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            product.setId(document.getId());
                            if (favoriteProductIds.contains(product.getId())) product.setFavorited(true);
                            moreProductsList.add(product);
                        }
                        moreProductsAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadUserData() {
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                greetingText.setText(String.format("Good Morning, %s", document.getString("name")));
                            }
                        }
                    });
        }
    }
}
