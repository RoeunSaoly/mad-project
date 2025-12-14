package com.example.mad_project.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mad_project.CarouselItem;
import com.example.mad_project.R;

import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder> {

    private final List<CarouselItem> carouselItems;

    public CarouselAdapter(List<CarouselItem> carouselItems) {
        this.carouselItems = carouselItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CarouselItem item = carouselItems.get(position);
        holder.carouselTitle.setText(item.getTitle());
        holder.carouselDescription.setText(item.getDescription());
        Glide.with(holder.itemView.getContext()).load(item.getImageResId()).into(holder.carouselImage);
    }

    @Override
    public int getItemCount() {
        return carouselItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView carouselImage;
        TextView carouselTitle;
        TextView carouselDescription;
        Button allFitnessButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            carouselImage = itemView.findViewById(R.id.carousel_image);
            carouselTitle = itemView.findViewById(R.id.carousel_title);
            carouselDescription = itemView.findViewById(R.id.carousel_description);
            allFitnessButton = itemView.findViewById(R.id.all_fitness_button);
        }
    }
}
