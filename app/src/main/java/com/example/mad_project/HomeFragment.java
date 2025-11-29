package com.example.mad_project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private TextView greetingText;
    private RecyclerView popularRecyclerView;
    private ProductAdapter productAdapter;
    private final List<Product> popularProductList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initFirebase();
        initViews(view);
        setupRecyclerView();

        loadUserData();
        loadPopularProducts();

        return view;
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initViews(View view) {
        greetingText = view.findViewById(R.id.greeting_text);
        popularRecyclerView = view.findViewById(R.id.popular_recycler_view);
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(getContext(), popularProductList);
        popularRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularRecyclerView.setAdapter(productAdapter);
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String name = document.getString("name");
                                if (name != null && !name.isEmpty()) {
                                    greetingText.setText(String.format("Good Morning, %s", name));
                                }
                            } else {
                                Log.d(TAG, "No such document for user profile");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    });
        }
    }

    private void loadPopularProducts() {
        db.collection("products")
                // You can add .whereEqualTo("popular", true) or .orderBy("sales", Query.Direction.DESCENDING) here
                .limit(10) // Limit to 10 popular products
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        popularProductList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Product product = document.toObject(Product.class);
                                popularProductList.add(product);
                            } catch (Exception e) {
                                Log.e(TAG, "Error converting document to Product object", e);
                            }
                        }
                        productAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Popular products loaded: " + popularProductList.size());
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        showSnackbar("Failed to load popular products.");
                    }
                });
    }

    private void showSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }
}
