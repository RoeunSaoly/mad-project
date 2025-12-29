package com.example.mad_project.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.mad_project.Adapter.ProductAdapter;
import com.example.mad_project.Product;
import com.example.mad_project.R;
import com.example.mad_project.db.AppDatabase;
import com.example.mad_project.db.FavoriteDao;
import com.example.mad_project.db.FavoriteItem;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HeartFragment extends Fragment {
    private static final String TAG = "HeartFragment";

    private RecyclerView productsRecyclerView;
    private ProgressBar progressBar;
    private TextView emptyFavoritesMessage;
    private ProductAdapter productAdapter;
    private final List<Product> productList = new ArrayList<>();

    private FirebaseFirestore db;
    private AppDatabase appDb;
    private FavoriteDao favoriteDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_heart, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        productsRecyclerView = view.findViewById(R.id.products_recycler_view);
        emptyFavoritesMessage = view.findViewById(R.id.empty_favorites_message);
        db = FirebaseFirestore.getInstance();

        // Initialize Room database
        appDb = Room.databaseBuilder(getContext().getApplicationContext(), AppDatabase.class, "mad-project-db")
                .fallbackToDestructiveMigration()
                .build();
        favoriteDao = appDb.favoriteDao();

        setupRecyclerView();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavoritesFromDb(); // Load favorites when fragment is resumed
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(getContext(), productList);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        productsRecyclerView.setLayoutManager(layoutManager);
        productsRecyclerView.setAdapter(productAdapter);
    }

    private void loadFavoritesFromDb() {
        setInProgress(true);
        showEmptyMessage(false);

        executor.execute(() -> {
            List<FavoriteItem> favoriteItems = favoriteDao.getAll();
            if (favoriteItems != null && !favoriteItems.isEmpty()) {
                List<String> favoriteProductIds = new ArrayList<>();
                for (FavoriteItem item : favoriteItems) {
                    favoriteProductIds.add(item.productId);
                }
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> loadFavoriteProducts(favoriteProductIds));
                }
            } else {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        setInProgress(false);
                        showEmptyMessage(true);
                        productList.clear();
                        productAdapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }

    private void loadFavoriteProducts(List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            setInProgress(false);
            showEmptyMessage(true);
            return;
        }

        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (String id : productIds) {
            tasks.add(db.collection("products").document(id).get());
        }

        Tasks.whenAllSuccess(tasks).addOnCompleteListener(task -> {
            productList.clear();
            if (task.isSuccessful() && task.getResult() != null) {
                for (Object snapshotObject : task.getResult()) {
                    DocumentSnapshot snapshot = (DocumentSnapshot) snapshotObject;
                    if (snapshot.exists()) {
                        Product product = snapshot.toObject(Product.class);
                        if (product != null) {
                            product.setId(snapshot.getId());
                            product.setFavorited(true); // All items in this fragment are favorites
                            productList.add(product);
                        }
                    }
                }
            } else {
                Log.w(TAG, "Error fetching one or more favorite products.", task.getException());
            }

            setInProgress(false);
            productAdapter.notifyDataSetChanged();
            showEmptyMessage(productList.isEmpty());
        });
    }

    private void setInProgress(boolean inProgress) {
        if (progressBar != null) {
            progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        }
    }

    private void showEmptyMessage(boolean show) {
        if (emptyFavoritesMessage != null && productsRecyclerView != null) {
            emptyFavoritesMessage.setVisibility(show ? View.VISIBLE : View.GONE);
            productsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
