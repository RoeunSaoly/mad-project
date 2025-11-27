package com.example.mad_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.widget.SearchView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private ShirtAdapter adapter;
    private List<Shirt> shirtList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Horizontal RecyclerView for popular shirts
        RecyclerView recyclerView = view.findViewById(R.id.horizontalRecyclerView);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        shirtList = new ArrayList<>(Arrays.asList(
                new Shirt("T-Shirt", 25, R.drawable.img),
                new Shirt("Jeans", 40, R.drawable.img_1),
                new Shirt("Jacket", 60, R.drawable.img_2),
                new Shirt("Sweater", 35, R.drawable.img_3),
                new Shirt("Dress", 50, R.drawable.img_4)
        ));

        adapter = new ShirtAdapter(shirtList);
        recyclerView.setAdapter(adapter);

        // HomeFragment search (filter horizontal list)
        SearchView homeSearch = view.findViewById(R.id.homeSearchView);
        homeSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        return view;
    }

    private void filterList(String query) {
        List<Shirt> filtered = new ArrayList<>();
        for (Shirt shirt : shirtList) {
            if (shirt.getName().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(shirt);
            }
        }
        adapter.updateList(filtered);
    }
}
