package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        TextView back_btn = findViewById(R.id.back_btn);
        ImageView qrcode = findViewById(R.id.qrcode);
        TextView totalTextView = findViewById(R.id.total);
        TextView nameTextView = findViewById(R.id.name);
        TextView emailTextView = findViewById(R.id.email);

        qrcode.setImageResource(R.drawable.qrcode);

        Intent intent = getIntent();
        int totalAmountInCents = intent.getIntExtra("totalAmount", 0);
        totalTextView.setText(String.format(Locale.US, "$%.2f USD", totalAmountInCents / 100.0));

        updateUserDetails(nameTextView, emailTextView);

        back_btn.setOnClickListener(v -> finish());

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
    private void updateUserDetails(TextView nameTextView, TextView emailTextView) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                nameTextView.setText(currentUser.getDisplayName());
            } else {
                nameTextView.setText(getUsernameFromEmail(currentUser.getEmail())); // Placeholder
            }
            emailTextView.setText(currentUser.getEmail());
        }
    }
}
