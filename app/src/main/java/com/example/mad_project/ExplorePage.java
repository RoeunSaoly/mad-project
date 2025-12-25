package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.mad_project.db.BagItem;
import com.example.mad_project.db.DatabaseClient;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class ExplorePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_explore_page);

        ArrayList<String> Size_select = new ArrayList<>();
        Spinner spinner = findViewById(R.id.spinner);
        TextView back_btn = findViewById(R.id.back_btn);
        TextView name = findViewById(R.id.name);
        TextView name1 = findViewById(R.id.name1);
        TextView Description2 = findViewById(R.id.Description2);
        TextView price = findViewById(R.id.price);
        ImageView images = findViewById(R.id.image);
        Button addToBagButton = findViewById(R.id.button);



        Intent product = getIntent();
//        if (product.getStringExtra("Description") != null)
//            Log.d("TAG", "onCreate: " + product.getStringExtra("Description"));
//            Description2.setText(product.getStringExtra("Description")
//            );
        if (product.getStringExtra("Name") != null)
            name.setText(product.getStringExtra("Name")
            );
        if (product.getStringExtra("Price") != null)
            name1.setText(product.getStringExtra("Name")
            );
        if (product.getStringExtra("Price") != null)
            price.setText(product.getStringExtra("Price")
            );
        if (product.getStringExtra("img") != null) {
            String imageUrl = product.getStringExtra("img");
            Glide.with(this).load(imageUrl).into(images);
        }

        Size_select.add("S");
        Size_select.add("M");
        Size_select.add("L");
        Size_select.add("XL");
        Size_select.add("XXL");
        Size_select.add("XXXL");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Size_select);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        back_btn.setOnClickListener(v -> {
            finish();
        });

        addToBagButton.setOnClickListener(v -> {
            String productName = product.getStringExtra("Name");
            String productPrice = product.getStringExtra("Price");
            String productImageUrl = product.getStringExtra("img");

            BagItem cartItem = new BagItem();
            cartItem.name = productName;
            cartItem.price = productPrice;
            cartItem.imageUrl = productImageUrl;
            cartItem.amount = 1;

            new Thread(() -> {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .bagDao()
                        .insert(cartItem);
                runOnUiThread(() -> Toast.makeText(ExplorePage.this, "Added to bag", Toast.LENGTH_SHORT).show());
            }).start();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}