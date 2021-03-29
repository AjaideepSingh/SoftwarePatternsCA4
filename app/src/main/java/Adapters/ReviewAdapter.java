package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.softwarepatternsca4.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import Model.Review;
import Model.User;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Review> reviews;
    private final Context context;

    public ReviewAdapter(ArrayList<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, final int position) {
        holder.setTitle(reviews.get(position).getProductTitle());
        holder.setRating(reviews.get(position).getRating());
        holder.setReview(reviews.get(position).getReview());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(reviews.get(position).getUserID());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                holder.setUserName(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title, userName, review, rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.reviewRowTitle);
            userName = itemView.findViewById(R.id.reviewRowName);
            review = itemView.findViewById(R.id.reviewRowMessage);
            rating = itemView.findViewById(R.id.reviewRowRating);
        }

        @SuppressLint("SetTextI18n")
        public void setTitle(String t) {
            title.setText("Title: " + t);
        }

        @SuppressLint("SetTextI18n")
        public void setUserName(String un) {
            userName.setText("User: " + un);
        }

        @SuppressLint("SetTextI18n")
        public void setReview(String r) {
            review.setText("Review: " + r);
        }

        @SuppressLint("SetTextI18n")
        public void setRating(String ra) {
            rating.setText(ra + " Stars");
        }
    }
}
