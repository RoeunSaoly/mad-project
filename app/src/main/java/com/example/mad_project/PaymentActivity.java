package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.mad_project.db.AppDatabase;
import com.example.mad_project.db.BagDao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaymentActivity extends AppCompatActivity {

    private AppDatabase appDb;
    private BagDao bagDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    TextView nameTextView;
    TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        TextView back_btn = findViewById(R.id.back_btn);
        ImageView qrcode = findViewById(R.id.qrcode);
        TextView totalTextView = findViewById(R.id.total);
        nameTextView = findViewById(R.id.name);
        emailTextView = findViewById(R.id.email);
        Button payButton = findViewById(R.id.pay);

        LinearLayout successOverlay = findViewById(R.id.success_overlay);
        ImageView successIcon = findViewById(R.id.success_icon);

        appDb = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "mad-project-db")
                .fallbackToDestructiveMigration()
                .build();
        bagDao = appDb.bagDao();

        payButton.setOnClickListener(v -> {
            // Disable button to prevent multiple clicks
            payButton.setEnabled(false);

            executor.execute(() -> {
                bagDao.deleteAll();
                runOnUiThread(() -> {
                    // Show success overlay with animation
                    successOverlay.setVisibility(View.VISIBLE);
                    successOverlay.setAlpha(0f);
                    successOverlay.animate()
                            .alpha(1f)
                            .setDuration(500)
                            .start();

                    // Animate the checkmark icon
                    successIcon.setScaleX(0f);
                    successIcon.setScaleY(0f);
                    successIcon.animate()
                            .scaleX(1.2f)
                            .scaleY(1.2f)
                            .setDuration(600)
                            .withEndAction(() -> {
                                successIcon.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .setDuration(200)
                                        .start();
                            })
                            .start();

                    // Navigate to Home after a short delay
                    payButton.postDelayed(() -> {
                        Intent intent = new Intent(this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }, 2000);
                });
            });
        });

        qrcode.setImageResource(R.drawable.qrcode);

        Intent intent = getIntent();
        int totalAmountInCents = intent.getIntExtra("totalAmount", 0);
        totalTextView.setText(String.format(Locale.US, "$%.2f USD", totalAmountInCents / 100.0));

        updateUserDetails();

        back_btn.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public static String getUsernameFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "User";
        }
        return email.substring(0, email.indexOf('@'));
    }

    private void updateUserDetails() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            emailTextView.setText(currentUser.getEmail());

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            if (name != null && !name.isEmpty()) {
                                nameTextView.setText(name);
                            } else {
                                setFallbackName(currentUser);
                            }
                        } else {
                            setFallbackName(currentUser);
                        }
                    })
                    .addOnFailureListener(e -> setFallbackName(currentUser));
        }
    }

    private void setFallbackName(FirebaseUser currentUser) {
        if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
            nameTextView.setText(currentUser.getDisplayName());
        } else {
            nameTextView.setText(getUsernameFromEmail(currentUser.getEmail()));
        }
    }
}
