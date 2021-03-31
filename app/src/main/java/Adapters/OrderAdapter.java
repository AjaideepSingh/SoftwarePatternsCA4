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
import Model.Order;
import Model.User;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Order> orders;
    private final Context context;

    public OrderAdapter(ArrayList<Order> orders,Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, final int position) {
        holder.setTitle(orders.get(position).getItem().getTitle());
        holder.setStock(String.valueOf(orders.get(position).getItem().getStockAmount()));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(orders.get(position).getUserID());
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
        holder.setTimeOfPurchase(orders.get(position).getDateTime());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title, userName, stock,timeOfPurchase;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.orderRowTitle);
            userName = itemView.findViewById(R.id.orderRowName);
            stock = itemView.findViewById(R.id.orderRowQuantity);
            timeOfPurchase = itemView.findViewById(R.id.top);
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
        public void setStock(String r) {
            stock.setText(r + " Qty purchased");
        }

        public void setTimeOfPurchase(String top) {
            timeOfPurchase.setText(top);
        }
    }
}
