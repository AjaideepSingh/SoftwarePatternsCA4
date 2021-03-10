package com.example.softwarepatternsca4;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
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
        recyclerView = findViewById(R.id.reviewRCV);
        EditText search = findViewById(R.id.reviewSearch);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        getAllReviews();
    }

    private void filter(String text) {
        ArrayList<Review> filteredList = new ArrayList<>();
        for(Review review : reviews) {
            if (review.getProductTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(review);
            }
        }
        reviewAdapter.filteredList(filteredList);
    }

    public void getAllReviews() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Review");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                    Review review = reviewSnapshot.getValue(Review.class);
                    reviews.add(review);
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(Reviews.this));
                reviewAdapter = new ReviewAdapter(reviews,Reviews.this);
                recyclerView.setAdapter(reviewAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Reviews.this, "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}