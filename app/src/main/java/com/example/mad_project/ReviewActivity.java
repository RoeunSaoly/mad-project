package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.mad_project.Adapter.ReviewBagAdapter;
import com.example.mad_project.db.AppDatabase;
import com.example.mad_project.db.BagDao;
import com.example.mad_project.db.BagItem;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReviewBagAdapter adapter;
    private AppDatabase appDb;
    private BagDao bagDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private TextView subtotalTextView;
    private TextView shippingFeeTextView;
    private TextView totalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        TextView back_btn = findViewById(R.id.back_btn);
        Button Continue = findViewById(R.id.Continue);

        recyclerView = findViewById(R.id.bag_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        subtotalTextView = findViewById(R.id.subtotal);
        shippingFeeTextView = findViewById(R.id.shippingFee);
        totalTextView = findViewById(R.id.total);

        appDb = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "mad-project-db")
                .fallbackToDestructiveMigration()
                .build();
        bagDao = appDb.bagDao();

        getBagItems();

        back_btn.setOnClickListener(v -> finish());

        Continue.setOnClickListener(v -> {
            Intent intent1 = new Intent(ReviewActivity.this, PaymentActivity.class);
            startActivity(intent1);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void getBagItems() {
        executor.execute(() -> {
            List<BagItem> bagItems = bagDao.getAll();
            int subtotalInCents = 0;
            for (BagItem item : bagItems) {
                subtotalInCents += item.price * item.amount;
            }

            final int finalSubtotalInCents = subtotalInCents;

            runOnUiThread(() -> {
                adapter = new ReviewBagAdapter(ReviewActivity.this, bagItems);
                recyclerView.setAdapter(adapter);

                Intent intent = getIntent();
                String shippingMethod = intent.getStringExtra("selectedShippingMethod");
                int shippingFeeInCents = 0;

                if (shippingMethod != null && shippingMethod.equals("Express")) {
                    shippingFeeInCents = 599;
                    shippingFeeTextView.setText(String.format(Locale.US, "$%.2f", 5.99));
                } else {
                    shippingFeeTextView.setText("Free");
                }

                int totalInCents = finalSubtotalInCents + shippingFeeInCents;

                subtotalTextView.setText(String.format(Locale.US, "$%.2f", finalSubtotalInCents / 100.0));
                totalTextView.setText(String.format(Locale.US, "$%.2f", totalInCents / 100.0));
            });
        });
    }
}
