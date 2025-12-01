package com.example.mad_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {

    private final List<CarouselItem> carouselItems;

    public CarouselAdapter(List<CarouselItem> carouselItems) {
        this.carouselItems = carouselItems;
    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CarouselViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.carousel_item, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        holder.bind(carouselItems.get(position));
    }

    @Override
    public int getItemCount() {
        return carouselItems.size();
    }

    static class CarouselViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView titleView;
        private final TextView descriptionView;

        CarouselViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.carousel_image);
            titleView = view.findViewById(R.id.carousel_title);
            descriptionView = view.findViewById(R.id.carousel_description);
        }

        void bind(CarouselItem carouselItem) {
            titleView.setText(carouselItem.getTitle());
            descriptionView.setText(carouselItem.getDescription());

            Glide.with(itemView.getContext())
                    .load(carouselItem.getImageUrl())
                    .into(imageView);
        }
    }
}

// A simple data class for the carousel items
class CarouselItem {
    private final String title;
    private final String description;
    private final String imageUrl;

    public CarouselItem(String title, String description, String imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
