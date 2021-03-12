package Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
        holder.setImageView(orders.get(position).getItem().getImage());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title, userName, stock;
        private final ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.orderRowImage);
            title = itemView.findViewById(R.id.orderRowTitle);
            userName = itemView.findViewById(R.id.orderRowName);
            stock = itemView.findViewById(R.id.orderRowQuantity);
        }

        public void setTitle(String t) {
            title.setText("Title: " + t);
        }

        public void setUserName(String un) {
            userName.setText("User: " + un);
        }

        public void setStock(String r) {
            stock.setText(r + " Quantity");
        }

        public void setImageView(String ra) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child(ra).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            });
        }
    }
}
