package com.example.mad_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {

    private final List<Integer> imageResourceIds; // List of drawable resource IDs

    // Constructor to get the list of images
    public CarouselAdapter(List<Integer> imageResourceIds) {
        this.imageResourceIds = imageResourceIds;
    }

    // This method creates a new ViewHolder for each item
    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carousel_item, parent, false);
        return new CarouselViewHolder(view);
    }

    // This method binds the data to the ViewHolder for a specific position
    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        // Set the image for the current item
        holder.carouselImageView.setImageResource(imageResourceIds.get(position));
    }

    // This method returns the total number of items
    @Override
    public int getItemCount() {
        return imageResourceIds.size();
    }

    // The ViewHolder class holds the views for a single item
    public static class CarouselViewHolder extends RecyclerView.ViewHolder {
        ImageView carouselImageView;

        public CarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            carouselImageView = itemView.findViewById(R.id.carouselImageView);
        }
    }
}
