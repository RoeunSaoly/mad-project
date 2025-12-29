package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.example.mad_project.db.AppDatabase;
import com.example.mad_project.db.BagDao;
import com.example.mad_project.db.BagItem;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExplorePage extends AppCompatActivity {

    private AppDatabase appDb;
    private BagDao bagDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_page);

        ArrayList<String> sizeSelect = new ArrayList<>();
        Spinner spinner = findViewById(R.id.spinner);
        TextView backBtn = findViewById(R.id.back_btn);
        TextView name = findViewById(R.id.name);
        TextView name1 = findViewById(R.id.name1);
        TextView description2 = findViewById(R.id.Description2);
        TextView price = findViewById(R.id.price);
        ImageView images = findViewById(R.id.image);
        Button addToBagButton = findViewById(R.id.button);

        appDb = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "mad-project-db")
                .fallbackToDestructiveMigration()
                .build();
        bagDao = appDb.bagDao();

        Intent product = getIntent();
        if (product.getStringExtra("Description") != null) {
            description2.setText(product.getStringExtra("Description"));
        }
        if (product.getStringExtra("Name") != null) {
            name.setText(product.getStringExtra("Name"));
            name1.setText(product.getStringExtra("Name"));
        }
        if (product.getStringExtra("Price") != null) {
            price.setText(product.getStringExtra("Price"));
        }
        if (product.getStringExtra("img") != null) {
            String imageUrl = product.getStringExtra("img");
            Glide.with(this).load(imageUrl).into(images);
        }

        sizeSelect.add("S");
        sizeSelect.add("M");
        sizeSelect.add("L");
        sizeSelect.add("XL");
        sizeSelect.add("XXL");
        sizeSelect.add("XXXL");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sizeSelect);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        backBtn.setOnClickListener(v -> finish());

        addToBagButton.setOnClickListener(v -> {
            String productName = product.getStringExtra("Name");
            String productPrice = product.getStringExtra("Price");
            String productImageUrl = product.getStringExtra("img");

            BagItem bagItem = new BagItem();
            bagItem.name = productName;
            bagItem.price = productPrice;
            bagItem.imageUrl = productImageUrl;
            bagItem.amount = 1;

            executor.execute(() -> {
                bagDao.insert(bagItem);
                runOnUiThread(() -> {
                    Toast.makeText(ExplorePage.this, "Added to bag", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ExplorePage.this, HomeActivity.class);
                    intent.putExtra("navigateTo", "BagFragment");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });
            });
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
