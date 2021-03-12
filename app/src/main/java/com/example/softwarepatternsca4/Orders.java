package com.example.softwarepatternsca4;

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

import Adapters.OrderAdapter;
import Model.Order;

public class Orders extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private final ArrayList<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        recyclerView = findViewById(R.id.orderRCV);
        Objects.requireNonNull(getSupportActionBar()).setTitle("User Order History");
        showAllOrders();
    }

    public void showAllOrders() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    orders.add(order);
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(Orders.this));
                orderAdapter = new OrderAdapter(orders,Orders.this);
                recyclerView.setAdapter(orderAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Orders.this,"Error occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}