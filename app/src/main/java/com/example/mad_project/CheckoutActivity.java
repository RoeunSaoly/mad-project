package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CheckoutActivity extends AppCompatActivity {

    private String selectedShippingMethod = "Standard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);

        TextView back_btn = findViewById(R.id.back_btn);
        Button Continue = findViewById(R.id.Continue);
        CardView standard = findViewById(R.id.standard);
        CardView express = findViewById(R.id.express);

        standard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
        express.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        standard.setOnClickListener(v -> {
            standard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
            express.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            selectedShippingMethod = "Standard";
        });

        express.setOnClickListener(v -> {
            express.setCardBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
            standard.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            selectedShippingMethod = "Express";
        });

        back_btn.setOnClickListener(v -> {
            finish();
        });

        Continue.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, ReviewActivity.class);
            intent.putExtra("selectedShippingMethod", selectedShippingMethod);
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
