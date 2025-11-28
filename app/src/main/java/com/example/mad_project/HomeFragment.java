package com.example.mad_project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button testFirestoreButton = view.findViewById(R.id.test_firestore_button);
        testFirestoreButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> product = new HashMap<>();
            product.put("category", "Electronics");
            product.put("description", "Premium sound...");
            product.put("image", "https://images.unsplash.com/photo-1505740420928-...");
            product.put("name", "Wireless Earbuds");
            product.put("price", 89.99);
            product.put("stock", 45);

            db.collection("products").add(product)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Snackbar.make(v, "Product added to Firestore!", Snackbar.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding document", e);
                        Snackbar.make(v, "Error adding product to Firestore: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    });
        });

        return view;
    }
}
