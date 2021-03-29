package com.example.softwarepatternsca4;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Objects;
import Adapters.ReviewAdapter;
import Model.Review;

public class Reviews extends AppCompatActivity {
    private ReviewAdapter reviewAdapter;
    private RecyclerView recyclerView;
    private final ArrayList<Review> reviews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Product Reviews");
        recyclerView = findViewById(R.id.reviewRCV);
        getAllReviews();
    }

    public void getAllReviews() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Review");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                    Review review = reviewSnapshot.getValue(Review.class);
                    reviews.add(review);
                }
                if (reviews.isEmpty()) {
                    Toast.makeText(Reviews.this, "No product reviews currently!", Toast.LENGTH_SHORT).show();
                } else {
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Reviews.this));
                    reviewAdapter = new ReviewAdapter(reviews, Reviews.this);
                    recyclerView.setAdapter(reviewAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Reviews.this, "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Reviews.this,Home.class));
    }
}