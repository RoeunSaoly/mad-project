package com.example.mad_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;



public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {

    private final List<Integer> imageResourceIds;

    public CarouselAdapter(List<Integer> imageResourceIds) {
        this.imageResourceIds = imageResourceIds;
    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carousel_item, parent, false);
        return new CarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        holder.carouselImageView.setImageResource(imageResourceIds.get(position));
    }

    @Override
    public int getItemCount() {
        return imageResourceIds.size();
    }

    public static class CarouselViewHolder extends RecyclerView.ViewHolder {
        ImageView carouselImageView;

        public CarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            carouselImageView = itemView.findViewById(R.id.carouselImageView);
        }
    }
}
