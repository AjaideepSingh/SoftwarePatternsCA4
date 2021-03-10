package com.example.softwarepatternsca4;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import Adapters.CheckoutAdapter;
import Model.Cart;
import Model.Item;
import Model.User;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class Checkout extends AppCompatActivity {
    private CheckoutAdapter checkoutAdapter;
    private RecyclerView recyclerView;
    private final ArrayList<Cart> cartArrayList = new ArrayList<>();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Your basket");
        recyclerView = findViewById(R.id.checkoutRCV);
        Button checkout = findViewById(R.id.checkout);
        constraintLayout = findViewById(R.id.checkoutCL);
        showAllItemsInCart();
        checkout.setOnClickListener(v -> {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(Checkout.this);
            if(cartArrayList.isEmpty()) {
                dlgAlert.setMessage("Your cart is empty!");
                dlgAlert.setTitle("Error...");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                ImageView imageView = ((Home) getApplicationContext()).findViewById(R.id.cart);
                imageView.setVisibility(View.INVISIBLE);
            } else {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(mAuth.getUid());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel notificationChannel = new NotificationChannel("My Notification", "test", NotificationManager.IMPORTANCE_DEFAULT);
                            NotificationManager notificationManager = getSystemService(NotificationManager.class);
                            notificationManager.createNotificationChannel(notificationChannel);
                        }
                        orderProcessed();
                        DatabaseReference priceReference = FirebaseDatabase.getInstance().getReference("Cart");
                        priceReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                double totalPrice = 0;
                                for(DataSnapshot cartSnapshot : snapshot.getChildren()) {
                                    Cart cart = cartSnapshot.getValue(Cart.class);
                                    assert cart != null;
                                    if(cart.getUserID().equals(mAuth.getUid())) {
                                        totalPrice += cart.getItem().getPrice();
                                    }
                                }
                                if(user.getStudent().equalsIgnoreCase("yes")) {
                                    totalPrice = totalPrice - (100 * 10 / 100);
                                    studentDiscount();
                                }
                                noStudentDiscount();
                                String message = "Order has been processed,Order total: " + totalPrice;
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(Checkout.this, "My Notification").setSmallIcon(
                                        R.drawable.info).setContentTitle("Thank you").setContentText(message).setAutoCancel(true);
                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(Checkout.this);
                                notificationManagerCompat.notify(0, builder.build());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(Checkout.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        DatabaseReference removeReference = FirebaseDatabase.getInstance().getReference("Cart");
                        for(int i = 0; i < cartArrayList.size(); i++) {
                            removeReference.child(cartArrayList.get(i).getId()).removeValue();
                            cartArrayList.clear();
                            checkoutAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Checkout.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    public void showAllItemsInCart() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Cart");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                    Cart cart = cartSnapshot.getValue(Cart.class);
                    assert cart != null;
                    cart.setId(cartSnapshot.getKey());
                    if (cart.getUserID().equals(mAuth.getUid())) {
                        cartArrayList.add(cart);
                    }
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(Checkout.this));
                checkoutAdapter = new CheckoutAdapter(cartArrayList);
                new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
                recyclerView.setAdapter(checkoutAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Checkout.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    ItemTouchHelper.SimpleCallback itemTouch = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            Collections.swap(cartArrayList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
            checkoutAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return false;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(Checkout.this, R.color.black))
                    .addActionIcon(R.drawable.settings)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            DatabaseReference deleteReference = FirebaseDatabase.getInstance().getReference("Cart");
            Cart cart = cartArrayList.get(viewHolder.getAdapterPosition());
            deleteReference.child(cart.getId()).removeValue();
            cartArrayList.clear();
            checkoutAdapter.notifyDataSetChanged();
            DatabaseReference updateReference = FirebaseDatabase.getInstance().getReference("Item");
            updateReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        Item item = itemSnapshot.getValue(Item.class);
                        assert item != null;
                        item.setId(itemSnapshot.getKey());
                        if (item.getTitle().equals(cart.getItem().getTitle())) {
                            updateReference.child(item.getId()).child("stockAmount").setValue(item.getStockAmount() + cart.getItem().getStockAmount()).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Checkout.this, "Item removed from basket", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(Checkout.this, "Error occurred: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Checkout.this, "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    public void orderProcessed() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Order processed", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void noStudentDiscount() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Sorry no discount available", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void studentDiscount() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "10% student discount applied", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}