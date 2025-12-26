package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.mad_project.Fragment.BagFragment;
import com.example.mad_project.Fragment.HeartFragment;
import com.example.mad_project.Fragment.HomeFragment;
import com.example.mad_project.Fragment.ProfileFragment;
import com.example.mad_project.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // Avoid padding bottom so nav bar sits flush
            return insets;
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        // Load the default fragment (Home) immediately
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            } else if (itemId == R.id.nav_heart) {
                selectedFragment = new HeartFragment();
            } else if (itemId == R.id.nav_bag) {
                selectedFragment = new BagFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        Intent intent = getIntent();
        if (intent != null && "BagFragment".equals(intent.getStringExtra("navigateTo"))) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new BagFragment())
                    .commit();
            bottomNav.setSelectedItemId(R.id.nav_bag);
        }
    }
}