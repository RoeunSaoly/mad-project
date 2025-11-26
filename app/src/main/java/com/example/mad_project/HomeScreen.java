package com.example.mad_project;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends AppCompatActivity {

    private ViewPager2 viewPager;
    private CarouselAdapter adapter;
    private TabLayout tabLayout; // 3. Declare TabLayout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);

        viewPager = findViewById(R.id.carouselViewPager);
        tabLayout = findViewById(R.id.tabIndicator); // 4. Find the TabLayout
        // 2. Prepare your data (e.g., a list of drawable images)
        // Make sure you have images named 'image1', 'image2', etc. in your res/drawable folder
        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.image1); // Replace with your actual drawable resources
        imageList.add(R.drawable.image2);
        imageList.add(R.drawable.image3);

        // 3. Create an instance of your adapter
        adapter = new CarouselAdapter(imageList);

        // 4. Set the adapter on the ViewPager2
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // This lambda is intentionally left empty.
            // The TabLayoutMediator handles creating and updating the dots.
        }).attach();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}