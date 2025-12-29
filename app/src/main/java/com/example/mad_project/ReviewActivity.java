package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.mad_project.Adapter.BagAdapter;
import com.example.mad_project.Adapter.ReviewBagAdapter;
import com.example.mad_project.db.AppDatabase;
import com.example.mad_project.db.BagDao;
import com.example.mad_project.db.BagItem;
import com.example.mad_project.db.DatabaseClient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReviewBagAdapter adapter;
    private AppDatabase appDb;
    private BagDao bagDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review);

        TextView back_btn = findViewById(R.id.back_btn);
        Button Continue = findViewById(R.id.Continue);

        recyclerView = findViewById(R.id.bag_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        appDb = Room.databaseBuilder(this.getApplicationContext(), AppDatabase.class, "mad-project-db")
                .fallbackToDestructiveMigration()
                .build();
        bagDao = appDb.bagDao();

        Intent intent = getIntent();
        String shippingMethod = intent.getStringExtra("selectedShippingMethod");

        if (shippingMethod.equals("Express")) {
            TextView shippingFee = findViewById(R.id.shippingFee);
            shippingFee.setText("$5.99");
        } else {
            TextView shippingFee = findViewById(R.id.shippingFee);
            shippingFee.setText("Free");
        }


        getBagItems();



        back_btn.setOnClickListener(v -> {
            finish();
        });

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
            if (this != null) {
                this.runOnUiThread(() -> {
                    adapter = new ReviewBagAdapter(this, bagItems);
                    recyclerView.setAdapter(adapter);
                });
            }
        });
    }
}