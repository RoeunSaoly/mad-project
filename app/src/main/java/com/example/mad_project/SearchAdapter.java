package com.example.mad_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.VH> implements Filterable {

    private final List<String> fullList;      // original data
    private List<String> filteredList;        // filtered data shown

    public SearchAdapter(List<String> data) {
        this.fullList = new ArrayList<>(data);
        this.filteredList = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.title.setText(filteredList.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title;
        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
        }
    }

    // Filterable
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint == null ? "" : constraint.toString().trim().toLowerCase();
                FilterResults results = new FilterResults();
                if (query.isEmpty()) {
                    results.values = new ArrayList<>(fullList);
                    results.count = fullList.size();
                } else {
                    List<String> filtered = new ArrayList<>();
                    for (String s : fullList) {
                        if (s.toLowerCase().contains(query)) {
                            filtered.add(s);
                        }
                    }
                    results.values = filtered;
                    results.count = filtered.size();
                }
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<String>) (results.values == null ? new ArrayList<String>() : results.values);
                notifyDataSetChanged();
            }
        };
    }
}
