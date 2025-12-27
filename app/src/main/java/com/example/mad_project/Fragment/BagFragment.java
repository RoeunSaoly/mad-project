package com.example.mad_project.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.Adapter.BagAdapter;
import com.example.mad_project.CheckoutActivity;
import com.example.mad_project.R;
import com.example.mad_project.db.BagItem;
import com.example.mad_project.db.DatabaseClient;

import java.util.List;

public class BagFragment extends Fragment {

    private RecyclerView recyclerView;
    private BagAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bag, container, false);

        Button checkoutButton = view.findViewById(R.id.checkout);

        checkoutButton.setOnClickListener(v -> {
            // Navigate to CheckoutActivity
            Intent intent = new Intent(getContext(), CheckoutActivity.class);
            startActivity(intent);
        });


        recyclerView = view.findViewById(R.id.bag_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getBagItems();
        return view;
    }

    private void getBagItems() {
        new Thread(() -> {
            List<BagItem> bagItems = DatabaseClient.getInstance(getContext()).getAppDatabase()
                    .bagDao().getAll();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    adapter = new BagAdapter(getContext(), bagItems);
                    recyclerView.setAdapter(adapter);
                });
            }
        }).start();
    }
}