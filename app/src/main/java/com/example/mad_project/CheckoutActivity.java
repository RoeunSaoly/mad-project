package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CheckoutActivity extends AppCompatActivity {

    private String selectedShippingMethod = "Standard";
    private TextView nameTextView;
    private TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        TextView back_btn = findViewById(R.id.back_btn);
        Button Continue = findViewById(R.id.Continue);
        CardView standard = findViewById(R.id.standard);
        CardView express = findViewById(R.id.express);
        nameTextView = findViewById(R.id.name);
        emailTextView = findViewById(R.id.email);

        updateUserDetails();

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

        back_btn.setOnClickListener(v -> finish());

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
    public static String getUsernameFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }
        return email.substring(0, email.indexOf('@'));
    }
    private void updateUserDetails() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            emailTextView.setText(currentUser.getEmail());
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                nameTextView.setText(currentUser.getDisplayName());
            } else {
                nameTextView.setText(getUsernameFromEmail(currentUser.getEmail())); // Placeholder
            }

        }
    }

}
