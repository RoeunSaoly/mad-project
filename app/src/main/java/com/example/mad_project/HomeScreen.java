package com.example.mad_project;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);

        ViewPager2 viewPager = findViewById(R.id.carouselViewPager);

        List<CarouselItem> imageList = new ArrayList<>();
        imageList.add(new CarouselItem("Title 1", "Description 1", R.drawable.id1));
        imageList.add(new CarouselItem("Title 2", "Description 2", R.drawable.id1));
        imageList.add(new CarouselItem("Title 3", "Description 3", R.drawable.id1));

        CarouselAdapter adapter = new CarouselAdapter(imageList);
        viewPager.setAdapter(adapter);

        RecyclerView carousel2RecyclerView = findViewById(R.id.recommendedRecyclerView);

        List<Integer> imageListPopular = new ArrayList<>();
        imageListPopular.add(R.drawable.image1);
        imageListPopular.add(R.drawable.image1);
        imageListPopular.add(R.drawable.image1);
        imageListPopular.add(R.drawable.image1);
        imageListPopular.add(R.drawable.image1);
        imageListPopular.add(R.drawable.image1);
        imageListPopular.add(R.drawable.image1);
        imageListPopular.add(R.drawable.image1);

        RecyclerViewAdapter recyclerView = new RecyclerViewAdapter(imageListPopular);
        carousel2RecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        carousel2RecyclerView.setAdapter(recyclerView);




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
